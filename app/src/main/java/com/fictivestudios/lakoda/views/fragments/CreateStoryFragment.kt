package com.fictivestudios.lakoda.views.fragments

//import com.sarthakdoshi.textonimage.TextOnImage

import android.Manifest.permission.*
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE
import android.widget.FrameLayout
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.drawToBitmap
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.utils.getPartMap
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.create_story_fragment.*
import kotlinx.android.synthetic.main.create_story_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class CreateStoryFragment : BaseFragment() {


    private var fileTemporaryFile: File? = null
    private var pressed_x = 0
    private var pressed_y = 0
    private lateinit var mView: View

    private var timer = 3

    private val PERMISSION_REQUEST_CODE = 515

    companion object {
        fun newInstance() = CreateStoryFragment()
    }


    private var imageURI: Uri? = null

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("CREATE STORY")
        //titlebar.makeTitleTransparent()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView =  inflater.inflate(R.layout.create_story_fragment, container, false)


        openImagePicker()

        mView.btn_add_to_story.setOnClickListener {

           // MainActivity.getMainActivity?.onBackPressed()

            number_picker.visibility = View.GONE
            createStory(saveImageWithMediaStore(requireContext()))
        /*    PermissionX.init(activity)
                .permissions(WRITE_EXTERNAL_STORAGE)
                .request { allGranted, grantedList, deniedList ->

                }*/





          /*  if (checkPermission()) {
                if (fileTemporaryFile != null)
                {

                    createStory(saveImageToGallery())
                }



            } else {
                requestPermission()
               // Toast.makeText(requireContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
            }*/

        }

        mView.btn_add_text.setOnClickListener {

           number_picker.visibility = View.GONE
            showWriteText(true)



        }
        mView.btn_done.setOnClickListener {
            showWriteText(false)
            number_picker.visibility = View.GONE

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

/*        mView.iv_story_image.setOnClickListener {

          var  pvTime = TimePickerBuilder(requireContext(),
                OnTimeSelectListener { date, v -> //Callback
                  //  tvTime.setText(jdk.nashorn.internal.objects.NativeDate.getTime(date))
                    timer = jdk.nashorn.internal.objects.NativeDate.getTime(date)
                })
                .build()
            pvTime.show()
        }*/

        mView.btn_add_timer.setOnClickListener {

            mView.  et_text.visibility = View.GONE
            mView.number_picker.visibility = View.VISIBLE
            mView.btn_done.visibility = View.GONE
         //   mView.btn_done.visibility = View.VISIBLE
        }


        mView.number_picker.setOnValueChangedListener(object: NumberPicker.OnValueChangeListener,
            com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener {
            override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {
              //  Log.d("selected value1",p2.toString() )
            }

            override fun onValueChange(
                picker: com.shawnlin.numberpicker.NumberPicker?,
                oldVal: Int,
                newVal: Int
            ) {
               // Log.d("selected value",newVal.toString() )
            }
        })


        mView.number_picker.setOnScrollListener(object :NumberPicker.OnScrollListener,
            com.shawnlin.numberpicker.NumberPicker.OnScrollListener {
            override fun onScrollStateChange(p0: NumberPicker?, p1: Int) {

            }

            override fun onScrollStateChange(
                view: com.shawnlin.numberpicker.NumberPicker?,
                scrollState: Int
            ) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    Log.d("selected value",view?.value.toString() )
                    timer = view?.value!!
                }
            }
        })

        return mView
    }


    val mOnTouchListenerTv1 = OnTouchListener { v, event ->
        val constraintLayoutParams = mView.tv1.getLayoutParams() as FrameLayout.LayoutParams
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
   /* private fun addTextOnImage(text:String?)
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
    }*/

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
     	    			//Crop image(Optional), Check Customization for more option
        .compress(1024)			//Final image size will be less than 1 MB(Optional)
        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
        .start()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

     /*   if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }*/

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



    private fun createStory(storyFile: File?)
    {
        mView.pb_createStory.visibility=View.VISIBLE
        mView.btn_add_to_story.isEnabled = false
        mView.btn_add_to_story.text = ""

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

/*
        val createStoryHm = HashMap<String, RequestBody>().apply {
            this["caption"] = mView.et_title .text.toString().getFormDataBody()
         }
*/


        var filePart: MultipartBody.Part? = null

        /*if (fileTemporaryFile != null){
            filePart = fileTemporaryFile?.getPartMap("image")
        }*/

        if (storyFile != null){
            filePart = storyFile?.getPartMap("image")
        }

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.createStory(filePart,timer).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_createStory.visibility=View.GONE
                        mView.btn_add_to_story.isEnabled = true
                        mView.btn_add_to_story.text = "Add To Story"


                    })


                    try {

                        Log.d("response", ""+response.body()?.message ?: "null")

                        val response: CommonResponse? =response.body()
                        val statuscode= response!!.status
                        if (statuscode==1) {


                            deleteFileFromMediaStore(requireContext(),storyFile?.path)


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
                        mView.btn_add_to_story.isEnabled = true
                        mView.btn_add_to_story.text = "Add To Story"

                        Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                        Log.d("response", ""+t.localizedMessage)
                    })

                }
            })

        }


    }

    private fun saveTextOnImage(): File? {


        var content = mView.story_layout


       /* val bitmap = Bitmap.createBitmap(content!!.width, content!!.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        content!!.draw(canvas)*/
        val bitmap =  content.drawToBitmap()
            //getBitmapFromView(content)



        var file = File(commonDocumentDirPath("temp").toString()+"/image.png")


        if (file.exists())
        {
            file.delete()
        }

       // file.copyTo()
       // var filename = File("image.png")

        try {

            var ostream =  FileOutputStream(file,false);
            bitmap?.compress(Bitmap.CompressFormat.PNG, 3, ostream);
            ostream.flush()
            ostream.close();




        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }







        return file

}






    @Throws(IOException::class)
    private fun saveImageWithMediaStore(
        context: Context
    ): File? {


        var content = mView.story_layout
        val bitmap =  content.drawToBitmap()
        var folderName = "TempStory"
        var fileName="Image_story"

        var fos: OutputStream? = null
        var imageFile: File? = null
        var imageUri: Uri? = null
        try {
            if (SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + folderName
                )
                imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri == null) throw IOException("Failed to create new MediaStore record.")
                fos = resolver.openOutputStream(imageUri)
            } else {
                val imagesDir = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ).toString() + File.separator + folderName
                )
                if (!imagesDir.exists()) imagesDir.mkdir()
                imageFile = File(imagesDir, "$fileName.png")
                fos = FileOutputStream(imageFile)
            }
            if (!bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    fos
                )
            ) throw IOException("Failed to save bitmap.")
            fos!!.flush()
        } finally {
            fos?.close()
        }
        if (imageFile != null) { //pre Q
            MediaScannerConnection.scanFile(context, arrayOf(imageFile.toString()), null, null)
            imageUri = Uri.fromFile(imageFile)
        }
        Log.d("file",imageFile.toString())

        var savedFile =  File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES )
                .toString() + "/" + folderName+File.separator+"$fileName.png"
        )

        return savedFile
    }

    fun deleteFileFromMediaStore(
        context: Context, fileFullPath: String?
    ): Boolean {
        val file = File(fileFullPath)
        val absolutePath: String?
        val canonicalPath: String?
        absolutePath = try {
            file.absolutePath
        } catch (ex: java.lang.Exception) {
            null
        }
        canonicalPath = try {
            file.canonicalPath
        } catch (ex: java.lang.Exception) {
            null
        }
        val paths: ArrayList<String> = ArrayList()
        if (absolutePath != null) paths.add(absolutePath)
        if (canonicalPath != null && !canonicalPath.equals(
                absolutePath,
                ignoreCase = true
            )
        ) paths.add(canonicalPath)
        if (paths.size === 0) return false
        val resolver: ContentResolver = context.getContentResolver()
        val uri = MediaStore.Files.getContentUri("external")
        var deleted = false
        for (path in paths) {
            val result = resolver.delete(
                uri,
                MediaStore.Files.FileColumns.DATA + "=?", arrayOf(path)
            )
            if (result != 0) deleted = true
        }
        return deleted
    }
    fun commonDocumentDirPath(FolderName: String): File? {
        var dir: File? = null
        dir = if (SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + FolderName
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/" + FolderName)
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {

            // Make it, if it doesn't exit
//            val success = dir.mkdirs()
            val success = dir.mkdir()
            if (!success) {
                dir = null
            }
        }
        else
        {
            dir.delete()

            dir = if (SDK_INT >= Build.VERSION_CODES.R) {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + "/" + FolderName
                )
            } else {
                File(Environment.getExternalStorageDirectory().toString() + "/" + FolderName)
            }
            val success = dir.mkdir()
            /*if (!success) {
                dir = null
            }
*/
        }
        return dir
    }

    fun getBitmapFromView(view: View): Bitmap? {
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    private fun checkPermission(): Boolean {
        if ( ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE)
             == PackageManager.PERMISSION_GRANTED){

            Log.v("TAG","Permission is granted");
            //File write logic here
            return true;
        }
        else{
            requestPermission()
        }
        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }
/*    private fun checkPermission(): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(requireContext(), READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(requireContext(), WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(
                        String.format(
                            "package:%s",
                            ApplicationProvider.getApplicationContext<Context>().getPackageName()
                        )
                    )
                startActivityForResult(intent, 2296)
            } catch (e: java.lang.Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                val READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                    // perform action when allow permission success
                    if (fileTemporaryFile != null)
                    {

                        createStory(saveTextOnImage())
                    }

                } else {
                    Toast.makeText(requireContext(), "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun showSnackBar(message: String?, activity: Activity?) {
        if (null != activity && null != message) {
            Snackbar.make(
                activity.findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT
            ).setAction(message, View.OnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", activity.getPackageName(), null)
                intent.data = uri
                startActivity(intent)
            }).show()
        }
    }
}