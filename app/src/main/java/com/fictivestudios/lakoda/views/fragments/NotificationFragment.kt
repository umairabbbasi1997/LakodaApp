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
import com.fictivestudios.lakoda.adapters.NotificationAdapter
import com.fictivestudios.lakoda.apiManager.response.GetFollowRequest
import com.fictivestudios.lakoda.apiManager.response.GetFollowRequestData
import com.fictivestudios.lakoda.apiManager.response.GetNotificationData
import com.fictivestudios.lakoda.apiManager.response.GetNotificationsResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.NotificationViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.follow_request_fragment.view.*
import kotlinx.android.synthetic.main.notification_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class NotificationFragment : BaseFragment() ,OnItemClickListener {



    companion object {
        fun newInstance() = NotificationFragment()
    }

    private lateinit var viewModel: NotificationViewModel
    private lateinit var mView: View
    private var notificationList = ArrayList<GetNotificationData>()


    override fun setTitlebar(titlebar: Titlebar) {

        titlebar.setHomeTitle("NOTIFICATIONS")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.notification_fragment, container, false)

        mView.follow_request.setOnClickListener {

            MainActivity?.getMainActivity?.navControllerMain?.navigate(R.id.followRequestFragment)
        }
        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getNotifications()
    }


    private fun getNotifications()
    {

        mView.shimmer_noti .startShimmer()
        mView.shimmer_noti.visibility = View.VISIBLE
        mView.noti_lay.visibility =View.GONE

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getNotifications().enqueue(object: retrofit2.Callback<GetNotificationsResponse> {
                override fun onResponse(
                    call: Call<GetNotificationsResponse>,
                    response: Response<GetNotificationsResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.shimmer_noti.stopShimmer()
                        mView.shimmer_noti.visibility = View.GONE
                        mView.noti_lay.visibility =View.VISIBLE
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
                            mView.shimmer_noti.stopShimmer()
                            mView.shimmer_noti.visibility = View.GONE
                            mView.noti_lay.visibility =View.GONE
                            mView.card_request.visibility = View.GONE
                            Toast.makeText(requireContext(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<GetNotificationsResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.shimmer_noti.stopShimmer()
                        mView.shimmer_noti.visibility = View.GONE
                        mView.noti_lay.visibility =View.GONE
                        mView.card_request.visibility = View.GONE
                        Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }

    private fun getFollowRequest()
    {

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

                                    setFollowerData(response)

                                }

                            }
                            else
                            {
                                activity?.runOnUiThread(java.lang.Runnable {

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

    private fun setFollowerData(response: List<GetFollowRequestData>?) {

    }


    private fun setData(response: List<GetNotificationData>?)
    {

        notificationList = response as ArrayList<GetNotificationData>
        var adapter = NotificationAdapter(notificationList,requireContext(),this)
        mView.rv_notification.adapter =adapter
        adapter.notifyDataSetChanged()

        var isFound = false
        var count = 0
       var followerName: String? =null
        for (item in response)
        {

            if (item?.type == "follow-request")
            {   count += 1

                mView.card_request.visibility = View.VISIBLE

                if (count > 3)
                {
                    break
                }
                else if (count ==1 )
                {
                    followerName = item?.sender?.name
                    if (!item.sender.image.isNullOrEmpty())
                    {
                        Picasso
                            .get().load(Constants.IMAGE_BASE_URL + item.sender.image).into( mView.iv_post_1)

                        mView.iv_post_2.visibility = View.INVISIBLE
                        mView.iv_post.visibility = View.INVISIBLE
                        mView.iv_post_1.visibility = View.VISIBLE

                    }

                }
                else if (count == 2)
                {
                    if (!item.sender.image.isNullOrEmpty())
                    {
                        Picasso
                            .get().load(Constants.IMAGE_BASE_URL + item.sender.image).into( mView.iv_post_2)

                        mView.iv_post.visibility = View.INVISIBLE
                        mView.iv_post_2.visibility = View.VISIBLE
                    }

                }
                else if (count == 3)
                {
                    if (!item.sender.image.isNullOrEmpty())
                    {
                        Picasso
                            .get().load(Constants.IMAGE_BASE_URL + item.sender.image).into( mView.iv_post)

                        mView.iv_post.visibility = View.VISIBLE
                    }
                }
            }
            else{
                mView.card_request.visibility = View.GONE
            }
        }

        if (count == 1)
        {
            mView.tv_notification.setText(followerName)
        }
        else
        {
            var userCount = count-1
            mView.tv_notification.setText(followerName+" +$userCount others")
        }



    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onItemClick(position: Int, view: View, value: String) {

    }

}