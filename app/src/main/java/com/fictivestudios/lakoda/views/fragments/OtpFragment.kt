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
import com.fictivestudios.docsvisor.apiManager.client.SessionManager
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.apiManager.response.VerifyOtpResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.OtpViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.CURRENT_USER_ID
import com.fictivestudios.ravebae.utils.Constants.Companion.EMAIL
import com.fictivestudios.ravebae.utils.Constants.Companion.FCM
import com.fictivestudios.ravebae.utils.Constants.Companion.USER_OBJECT
import com.fictivestudios.ravebae.utils.Constants.Companion.VERIFY_TYPE_ACCOUNT
import com.fictivestudios.ravebae.utils.Constants.Companion.VERIFY_TYPE_PASSWORD
import com.google.gson.Gson
import kotlinx.android.synthetic.main.otp_fragment.view.*
import kotlinx.android.synthetic.main.signup_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class OtpFragment : BaseFragment() {

    companion object {
        fun newInstance() = OtpFragment()
    }

    private lateinit var viewModel: OtpViewModel

    private lateinit var mView: View
    lateinit var sessionManager: SessionManager

    var type = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.otp_fragment, container, false)

        sessionManager = SessionManager(requireContext())
        mView.btn_verify.setOnClickListener {

/*                startActivity(Intent(requireContext(), MainActivity::class.java))
                RegisterationActivity.getRegActivity?.finish()
                RegisterationActivity.getRegActivity = null*/

                verifyOTP()




        }

        mView.tv_didnt_received_code.setOnClickListener {

            resendOTP()
        }

        return  mView
    }


    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack(getString(R.string.otp_verification))
    }

    private fun verifyOTP()
    {
        mView?.pb_otp?.visibility = View.VISIBLE
        mView.btn_verify.isEnabled=false
        mView.btn_verify.text=""


        if (Constants.isAccountLogin)
        {
            type = VERIFY_TYPE_ACCOUNT
        }
        else
        {
            type = VERIFY_TYPE_PASSWORD
        }

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("verified_code", mView?.otp_view?.text.toString())
            .addFormDataPart("id", PreferenceUtils.getString(CURRENT_USER_ID))
            .addFormDataPart("type", type)
            .addFormDataPart("device_type", "android")
            .addFormDataPart("device_token", PreferenceUtils.getString(FCM))
            .build()

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.verifyOTP(requestBody).enqueue(object: retrofit2.Callback<VerifyOtpResponse> {
                override fun onResponse(
                    call: Call<VerifyOtpResponse>,
                    response: Response<VerifyOtpResponse>
                )
                {
                    activity?.runOnUiThread {
                        mView?.pb_otp?.visibility = View.GONE
                      mView.btn_verify.isEnabled=true
                      mView.btn_verify.text="VERIFY"
                    }


                    try {
/*                    loginProg.visibility=View.GONE
                    loginBtn.isEnabled=true*/

                        val verifyResponse: VerifyOtpResponse? =response.body()
                        val statuscode= verifyResponse!!.status
                        Log.d("response",verifyResponse.message)


                        if (statuscode==1) {


                            if (Constants.isAccountLogin)
                            {
                                val gson = Gson()
                                val json:String = gson.toJson(verifyResponse.data.user)
                                PreferenceUtils.saveString(USER_OBJECT,json)
                                sessionManager.saveAuthToken(verifyResponse.data.bearer_token)

                                startActivity(Intent(requireContext(), MainActivity::class.java))
                                RegisterationActivity.getRegActivity?.finish()
                                RegisterationActivity.getRegActivity = null
                            }
                            else
                            {
                                RegisterationActivity.getRegActivity
                                    ?.navControllerReg?.navigate(R.id.changePassowrdFragment)

                            }


                        }
                        else {
                            Toast.makeText(requireContext(), response?.body()?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        activity?.runOnUiThread(java.lang.Runnable {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        })
                    }
                }

                override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        mView?.pb_otp?.visibility=View.GONE
                      mView.btn_verify.isEnabled=true
                      mView.btn_verify.text="VERIFY"
                        Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                        Log.d("response", t.localizedMessage)
                    })
                }
            })

        }


    }


    private fun resendOTP()
    {



         mView?.pb_otp?.visibility = View.VISIBLE
        mView.btn_verify.isEnabled=false
        mView.btn_verify.text=""

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)

            .addFormDataPart("email", PreferenceUtils.getString(EMAIL))
            .build()


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.resendOTP(requestBody).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {

                    activity?.runOnUiThread {
                        mView?.pb_otp?.visibility = View.GONE
                        mView.btn_verify.isEnabled=true
                        mView.btn_verify.text="VERIFY"
                    }


                    try {
/*                    loginProg.visibility=View.GONE
                    loginBtn.isEnabled=true*/

                        val resendOTPResponse: CommonResponse? =response.body()
                        val statuscode= resendOTPResponse!!.status
                        if (statuscode==1) {

                            Log.d("response", resendOTPResponse.message)
                            Toast.makeText(requireContext(), resendOTPResponse.message, Toast.LENGTH_SHORT).show()


                        }
                        else {
                            Toast.makeText(requireContext(), "msg: " +resendOTPResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e:Exception)
                    {
                        //
                        activity?.runOnUiThread {
                            mView?.pb_otp?.visibility = View.GONE
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {
                    activity?.runOnUiThread {
                        mView?.pb_otp?.visibility = View.GONE
                        mView.btn_verify.isEnabled=true
                        mView.btn_verify.text="VERIFY"
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }


    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OtpViewModel::class.java)
        // TODO: Use the ViewModel
    }

}