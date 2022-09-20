package com.fictivestudios.lakoda.views.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.apiManager.response.ForgetPasswordResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.EMAIL
import kotlinx.android.synthetic.main.change_passowrd_fragment.view.*
import kotlinx.android.synthetic.main.forget_password_fragment.view.*
import kotlinx.android.synthetic.main.otp_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.util.regex.Pattern

class ChangePassowrdFragment : BaseFragment() {

    companion object {
        fun newInstance() = ChangePassowrdFragment()
    }

    private var isShowRepeatPass = false
    private var isShowNewPass = false


    private lateinit var mView: View
    override fun setTitlebar(titlebar: Titlebar) {

        titlebar.setTitleOnly("RESET PASSWORD")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.change_passowrd_fragment, container, false)

        mView.btn_update.setOnClickListener {

            validateFields()

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

        mView.iv_show_new_pass.setOnClickListener {
            if (isShowNewPass)
            {
                isShowNewPass = false
                mView.iv_show_new_pass.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                mView.et_new_pass.transformationMethod = PasswordTransformationMethod()
            }
            else
            {
                isShowNewPass = true
                mView.iv_show_new_pass.setImageResource(R.drawable.ic_baseline_visibility_24)
                mView.et_new_pass.transformationMethod = null
            }

        }

        return mView
    }

    private fun validateFields() {



        if (!mView.et_new_pass.text.toString().isValidPassword() )
        {
            mView.et_new_pass.setError(getString(R.string.password_must_be))
            return
        }


        if (mView.et_confirm_pass.text.toString() != mView.et_new_pass.text.toString())
        {
            mView.et_confirm_pass.setError(getString(R.string.password_doesnt_match))
            return
        }



        else
        {

            changePasswordRequest()
        }
    }



    private fun changePasswordRequest()
    {
        mView?.pb_change_pass?.visibility = View.VISIBLE
        mView.btn_update.isEnabled=false
        mView.btn_update.text=""

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("email", PreferenceUtils.getString(EMAIL))
            .addFormDataPart("new_password", mView.et_new_pass.text.toString())
            .addFormDataPart("new_password_confirmation", mView.et_confirm_pass.text.toString())
            .build()

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.resetPassword(requestBody).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {
                    activity?.runOnUiThread {
                        mView?.pb_change_pass?.visibility = View.GONE
                        mView.btn_update.isEnabled=true
                        mView.btn_update.text="Update password"



                    try {
/*                    loginProg.visibility=View.GONE
                    loginBtn.isEnabled=true*/
                        Log.d("response", response.message().toString())
                        val response: CommonResponse? =response.body()
                        val statuscode= response!!.status
                        Log.d("response",response.message)


                        if (statuscode==1) {

                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                            RegisterationActivity.getRegActivity
                                ?.navControllerReg?.navigate(R.id.loginFragment)

                        }
                        else {
                            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()

                            Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()

                    }
                    }    }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        mView?.pb_change_pass?.visibility=View.GONE
                        mView.btn_update.isEnabled=true
                        mView.btn_update.text="Update password"
                        Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                        Log.d("response", t.localizedMessage)
                    })
                }
            })

        }


    }



    fun CharSequence.isValidPassword(): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(this)
        return matcher.matches()
    }




}