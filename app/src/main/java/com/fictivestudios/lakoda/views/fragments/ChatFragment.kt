package com.fictivestudios.lakoda.views.fragments

import SocketApp
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.adapters.ChatAdapter
import com.fictivestudios.lakoda.adapters.SwipeReply
import com.fictivestudios.lakoda.apiManager.response.ChatAttachmentResponse
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.apiManager.response.UpdateProfileResponse
import com.fictivestudios.lakoda.model.*
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.utils.getFormDataBody
import com.fictivestudios.lakoda.utils.getPartMap
import com.fictivestudios.lakoda.viewModel.ChatViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.MESSAGE
import com.fictivestudios.ravebae.utils.Constants.Companion.MESSAGE_TYPE
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_IMAGE
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_TEXT
import com.fictivestudios.ravebae.utils.Constants.Companion.getUser
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.permissionx.guolindev.PermissionX
import io.socket.client.Ack
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.edit_profile_fragment.view.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.HashMap


class ChatFragment : BaseFragment(),SwipeReply {


    private var fileTemporary: File? = null
    private var chatAdapter: ChatAdapter? = null
    private var messageList = ArrayList<receivedMessageData>()

    private var receiverUserId: String? = null
    private  var userName: String? = null
    private  var profileImage: String? = null
    private var message: String? = null
    private var messageType: String? = null

    private val PICKFILE_RESULT_CODE: Int =88
    private val SELECT_VIDEO: Int = 29
    var isAttachment = false


    var socket:Socket?=null

    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel

    private lateinit var mView: View

