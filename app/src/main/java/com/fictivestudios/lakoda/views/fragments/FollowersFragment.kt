package com.fictivestudios.lakoda.views.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
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
import com.fictivestudios.lakoda.adapters.FollowersAdapter
import com.fictivestudios.lakoda.apiManager.response.CommentResponse
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.apiManager.response.GetFollowingData
import com.fictivestudios.lakoda.apiManager.response.GetFollowingResponse
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import kotlinx.android.synthetic.main.feeds_fragment.view.*
import kotlinx.android.synthetic.main.followers_fragment.view.*
import kotlinx.android.synthetic.main.my_profile_fragment.view.shimmer_view_container
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class FollowersFragment : BaseFragment() ,OnItemClickListener{

    companion object {
        fun newInstance() = FollowersFragment()
    }

    private var userID: String? = null
    private var isRemove: Boolean? = false
    private lateinit var mView: View

    private var followersList = ArrayList<GetFollowingData>()
    override fun setTitlebar(titlebar: Titlebar) {

        titlebar.setBtnBack("FOLLOWERS")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        userID = arguments?.getString(Constants.USER_ID)
        isRemove = arguments?.getBoolean(Constants.IS_REMOVE)


        if (!this::mView.isInitialized) {

            mView = inflater.inflate(R.layout.followers_fragment, container, false)

            if (! userID.isNullOrEmpty() || userID.equals("null"))
            {

                getFollowers(userID?.toInt()!!)

            }
        }



        Log.d("userId", "$userID")



        return mView
    }

    private fun getFollowers(userID: Int)
    {

        //mView.pb_pofile.visibility=View.VISIBLE
            mView.shimmer_followers.startShimmer()
            mView.shimmer_followers.visibility = View.VISIBLE
            mView.rv_followers.visibility = View.GONE


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getMyFollowers(userID).enqueue(object: retrofit2.Callback<GetFollowingResponse> {
                override fun onResponse(
                    call: Call<GetFollowingResponse>,
                    response: Response<GetFollowingResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE
                          mView.shimmer_followers.stopShimmer()
                          mView.shimmer_followers.visibility = View.GONE
                        mView.rv_followers.visibility = View.VISIBLE
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
                                {

                                    var response = response.body()?.data

                                    setData(response)
                                }


                            }
                            else
                            {
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()
                                })
                            }

                        }
                        else {
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()
                            })

                        }
                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        activity?.runOnUiThread(java.lang.Runnable {
                            //mView.pb_pofile.visibility=View.GONE
                                  mView.shimmer_followers.stopShimmer()
                                  mView.shimmer_followers.visibility = View.GONE

                            Toast.makeText(requireContext(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<GetFollowingResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE
                        mView.shimmer_followers.stopShimmer()
                        mView.shimmer_followers.visibility = View.GONE

                        Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }

    private fun followRequest(userID: Int)
    {

/*        mView.shimmer_following .startShimmer()
        mView.shimmer_following.visibility = View.VISIBLE
        mView.rv_followers.visibility =View.GONE*/

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.followRequest(userID).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {
/*                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.shimmer_following.stopShimmer()
                        mView.shimmer_following.visibility = View.GONE
                        mView.rv_followers.visibility =View.VISIBLE
                    })*/

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
                                        Toast.makeText(requireContext(), "" + response?.body()?.message, Toast.LENGTH_SHORT).show()
                                    })

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
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        activity?.runOnUiThread(java.lang.Runnable {
                            //mView.pb_pofile.visibility=View.GONE
/*                            mView.shimmer_following.stopShimmer()
                            mView.shimmer_following.visibility = View.GONE*/
                            Toast.makeText(requireContext(), "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE
/*                        mView.shimmer_following.stopShimmer()
                        mView.shimmer_following.visibility = View.GONE
                        mView.rv_followers.visibility =View.VISIBLE*/
                        Toast.makeText(requireContext(), ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }

    private fun setData(response: List<GetFollowingData>?)
    {

        followersList = response as ArrayList<GetFollowingData>

        var adapter = isRemove?.let { FollowersAdapter(followersList,requireContext(),this, it) }
        mView.rv_followers.adapter =adapter
        adapter?.notifyDataSetChanged()
      //  mView.rv_followers.adapter = FollowersAdapter(followersList, requireContext(), this)
    }




    override fun onItemClick(position: Int, view: View, value: String) {

        if (value ==Constants.FOLLOW)
        {
            followRequest(followersList[position].id)

        }
        else if (value == Constants.PROFILE)
        {
            val bundle = bundleOf(





                Constants.USER_ID to   followersList?.get(position)?.id.toString(),
                Constants.USER_NAME to   followersList?.get(position)?.name.toString(),
                Constants.PROFILE to   followersList?.get(position)?.image?.toString()
            )


            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.friendProfileFragment,bundle)
        }
    }

}