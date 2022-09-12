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
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL

import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.adapters.ChatAdapter
import com.fictivestudios.lakoda.adapters.SwipeReply
import com.fictivestudios.lakoda.apiManager.response.ChatAttachmentResponse
import com.fictivestudios.lakoda.liveData.LiveData
import com.fictivestudios.lakoda.model.*
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.utils.getPartMap
import com.fictivestudios.lakoda.viewModel.ChatViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.MESSAGE
import com.fictivestudios.ravebae.utils.Constants.Companion.MESSAGE_TYPE
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_DOCUMENT
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_IMAGE
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_LOCATION
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_TEXT
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_VIDEO
import com.fictivestudios.ravebae.utils.Constants.Companion.getUser
import com.fictivestudios.ravebae.utils.Constants.Companion.state
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.permissionx.guolindev.PermissionX
import global.msnthrp.staticmap.model.Tile
import global.msnthrp.staticmap.tile.TileEssential
import global.msnthrp.staticmap.tile.TileLoader
import global.msnthrp.staticmap.tile.TileProvider
import io.socket.client.Ack
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Response
import java.io.File


class ChatFragment : BaseFragment(),SwipeReply ,OnItemClickListener{


    private var tileEssential: TileEssential?=null
    private var fileTemporary: File? = null
    private var chatAdapter: ChatAdapter? = null
    private var messageList = ArrayList<ReceivedLastMessage>()

    private var receiverUserId: String? = null
    private  var userName: String? = null
    private  var profileImage: String? = null
    private var message: String? = null
    private var messageType: String? = null

    private var replyId:String? = null
    private val PICKFILE_RESULT_CODE: Int =88
    private val SELECT_VIDEO: Int = 29
    var isAttachment = false


    var socket:Socket?=null

    companion object {
        fun newInstance() = ChatFragment()
        var isLocation = false
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

       // MapFragment().initOnclickListener(this)
        receiverUserId = arguments?.getString(Constants.USER_ID).toString()
        userName = arguments?.getString(Constants.USER_NAME).toString()
        profileImage = arguments?.getString(Constants.PROFILE).toString()
        messageList = ArrayList()
/*        if (!receiverUserId.equals(null) || !receiverUserId.equals("null"))
        {
            PreferenceUtils.saveString(RECEIVER_USER_ID, receiverUserId!!)
        }
        else{
            receiverUserId = PreferenceUtils.getString(RECEIVER_USER_ID)
        }*/


        if (!arguments?.getString(Constants.USER_ID).toString().isNullOrEmpty() || !arguments?.getString(Constants.USER_ID).toString().equals("null"))
        {
            PreferenceUtils.saveString("chatReceiverId",receiverUserId.toString())
            PreferenceUtils.saveString("chatReceiverName",userName.toString())

        }

        else
        {
            receiverUserId =   PreferenceUtils.getString("chatReceiverId")
            userName = PreferenceUtils.getString("chatReceiverName")
        }

        mView = inflater.inflate(R.layout.fragment_chat, container, false)


        val nameObserver = Observer<LatLng> { mapLink ->
            // Update the UI, in this case, a TextView.
            Log.e("Message", mapLink.toString())

            var mapLink = mapLink

            if (isLocation)
            {
                isLocation = false
                sendMessage(mapLink.latitude.toString(), TYPE_LOCATION,mapLink.longitude.toString())
            }


//            mView.et_write_msg.setText(mapLink)

          //  getMessage()
            //getSocket()
        }

// Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        LiveData.getGetMapLink().observe(requireActivity(), nameObserver)


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

//            val bundle = bundleOf(Constants.TYPE_LOCATION to this )



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

            mView.lay_attachment.visibility = View.GONE

            if (!mView.et_write_msg.text.isNullOrBlank())
            {
                if (mView.ll_message_reply.visibility == View.VISIBLE)
                {

                    sendMessage(mView.et_write_msg.text.toString(), "replyId:$replyId","")

                }
                else{
                    sendMessage(mView.et_write_msg.text.toString(), TYPE_TEXT,"")
                }
            }



        }



