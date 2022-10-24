package com.fictivestudios.lakoda.views.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.ForgetPasswordResponse
import com.fictivestudios.lakoda.apiManager.response.VerifyOtpResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.CURRENT_USER_ID
import com.fictivestudios.ravebae.utils.Constants.Companion.EMAIL
import com.google.gson.Gson
import kotlinx.android.synthetic.main.forget_password_fragment.view.*
import kotlinx.android.synthetic.main.forget_password_fragment.view.et_email
import kotlinx.android.synthetic.main.login_fragment.view.*
import kotlinx.android.synthetic.main.otp_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class ForgetPasswordFragment : BaseFragment() {

    companion object {
        fun newInstance() = ForgetPasswordFragment()
    }

    private lateinit var mView: View
    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack(getString(R.string.forgot_password_))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.forget_password_fragment, container, false)

        mView.btn_forget_pass.setOnClickListener {

            if (
                mView.et_email.text.toString().isNullOrBlank()
            )
            {
                mView.et_email.setError(getString(R.string.fields_cant_be_empty))


            }

            else if (Constants.isValidEmail( mView.et_email.text.toString()))
            {

                forgetPasswordRequest()
            }
            else
            {
             mView.et_email.setError(getString(R.string.invalid_email_format))
            }

        }


        return mView
    }


    private fun forgetPasswordRequest()
    {
        mView?.pb_forget_pass?.visibility = View.VISIBLE
        mView.btn_forget_pass.isEnabled=false
        mView.btn_forget_pass.text=""

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("email", mView?.et_email?.text.toString())
            .build()

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.forgetPassword(requestBody).enqueue(object: retrofit2.Callback<ForgetPasswordResponse> {
                override fun onResponse(
                    call: Call<ForgetPasswordResponse>,
                    response: Response<ForgetPasswordResponse>
                )
                {
                    activity?.runOnUiThread {
                        mView?.pb_forget_pass?.visibility = View.GONE
                        mView.btn_forget_pass.isEnabled=true
                        mView.btn_forget_pass.text="get Code"


                    try {
/*                    loginProg.visibility=View.GONE
                    loginBtn.isEnabled=true*/

                        val response: ForgetPasswordResponse? =response.body()
                        val statuscode= response!!.status
                        if (statuscode==1) {

                            Log.d("response",""+response.message)
                            Toast.makeText(requireContext(), ""+response.message, Toast.LENGTH_SHORT).show()
                            PreferenceUtils.saveString(EMAIL,mView?.et_email?.text.toString())
                            PreferenceUtils.saveString(CURRENT_USER_ID,response.data.id.toString())
                            Constants.isAccountLogin = false
                            RegisterationActivity.getRegActivity
                                ?.navControllerReg?.navigate(R.id.otpFragment)

                        }
                        else {
                            Toast.makeText(requireContext(), ""+response.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()

                            Toast.makeText(requireContext(), ""+e.message, Toast.LENGTH_SHORT).show()

                    }
                    }
                }

                override fun onFailure(call: Call<ForgetPasswordResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        mView?.pb_forget_pass?.visibility=View.GONE
                        mView.btn_forget_pass.isEnabled=true
                        mView.btn_forget_pass.text="get Code"
                        Toast.makeText(requireContext(),""+ t.localizedMessage, Toast.LENGTH_SHORT).show()
                        Log.d("response", ""+t.localizedMessage)
                    })
                }
            })

        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }


}