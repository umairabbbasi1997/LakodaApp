package com.fictivestudios.lakoda.views.fragments

//import com.sarthakdoshi.textonimage.TextOnImage

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.CommentResponse
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.apiManager.response.CreatePostResponse
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.utils.getFormDataBody
import com.fictivestudios.lakoda.utils.getPartMap
import com.fictivestudios.lakoda.viewModel.CreateStoryViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.create_post_fragment.view.*
import kotlinx.android.synthetic.main.create_story_fragment.*
import kotlinx.android.synthetic.main.create_story_fragment.view.*
import kotlinx.android.synthetic.main.edit_profile_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.InputStream
import java.util.HashMap


class CreateStoryFragment : BaseFragment() {


    private var fileTemporaryFile: File? = null
    private var pressed_x = 0
    private var pressed_y = 0
    private lateinit var mView: View
    companion object {
        fun newInstance() = CreateStoryFragment()
    }


    private var imageURI: Uri? = null
    private lateinit var viewModel: CreateStoryViewModel

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("CREATE STORY")
        //titlebar.makeTitleTransparent()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView =  inflater.inflate(R.layout.create_story_fragment, container, false)


        openImagePicker()

        mView.btn_add_to_story.setOnClickListener {

           // MainActivity.getMainActivity?.onBackPressed()

            if (fileTemporaryFile != null)
            {
                createStory()
            }

        }

        mView.btn_add_text.setOnClickListener {

            showWriteText(true)



        }
        mView.btn_done.setOnClickListener {
            showWriteText(false)

           // addTextOnImage(et_text.text.toString())

            mView.et_text.hideKeyboard()
            mView.tv1.visibility=View.VISIBLE
            mView.tv1.setText(et_text.text)
        }

        mView.tv1.setOnTouchListener(mOnTouchListenerTv1)



/*        mView.iv_story_image.setOnTouchListener(object:View.OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

                Xvariable = p1!!.getX()
                Yvariable = p1!!.getY();
                redrawImage();
                return true
            }
        })*/

        return mView
    }


    val mOnTouchListenerTv1 = OnTouchListener { v, event ->
        val constraintLayoutParams = mView.tv1.getLayoutParams() as ConstraintLayout .LayoutParams
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("TAG", "@@@@ TV1 ACTION_UP")
                // Where the user started the drag
                pressed_x = event.rawX.toInt()
                pressed_y = event.rawY.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("TAG", "@@@@ TV1 ACTION_UP")
                // Where the user's finger is during the drag
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()

                // Calculate change in x and change in y
                val dx: Int = x - pressed_x
                val dy: Int = y - pressed_y

                // Update the margins
                constraintLayoutParams.leftMargin += dx
                constraintLayoutParams.topMargin += dy
                mView.tv1.setLayoutParams(constraintLayoutParams)

                // Save where the user's finger was for the next ACTION_MOVE
                pressed_x = x
                pressed_y = y
            }
            MotionEvent.ACTION_UP -> Log.d("TAG", "@@@@ TV1 ACTION_UP")
        }
        true
    }
    private fun addTextOnImage(text:String?)
    {
        val ims: InputStream? = imageURI?.let { context?.getContentResolver()?.openInputStream(it) }
        val bm = BitmapFactory.decodeStream( ims)

        val config: Bitmap.Config = bm.config
        val width = bm.width
        val height = bm.height

        val newImage = Bitmap.createBitmap(width, height, config)

        val c = Canvas(newImage)
        c.drawBitmap(bm, 0f, 0f, null)

        val paint = Paint()
        paint.setColor(Color.RED)
        paint.setStyle(Paint.Style.FILL)
        paint.setTextSize(20f)
        c.drawText(text.toString(), 0f, 25f, paint)

        mView.iv_story_image.setImageBitmap(newImage)
    }

    private fun showWriteText(isShow: Boolean) {

        if (isShow)
        {
            mView.et_text.visibility = View.VISIBLE
            mView.btn_done.visibility = View.VISIBLE
            mView.et_text.requestFocus()
            mView.et_text.showKeyboard()
        }
        else
        {
            mView.et_text.visibility = View.GONE
            mView.btn_done.visibility = View.GONE
        }

    }


    private fun openImagePicker()
    {    ImagePicker.with(this)
        .crop()	    			//Crop image(Optional), Check Customization for more option
        .compress(1024)			//Final image size will be less than 1 MB(Optional)
        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
        .start()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
         //   imageURI = data?.data!!


            // Use Uri object instead of File to avoid storage permissions
            val uri: Uri = data?.data!!
            fileTemporaryFile =  File(uri.path!!)
            mView.iv_story_image.setImageURI(uri)


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()

            MainActivity?.getMainActivity?.onBackPressed()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            MainActivity?.getMainActivity?.onBackPressed()
        }
    }

/*
    fun redrawImage() {
        val bm = BitmapFactory.decode decodeResource(resources, imageURI)
        val proxy = Bitmap.createBitmap(bm.width, bm.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(proxy)

        //Here, we draw the background image.
        c.drawBitmap(bm, Matrix(), null)

        //Here, we draw the text where the user last touched.
        c.drawText(mView.et_text.text.toString(), Xvariable, Yvariable, 1)
        mView.iv_story_image.setImageBitmap(proxy)
    }*/



    fun View.showKeyboard() = ViewCompat.getWindowInsetsController(this)
        ?.show(WindowInsetsCompat.Type.ime())

    fun View.hideKeyboard() = ViewCompat.getWindowInsetsController(this)
        ?.hide(WindowInsetsCompat.Type.ime())

/*        private fun addTextOnImage(text: String) {
        //pass the data to add it in image
        val intent = Intent(requireContext(), TextOnImage::class.java)
        val bundle = Bundle()
        bundle.putString(TextOnImage.IMAGE_IN_URI, imageURI.toString()) //image uri
        bundle.putString(TextOnImage.TEXT_COLOR, "#27ceb8") //initial color of the text
        bundle.putFloat(TextOnImage.TEXT_FONT_SIZE, 20.0f) //initial text size
        bundle.putString(TextOnImage.TEXT_TO_WRITE, text) //text to be add in the image
        intent.putExtras(bundle)
        startActivityForResult(
            intent,
            TextOnImage.TEXT_ON_IMAGE_REQUEST_CODE
        ) //start activity for the result
    }*/
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateStoryViewModel::class.java)
        // TODO: Use the ViewModel
    }



    private fun createStory()
    {
        mView.pb_createStory.visibility=View.VISIBLE

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

/*
        val createStoryHm = HashMap<String, RequestBody>().apply {
            this["caption"] = mView.et_title .text.toString().getFormDataBody()
         }
*/


        var filePart: MultipartBody.Part? = null

        if (fileTemporaryFile != null){
            filePart = fileTemporaryFile?.getPartMap("image")
        }

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.createStory(filePart).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_createStory.visibility=View.GONE


                    })


                    try {

                        Log.d("response", response.body()?.message ?: "null")

                        val response: CommonResponse? =response.body()
                        val statuscode= response!!.status
                        if (statuscode==1) {

                            Log.d("response",response.message)
                            MainActivity.getMainActivity?.onBackPressed()
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

                        activity?.runOnUiThread(java.lang.Runnable {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                            Log.d("response", e.localizedMessage)
                            Log.d("response", e.message.toString())
                        })
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_createStory.visibility=View.GONE

                        Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show()
                        Log.d("response", t.localizedMessage)
                    })

                }
            })

        }


    }


}