         tileEssential = TileEssential(CustomTileProvider(), CustomTileLoader())


        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        state.let {   (mView.rv_chat_message.layoutManager as LinearLayoutManager).onRestoreInstanceState(it)}
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
                if (!arguments?.getString(MESSAGE).isNullOrBlank() || arguments?.getString(MESSAGE)?.equals("null") == true)
                {
                    message = arguments?.getString(MESSAGE)
                    messageType =arguments?.getString(MESSAGE_TYPE)
                    message?.let { messageType?.let { it1 -> sendMessage(it, it1,"") } }
                }

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


            val json =  JSONTokener(data.getString("data")).nextValue();
            if (json is JSONObject)
            {
                Log.e("receiveMessage", "Json Object")
                val gson = Gson()
                var objectFromJson:ReceivedLastMessage = gson.fromJson(data.getString("data"),object :TypeToken<ReceivedLastMessage>(){}.type)
                Log.e("lastReceiveMessage", objectFromJson.toString())
                messageList.add(objectFromJson)
                activity?.runOnUiThread {

                    chatAdapter =  ChatAdapter( messageList!!,this,this,requireActivity(),tileEssential)
                    chatAdapter?.notifyItemChanged(messageList!!.size - 1)
                    mView.rv_chat_message?.smoothScrollToPosition(messageList!!.size - 1);
                }


            }
            //you have an object
            else if (json is JSONArray)
            {
                val gson = Gson()
                val listFromGson: ArrayList<ReceivedLastMessage> = gson.fromJson(
                    data.getString("data"),
                    object : TypeToken<ArrayList<ReceivedLastMessage?>?>() {}.type
                )


                messageList =   listFromGson
                if (!messageList.isNullOrEmpty())
                {


                    activity?.runOnUiThread {
                        chatAdapter =  ChatAdapter( messageList!!,this,this,requireActivity(),tileEssential)
                        mView.rv_chat_message?.adapter = chatAdapter
                        mView.rv_chat_message?.adapter?.notifyDataSetChanged()
                        mView.rv_chat_message?.smoothScrollToPosition(messageList?.size - 1);
                    }




                    /*if (listFromGson.size != 1)
                    {
                        chatAdapter =  ChatAdapter( messageList!!,this,activity as Context)
                        mView.rv_chat_message?.adapter = chatAdapter
                        mView.rv_chat_message?.adapter?.notifyDataSetChanged()
                        mView.rv_chat_message?.smoothScrollToPosition(messageList?.size - 1);
                    }
                    else{
                        messageList?.add(listFromGson.get(0))
                        chatAdapter?.notifyItemChanged(messageList!!.size - 1)
                        mView.rv_chat_message?.smoothScrollToPosition(messageList!!.size - 1);
                    }*/
                    Log.e("receiveMessage", listFromGson.toString())
                }
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
            GetMessage(getUser().id,it.toInt())
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

    private fun sendMessage(message:String,type:String,thumbnail:String)
    {
        var model:SendMessage2?
        if (type == TYPE_LOCATION)
        {
            model = receiverUserId?.let { SendMessage2( getUser().id, it.toInt(),message,type,thumbnail) }
        }
        else
        {
           model = receiverUserId?.let { SendMessage2( getUser().id, it.toInt(),message,type) }
        }


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
//            Toast.makeText(requireActivity(), "Not Connect ,Please reload this screen", Toast.LENGTH_LONG).show()
        }
        mView.ll_message_reply.visibility = View.GONE
        mView.tv_reply.setText("")
    }


    private fun chatAttachment(type: String)
    {


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())



        var part : MultipartBody.Part? = null


        if (fileTemporary != null){
            part = fileTemporary?.getPartMap("message")
        }
        else
        {

        }

