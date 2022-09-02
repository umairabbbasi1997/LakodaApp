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
import com.fictivestudios.lakoda.adapters.MyProfileFeedsAdapter
import com.fictivestudios.lakoda.apiManager.response.*
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.FriendProfileViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.BLOCK
import com.fictivestudios.ravebae.utils.Constants.Companion.STATUS_REQUEST_SENT
import com.fictivestudios.ravebae.utils.Constants.Companion.STATUS_UNFOLLOWED
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.friend_profile_fragment.*
import kotlinx.android.synthetic.main.friend_profile_fragment.view.*
import kotlinx.android.synthetic.main.friend_profile_fragment.view.iv_user_profile
import kotlinx.android.synthetic.main.friend_profile_fragment.view.lay_following
import kotlinx.android.synthetic.main.friend_profile_fragment.view.tv_city
import kotlinx.android.synthetic.main.friend_profile_fragment.view.tv_followers_count
import kotlinx.android.synthetic.main.friend_profile_fragment.view.tv_following_count
import kotlinx.android.synthetic.main.friend_profile_fragment.view.tv_posts_count
import kotlinx.android.synthetic.main.friend_profile_fragment.view.tv_username_name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class FriendProfileFragment : BaseFragment(),OnItemClickListener {

    companion object {
        fun newInstance() = FriendProfileFragment()
    }

    private var postsArray = ArrayList<HomePostData>()
    private  var userID: String? = null
    private  var userName: String? = null
    private  var profileImage: String? = null
    private lateinit var viewModel: FriendProfileViewModel

    private lateinit var mView: View

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setCustomTitleBar("PROFILE",
            View.OnClickListener {
                MainActivity.getMainActivity?.onBackPressed()
            },R.drawable.icon_btn_back,
            View.OnClickListener {
                val bundle = bundleOf(
                    Constants.USER_ID to userID ,
                    Constants.USER_NAME to userName,
                    Constants.PROFILE to profileImage
                )


                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.chatFragment,bundle)

            },
            R.drawable.chat_icon,
            false
        )
        MainActivity.getMainActivity?.hideBottomBar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.friend_profile_fragment, container, false)



        userID = arguments?.getString(Constants.USER_ID).toString()
        userName = arguments?.getString(Constants.USER_NAME).toString()
        profileImage = arguments?.getString(Constants.PROFILE).toString()

        Log.d("postId", "retrievedId$userID")

        mView.lay_following.setOnClickListener {

            if (! userID.isNullOrEmpty() || userID.equals("null"))
            {
                val bundle = bundleOf(Constants.USER_ID to userID)
                Log.d(Constants.USER_ID,  userID.toString())


                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.followingFragment,bundle)
            }



        }
