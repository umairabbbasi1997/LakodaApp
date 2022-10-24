package com.fictivestudios.lakoda.views.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.docsvisor.apiManager.client.SessionManager
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.LoginResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.FCM
import com.fictivestudios.ravebae.utils.Constants.Companion.IS_ID_CARD_VERIFIED
import com.fictivestudios.ravebae.utils.Constants.Companion.USER_OBJECT
import com.google.gson.Gson
import kotlinx.android.synthetic.main.login_fragment.view.*
import kotlinx.android.synthetic.main.login_fragment.view.et_email
import kotlinx.android.synthetic.main.login_fragment.view.et_pass
import kotlinx.android.synthetic.main.login_fragment.view.iv_show_pass
import kotlinx.android.synthetic.main.map_fragment.*
import kotlinx.android.synthetic.main.signup_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern

class LoginFragment : BaseFragment() {

    private var isShowPass =false

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var loginBinding:View
    lateinit var sessionManager: SessionManager
    override fun setTitlebar(titlebar: Titlebar) {

        titlebar.setBtnBack(getString(R.string.login))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginBinding = inflater.inflate(R.layout.login_fragment, container, false)

        sessionManager = SessionManager(requireContext())




        loginBinding.btn_login.setOnClickListener {
            if (!PreferenceUtils.getBoolean(Constants.IS_FIRST_TIME,false))
            {
                PreferenceUtils.saveBoolean(Constants.IS_FIRST_TIME,true)
                showTutorialDialog()
            }
            else{
                validateFields()
            }



        }
        loginBinding.tv_dont_have_acc.setOnClickListener {
            RegisterationActivity.getRegActivity
                ?.navControllerReg?.navigate(R.id.signupFragment)
        }

        loginBinding.tv_forget_pass.setOnClickListener {
            RegisterationActivity.getRegActivity
                ?.navControllerReg?.navigate(R.id.forgetPasswordFragment)
        }





        loginBinding.iv_show_pass.setOnClickListener {
            if (isShowPass)
            {
                isShowPass = false
                loginBinding.iv_show_pass.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                loginBinding.et_pass.transformationMethod = PasswordTransformationMethod()
            }
            else
            {
                isShowPass = true
                loginBinding.iv_show_pass.setImageResource(R.drawable.ic_baseline_visibility_24)
                loginBinding.et_pass.transformationMethod = null
            }

        }


        return loginBinding
    }
    private fun validateFields() {

        /*if (mView.et_email.text.toString().isNullOrBlank()&&
            mView.et_pass.text.toString().isNullOrBlank()
        )
        {
            Toast.makeText(requireContext(), getString(R.string.fields_cant_be_empty), Toast.LENGTH_SHORT).show()
            return
        }
*/
        if (
            loginBinding.et_email.text.toString().isNullOrBlank()
        )
        {
            loginBinding.et_email.setError(getString(R.string.fields_cant_be_empty))

            return
        }
        if (!Constants.isValidEmail(loginBinding.et_email.text.toString()))
        {
            loginBinding.et_email.setError(getString(R.string.invalid_email_format))
            return
        }

        if (
            loginBinding.et_pass.text.toString().isNullOrBlank()
        )
        {
            loginBinding.et_pass.setError(getString(R.string.fields_cant_be_empty))

            return
        }

        else
        {

            login()

        }
    }

    fun CharSequence.isValidPassword(): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }


    private fun showTutorialDialog() {

        var dialog = Dialog(context as Activity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setContentView(R.layout.fragment_tutorial)

        resizeDialogView(dialog!!, 70)
        dialog!!.show()

        loginBinding.setOnClickListener {
            dialog.dismiss()
        }

        var btnPlay: ImageView = dialog!!.findViewById<ImageView>(R.id.btn_play)

        btnPlay.setOnClickListener {
            dialog.dismiss()
        }


    }

        private fun login()
        {
           loginBinding.pb_login.visibility=View.VISIBLE
            loginBinding.btn_login.isEnabled=false
            loginBinding.btn_login.text=""

            var token = PreferenceUtils.getString(FCM,"")

            Log.d("fcmToken",token.toString())
            val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

            val requestBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)



                .addFormDataPart("email", loginBinding.et_email.text.toString())
                .addFormDataPart("password", loginBinding.et_pass.text.toString())
                .addFormDataPart("device_type", "android")
                .addFormDataPart("device_token", token.toString())
                .build()


            GlobalScope.launch(Dispatchers.IO)
            {

                apiClient.login(requestBody).enqueue(object: retrofit2.Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    )
                    {
                        activity?.runOnUiThread(java.lang.Runnable {
                           loginBinding.pb_login.visibility=View.GONE
                          loginBinding.btn_login.isEnabled=true
                          loginBinding.btn_login.text="LOGIN"




                        try {
                            loginBinding.pb_login.visibility=View.GONE
                            loginBinding.btn_login.isEnabled=true
                            val loginResponse: LoginResponse? =response.body()
                            val statuscode= loginResponse!!.status
                            Log.d("response",loginResponse.message)
                            Log.d("response",loginResponse.status.toString())



                            if (statuscode==1)
                            {
                                Log.d("response",loginResponse.data.toString())
                                val gson = Gson()
                                val json:String = gson.toJson(loginResponse.data.user )
                                PreferenceUtils.saveString(USER_OBJECT,json)
                                PreferenceUtils.saveString(IS_ID_CARD_VERIFIED,loginResponse.data.user.is_berbix_verified)
                                sessionManager.saveAuthToken(loginResponse.data.bearer_token)

                                if (loginResponse.data.user.is_berbix_verified.equals(Constants.STATUS_NOT_VERIFIED))
                                {
                                    RegisterationActivity.getRegActivity
                                        ?.navControllerReg?.navigate(R.id.idVerificationFragment)
                                }
                                else if (loginResponse.data.user.is_berbix_verified.equals(Constants.STATUS_REJECTED))
                                    {
                                        RegisterationActivity.getRegActivity
                                            ?.navControllerReg?.navigate(R.id.idVerificationFragment)
                                    }
                                else
                                {
                                    startActivity(Intent(requireContext(), MainActivity::class.java))
                                    RegisterationActivity.getRegActivity?.finish()
                                    RegisterationActivity.getRegActivity = null

                                }

                            }


                            else {

                                    Toast.makeText(requireContext(), ""+response?.body()?.message.toString(), Toast.LENGTH_SHORT).show()

                            }
                        }
                        catch (e:Exception)
                        {


                                Toast.makeText(requireContext(),"msg:"+ e.message.toString(), Toast.LENGTH_SHORT).show()
                                Log.d("exception","msg:"+e.localizedMessage.toString())

                        }


                        })
                        }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable)
                    {


                        activity?.runOnUiThread(java.lang.Runnable {
                           loginBinding.pb_login.visibility=View.GONE
                          loginBinding.btn_login.isEnabled=true
                          loginBinding.btn_login.text="LOGIN"
                            Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                        })
                    }
                })

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