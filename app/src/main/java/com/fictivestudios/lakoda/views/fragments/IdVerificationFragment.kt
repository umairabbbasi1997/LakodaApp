package com.fictivestudios.lakoda.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.berbix.berbixverify.*
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.BerbixTokenResponse
import com.fictivestudios.lakoda.apiManager.response.IdVerifyResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.BERBIX_TOKEN
import com.fictivestudios.ravebae.utils.Constants.Companion.CURRENT_USER_ID
import com.fictivestudios.ravebae.utils.Constants.Companion.IS_ID_CARD_VERIFIED
import com.fictivestudios.ravebae.utils.Constants.Companion.STATUS_NOT_VERIFIED
import com.fictivestudios.ravebae.utils.Constants.Companion.STATUS_REJECTED
import kotlinx.android.synthetic.main.fragment_id_verification.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IdVerificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IdVerificationFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mView: View
    private var buttonText:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setTitleOnly("ID VERIFICATION")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_id_verification, container, false)

//        startBerbixVerify()
        Toast.makeText(requireContext(), "Please Verify ID Card", Toast.LENGTH_SHORT).show()

        if (PreferenceUtils.getString(IS_ID_CARD_VERIFIED).equals(Constants.STATUS_NOT_VERIFIED))
        {
                mView.btn_verify.text = "VERIFY"
                mView.tv_status.text = "NOT VERIFIED"
                mView.iv_verify.setBackgroundResource(R.drawable.not_verified)
        }
        else if (PreferenceUtils.getString(IS_ID_CARD_VERIFIED).equals(Constants.STATUS_REJECTED))
        {
            mView.btn_verify.text = "RE-VERIFY"
            mView.tv_status.text = "NOT VERIFIED"
            mView.iv_verify.setBackgroundResource(R.drawable.not_verified)
        }

        mView.btn_verify.setOnClickListener {
            if(mView.btn_verify.text.toString().toLowerCase().equals("re-verify") ||
                mView.btn_verify.text.toString().toLowerCase().equals("verify") )
            {
                buttonText = mView.btn_verify.text.toString()
                getBerbixToken()
            }
            else{
                startActivity(Intent(requireContext(), MainActivity::class.java))
                RegisterationActivity.getRegActivity?.finish()
                RegisterationActivity.getRegActivity = null
            }
        }

        return mView
    }

    private fun startBerbixVerify(clientToken: String?)
    {
        val sdk = BerbixSDK()
        val config = BerbixConfigurationBuilder()
            .setClientToken(/*PreferenceUtils.getString(BERBIX_TOKEN)*/clientToken.toString()) // fetch token from backend or paste in for demo
            .build()
        sdk.startFlow(this, config)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == BerbixConstants.REQUEST_CODE_BERBIX_FLOW) {
            BerbixResultStatus.handleResult(resultCode, data,object : BerbixResultListener {


                override fun onComplete() {
                    data?.data?.toString()?.let { Log.d("data", it) }
                    data?.data?.toString()
                  //  Toast.makeText(requireContext(), "flow completed", Toast.LENGTH_LONG).show()
                    verifyID()
                }

                override fun onFailure(status: BerbixErrorReason, reason: String?) {
                    PreferenceUtils.saveString(IS_ID_CARD_VERIFIED, STATUS_REJECTED)

                    mView.btn_verify.text = "RE-VERIFY"
                    mView.tv_status.text = "NOT VERIFIED"
                    mView.iv_verify.setBackgroundResource(R.drawable.not_verified)
                    when (status) {
                        BerbixErrorReason.USER_EXIT -> Toast.makeText(requireContext(), "user exited flow", Toast.LENGTH_LONG).show()
                        BerbixErrorReason.NO_CAMERA_PERMISSIONS -> Toast.makeText(requireContext(), "no camera permission", Toast.LENGTH_LONG).show()
                        BerbixErrorReason.UNABLE_TO_LAUNCH_CAMERA -> Toast.makeText(requireContext(), "could not launch camera", Toast.LENGTH_LONG).show()
                        BerbixErrorReason.BERBIX_ERROR -> Toast.makeText(requireContext(), "Berbix error occurred", Toast.LENGTH_LONG).show()
                        BerbixErrorReason.UNKNOWN_ERROR -> Toast.makeText(requireContext(), "unknown error received", Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(requireContext(), "unknown error received", Toast.LENGTH_LONG).show()
                    }
                }


            })
        }
    }



    private fun verifyID()
    {

        mView.pb_verify.visibility=View.VISIBLE
        mView.btn_verify.text = ""

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.idVerification(PreferenceUtils.getString(CURRENT_USER_ID)).enqueue(object: retrofit2.Callback<IdVerifyResponse> {
                override fun onResponse(
                    call: Call<IdVerifyResponse>,
                    response: Response<IdVerifyResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_verify.visibility=View.GONE




                        Log.d("Response", ""+response?.body()?.message)

                        try {


                            if (response.isSuccessful) {

                                if (response.body()?.status==1)
                                {
                                    if (response.body()?.data != null)
                                    {
                                        PreferenceUtils.saveString(IS_ID_CARD_VERIFIED,
                                            response?.body()?.data!!.is_berbix_verified)

                                        if (response?.body()?.data!!.is_berbix_verified.equals(
                                                STATUS_REJECTED))
                                        {
                                            mView.btn_verify.text = "RE-VERIFY"
                                            mView.tv_status.text = "NOT VERIFIED"
                                            mView.iv_verify.setBackgroundResource(R.drawable.not_verified)

                                        }
                                        else if (response?.body()?.data!!.is_berbix_verified.equals(
                                                STATUS_NOT_VERIFIED))
                                        {
                                            mView.btn_verify.text = "RE-VERIFY"
                                            mView.tv_status.text = "NOT VERIFIED"
                                            mView.iv_verify.setBackgroundResource(R.drawable.not_verified)
                                        }
                                        else  {
                                            mView.btn_verify.text = "CONTINUE"
                                            mView.tv_status.text = "VERIFIED"
                                            mView.iv_verify.setBackgroundResource(R.drawable.verified)
                                        }
                                    }


                                }
                                else
                                {
                                    PreferenceUtils.saveString(IS_ID_CARD_VERIFIED,
                                        response?.body()?.data!!.is_berbix_verified)
                                    mView.btn_verify.text = "RE-VERIFY"
                                    mView.tv_status.text = "NOT VERIFIED"
                                    mView.iv_verify.setBackgroundResource(R.drawable.not_verified)
                                    Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                }

                            }
                            else {
                                Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        catch (e:Exception)
                        {
                            //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()

                            mView.pb_verify.visibility=View.GONE
                            mView.btn_verify.text = "RE-VERIFY"
                            mView.tv_status.text = "NOT VERIFIED"
                            mView.iv_verify.setBackgroundResource(R.drawable.not_verified)
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

                        }
                    })
                }

                override fun onFailure(call: Call<IdVerifyResponse>, t: Throwable)
                {
                    Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_verify.visibility=View.GONE
                        mView.btn_verify.text = "RE-VERIFY"
                        mView.tv_status.text = "NOT VERIFIED"
                        mView.iv_verify.setBackgroundResource(R.drawable.not_verified)
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }

    private fun getBerbixToken()
    {

        mView.pb_verify.visibility=View.VISIBLE
        mView.btn_verify.text = ""

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getBerbixToken().enqueue(object: retrofit2.Callback<BerbixTokenResponse> {
                override fun onResponse(
                    call: Call<BerbixTokenResponse>,
                    response: Response<BerbixTokenResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_verify.visibility=View.GONE




                        Log.d("Response", ""+response?.body()?.message)

                        try {


                            if (response.isSuccessful) {

                                if (response.body()?.status==1)
                                {
                                    if (response.body()?.data != null)
                                    {
                                        mView.btn_verify.text = buttonText.toString()
                                        mView.tv_status.text = "NOT VERIFIED"
                                        mView.iv_verify.setBackgroundResource(R.drawable.not_verified)
                                        startBerbixVerify(response.body()?.data?.client_token)

                                    }


                                }
                                else
                                {
                                    mView.btn_verify.text = buttonText.toString()
                                    mView.tv_status.text = "NOT VERIFIED"
                                    mView.iv_verify.setBackgroundResource(R.drawable.not_verified)
                                    Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                }

                            }
                            else {
                                Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        catch (e:Exception)
                        {
                            //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()

                            mView.pb_verify.visibility=View.GONE
                            mView.btn_verify.text = buttonText.toString()

                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

                        }
                    })
                }

                override fun onFailure(call: Call<BerbixTokenResponse>, t: Throwable)
                {
                    Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_verify.visibility=View.GONE
                        mView.btn_verify.text = buttonText.toString()

                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IdVerificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IdVerificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}