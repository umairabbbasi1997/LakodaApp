package com.fictivestudios.lakoda.views.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.adapters.FollowRequestAdapter
import com.fictivestudios.lakoda.apiManager.response.*
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.FollowRequestViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.ACCEPT
import com.fictivestudios.ravebae.utils.Constants.Companion.REJECT
import kotlinx.android.synthetic.main.change_passowrd_fragment.view.*
import kotlinx.android.synthetic.main.follow_request_fragment.view.*
import kotlinx.android.synthetic.main.following_fragment.view.*
import kotlinx.android.synthetic.main.following_fragment.view.rv_followers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class FollowRequestFragment : BaseFragment(),OnItemClickListener {

    companion object {
        fun newInstance() = FollowRequestFragment()
    }

    private var followReqList = ArrayList<GetFollowRequestData>()
    private lateinit var viewModel: FollowRequestViewModel

    private lateinit var mView: View
    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("FOLLOW REQUEST")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.follow_request_fragment, container, false)


        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFollowRequest()
    }

    private fun getFollowRequest()
    {

        mView.shimmer_follow_req .startShimmer()
        mView.shimmer_follow_req.visibility = View.VISIBLE
        mView.rv_follow_request.visibility =View.GONE

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getFollowRequest().enqueue(object: retrofit2.Callback<GetFollowRequest> {
                override fun onResponse(
                    call: Call<GetFollowRequest>,
                    response: Response<GetFollowRequest>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.shimmer_follow_req.stopShimmer()
                        mView.shimmer_follow_req.visibility = View.GONE
                        mView.rv_follow_request.visibility =View.VISIBLE
                    })

                    response?.body()?.message?.let { Log.d("Response", it) }

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
                                {  response?.body()?.data?.let { Log.d("Response", it.toString()) }

                                    var response = response.body()?.data

                                    setData(response)
                                }

                            }
                            else
                            {
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()

                                    followReqList.clear()
                                    var adapter = FollowRequestAdapter(followReqList,requireContext(),this@FollowRequestFragment)
                                    mView.rv_follow_request.adapter =adapter
                                    adapter.notifyDataSetChanged()
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
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        activity?.runOnUiThread(java.lang.Runnable {
                            //mView.pb_pofile.visibility=View.GONE
                            mView.shimmer_follow_req.stopShimmer()
                            mView.shimmer_follow_req.visibility = View.GONE
                            Toast.makeText(requireContext(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<GetFollowRequest>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE
                        mView.shimmer_follow_req.stopShimmer()
                        mView.shimmer_follow_req.visibility = View.GONE
                        mView.rv_follow_request.visibility =View.VISIBLE
                        Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }

    private fun setData(response: List<GetFollowRequestData>?)
    {
        followReqList = response as ArrayList<GetFollowRequestData>

        var adapter = FollowRequestAdapter(followReqList,requireContext(),this)
        mView.rv_follow_request.adapter =adapter
        adapter.notifyDataSetChanged()


    }


    private fun acceptFollow(userID: Int, type: String)
    {


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("type", type)
            .build()



        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.acceptFollowRequest(userID,requestBody).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {

                    })

                    response?.body()?.message?.let { Log.d("Response", it) }

                    try {

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
                                getFollowRequest()

                            }
                            else
                            {
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                })
                            }

                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        activity?.runOnUiThread(java.lang.Runnable {

                            Toast.makeText(requireContext(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE
                        mView.shimmer_following.stopShimmer()
                        mView.shimmer_following.visibility = View.GONE
                        mView.rv_followers.visibility =View.VISIBLE
                        Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FollowRequestViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onItemClick(position: Int, view: View, value: String) {

        if (value == ACCEPT)
        {
            acceptFollow(followReqList[position].follower.id, ACCEPT)
        }
        else if (value == REJECT)
        {
            acceptFollow(followReqList[position].follower.id, REJECT)
        }
    }

}