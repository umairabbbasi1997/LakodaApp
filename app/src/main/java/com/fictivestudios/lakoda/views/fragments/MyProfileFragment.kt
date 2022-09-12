package com.fictivestudios.lakoda.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
 
import com.fictivestudios.docsvisor.apiManager.client.ApiClient
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.adapters.MyProfileFeedsAdapter
import com.fictivestudios.lakoda.apiManager.response.*
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.MyProfileViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.getUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.my_profile_fragment.view.*
import kotlinx.android.synthetic.main.my_profile_fragment.view.iv_user_profile
import kotlinx.android.synthetic.main.my_profile_fragment.view.shimmer_view_container
import kotlinx.android.synthetic.main.my_profile_fragment.view.tv_city
import kotlinx.android.synthetic.main.my_profile_fragment.view.tv_followers_count
import kotlinx.android.synthetic.main.my_profile_fragment.view.tv_following_count
import kotlinx.android.synthetic.main.my_profile_fragment.view.tv_posts_count
import kotlinx.android.synthetic.main.my_profile_fragment.view.tv_username_name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response


class MyProfileFragment : BaseFragment() ,OnItemClickListener {

    private var userID: String? = null
    private lateinit var mView: View

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private lateinit var viewModel: MyProfileViewModel

    private var postsArray = ArrayList<HomePostData>()

    override fun setTitlebar(titlebar: Titlebar) {

        titlebar.setCustomTitleBar("MY PROFILE",
            View.OnClickListener {
            MainActivity.getMainActivity?.onBackPressed()
        },R.drawable.icon_btn_back,
            View.OnClickListener {
                MainActivity.getMainActivity?.navControllerMain?.navigate(R.id.settingsFragment)
            },
            R.drawable.icon_setting,
            false
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.my_profile_fragment, container, false)



        userID = arguments?.getString(Constants.USER_ID).toString()

        Log.d("userId", "retrievedId$userID")

/*
        mView.lay_following.setOnClickListener {

            if (! userID.isNullOrEmpty() || userID.equals("null"))
            {
                val bundle = bundleOf(Constants.USER_ID to userID)
                Log.d(Constants.USER_ID,  userID.toString())


                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.followingFragment,bundle)
            }



        }
        mView.lay_followers.setOnClickListener {


            if (! userID.isNullOrEmpty() || userID.equals("null"))
            {
                val bundle = bundleOf(Constants.USER_ID to userID)
                Log.d(Constants.USER_ID,  userID.toString())


                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.followersFragment,bundle)
            }

        }

*/

        mView.tv_followers_count.setOnClickListener {


                val bundle = bundleOf(Constants.USER_ID to getUser().id.toString(),
                    Constants.IS_REMOVE to true
                    )

                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.followersFragment,bundle)
        }

