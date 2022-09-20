package com.fictivestudios.lakoda.views.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.adapters.MessagesAdapter
import com.fictivestudios.lakoda.apiManager.response.GetChatListData
import com.fictivestudios.lakoda.apiManager.response.GetChatListResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import kotlinx.android.synthetic.main.fragment_messages.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class MessagesFragment : BaseFragment() , OnItemClickListener {

    companion object {
        fun newInstance() = MessagesFragment()
    }


    private lateinit var mView: View
    private var chatList = ArrayList<GetChatListData>()
    private var chatAdapter : MessagesAdapter? = null

    override fun setTitlebar(titlebar: Titlebar) {


            titlebar.setCustomTitleBar("MESSAGES",
                View.OnClickListener {
                    MainActivity.getMainActivity?.navControllerMain?.navigate(R.id.myProfileFragment)
                },R.drawable.user_dp,
                View.OnClickListener {
                    MainActivity.getMainActivity?.navControllerMain?.navigate(R.id.settingsFragment)
                },
                R.drawable.icon_setting,
                true
            )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!this::mView.isInitialized) {
            mView = inflater.inflate(R.layout.fragment_messages, container, false)

            getChatList()
        }



        return mView
    }



    private fun getChatList()
    {

        mView.shimmer_msg .startShimmer()
        mView.shimmer_msg.visibility = View.VISIBLE
        mView.main.visibility =View.GONE

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getChatList().enqueue(object: retrofit2.Callback<GetChatListResponse> {
                override fun onResponse(
                    call: Call<GetChatListResponse>,
                    response: Response<GetChatListResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.shimmer_msg.stopShimmer()
                        mView.shimmer_msg.visibility = View.GONE
                        mView.main.visibility =View.VISIBLE
                    })

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

                                if (response.body()?.data != null)
                                {
                                    Log.d("data", ""+response.body()?.data.toString())
                                    var response = response.body()?.data


                                    setData(response)
                                }

                            }
                            else
                            {
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                })
                            }

                        }
                        else {
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                            })

                        }
                    }
                    catch (e:Exception)
                    {
                        activity?.runOnUiThread(java.lang.Runnable {
                            mView.shimmer_msg.stopShimmer()
                            mView.shimmer_msg.visibility = View.GONE
                            Toast.makeText(requireContext(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<GetChatListResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.shimmer_msg.stopShimmer()
                        mView.shimmer_msg.visibility = View.GONE
                        mView.main.visibility =View.VISIBLE
                        Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }

    private fun setData(response: List<GetChatListData>?)
    {
        chatList = response as ArrayList<GetChatListData>

        chatAdapter = MessagesAdapter(requireContext(),chatList,this@MessagesFragment)
        mView.rv_messages.adapter = chatAdapter
        chatAdapter?.notifyDataSetChanged()

        mView.tv_search.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                chatAdapter?.filter?.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

    }



    override fun onItemClick(position: Int, view: View, value: String) {

        val bundle = bundleOf(
            Constants.USER_ID to   chatList?.get(position)?.user.id.toString(),
            Constants.PROFILE to   chatList?.get(position)?.user?.image?.toString(),
            Constants.USER_NAME to   chatList?.get(position)?.user.name.toString()
        )

        MainActivity.getMainActivity?.navControllerMain?.navigate(R.id.chatFragment,bundle)
    }

}