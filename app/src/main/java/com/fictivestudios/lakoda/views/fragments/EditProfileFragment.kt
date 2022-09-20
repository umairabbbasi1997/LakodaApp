package com.fictivestudios.lakoda.views.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
 
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.UpdateProfileResponse
import com.fictivestudios.lakoda.model.User
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.utils.getFormDataBody
import com.fictivestudios.lakoda.utils.getPartMap
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.IMAGE_BASE_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.USER_OBJECT
import com.fictivestudios.ravebae.utils.Constants.Companion.getUser
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.edit_profile_fragment.view.*
import kotlinx.android.synthetic.main.edit_profile_fragment.view.ccp
import kotlinx.android.synthetic.main.edit_profile_fragment.view.et_phone
import kotlinx.android.synthetic.main.edit_profile_fragment.view.et_username
import kotlinx.android.synthetic.main.edit_profile_fragment.view.iv_upload
import kotlinx.android.synthetic.main.edit_profile_fragment.view.iv_user_profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.HashMap
import java.util.regex.Pattern

class EditProfileFragment : BaseFragment() {


    private var isShowPass=false
    private var isShowRepeatPass=false
    private lateinit var mView: View

    private var userObj:User?=null

    private var fileTemporaryProfilePicture: File? = null

    companion object {
        fun newInstance() = EditProfileFragment()
    }


    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("EDIT PROFILE")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        if (!this::mView.isInitialized) {
            mView =  inflater.inflate(R.layout.edit_profile_fragment, container, false)

            setUser()
        }


        mView.btn_save.setOnClickListener {

            validateFields()

        }

        mView.iv_upload.setOnClickListener {

            openImagePicker()

        }

/*
        mView.iv_show_new_pass.setOnClickListener {
            if (isShowPass)
            {
                isShowPass = false
                mView.iv_show_new_pass.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                mView.et_pass.transformationMethod = PasswordTransformationMethod()
            }
            else
            {
                isShowPass = true
                mView.iv_show_new_pass.setImageResource(R.drawable.ic_baseline_visibility_24)
                mView.et_pass.transformationMethod = null
            }

        }

        mView.iv_show_repeat_pass.setOnClickListener {
            if (isShowRepeatPass)
            {
                isShowRepeatPass = false
                mView.iv_show_repeat_pass.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                mView.et_confirm_pass.transformationMethod = PasswordTransformationMethod()
            }
            else
            {
                isShowRepeatPass = true
                mView.iv_show_repeat_pass.setImageResource(R.drawable.ic_baseline_visibility_24)
                mView.et_confirm_pass.transformationMethod = null
            }

        }
*/


        return mView
    }



    private fun validateFields() {



      /*  if (!mView.et_pass.text.toString().isValidPassword() )
        {
            mView.et_pass.setError(getString(R.string.password_must_be))
            return
        }

        if (mView.et_pass.text.toString() != mView.et_confirm_pass.text.toString() )
        {
            mView.et_confirm_pass.setError(getString(R.string.confirm_pass_doesnt_match))

            return
        }*/

        if (
            mView.et_username.text.toString().isNullOrBlank()
        )
        {
            mView.et_username.setError(getString(R.string.fields_cant_be_empty))

            return
        }

        if (
            mView.et_phone.text.toString().isNullOrBlank()
        )
        {
            mView.et_phone.setError(getString(R.string.fields_cant_be_empty))

            return
        }


        if (
            mView.et_city.text.toString().isNullOrBlank()
        )
        {
            mView.et_city.setError(getString(R.string.fields_cant_be_empty))

            return
        }


        else
        {

            updateProfileRequest()

        }
    }





    private fun updateProfileRequest()
    {
        mView.pb_edit_profile.visibility=View.VISIBLE
        mView.btn_save.isEnabled=false
        mView.btn_save.text=""


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())



        var part : MultipartBody.Part? = null
        var updateHM = HashMap<String, RequestBody>().apply {


            this["name"] = mView.et_username.text.toString().getFormDataBody()
            this["phone_number"] = mView.et_phone.text.toString().getFormDataBody()
            this["country_code"] = mView.ccp.selectedCountryCodeWithPlus.toString().getFormDataBody()
            this["city"] = mView.et_city.text.toString().getFormDataBody()

        }
        if (fileTemporaryProfilePicture != null){
            part = fileTemporaryProfilePicture?.getPartMap("image")
        }

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.updateProfile(updateHM,part).enqueue(object: retrofit2.Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_edit_profile.visibility=View.GONE
                        mView.btn_save.isEnabled=true
                        mView.btn_save.text="save changes"


                    try {


                        val response: UpdateProfileResponse? =response.body()
                        val statuscode= response?.status
                        if (statuscode==1) {

                            Log.d("response", response.message)

                            val gson = Gson()
                            val json:String = gson.toJson(response.data.user)
                            PreferenceUtils.saveString(USER_OBJECT,json)
                            Toast.makeText(requireContext(), " Profile Updated SuccessFully", Toast.LENGTH_SHORT).show()

                            MainActivity.getMainActivity?.onBackPressed()
                        }
                        else {



                                Toast.makeText(requireContext(), "msd: "+response?.message, Toast.LENGTH_SHORT).show()
                                response?.message?.let { Log.d("response", it) }


                        }
                    }
                    catch (e:Exception)
                    {

                        Log.d("response", "msg "+ e.localizedMessage)

                            Toast.makeText(requireContext(), "msg "+ e.localizedMessage, Toast.LENGTH_SHORT).show()

                    }
                    })
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        Toast.makeText(requireContext(), "msg "+  t.localizedMessage, Toast.LENGTH_SHORT).show()
                        Log.d("response", "msg "+  t.localizedMessage)

                        mView.pb_edit_profile.visibility=View.GONE
                        mView.btn_save.isEnabled=true
                        mView.btn_save.text="save changes"

                    })
                }
            })

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
            val uri: Uri = data?.data!!
            fileTemporaryProfilePicture = File(uri.path!!)

            // Use Uri object instead of File to avoid storage permissions
            mView.iv_user_profile.setImageURI(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }





           /* var gson =  Gson()
            var json:String= PreferenceUtils.getString(USER_OBJECT)
            userObj = gson.fromJson(json,User::class.java)*/






    private fun setUser() {

        userObj = getUser()


        if (userObj!=null)
        {
            mView.et_username.setText(userObj?.name)

            mView.et_phone.setText(userObj?.phone_number)
            mView.et_city.setText(userObj?.city)
            userObj?.country_code?.let { mView.ccp.setCountryForPhoneCode(it.toInt()) }
            if (!userObj?.image.isNullOrBlank())
            {
                Picasso.get()
                    .load(IMAGE_BASE_URL+userObj?.image)
                    //.placeholder(R.drawable.loading_spinner)
                    .into(mView.iv_user_profile);
            }

        }


    }


    fun CharSequence.isValidPassword(): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }

}