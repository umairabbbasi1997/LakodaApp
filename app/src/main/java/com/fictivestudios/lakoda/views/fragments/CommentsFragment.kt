package com.fictivestudios.lakoda.views.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.adapters.CommentsAdapter
import com.fictivestudios.lakoda.apiManager.response.CommentResponse
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.apiManager.response.GetCommentsData
import com.fictivestudios.lakoda.apiManager.response.GetCommentsResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.utils.getFormDataBody
import com.fictivestudios.lakoda.utils.getPartMap
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.POST_ID
import com.fictivestudios.ravebae.utils.Constants.Companion.POST_TYPE
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_POST
import com.github.dhaval2404.imagepicker.ImagePicker
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.change_passowrd_fragment.view.*
import kotlinx.android.synthetic.main.comments_fragment.view.*
import kotlinx.android.synthetic.main.feeds_fragment.view.*
import kotlinx.android.synthetic.main.my_profile_fragment.view.shimmer_view_container
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File


class CommentsFragment : BaseFragment() ,OnItemClickListener{

    companion object {
        fun newInstance() = CommentsFragment()
    }

    private var commentsList: ArrayList<GetCommentsData>? = ArrayList<GetCommentsData>()
    private var postID: String?=null
    private var postType: String?=null
    private var fileTemporaryFile: File? = null
    private val PICKFILE_RESULT_CODE: Int =88
    private val SELECT_VIDEO: Int = 29
    var isAttachment = false

    private lateinit var mView: View
    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("COMMENTS")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        if (!this::mView.isInitialized) {
            mView = inflater.inflate(R.layout.comments_fragment, container, false)

            if (!postID.isNullOrEmpty() || !postID.equals( "null") && !postType.isNullOrEmpty() || !postType.equals( "null"))
            {
                getComments()
            }
        }

        postID = arguments?.getString(POST_ID).toString()
        postType = arguments?.getString(POST_TYPE).toString()

        Log.d("postId", "postId$postID")
        Log.d("postType: ", postType.toString())


       // val bundle = getIntent().extras
       // bundle!!.getSerializable("ModelAddress")
       //    arrayComments = arguments?.getParcelableArrayList<Parcelable>(COMMENTS) as ArrayList<Post>

      //  Log.d("commentsList",arrayComments.toString())





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



        mView.btn_video.setOnClickListener {

            videoPicker()
        }

        mView.tv_send.setOnClickListener {

            createComment()
        }