        mView.tv_following_count.setOnClickListener {


                val bundle = bundleOf(Constants.USER_ID to getUser().id.toString())

                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.followingFragment,bundle)


        }

        mView.iv_edit.setOnClickListener {

            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.editProfileFragment)
        }


        return mView
    }

    override fun onStart() {
        super.onStart()
        getProfile()
    }




    private fun getProfile()
    {

        //mView.pb_pofile.visibility=View.VISIBLE
        mView.shimmer_view_container.startShimmer()
        mView.shimmer_view_container.visibility = View.VISIBLE
        mView.main_profile_layout.visibility = View.GONE


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getProfile().enqueue(object: retrofit2.Callback<GetMyProfileResponse> {
                override fun onResponse(
                    call: Call<GetMyProfileResponse>,
                    response: Response<GetMyProfileResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.shimmer_view_container.stopShimmer()
                        mView.shimmer_view_container.visibility = View.GONE


                    Log.d("Response", ""+response?.body()?.message)

                    try {


                        if (response.isSuccessful) {

                            if (response.body()?.status==1)
                            {
                                if (response.body()?.data != null)
                                {

                                    var Apiresponse = response.body()?.data
                                   // Toast.makeText(requireContext(), " "+response?.body()?.message, Toast.LENGTH_SHORT).show()
                                    Log.d("profileData",Apiresponse.toString())

                                    if (Apiresponse != null) {
                                        setProfile(Apiresponse)
                                    }
                                }


                            }
                            else
                            {
                                Toast.makeText(requireContext(), "msg: "+response.body()?.message, Toast.LENGTH_SHORT).show()
                            }

                        }
                        else {
                            Toast.makeText(requireContext(), "msg: "+response.body()?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()

                            //mView.pb_pofile.visibility=View.GONE
                            mView.shimmer_view_container.stopShimmer()
                            mView.shimmer_view_container.visibility = View.GONE
                            Toast.makeText(requireContext(),"msg: "+ e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)

                    }
                    })
                }

                override fun onFailure(call: Call<GetMyProfileResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE
                        Toast.makeText(requireActivity(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                        mView.shimmer_view_container.stopShimmer()
                        mView.shimmer_view_container.visibility = View.GONE
                        mView.main_profile_layout.visibility =View.VISIBLE
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }




    private fun setProfile(response: MyProfileData) {

        postsArray.clear()
        for (item in response?.user?.posts)
        {
            if (item.type.equals("post"))
            {
                postsArray.add(item)
            }
        }

        var adapter = MyProfileFeedsAdapter(requireContext(),postsArray,this)
        mView.rv_my_post.adapter = adapter
        adapter.notifyDataSetChanged()



        mView.tv_username_name.setText(response?.user?.name)
        mView.tv_followers_count.setText(response?.user?.follower_count.toString())
        mView.tv_following_count.setText(response?.user?.following_count.toString())
        mView.tv_posts_count.setText((response?.user?.post_post_count + response?.user?.share_count) .toString())
        mView.tv_city.setText(response?.user?.city)

        if (!response?.user?.image.isNullOrBlank())
        {
            Picasso.get()
                .load(Constants.IMAGE_BASE_URL +response?.user?.image)
                //.placeholder(R.drawable.loading_spinner)
                .into(mView.iv_user_profile);
        }

        mView.shimmer_view_container.stopShimmer()
        mView.shimmer_view_container.visibility = View.GONE
        mView.main_profile_layout.visibility =View.VISIBLE

    }


    private fun likeUnlike(postType:String,postId:Int)
    {

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.likePost(postId,postType).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        //  mView.pb_createPos.visibility=View.GONE
                        //Toast.makeText(requireContext(),"Liking post...", Toast.LENGTH_SHORT).show()
                    })

                    try {

                        Log.d("response", response.body()?.message ?: "null")

                        val response: CommonResponse? =response.body()
                        val statuscode= response!!.status
                        if (statuscode==1) {

                            Log.d("response",response.message)


                            //    MainActivity.getMainActivity?.onBackPressed()
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(),response.message, Toast.LENGTH_SHORT).show()
                            })

                        }
                        else {
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(),response.message, Toast.LENGTH_SHORT).show()
                            })
                        }
                    }
                    catch (e:Exception)
                    {

                        activity?.runOnUiThread(java.lang.Runnable {
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                            Log.d("response", e.localizedMessage)
                            Log.d("response", e.message.toString())
                        })
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        // mView.pb_createPos.visibility=View.GONE

                        Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                        Log.d("response", t.localizedMessage)
                    })

                }
            })

        }


    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onItemClick(position: Int, view: View, value: String) {


        if (value == Constants.COMMENTS)
        {
            val bundle = bundleOf(Constants.POST_ID to   postsArray?.get(position)?.id.toString())
            Log.d(Constants.POST_ID,  postsArray?.get(position)?.id.toString())

            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.commentsFragment,bundle)
        }
        else if(value == Constants.LIKES)
        {

            likeUnlike(Constants.TYPE_POST, postsArray?.get(position)?.id)
        }
        else if (value == Constants.PROFILE)
        {
            val bundle = bundleOf(
                Constants.USER_ID to   postsArray?.get(position)?.user.id.toString(),
                Constants.USER_NAME to   postsArray?.get(position)?.user.name.toString(),
                Constants.PROFILE to   postsArray?.get(position)?.user?.image?.toString()
            )


            if (!Constants.getUser().id.equals(postsArray?.get(position)?.user?.id))
            {
                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.friendProfileFragment,bundle)

            }
            else
            {
                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.myProfileFragment)

            }

        }

    }

}