/*
        mView.lay_followers.setOnClickListener {


            if (! userID.isNullOrEmpty() || userID.equals("null"))
            {
                val bundle = bundleOf(
                    Constants.USER_ID to userID,
                    Constants.IS_REMOVE to false
                    )
                Log.d(Constants.USER_ID,  userID.toString())


                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.followersFragment,bundle)
            }

        }
*/

        mView.tv_followers_count.setOnClickListener {

            if (! userID.isNullOrEmpty() || userID.equals("null"))
            {
                val bundle = bundleOf(Constants.USER_ID to userID,
                    Constants.IS_REMOVE to false
                    )
                Log.d(Constants.USER_ID,  userID.toString())


                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.followersFragment,bundle)
            }
        }

        mView.tv_following_count.setOnClickListener {

            if (! userID.isNullOrEmpty() || userID.equals("null"))
            {
                val bundle = bundleOf(Constants.USER_ID to userID)
                Log.d(Constants.USER_ID,  userID.toString())


                MainActivity.getMainActivity
                    ?.navControllerMain?.navigate(R.id.followingFragment,bundle)
            }

        }


        mView.btn_block.setOnClickListener {


            if (! userID.isNullOrEmpty() || !userID .equals( "null"))
            {

                blockUser(userID?.toInt()!!,btn_block.text.toString().toLowerCase())
            }


        }

        mView.btn_unfollow.setOnClickListener {

            if (mView.btn_unfollow.text.toString().toLowerCase() == "follow")
            {
                if (! userID.isNullOrEmpty() || userID.equals("null"))
                {   mView.btn_unfollow.setText("following...")
                    userID?.toInt()?.let { it1 -> followRequest(it1) }
                }

            }
            else if (mView.btn_unfollow.text.toString().toLowerCase() == "cancel request")
            {
                if (! userID.isNullOrEmpty() || userID.equals("null"))
                {
                    mView.btn_unfollow.setText("canceling...")
                    userID?.toInt()?.let { it1 -> followRequest(it1) }
                }



            }
            else
            {
                if (! userID.isNullOrEmpty() || userID.equals("null"))
                {
                    userID?.toInt()?.let { it1 -> unFollowRequest(it1) }
                }



            }




        }

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (! userID.isNullOrEmpty() || userID .equals("null") )
        {
            getProfile(userID?.toInt())
        }

    }


    private fun followRequest(userID: Int)
    {

        mView.btn_unfollow.isEnabled = false

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())
        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.followRequest(userID).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {


                    Log.d("Response", ""+response?.body()?.message)


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
                                    activity?.runOnUiThread(java.lang.Runnable {
                                        Toast.makeText(requireContext(), "" + response?.body()?.message, Toast.LENGTH_SHORT).show()
                                        mView.btn_unfollow.setText("unfollow")
                                        mView.btn_unfollow.isEnabled = true

                                        if (userID != null)
                                        {
                                            getProfile(userID)
                                        }

                                    })



                            }
                            else
                            {
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                    mView.btn_unfollow.setText("follow")
                                    mView.btn_unfollow.isEnabled = true
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
                            mView.btn_unfollow.setText("follow")
                            mView.btn_unfollow.isEnabled = true
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
                        mView.btn_unfollow.setText("follow")
                        mView.btn_unfollow.isEnabled = true
                    })
                }
            })

        }


    }

    private fun unFollowRequest(userID: Int)
    {
        mView.btn_unfollow.setText("unfollowing...")
        mView.btn_unfollow.isEnabled = false

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())
        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.unFollowRequest(userID).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {


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
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), "" + response?.body()?.message, Toast.LENGTH_SHORT).show()
                                    mView.btn_unfollow.setText("follow")
                                    mView.btn_unfollow.isEnabled = true

                                    if (userID != null)
                                    {
                                        getProfile(userID)
                                    }

                                })



                        }
                        else
                        {
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                mView.btn_unfollow.setText("unfollow")
                                mView.btn_unfollow.isEnabled = true
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
                            mView.btn_unfollow.setText("unfollow")
                            mView.btn_unfollow.isEnabled = true
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
                        mView.btn_unfollow.setText("unfollow")
                        mView.btn_unfollow.isEnabled = true
                    })
                }
            })

        }


    }


    private fun getProfile(userID: Int?)
    {

        //mView.pb_pofile.visibility=View.VISIBLE
        mView.shimmer_friend_profile.startShimmer()
        mView.shimmer_friend_profile.visibility = View.VISIBLE
        mView.btn_unfollow.visibility =View.GONE
        mView.btn_block.visibility =View.GONE
        mView.main_scroll_ll.visibility =View.GONE

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getOtherUserProfile(userID!!).enqueue(object: retrofit2.Callback<GetMyProfileResponse> {
                override fun onResponse(
                    call: Call<GetMyProfileResponse>,
                    response: Response<GetMyProfileResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        mView.shimmer_friend_profile.stopShimmer()
                        mView.shimmer_friend_profile.visibility = View.GONE
                    })

                    Log.d("Response", ""+response?.body()?.message)
                    response?.body()?.message?.let { Log.d("Response", it) }

                    try {


                        if (response.isSuccessful) {

                            if (response.body()?.status==1)
                            {
                                if (response.body()?.data != null)
                                {

                                    var Apiresponse = response.body()?.data
                                    // Toast.makeText(requireContext(), " "+response?.body()?.message, Toast.LENGTH_SHORT).show()
                                    Log.d("profileData",Apiresponse.toString())

                                    Apiresponse?.let { setProfile(it) }
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
                        activity?.runOnUiThread(java.lang.Runnable {
                            //mView.pb_pofile.visibility=View.GONE
                            mView.shimmer_friend_profile.stopShimmer()
                            mView.shimmer_friend_profile.visibility = View.GONE
                            mView.main_scroll_ll.visibility =View.VISIBLE
                            mView.btn_unfollow.visibility =View.VISIBLE
                            mView.btn_block.visibility =View.VISIBLE
                            Toast.makeText(requireContext(),"msg: "+ e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<GetMyProfileResponse>, t: Throwable)
                {

                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE
                        Toast.makeText(requireActivity(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                        mView.shimmer_friend_profile.stopShimmer()
                        mView.shimmer_friend_profile.visibility = View.GONE
                        mView.main_scroll_ll.visibility =View.VISIBLE
                        mView.btn_unfollow.visibility =View.VISIBLE
                        mView.btn_block.visibility =View.VISIBLE
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }




    private fun setProfile(response: MyProfileData) {

        mView.tv_username_name.setText(response?.user?.name)
        mView.tv_followers_count.setText(response?.user?.follower_count.toString())
        mView.tv_following_count.setText(response?.user?.following_count.toString())
        mView.tv_posts_count.setText(response?.user?.post_count.toString())
        mView.tv_city.setText(response?.user?.city)


        for (item in response?.user?.posts)
        {
            if (item.type.equals("post"))
            {
                postsArray.add(item)
            }
        }


        if (response.user.follow_status == STATUS_UNFOLLOWED)
        {
            mView.btn_unfollow.setText("Follow")
            mView.btn_block.visibility = View.GONE
        }
        else if(response.user.follow_status == STATUS_REQUEST_SENT)
        {
            mView.btn_unfollow.setText("Cancel Request")
            mView.btn_block.visibility = View.GONE
        }
        else
        {
            mView.btn_unfollow.setText("UnFollow")
            mView.btn_block.visibility = View.VISIBLE

            var adapter = MyProfileFeedsAdapter(requireContext(),postsArray,this)
            mView.rv_friend_post.adapter = adapter
            adapter.notifyDataSetChanged()





            if (!response?.user?.image.isNullOrBlank())
            {
                Picasso.get()
                    .load(Constants.IMAGE_BASE_URL +response?.user?.image)
                    //.placeholder(R.drawable.loading_spinner)
                    .into(mView.iv_user_profile);
            }
            mView.btn_block.visibility =View.VISIBLE
        }


    /*    if (response.user.is_blocked == 1)
        {

        }*/

        mView.shimmer_friend_profile.stopShimmer()
        mView.shimmer_friend_profile.visibility = View.GONE
        mView.main_scroll_ll.visibility =View.VISIBLE
        mView.btn_unfollow.visibility =View.VISIBLE


    }


    private fun blockUser(userID: Int,type:String)
    {
        if (type == BLOCK)
        {
            mView.btn_block.setText("blocking")
            mView.btn_block.isEnabled = false
        }
        else
        {
            mView.btn_block.setText("unblocking")
            mView.btn_block.isEnabled = false
        }

        Toast.makeText(requireContext(), "Blocking", Toast.LENGTH_SHORT).show()

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())
        GlobalScope.launch(Dispatchers.IO)
        {



            apiClient.blockUser(userID,type).enqueue(object: retrofit2.Callback<CommonResponse> {
                override fun onResponse(
                    call: Call<CommonResponse>,
                    response: Response<CommonResponse>
                )
                {


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
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(), "" + response?.body()?.message, Toast.LENGTH_SHORT).show()
                                if (type == BLOCK)
                                {
                                    mView.btn_block.setText("unblock")
                                    mView.btn_block.isEnabled = true
                                }
                                else
                                {
                                    mView.btn_block.setText("block")
                                    mView.btn_block.isEnabled = true
                                }

                                if (userID != null)
                                {
                                    getProfile(userID)
                                }

                            })



                        }
                        else
                        {
                            activity?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(requireContext(), ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                if (type == BLOCK)
                                {
                                    mView.btn_block.setText("unblock")
                                    mView.btn_block.isEnabled = true
                                }
                                else
                                {
                                    mView.btn_block.setText("block")
                                    mView.btn_block.isEnabled = true
                                }
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
                            if (type == BLOCK)
                            {
                                mView.btn_block.setText("unblock")
                                mView.btn_block.isEnabled = true
                            }
                            else
                            {
                                mView.btn_block.setText("block")
                                mView.btn_block.isEnabled = true
                            }
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
                        if (type == BLOCK)
                        {
                            mView.btn_block.setText("unblock")
                            mView.btn_block.isEnabled = true
                        }
                        else
                        {
                            mView.btn_block.setText("block")
                            mView.btn_block.isEnabled = true
                        }
                    })
                }
            })

        }


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
        viewModel = ViewModelProvider(this).get(FriendProfileViewModel::class.java)
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