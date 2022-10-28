package com.fictivestudios.lakoda.views.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.SignupResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.utils.getFormDataBody
import com.fictivestudios.lakoda.utils.getPartMap
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.BERBIX_TOKEN
import com.fictivestudios.ravebae.utils.Constants.Companion.CURRENT_USER_ID
import com.fictivestudios.ravebae.utils.Constants.Companion.EMAIL
import com.fictivestudios.ravebae.utils.Constants.Companion.FCM
import com.fictivestudios.ravebae.utils.Constants.Companion.IS_ID_CARD_VERIFIED
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.signup_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.ArrayList
import java.util.HashMap
import java.util.regex.Pattern

class SignupFragment : BaseFragment() {

    companion object {
        fun newInstance() = SignupFragment()
    }

    private var idImage: ImageView? = null
    private var tv_upload: TextView? = null
    var dialog:Dialog? = null
    private var isShowPass=false
    private var isShowRepeatPass=false
    var isId=false
    private var fileTemporaryProfilePicture: File? = null
    private var fileTemporaryIdCard: File? = null

    private lateinit var signupBinding:View

    override fun setTitlebar(titlebar: Titlebar) {

        titlebar.setBtnBack(getString(R.string.sign_up))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signupBinding =inflater.inflate(R.layout.signup_fragment, container, false)

         dialog = Dialog(requireActivity())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.upload_id_fragment)

        signupBinding.tv_already_have_acc.setOnClickListener {
            RegisterationActivity.getRegActivity
                ?.navControllerReg?.navigate(R.id.loginFragment)
        }
        signupBinding.btn_signup.setOnClickListener {

            validateFields()


        }

        signupBinding.iv_upload.setOnClickListener {

            openImagePicker()
        }

        signupBinding.iv_show_pass.setOnClickListener {
            if (isShowPass)
            {
                isShowPass = false
                signupBinding.iv_show_pass.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                signupBinding.et_pass.transformationMethod = PasswordTransformationMethod()
            }
            else
            {
                isShowPass = true
                signupBinding.iv_show_pass.setImageResource(R.drawable.ic_baseline_visibility_24)
                signupBinding.et_pass.transformationMethod = null
            }

        }

        signupBinding.iv_show_repeat_pass.setOnClickListener {
            if (isShowRepeatPass)
            {
                isShowRepeatPass = false
                signupBinding.iv_show_repeat_pass.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                signupBinding.et_confirm_pass.transformationMethod = PasswordTransformationMethod()
            }
            else
            {
                isShowRepeatPass = true
                signupBinding.iv_show_repeat_pass.setImageResource(R.drawable.ic_baseline_visibility_24)
                signupBinding.et_confirm_pass.transformationMethod = null
            }

        }


        return signupBinding
    }

    private fun validateFields() {


        if (
            signupBinding.et_username.text.toString().isNullOrBlank()
        )
        {
            signupBinding.et_username.setError(getString(R.string.fields_cant_be_empty))

            return
        }

        if (
            signupBinding.et_email.text.toString().isNullOrBlank()
        )
        {
            signupBinding.et_email.setError(getString(R.string.fields_cant_be_empty))

            return
        }

        if (!Constants.isValidEmail(signupBinding.et_email.text.toString()))
        {
            signupBinding.et_email.setError(getString(R.string.invalid_email_format))
            return
        }

        if (
            signupBinding.et_city.text.toString().isNullOrBlank()
        )
        {
            signupBinding.et_city.setError(getString(R.string.fields_cant_be_empty))

            return
        }

        if (
            signupBinding.et_phone.text.toString().isNullOrBlank()
        )
        {
            signupBinding.et_phone.setError(getString(R.string.fields_cant_be_empty))

            return
        }



        if (!signupBinding.et_pass.text.toString().isValidPassword() )
        {
            signupBinding.et_pass.setError(getString(R.string.password_must_be))
            return
        }

        if (signupBinding.et_pass.text.toString() != signupBinding.et_confirm_pass.text.toString() )
        {
            signupBinding.et_confirm_pass.setError(getString(R.string.confirm_pass_doesnt_match))

            return
        }



        else
        {
            Constants.isAccountLogin = true

            //showAgeVerifyDialog()
            signUp()
//


        }
    }


    private fun signUp()
    {
        signupBinding.pb_signup.visibility=View.VISIBLE
        signupBinding.btn_signup.isEnabled=false
        signupBinding.btn_signup.text=""

        var token = PreferenceUtils.getString(FCM,"")

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())



        val signupHM = HashMap<String, RequestBody>().apply {
            this["name"] = signupBinding.et_username.text.toString().getFormDataBody()
            this["email"] = signupBinding.et_email.text.toString().getFormDataBody()
            this["password"] = signupBinding.et_pass.text.toString().getFormDataBody()
            this["password_confirmation"] = signupBinding.et_confirm_pass.text.toString().getFormDataBody()
            this["phone_number"] =signupBinding.et_phone.text.toString().getFormDataBody()
            this["token"] =token.toString().getFormDataBody()
            this["country_code"] =signupBinding.ccp.selectedCountryCodeWithPlus.toString().getFormDataBody()
            this["city"] =signupBinding.et_city.text.toString() .getFormDataBody()


        }

        var imagepart: MultipartBody.Part? = null
       // var idCardpart: MultipartBody.Part? = null

        if (fileTemporaryProfilePicture != null){
            imagepart = fileTemporaryProfilePicture?.getPartMap("image")
        }

