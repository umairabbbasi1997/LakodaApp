package com.fictivestudios.lakoda.views.fragments

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
import com.fictivestudios.lakoda.apiManager.response.ContentResponse
import com.fictivestudios.lakoda.utils.Titlebar
import kotlinx.android.synthetic.main.fragment_privacy_and_policy.view.*
import kotlinx.android.synthetic.main.fragment_terms_and_condition.view.*
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
 * Use the [PrivacyAndPolicyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrivacyAndPolicyFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack(getString(R.string.privacy_policy_))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_privacy_and_policy, container, false)

        getPrivacy()
        return mView
    }

    private fun getPrivacy()
    {

        mView.pb_privacy.visibility=View.VISIBLE

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getContent("pp").enqueue(object: retrofit2.Callback<ContentResponse> {
                override fun onResponse(
                    call: Call<ContentResponse>,
                    response: Response<ContentResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_privacy.visibility=View.GONE



                    Log.d("Response", ""+response?.body()?.message)

                    try {


                        if (response.isSuccessful) {

                            if (response.body()?.status==1)
                            {
                                if (response.body()?.data != null)
                                {
                                    mView.text_pp.setText(response.body()!!.data.content.content)
                                }


                            }
                            else
                            {
                                Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()
                            }

                        }
                        else {
                            Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()

                            mView.pb_privacy.visibility=View.GONE

                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

                    }
                    })
                }

                override fun onFailure(call: Call<ContentResponse>, t: Throwable)
                {
                    Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.pb_privacy.visibility=View.GONE

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
         * @return A new instance of fragment PrivacyAndPolicyFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrivacyAndPolicyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}