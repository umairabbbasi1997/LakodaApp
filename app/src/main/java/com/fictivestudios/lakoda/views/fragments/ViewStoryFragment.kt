package com.fictivestudios.lakoda.views.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.ViewStoryViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.github.dhaval2404.imagepicker.ImagePicker
import com.permissionx.guolindev.PermissionX
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_story.view.*

import kotlinx.android.synthetic.main.view_story_fragment.view.*
import kotlinx.android.synthetic.main.view_story_fragment.view.btn_attach
import kotlinx.android.synthetic.main.view_story_fragment.view.btn_close
import kotlinx.android.synthetic.main.view_story_fragment.view.btn_document
import kotlinx.android.synthetic.main.view_story_fragment.view.btn_location
import kotlinx.android.synthetic.main.view_story_fragment.view.btn_photos
import kotlinx.android.synthetic.main.view_story_fragment.view.btn_video
import kotlinx.android.synthetic.main.view_story_fragment.view.lay_attachment

class ViewStoryFragment : BaseFragment() {

    companion object {
        fun newInstance() = ViewStoryFragment()
    }

    private var storyDuration: Int? = null
    private var userId: String ? = null
    private var userImage: String?=null
    private var userName: String?=null
    private var imageUrl: String?= null
    private val PICKFILE_RESULT_CODE: Int =88
    private val SELECT_VIDEO: Int = 29
    var isAttachment = false
    private lateinit var mView: View
    private lateinit var viewModel: ViewStoryViewModel
    override fun setTitlebar(titlebar: Titlebar) {

        titlebar.hideTitleBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.view_story_fragment, container, false)


        imageUrl = arguments?.getString(Constants.IMAGE).toString()
        userName = arguments?.getString(Constants.USER_NAME).toString()
        userImage = arguments?.getString(Constants.USER_IMAGE).toString()
        userId = arguments?.getString(Constants.USER_ID)
        storyDuration = arguments?.getInt(Constants.STORY_DURATION)

        if (!imageUrl.isNullOrEmpty() || imageUrl != "null" || !userImage.isNullOrEmpty() || !userName.isNullOrEmpty())
        {
            Log.d("imgurl", "img$imageUrl")

            mView.tv_username.setText(userName)

            Picasso
                .get()
                .load(Constants.IMAGE_BASE_URL +imageUrl)
                //.placeholder(R.drawable.loading_spinner)
                .into(mView.story);

            Picasso
                .get()
                .load(Constants.IMAGE_BASE_URL +userImage)
                .placeholder(R.drawable.user_dp)
                .into(mView.iv_profile);

            if (storyDuration !=null )
            {
                Log.d("durataion",storyDuration.toString())


                val timer = object: CountDownTimer(storyDuration?.times(1000)?.toLong()!!, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        mView.tv_timer.setText((millisUntilFinished/ 1000L).toString())
                    }

                    override fun onFinish() {

                        if (MainActivity.getMainActivity?.navControllerMain?.currentDestination?.id == R.id.viewStoryFragment)
                        {
                            activity?.onBackPressed()
                        }

                    }
                }
                timer.start()



            }

        }

        mView.btn_close.setOnClickListener {

            MainActivity?.getMainActivity?.onBackPressed()
        }
        mView.iv_profile.setOnClickListener {

            if (userId != null )
            {
                val bundle = bundleOf(
                    Constants.USER_ID to userId.toString(),
                    Constants.USER_NAME to  userName.toString(),
                    Constants.PROFILE to   imageUrl?.toString()
                    )
                MainActivity?.getMainActivity?.navControllerMain
                    ?.navigate(R.id.friendProfileFragment,bundle)
            }

        }




/*
        mView.btn_close.setOnClickListener {

            mView.ll_message_reply.visibility = View.GONE
        }
*/

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

        mView.tv_send.setOnClickListener {

            if (!mView.et_text.text.toString().isNullOrEmpty())
            {

                val bundle = bundleOf(
                    Constants.USER_ID to  userId.toString(),
                    Constants.PROFILE to  userImage?.toString(),
                    Constants.USER_NAME to userName.toString(),
                    Constants.MESSAGE to mView.et_text.text.toString(),
                    Constants.MESSAGE_TYPE to Constants.TYPE_TEXT
                )

                MainActivity.getMainActivity?.navControllerMain?.navigate(R.id.chatFragment,bundle)
            }



        }


        return mView
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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewStoryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}