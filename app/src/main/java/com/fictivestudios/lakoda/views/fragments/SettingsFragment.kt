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
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.SettingsViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.ACCESS_TOKEN
import com.fictivestudios.ravebae.utils.Constants.Companion.USER_OBJECT
import kotlinx.android.synthetic.main.otp_fragment.view.*
import kotlinx.android.synthetic.main.settings_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class SettingsFragment : BaseFragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var mView: View
    private lateinit var viewModel: SettingsViewModel

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("SETTINGS")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.settings_fragment, container, false)

        mView.tv_terms.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.termsAndConditionFragment)
        }

        mView.tv_privacy.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.privacyAndPolicyFragment)
        }


        mView.btn_logout.setOnClickListener {

            logout()


        }

        return mView
    }


    private fun logout()
    {


        mView?.pb_logout?.visibility = View.VISIBLE
        mView?.iv_switch?.visibility = View.GONE
        mView.btn_logout.isEnabled=false
        mView.tv_logout.text=""


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.logout().enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {
                    activity?.runOnUiThread {
                        mView?.pb_logout?.visibility = View.GONE
                        mView?.iv_switch?.visibility = View.VISIBLE
                        mView.btn_logout.isEnabled=true
                        mView.tv_logout.text="Logout"
                    }

                    Log.d("Response", response.message())
                    response.body()?.let { Log.d("Response", it.message) }
                    try {

                            response.body()?.let { Log.d("Response", it.message) }

                            if (response?.message() == "Unauthorized" || response?.message() == "Unauthenticated.")
                            {
                                PreferenceUtils.remove(Constants.USER_OBJECT)
                                PreferenceUtils.remove(Constants.ACCESS_TOKEN)
                                MainActivity.getMainActivity?.finish()
                                MainActivity.getMainActivity=null
                                startActivity(Intent(requireContext(), RegisterationActivity::class.java))

                            }

                        response.code()?.let { Log.d("status code", it.toString()) }

                            if (response.body()?.status == 1)
                            {


                                PreferenceUtils.remove(USER_OBJECT)
                                PreferenceUtils.remove(ACCESS_TOKEN)
                                Toast.makeText(context, response.body()?.message , Toast.LENGTH_SHORT).show()
                                startActivity(Intent(requireContext(), RegisterationActivity::class.java))
                                MainActivity.getMainActivity?.finish()
                                MainActivity.getMainActivity=null



                            }

                            else
                            {

                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(context, "msg "+response.body()?.message ?: "something went wrong", Toast.LENGTH_SHORT).show()
                                    response.body()?.let { Log.d("Response", "msg "+it.message) }
                                })
                            }

                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        activity?.runOnUiThread(java.lang.Runnable {
                            Toast.makeText(context,"msg "+ e.message, Toast.LENGTH_SHORT).show()
                            response.body()?.let { Log.d("msg "+"Response", it.message) }
                        })
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    activity?.runOnUiThread {
                        mView?.pb_logout?.visibility = View.GONE
                        mView?.iv_switch?.visibility = View.VISIBLE
                        mView.btn_logout.isEnabled=true
                        mView.tv_logout.text="Logout"
                        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}