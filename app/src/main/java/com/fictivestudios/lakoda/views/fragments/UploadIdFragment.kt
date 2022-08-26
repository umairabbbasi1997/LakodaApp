package com.fictivestudios.lakoda.views.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.viewModel.UploadIdViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.signup_fragment.view.*
import kotlinx.android.synthetic.main.upload_id_fragment.view.*

class UploadIdFragment : Fragment() {

    companion object {
        fun newInstance() = UploadIdFragment()
    }

    private lateinit var mView: View
    private lateinit var viewModel: UploadIdViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.upload_id_fragment, container, false)

        mView.card_upload.setOnClickListener {

            openImagePicker()

        }

        mView.btn_verify.setOnClickListener {

            RegisterationActivity.getRegActivity
                ?.navControllerReg?.navigate(R.id.otpFragment)


        }
        mView.btn_decline.setOnClickListener {

            RegisterationActivity.getRegActivity?.onBackPressed()
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
 /*           signupBinding.iv_user_profile.setImageURI(uri)*/
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UploadIdViewModel::class.java)
        // TODO: Use the ViewModel
    }


}