        return mView
    }



    private fun getComments()
    {
            mView.shimmer_comments.startShimmer()
            mView.shimmer_comments.visibility = View.VISIBLE
        mView.commment_lay.visibility =View.GONE


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        GlobalScope.launch(Dispatchers.IO)
        {

            postID?.toInt()?.let {
                postType?.let { it1 ->
                    apiClient.getComments(it, it1).enqueue(object: retrofit2.Callback<GetCommentsResponse> {
                        override fun onResponse(
                            call: Call<GetCommentsResponse>,
                            response: Response<GetCommentsResponse>
                        ) {
                            activity?.runOnUiThread(java.lang.Runnable {

                                mView.shimmer_comments.stopShimmer()
                                mView.shimmer_comments.visibility = View.GONE
                                mView.commment_lay.visibility =View.VISIBLE
                            })

                            Log.d("Response", ""+response?.body()?.message)
                            Log.d("Response", ""+response?.body()?.message)
                            try {


                                if (response.isSuccessful) {


                                    if (response?.message() == "Unauthorized"||
                                        response?.body()?.message == "Unauthorized"
                                        || response?.message() == "Unauthenticated.") {
                                        PreferenceUtils.remove(Constants.USER_OBJECT)
                                        PreferenceUtils.remove(Constants.ACCESS_TOKEN)
                                        MainActivity.getMainActivity?.finish()
                                        MainActivity.getMainActivity=null
                                        startActivity(Intent(requireContext(), RegisterationActivity::class.java))
                                        activity?.runOnUiThread(java.lang.Runnable {
                                            Toast.makeText(requireContext(), "Login expired please login again", Toast.LENGTH_SHORT).show()
                                        })
                                    }

                                    if (response.body()?.status==1) {

                                        if (response.body()?.data != null) {

                                            var response = response.body()?.data

                                            setData(response)
                                        }


                                    } else {
                                        activity?.runOnUiThread(java.lang.Runnable {
                                            Toast.makeText(requireContext(),""+ response.body()?.message, Toast.LENGTH_SHORT).show()
                                        })
                                    }

                                } else {
                                    activity?.runOnUiThread(java.lang.Runnable {
                                        Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                    })

                                }
                            } catch (e:Exception) {
                                //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                                activity?.runOnUiThread(java.lang.Runnable {

                                    mView.shimmer_comments.stopShimmer()
                                    mView.shimmer_comments.visibility = View.GONE
                                    Toast.makeText(requireContext(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                                    Log.d("execption","msg: "+e.localizedMessage)
                                })
                            }
                        }

                        override fun onFailure(call: Call<GetCommentsResponse>, t: Throwable) {

                            activity?.runOnUiThread(java.lang.Runnable {
                                //mView.pb_pofile.visibility=View.GONE
                                mView.shimmer_comments.stopShimmer()
                                mView.shimmer_comments.visibility = View.GONE
                                mView.commment_lay.visibility =View.VISIBLE
                                Toast.makeText(requireContext(), ""+t.message, Toast.LENGTH_SHORT).show()
                            })
                        }
                    })
                }
            }

        }


    }



    private fun createComment()
    {
/*        mView.pb_createPos.visibility=View.VISIBLE*/
        mView.tv_send.isEnabled = false
        Toast.makeText(requireContext(),"Posting Comment...", Toast.LENGTH_SHORT).show()

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        val commentHM = HashMap<String, RequestBody>().apply {
            this["comment"] = mView.et_comment .text.toString().getFormDataBody()
            this["post_id"] = postID.toString().getFormDataBody()
            this["type"] = postType.toString().getFormDataBody()
        }


        var filePart: MultipartBody.Part? = null

        if (fileTemporaryFile != null){
            filePart = fileTemporaryFile?.getPartMap("attachment")
        }

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.createComment(commentHM,filePart).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                      //  mView.pb_createPos.visibility=View.GONE
                        mView.tv_send.isEnabled = true




                    try {

                        Log.d("response", response.body()?.message ?: "null")

                        val response: CommonResponse? =response.body()
                        val statuscode= response!!.status
                        if (statuscode==1) {

                            mView.et_comment .setText("")
                            Log.d("response",response.message)

                            getComments()

                        //    MainActivity.getMainActivity?.onBackPressed()
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(),response.message, Toast.LENGTH_SHORT).show()
                            })

                        }
                        else {
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(),response.message, Toast.LENGTH_SHORT).show()
                            })
                        }
                    }
                    catch (e:Exception)
                    {


                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                            Log.d("response", e.localizedMessage)
                            Log.d("response", e.message.toString())

                    }
                    }) }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                       // mView.pb_createPos.visibility=View.GONE
                        mView.tv_send.isEnabled = true
                        Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                        Log.d("response", t.localizedMessage)
                    })

                }
            })

        }


    }



    private fun setData(response: List<GetCommentsData>?) {

        commentsList = response as ArrayList<GetCommentsData>?

        var adapter = CommentsAdapter(commentsList,requireContext(),this)
        mView.rv_comment.adapter = adapter
        adapter.notifyDataSetChanged()

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {


            val uri: Uri = data?.data!!


            fileTemporaryFile = File(uri.path!!)

      /*      if (requestCode == SELECT_VIDEO)
            {

            }
            else if (requestCode == PICKFILE_RESULT_CODE)
            {


            }
            else
            {
                val uri: Uri = data?.data!!

                // Use Uri object instead of File to avoid storage permissions
                *//*  mView.btn_picture_video.setImageURI(uri)*//*
            }*/
            //Image Uri will not be null for RESULT_OK

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }




    override fun onItemClick(position: Int, view: View, value: String) {
         if (value == Constants.PROFILE)
        {
            val bundle = bundleOf(
                Constants.USER_ID to   commentsList?.get(position)?.user?.id.toString(),
                Constants.USER_NAME to   commentsList?.get(position)?.user?.name.toString(),
                Constants.PROFILE to   commentsList?.get(position)?.user?.image.toString()
            )


            if (!Constants.getUser().id.equals(commentsList?.get(position)?.user?.id))
            {
                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.friendProfileFragment,bundle)

            }
            else
            {
                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.myProfileFragment)

            }

        }

    }

}