        GlobalScope.launch(Dispatchers.IO)
        {

            receiverUserId?.toInt()?.let {
                apiClient.chatAttachment(it, getUser().id,type,part).enqueue(object: retrofit2.Callback<ChatAttachmentResponse> {
                    override fun onResponse(
                        call: Call<ChatAttachmentResponse>,
                        response: Response<ChatAttachmentResponse>
                    ) {

                        try {


                            val response: ChatAttachmentResponse? =response.body()
                            val statuscode= response?.status
                            if (statuscode==1) {

                                Log.d("response", response.message)


                                getMessage()
                              //  sendMessage(response.data.attachment.attachment, type)


                            } else {


                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), "msd: "+response?.message, Toast.LENGTH_SHORT).show()
                                    response?.message?.let { Log.d("response", it) }

                                })
                            }
                        } catch (e:Exception) {

                            Log.d("response", "msg "+ e.localizedMessage)
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(), "msg "+ e.localizedMessage, Toast.LENGTH_SHORT).show()
                            })
                        }
                    }

                    override fun onFailure(call: Call<ChatAttachmentResponse>, t: Throwable) {

                        activity?.runOnUiThread(java.lang.Runnable {
                            Toast.makeText(requireContext(), "msg "+  t.localizedMessage, Toast.LENGTH_SHORT).show()
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

    fun View.showKeyboard() = ViewCompat.getWindowInsetsController(this)
        ?.show(WindowInsetsCompat.Type.ime())

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_VIDEO)
            {
                val uri: Uri = data?.data!!
                fileTemporary =  File(uri?.let {
                    Constants.getMediaFilePathFor(
                        it,
                        requireContext()
                    )
                })
                mView.lay_attachment.visibility = View.GONE
                chatAttachment(TYPE_VIDEO)

            }
            else if (requestCode == PICKFILE_RESULT_CODE)
            {
                val uri: Uri = data?.data!!
                fileTemporary =  File(uri?.let {
                    Constants.getMediaFilePathFor(
                        it,
                        requireContext()
                    )
                })
                mView.lay_attachment.visibility = View.GONE
                chatAttachment(TYPE_DOCUMENT)
            }
            else
            {
                val uri: Uri = data?.data!!
                fileTemporary = File(uri.path)
                mView.lay_attachment.visibility = View.GONE
                chatAttachment(TYPE_IMAGE)

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

    override fun onPause() {
        super.onPause()
        state = (mView.rv_chat_message.layoutManager as LinearLayoutManager).onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        getMessage()
    }

    override fun onSwipe(position: Int) {

        mView.ll_message_reply.visibility = View.VISIBLE

        mView.tv_reply.setText( messageList[position].message)
        mView.et_write_msg.requestFocus()
        mView.et_write_msg.showKeyboard()

        replyId = messageList[position].id.toString()
    }

    override fun onItemClick(position: Int, view: View, value: String) {


        if (value == TYPE_IMAGE)
        {

            val bundle = bundleOf(
                Constants.IMAGE to   messageList?.get(position)?.message.toString(),
                Constants.TYPE_TEXT to   TYPE_IMAGE

            )

            MainActivity.getMainActivity?.navControllerMain?.navigate(R.id.viewImageFragment,bundle)
        }
        else if (value == TYPE_VIDEO)
        {
            val bundle = bundleOf(
                Constants.IMAGE to   messageList?.get(position)?.message.toString(),
                Constants.TYPE_TEXT to   TYPE_VIDEO

            )

            MainActivity.getMainActivity?.navControllerMain?.navigate(R.id.viewImageFragment,bundle)
        }

    }


    private inner class CustomTileLoader : TileLoader {
        override fun loadTile(tileUrl: String, callback: TileLoader.Callback) {
            try {

                val theImage = GlideUrl(
                    tileUrl, LazyHeaders.Builder()
                        .addHeader("User-Agent", "5")
                        .build()
                )

                val bitmap = Glide.with(requireActivity())
                    .asBitmap()
                    .load(theImage)
                    .into(MATCH_PARENT, 200)
                    .get()
                callback.onLoaded(bitmap)
            } catch (e: Exception) {
                callback.onFailed(e)
                Log.d("mapfailed",e.localizedMessage)
            }
        }
    }

    private inner class CustomTileProvider : TileProvider {
        override fun getTileUrl(tile: Tile): String =
            "https://c.tile.openstreetmap.org/${tile.z}/${tile.x}/${tile.y}.png"
    }

}