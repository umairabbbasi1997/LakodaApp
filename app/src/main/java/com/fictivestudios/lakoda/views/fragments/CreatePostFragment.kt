package com.fictivestudios.lakoda.views.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.CreatePostResponse
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.utils.getFormDataBody
import com.fictivestudios.lakoda.utils.getPartMap
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.ravebae.utils.Constants.Companion.createVideoThumbNail
import com.fictivestudios.ravebae.utils.Constants.Companion.getMediaDuration
import com.fictivestudios.ravebae.utils.Constants.Companion.getMediaFilePathFor
import com.fictivestudios.ravebae.utils.Constants.Companion.prepareFilePart
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.create_post_fragment.view.*
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File


class CreatePostFragment : BaseFragment() {

    private var fileTemporaryFile: File? = null
    private var fileTemporaryVideo: File? = null
    private var SELECT_VIDEO = 25
    private lateinit var mView: View
    private var isVideo = false
    private var isImage = false

    private var dialog: Dialog? = null
    companion object {
        fun newInstance() = CreatePostFragment()
    }


    override fun setTitlebar(titlebar: Titlebar) {

        titlebar.setHomeTitle("CREATE POST")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView =inflater.inflate(R.layout.create_post_fragment, container, false)

        mView.btn_create.setOnClickListener {

            if (!mView.et_des.text.toString().isNullOrEmpty())
            {
                createPost()
            }
            else{
                Toast.makeText(requireActivity(), "Description is required", Toast.LENGTH_SHORT).show()
            }

        }

        mView.btn_upload_video.setOnClickListener {

            videoPicker()
        }
        mView.btn_picture_video.setOnClickListener {
            openImagePicker()
        }
        return mView
    }





    private fun createPost()
    {
        mView.pb_createPos.visibility=View.VISIBLE
        mView.btn_create.isEnabled = false
        mView.btn_create.text = ""

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        val hashMap = HashMap<String, RequestBody>().apply {
            this["title"] = ".".getFormDataBody()
            this["description"] = mView.et_des.text.toString().getFormDataBody() }


        var filePart: MultipartBody.Part? = null

        if (isVideo && fileTemporaryVideo != null)
        {

         filePart =  prepareFilePart("video_image", fileTemporaryVideo!!/*File(fileTemporaryUri?.let { getMediaFilePathFor(it,requireContext()) })*/)

        }
        else if (isImage && fileTemporaryFile != null)
        {
            filePart = fileTemporaryFile?.getPartMap("video_image")
        }

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.createPost(hashMap,filePart).enqueue(object: retrofit2.Callback<CreatePostResponse> {
                override fun onResponse(
                    call: Call<CreatePostResponse>,
                    response: Response<CreatePostResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_createPos.visibility=View.GONE
                        mView.btn_create.isEnabled = true
                        mView.btn_create.text = "create"




                    try {

                        Log.d("response", ""+response.body()?.message ?: "null")

                        val response: CreatePostResponse? =response.body()
                        val statuscode= response!!.status
                        if (statuscode==1) {

                            Log.d("response",""+response.message)
                            MainActivity.getMainActivity?.onBackPressed()

                                Toast.makeText(requireContext(),""+response.message, Toast.LENGTH_SHORT).show()


                        }
                        else {

                                Toast.makeText(requireContext(),""+response.message, Toast.LENGTH_SHORT).show()

                        }
                    }
                    catch (e:Exception)
                    {


                            Toast.makeText(requireContext(), ""+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("response", ""+e.localizedMessage)
                            Log.d("response",""+ e.message.toString())

                    }
                    })  }

                override fun onFailure(call: Call<CreatePostResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_createPos.visibility=View.GONE
                        mView.btn_create.isEnabled = true
                        mView.btn_create.text = "create"
                                               Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                        Log.d("response",""+ t.localizedMessage)
                    })

                }
            })

        }


    }

    fun videoPicker()
    {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, SELECT_VIDEO)
    }


    private fun openImagePicker()
    {    ImagePicker.with(this)
        .crop()	    			//Crop image(Optional), Check Customization for more option
        .compress(1024)			//Final image size will be less than 1 MB(Optional)
        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
        .start()

    }



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_VIDEO)
            {
                if (data?.data != null)
                {
                    val uri: Uri = data?.data!!

                    fileTemporaryVideo  =   File(uri?.let { getMediaFilePathFor(it,requireContext()) })
                    // fileTemporaryFile =  File(uri.path!!)


                    if (fileTemporaryVideo?.getMediaDuration(requireContext())!! <= 30000L )
                    {
                        mView.post_layout.visibility = View.GONE
                        isVideo = true
                        isImage = false

                        var bitmap = createVideoThumbNail(/*fileTemporaryVideo!!.toUri()*/ /*Uri.fromFile(fileTemporaryVideo)*/ fileTemporaryVideo!!.path.toString(),requireContext())
                        Log.d("bitmap",bitmap.toString())
                        mView.iv_Post_media.setImageBitmap(bitmap)
                        mView.iv_Post_media.visibility = View.VISIBLE

                       /* val f =
                            File(getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString() + "/Silicompressor/videos")

                        if (f.mkdirs() || f.isDirectory()) {
                            //compress and output new video specs
                            //new VideoCompressAsyncTask(this).execute("true", mCurrentPhotoPath, f.getPath());
                            compressVideo(fileTemporaryVideo?.toURI() .toString(), f.path)
                        }

*/
                    }
                    else
                    {
                        Toast.makeText(requireContext(), "Video duration should Be less then 30 seconds", Toast.LENGTH_LONG).show()
                    }

                }



            }
            else
            {
                val uri: Uri = data?.data!!
               // fileTemporaryUri = uri
                fileTemporaryFile =  File(uri.path!!)
                mView.iv_Post_media.setImageURI(uri)
                mView.iv_Post_media.visibility = View.VISIBLE
                mView.post_layout.visibility = View.GONE
                isVideo = false
                isImage = true

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



/*    private fun compressVideo(uri: String, path: String)
    {


        lifecycleScope.executeAsyncTask(

            onPreExecute = {
            dialog = ProgressDialog.show(
                requireContext(), "", "Compressing...");

        },
            doInBackground = {

            var videoPath: String? = null

            try {
                // Initialize uri
*//*                val uri = Uri.parse(path)


                val filePath = File(path)
                filePath.mkdir()
               var pathDest= filePath.path*//*
*//*
                val folder_main = "Temporary_Compressed_Videos"

                val fileDest = File(Environment.getExternalStorageDirectory(), folder_main)
                if (!fileDest.exists()) {
                    fileDest.mkdirs()
                }*//*

                // Compress video
                videoPath = SiliCompressor.with(requireContext())
                    .compressVideo(uri, path)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            // Return Video path
            // Return Video path
           // return videoPath

        },
            onPostExecute = {
            // ... here "it" is a data returned from "doInBackground"
                Toast.makeText(requireContext(), "Video compressed", Toast.LENGTH_LONG).show()
            dialog?.dismiss()
        })
    }*/

/*    fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit
    ) = launch {
        onPreExecute()
        val result = withContext(Dispatchers.IO) { // runs in background thread without blocking the Main Thread
            doInBackground()
        }
        onPostExecute(result)
    }*/



}