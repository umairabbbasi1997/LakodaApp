package com.fictivestudios.lakoda.views.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.apiManager.response.NotificationToggleResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.ACCESS_TOKEN
import com.fictivestudios.ravebae.utils.Constants.Companion.USER_OBJECT
import com.fictivestudios.ravebae.utils.Constants.Companion.getUser
import com.google.gson.Gson
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

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("SETTINGS")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.settings_fragment, container, false)

       /* mView.tv_terms.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.termsAndConditionFragment)
        }*/

        mView.tv_privacy.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.privacyAndPolicyFragment)
        }


        mView.tv_bundles.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.bundlesFragment)
        }


        mView.sw_push.isChecked = getUser().is_notification == 1


        mView.btn_logout.setOnClickListener {



            showDialog(true)

        }
        mView.tv_blocked.setOnClickListener {

            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.blockedListFragment)
        }

        mView.sw_push.setOnCheckedChangeListener { compoundButton, b ->
            notificationToggle()
        }

        mView.tv_del_acc.setOnClickListener {
            showDialog(false)
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


                    Log.d("Response", ""+response?.body()?.message)
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
                                Toast.makeText(requireContext(), ""+response.body()?.message , Toast.LENGTH_SHORT).show()
                                startActivity(Intent(requireContext(), RegisterationActivity::class.java))
                                MainActivity.getMainActivity?.finish()
                                MainActivity.getMainActivity=null



                            }

                            else
                            {


                                    Toast.makeText(requireContext(), "msg "+response.body()?.message ?: "something went wrong", Toast.LENGTH_SHORT).show()
                                    response.body()?.let { Log.d("Response", "msg "+it.message) }

                            }

                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()

                            Toast.makeText(requireContext(),"msg "+ e.message, Toast.LENGTH_SHORT).show()
                            response.body()?.let { Log.d("msg "+"Response", it.message) }

                    }
                }
                }
                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    activity?.runOnUiThread {
                        mView?.pb_logout?.visibility = View.GONE
                        mView?.iv_switch?.visibility = View.VISIBLE
                        mView.btn_logout.isEnabled=true
                        mView.tv_logout.text="Logout"
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }


    }


    private fun deleteAccount()
    {


        mView?.pb_logout?.visibility = View.VISIBLE
        mView?.iv_switch?.visibility = View.GONE
        mView.btn_logout.isEnabled=false
        mView.tv_logout.text=""


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.deleteAccount().enqueue(object: retrofit2.Callback<CommonResponse> {
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


                        Log.d("Response", ""+response?.body()?.message)
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
                                Toast.makeText(requireContext(), ""+response.body()?.message , Toast.LENGTH_SHORT).show()
                                startActivity(Intent(requireContext(), RegisterationActivity::class.java))
                                MainActivity.getMainActivity?.finish()
                                MainActivity.getMainActivity=null



                            }

                            else
                            {


                                Toast.makeText(requireContext(), "msg "+response.body()?.message ?: "something went wrong", Toast.LENGTH_SHORT).show()
                                response.body()?.let { Log.d("Response", "msg "+it.message) }

                            }

                        }
                        catch (e:Exception)
                        {
                            //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()

                            Toast.makeText(requireContext(),"msg "+ e.message, Toast.LENGTH_SHORT).show()
                            response.body()?.let { Log.d("msg "+"Response", it.message) }

                        }
                    }
                }
                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    activity?.runOnUiThread {
                        mView?.pb_logout?.visibility = View.GONE
                        mView?.iv_switch?.visibility = View.VISIBLE
                        mView.btn_logout.isEnabled=true
                        mView.tv_logout.text="Logout"
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }


    }

    private fun notificationToggle()
    {


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.notificationToggle().enqueue(object: retrofit2.Callback<NotificationToggleResponse> {
                override fun onResponse(
                    call: Call<NotificationToggleResponse>,
                    response: Response<NotificationToggleResponse>
                )
                {


                    Log.d("Response", ""+response?.body()?.message)

                    try {


                        if (response.isSuccessful) {



                            if (response?.message() == "Unauthorized"||
                                response?.body()?.message == "Unauthorized"
                                || response?.message() == "Unauthenticated.")
                            {
                                PreferenceUtils.remove(Constants.USER_OBJECT)
                                PreferenceUtils.remove(Constants.ACCESS_TOKEN)
                                MainActivity.getMainActivity?.finish()
                                MainActivity.getMainActivity=null
                                startActivity(Intent(requireContext(), RegisterationActivity::class.java))
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), "Login expired please login again", Toast.LENGTH_SHORT).show()
                                })
                            }

                            if (response.body()?.status==1)
                            {

                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireActivity(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                })

                                    Log.d("response",response?.body()?.data.toString())
                                    val gson = Gson()
                                    val json:String = gson.toJson(response?.body()?.data?.user )
                                    PreferenceUtils.saveString(USER_OBJECT,json)

                            }
                            else
                            {
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireActivity(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                })
                            }

                        }
                        else {
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireActivity(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                            })

                        }
                    }
                    catch (e:Exception)
                    {
                        activity?.runOnUiThread(java.lang.Runnable {

                            //Toast.makeText(requireActivity(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<NotificationToggleResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {

                        Toast.makeText(requireActivity(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }


    private fun showDialog(isLogout:Boolean) {

        var dialog = Dialog(context as Activity)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.fragment_agreement)

        resizeDialogView(dialog,70)
        dialog?.show()

        var btnAccept: Button = dialog?.findViewById<Button>(R.id.btn_accept)
        var btnDecline: Button = dialog?.findViewById<Button>(R.id.btn_reject)

        var textTitle: TextView = dialog?.findViewById<TextView>(R.id.tv_agreement)
        var tvDesc: TextView = dialog?.findViewById<TextView>(R.id.tv_i_have)

        if (isLogout)
        {
            textTitle.setText(resources.getString(R.string.logout))
            tvDesc.setText(resources.getString(R.string.are_you_sure_want_to_logout))
        }
        else{
            textTitle.setText(resources.getString(R.string.delete_account))
            tvDesc.setText(resources.getString(R.string.are_you_sure_want_to_delete_acc__))
        }

/*        var btnTerms: CheckBox = dialog?.findViewById<CheckBox>(R.id.tv_terms)
        var btnPrivacy: CheckBox = dialog?.findViewById<CheckBox>(R.id.tv_privacy)

        var tvTerms: TextView = dialog?.findViewById<TextView>(R.id.text_term)
        var tvPrivacy: TextView = dialog?.findViewById<TextView>(R.id.text_privacy)*/

        btnAccept.setOnClickListener {

            if (isLogout)
            {
                dialog.dismiss()
                logout()
            }
            else{
                dialog.dismiss()
                deleteAccount()
            }

/*
            if (!btnTerms.isChecked )
            {
                Toast.makeText(requireContext(), getString(R.string.please_accept_terms), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            if (!btnPrivacy.isChecked)
            {
                Toast.makeText(requireContext(), getString(R.string.please_accept_terms), Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }
            else
            {
                dialog?.dismiss()
                RegisterationActivity?.getRegActivity
                    ?.navControllerReg?.navigate(R.id.loginFragment)
            }*/

        }

        btnDecline.setOnClickListener {
            dialog?.dismiss()
        }

/*        tvTerms.setOnClickListener {
            dialog?.dismiss()
            RegisterationActivity?.getRegActivity
                ?.navControllerReg?.navigate(R.id.termsAndConditionFragment)
        }

        tvPrivacy.setOnClickListener {
            dialog?.dismiss()
            RegisterationActivity?.getRegActivity
                ?.navControllerReg?.navigate(R.id.privacyAndPolicyFragment)
        }*/





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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}