    override fun setTitlebar(titlebar: Titlebar) {

        titlebar.setChatTitle(userName.toString(),profileImage.toString(),
            object :View.OnClickListener{
                override fun onClick(p0: View?) {


                    val bundle = bundleOf(
                        Constants.USER_ID to  receiverUserId.toString(),
                        Constants.USER_NAME to   userName.toString(),
                        Constants.PROFILE to   profileImage.toString()

                    )

                    MainActivity.getMainActivity
                        ?.navControllerMain?.navigate(R.id.friendProfileFragment,bundle)
                }
            }
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        receiverUserId = arguments?.getString(Constants.USER_ID).toString()
        userName = arguments?.getString(Constants.USER_NAME).toString()
        profileImage = arguments?.getString(Constants.PROFILE).toString()



        mView = inflater.inflate(R.layout.fragment_chat, container, false)

        mView.btn_close.setOnClickListener {

            mView.ll_message_reply.visibility = View.GONE
        }

        mView.btn_attach.setOnClickListener {

            if (isAttachment)
            {
                isAttachment = false
                mView.lay_attachment.visibility = View.GONE
            }
            else
            {
                isAttachment = true
                mView.lay_attachment.visibility = View.VISIBLE
            }

        }

        mView.btn_photos.setOnClickListener {

            openImagePicker()

        }

        mView.btn_location.setOnClickListener {

            MainActivity?.getMainActivity?.navControllerMain
                ?.navigate(R.id.mapFragment)
        }

        mView.btn_document.setOnClickListener {

            filePicker()
        }

        mView.btn_video.setOnClickListener {

            videoPicker()
        }

        getSocket()



        mView.tv_send.setOnClickListener {
            sendMessage(mView.et_write_msg.text.toString(), TYPE_TEXT)
        }




        if (!arguments?.getString(MESSAGE).isNullOrBlank() || arguments?.getString(MESSAGE)?.equals("null") == true)
        {
            message = arguments?.getString(MESSAGE)
            messageType =arguments?.getString(MESSAGE_TYPE)
            message?.let { messageType?.let { it1 -> sendMessage(it, it1) } }
        }

        return mView
    }






    private fun getSocket() {
        if(socket==null) {
            val app: SocketApp = SocketApp.instance!!
            socket = app.socket
            socket!!.on(Socket.EVENT_CONNECT, onConnect)
            socket!!.on(Socket.EVENT_DISCONNECT, onDisconnect)
            socket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            socket!!.on("response", receiveMessage)
            socket!!.connect()
        }
    }
    private val onConnect = Emitter.Listener {
        Handler(Looper.getMainLooper()).post {
            var toast: Toast? = null
            Log.e("Socket-onConnect", "Socket-onConnect")
            //   toast = Toast.makeText(this, "Connected", Toast.LENGTH_SHORT)
            //    toast.show()
            try {
                getMessage()

            }
            catch (e:java.lang.Exception){
                e.printStackTrace()
            }
        }
    }
    private val onConnectError = Emitter.Listener {
        Handler(Looper.getMainLooper()).post {
            Log.e("Socket-onConnectError", "Socket-onConnectError")

        }
    }
    private val onDisconnect = Emitter.Listener {
        Handler(Looper.getMainLooper()).post {
            var toast: Toast? = null
            //  Log.e("Socket-Disconnected", "Socket-Disconnected")
            //  toast = Toast.makeText(this, "Disconnected", Toast.LENGTH_SHORT)
            //   toast.show()
        }
    }

    private val receiveMessage = Emitter.Listener { arg ->
        Handler(Looper.getMainLooper()).post {
            val data = arg[0] as JSONObject
            Log.e("receiveMessage", "" + data.getString("data"))
            val gson = Gson()
            val listFromGson: ArrayList<receivedMessageData> = gson.fromJson(
                data.getString("data"),
                object : TypeToken<ArrayList<receivedMessageData?>?>() {}.type
            )
            if (listFromGson.size != 1)
            {
                messageList = ArrayList()
                messageList =   listFromGson
                if (!messageList.isNullOrEmpty())
                {
                    chatAdapter =  ChatAdapter( messageList!!,this)
                   mView.rv_chat_message?.adapter = chatAdapter
                    mView.rv_chat_message?.adapter?.notifyDataSetChanged()
                    mView.rv_chat_message?.smoothScrollToPosition(messageList?.size - 1);
                }

                Log.e("receiveMessage", listFromGson.toString())
            }
            else{
                messageList?.add(listFromGson.get(0))
                chatAdapter?.notifyItemChanged(messageList!!.size - 1)
                mView.rv_chat_message?.smoothScrollToPosition(messageList!!.size - 1);
            }

        }
    }


    fun offSocket(){
        try {
            if(socket!=null && socket!!.connected()) {
                socket!!.off(Socket.EVENT_CONNECT, onConnect)
                socket!!.off(Socket.EVENT_DISCONNECT, onDisconnect)
                socket!!.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
                socket!!.off("response", receiveMessage)
                socket!!.disconnect()
            }
        }
        catch (e :Exception){
            e.printStackTrace()
        }
    }


    private fun getMessage() {
        var parameterName = receiverUserId?.let {
            GetMessage(getUser().id.toString(),it,
                getUser().id.toString())
        }
//        Log.e("GetMessage", "userData.toString() " + userData.id)
        //      Log.e("GetMessage", "navArgs.value.bookingId " + navArgs.value.bookingId)
        var jsonObject: JSONObject? = null
        val gson: Gson? = Gson()
        if (gson != null) {
            jsonObject = JSONObject(gson.toJson(parameterName))
        }
        if (socket != null && socket!!.connected()) {
            socket!!.emit("get_messages", jsonObject, Ack() {
            });
            Log.e("get_messages", "jsonObject" + jsonObject.toString())
        } else {
            // Toast.makeText(activity, "Not Connect ,Please reload this screen", Toast.LENGTH_LONG).show()
        }

    }

    private fun sendMessage(message:String,type:String)
    {

        val model = receiverUserId?.let { SendMessage( getUser().id.toString(), it,message,type) }

//        Log.e("GetMessage", "userData.toString() " + userData.id)
//      Log.e("GetMessage", "navArgs.value.bookingId " + navArgs.value.bookingId)
        var jsonObject: JSONObject? = null
        val gson: Gson? = Gson()
        if (gson != null) {
            jsonObject= JSONObject(gson.toJson(model))
        }
        if (socket != null && socket!!.connected()) {

            mView.et_write_msg?.setText("")

            socket!!.emit("send_message", jsonObject, Ack() {
            });
            Log.e("send_message", "jsonObject" + jsonObject.toString())
        } else {
            Toast.makeText(activity, "Not Connect ,Please reload this screen", Toast.LENGTH_LONG).show()
        }
    }


    private fun chatAttachment(type: String)
    {


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())



        var part : MultipartBody.Part? = null


        if (fileTemporary != null){
            part = fileTemporary?.getPartMap("attachment")
        }

        GlobalScope.launch(Dispatchers.IO)
        {

            receiverUserId?.toInt()?.let {
                apiClient.chatAttachment("file",it,part).enqueue(object: retrofit2.Callback<ChatAttachmentResponse> {
                    override fun onResponse(
                        call: Call<ChatAttachmentResponse>,
                        response: Response<ChatAttachmentResponse>
                    ) {

                        try {


                            val response: ChatAttachmentResponse? =response.body()
                            val statuscode= response?.status
                            if (statuscode==1) {

                                Log.d("response", response.message)

                                sendMessage(response.data.attachment.attachment, TYPE_IMAGE)


                            } else {


                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(context, "msd: "+response?.message, Toast.LENGTH_SHORT).show()
                                    response?.message?.let { Log.d("response", it) }

                                })
                            }
                        } catch (e:Exception) {

                            Log.d("response", "msg "+ e.localizedMessage)
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(context, "msg "+ e.localizedMessage, Toast.LENGTH_SHORT).show()
                            })
                        }
                    }

                    override fun onFailure(call: Call<ChatAttachmentResponse>, t: Throwable) {

                        activity?.runOnUiThread(java.lang.Runnable {
                            Toast.makeText(context, "msg "+  t.localizedMessage, Toast.LENGTH_SHORT).show()
                            Log.d("response", "msg "+  t.localizedMessage)



                        })
                    }
                })
            }

        }


    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun openImagePicker()
    {    ImagePicker.with(this)
        .crop()	    			//Crop image(Optional), Check Customization for more option
        .compress(1024)			//Final image size will be less than 1 MB(Optional)
        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
        .start()

    }

    fun videoPicker()
    {
        PermissionX.init(requireActivity())
            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(i, SELECT_VIDEO)

                } else {
                    Toast.makeText(requireContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }

    }

    fun filePicker()
    {
        PermissionX.init(requireActivity())
            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {

                    var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
                    chooseFile.type = "*/*"
                    chooseFile = Intent.createChooser(chooseFile, "Choose a file")
                    startActivityForResult(chooseFile, PICKFILE_RESULT_CODE)

                } else {
                    Toast.makeText(requireContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }

    }
    override fun onSwipe(position: Int) {

        mView.ll_message_reply.visibility = View.VISIBLE
        mView.et_write_msg.requestFocus()

        mView.et_write_msg.showKeyboard()
    }
    fun View.showKeyboard() = ViewCompat.getWindowInsetsController(this)
        ?.show(WindowInsetsCompat.Type.ime())

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_VIDEO)
            {

            }
            else if (requestCode == PICKFILE_RESULT_CODE)
            {

/*                val uri: Uri = attr.data.getData()
                val src = uri.path*/
            }
            else
            {
                val uri: Uri = data?.data!!
                fileTemporary = File(uri.path)
                chatAttachment("file")

                // Use Uri object instead of File to avoid storage permissions
                /*  mView.btn_picture_video.setImageURI(uri)*/
            }
            //Image Uri will not be null for RESULT_OK

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        offSocket()
    }


}