/*
        if (fileTemporaryIdCard != null){
            idCardpart = fileTemporaryIdCard?.getPartMap("id_card")
        }
*/


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.signup(signupHM,imagepart).enqueue(object: retrofit2.Callback<SignupResponse> {
                override fun onResponse(
                    call: Call<SignupResponse>,
                    response: Response<SignupResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        signupBinding.pb_signup.visibility=View.GONE
                        signupBinding.btn_signup.isEnabled=true
                        signupBinding.btn_signup.text="SIGNUP"




                    try {

                        Log.d("response", ""+response.body()?.message ?: "null")

                        val response: SignupResponse? =response.body()
                        val statuscode= response!!.status
                        if (statuscode==1) {



                            Log.d("response",""+response.message)

                            //                        val gson = Gson()
                            //                          val json:String = gson.toJson(response.data )
//                            PreferenceUtils.saveString(USER_OBJECT,json)


                            PreferenceUtils.saveString(CURRENT_USER_ID,response.data.id.toString())
                            PreferenceUtils.saveString(EMAIL,signupBinding.et_email.text.toString())
                            PreferenceUtils.saveString(BERBIX_TOKEN,response.data.client_token)




                            RegisterationActivity.getRegActivity
                                ?.navControllerReg?.navigate(R.id.otpFragment)

                        }
                        else {

                                Toast.makeText(requireContext(),""+response.message, Toast.LENGTH_SHORT).show()

                        }
                    }
                    catch (e:Exception)
                    {


                            Toast.makeText(requireContext(),""+ e.message, Toast.LENGTH_SHORT).show()
                            Log.d("response",""+ e.localizedMessage)
                            Log.d("response", ""+e.message.toString())

                    }
                    })
                }

                override fun onFailure(call: Call<SignupResponse>, t: Throwable)
                {

                    activity?.runOnUiThread {
                        signupBinding.pb_signup.visibility=View.GONE
                        signupBinding.btn_signup.isEnabled=true
                        signupBinding.btn_signup.text="SIGNUP"
                        Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                        Log.d("response", ""+t.localizedMessage)
                    }


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

            if (!isId)
            {
                fileTemporaryProfilePicture = File(uri.path!!)
                signupBinding.iv_user_profile.setImageURI(uri)
            }
            else{
                isId = false

                fileTemporaryIdCard = File(uri.path!!)
                idImage?.setImageURI(uri)
                idImage?.visibility = View.VISIBLE

            }
            // Use Uri object instead of File to avoid storage permissions

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            if (isId)
            {
                tv_upload?.setText(getString(R.string.upload_your_id_card))
            }

            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            if (isId)
            {
                tv_upload?.setText(getString(R.string.upload_your_id_card))
            }

            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }



    fun CharSequence.isValidPassword(): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }


    private fun showAgeVerifyDialog() {

        resizeDialogView(dialog!!,70)
        dialog?.show()

        var btnAccept: Button = dialog!!.findViewById<Button>(R.id.btn_verify)
        var btnDecline: Button = dialog!!.findViewById<Button>(R.id.btn_decline)
        var btnUpload: CardView = dialog!!.findViewById<CardView>(R.id.card_upload)
         tv_upload = dialog?.findViewById<TextView>(R.id.tv_upload_video)



         idImage = dialog?.findViewById<ImageView>(R.id.id_image)



        btnAccept.setOnClickListener {

            dialog?.dismiss()

            signUp()

/*            RegisterationActivity.getRegActivity
                ?.navControllerReg?.navigate(R.id.otpFragment)*/

        }

        btnDecline.setOnClickListener {
            dialog?.dismiss()
        }



        btnUpload.setOnClickListener {
            isId = true
            tv_upload?.setText("")
            openImagePicker()
        }

    }
    private fun resizeDialogView(dialog: Dialog, percent: Int) {
        val displayMetrics = DisplayMetrics()

        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)

        val dialogWidth = screenWidth * 95 / 100
        val dialogHeight = screenHeight * percent / 100

        layoutParams.width = dialogWidth
        layoutParams.height = dialogHeight

        dialog.window?.attributes = layoutParams
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
    }
}

