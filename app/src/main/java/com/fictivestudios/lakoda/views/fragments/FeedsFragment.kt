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
import com.fictivestudios.lakoda.adapters.FeedsAdapter
import com.fictivestudios.lakoda.adapters.StoriesAdapter
import com.fictivestudios.lakoda.apiManager.response.*
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.viewModel.FeedsViewModel
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.COMMENTS
import com.fictivestudios.ravebae.utils.Constants.Companion.CREATE_STORY
import com.fictivestudios.ravebae.utils.Constants.Companion.IMAGE
import com.fictivestudios.ravebae.utils.Constants.Companion.IMAGE_BASE_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.LIKES
import com.fictivestudios.ravebae.utils.Constants.Companion.POST_ID
import com.fictivestudios.ravebae.utils.Constants.Companion.PROFILE
import com.fictivestudios.ravebae.utils.Constants.Companion.SHARER_PROFILE
import com.fictivestudios.ravebae.utils.Constants.Companion.STORY_DURATION
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_POST
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_SHARE
import com.fictivestudios.ravebae.utils.Constants.Companion.USER_ID
import com.fictivestudios.ravebae.utils.Constants.Companion.USER_IMAGE
import com.fictivestudios.ravebae.utils.Constants.Companion.USER_NAME
import com.fictivestudios.ravebae.utils.Constants.Companion.VIEW_STORY
import kotlinx.android.synthetic.main.feeds_fragment.view.*
import kotlinx.android.synthetic.main.feeds_fragment.view.shimmer_view_container
import kotlinx.android.synthetic.main.feeds_fragment.view.tv_search
import kotlinx.android.synthetic.main.fragment_messages.view.*
import kotlinx.android.synthetic.main.friend_profile_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class FeedsFragment : BaseFragment(),OnItemClickListener {

    private var arrayPostList= ArrayList<HomePostData>()
    private var arrayStoryList= ArrayList<GetStoryData>()
    private lateinit var mView: View

    companion object {
        fun newInstance() = FeedsFragment()
    }

    private lateinit var viewModel: FeedsViewModel

    override fun setTitlebar(titlebar: Titlebar) {

        titlebar.setHomeTitle("FEEDS")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mView = inflater.inflate(R.layout.feeds_fragment, container, false)


        //mView.rv_stories.adapter = StoriesAdapter(requireContext(), response, this)

        return  mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // mView.rv_stories.adapter= StoriesAdapter(requireContext(),null,this@FeedsFragment)
        getAllPost()
        getStories()
    }

    override fun onStart() {
        super.onStart()


    }


    private fun getAllPost()
    {

        //mView.pb_pofile.visibility=View.VISIBLE
        mView.shimmer_view_container.startShimmer()
        mView.feed_post_layout.visibility = View.GONE
        mView.shimmer_view_container.visibility = View.VISIBLE


        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())


        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getAllPost("post").enqueue(object: retrofit2.Callback<HomePostResponse> {
                override fun onResponse(
                    call: Call<HomePostResponse>,
                    response: Response<HomePostResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE
                        mView.shimmer_view_container.stopShimmer()
                        mView.shimmer_view_container.visibility = View.GONE
                        mView.feed_post_layout.visibility = View.VISIBLE
                    })

                    Log.d("Response",""+ response.message())

                    try {






                            if (response?.message() == "Unauthorized" || response?.message() == "Unauthenticated."||
                                response?.body()?.message == "Unauthorized" || response?.body()?.message == "Unauthenticated."
                                    )
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
                                    Log.d("Response", response.toString())
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
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        activity?.runOnUiThread(java.lang.Runnable {
                            //mView.pb_pofile.visibility=View.GONE
                            mView.shimmer_view_container.stopShimmer()
                            mView.shimmer_view_container.visibility = View.GONE
                            mView.feed_post_layout.visibility = View.VISIBLE
                            Toast.makeText(requireContext(),""+ e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption",""+e.localizedMessage)
                        })
                    }
                }

                override fun onFailure(call: Call<HomePostResponse>, t: Throwable)
                {
                    Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE
                        mView.shimmer_view_container.stopShimmer()
                        mView.shimmer_view_container.visibility = View.GONE

                        mView.feed_post_layout.visibility = View.VISIBLE
                        Toast.makeText(requireContext(),""+ t.message, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }

    private fun getStories()
    {

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.getStories().enqueue(object: retrofit2.Callback<GetStoryResponse> {
                override fun onResponse(
                    call: Call<GetStoryResponse>,
                    response: Response<GetStoryResponse>
                )
                {

                    Log.d("Response", ""+response?.body()?.message)

                    try {
                            if (response?.message() == "Unauthorized" || response?.message() == "Unauthenticated."||
                                response?.body()?.message == "Unauthorized" || response?.body()?.message == "Unauthenticated."
                            )
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
                                    Log.d("Response", response.toString())

                                    setStories(response)
                                }
                                else
                                {
                                    mView.rv_stories.adapter= StoriesAdapter(requireContext(),null,this@FeedsFragment)
                                }


                            }
                            else
                            {
                                activity?.runOnUiThread(java.lang.Runnable {
                                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()
                                    mView.rv_stories.adapter= StoriesAdapter(requireContext(),null,this@FeedsFragment)
                                })
                            }


                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        activity?.runOnUiThread(java.lang.Runnable {
                            //mView.pb_pofile.visibility=View.GONE
/*                            mView.shimmer_view_container.stopShimmer()
                            mView.shimmer_view_container.visibility = View.GONE*/
                            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption",e.localizedMessage)
                            activity?.runOnUiThread(java.lang.Runnable {
                                mView.rv_stories.adapter= StoriesAdapter(requireContext(),null,this@FeedsFragment)
                            })
                        })
                    }
                }

                override fun onFailure(call: Call<GetStoryResponse>, t: Throwable)
                {
                    Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
                    activity?.runOnUiThread(java.lang.Runnable {
                        //mView.pb_pofile.visibility=View.GONE
/*
                        mView.shimmer_view_container.stopShimmer()
                        mView.shimmer_view_container.visibility = View.GONE
                        mView.feed_post_layout.visibility =View.VISIBLE
*/                                activity?.runOnUiThread(java.lang.Runnable {
                        mView.rv_stories.adapter= StoriesAdapter(requireContext(),null,this@FeedsFragment)
                    })
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    })
                }
            })

        }


    }

    private fun setStories(response: List<GetStoryData>?) {
        arrayStoryList = response as ArrayList<GetStoryData>


        var adapter = StoriesAdapter(requireContext(),arrayStoryList,this)
        mView.rv_stories.adapter = adapter
        adapter.notifyDataSetChanged()

/*        mView.shimmer_view_container.stopShimmer()
        mView.shimmer_view_container.visibility = View.GONE
        mView.feed_post_layout.visibility =View.VISIBLE*/
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


    private fun sharePost(postId:Int)
    {

        val apiClient = ApiClient.RetrofitInstance.getApiService(requireContext())

        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.sharePost(postId).enqueue(object: retrofit2.Callback<CommentResponse> {
                override fun onResponse(
                    call: Call<CommentResponse>,
                    response: Response<CommentResponse>
                )
                {
                    activity?.runOnUiThread(java.lang.Runnable {
                        //  mView.pb_createPos.visibility=View.GONE
                        Toast.makeText(requireContext(),"Sharing post...", Toast.LENGTH_SHORT).show()
                    })

                    try {

                        Log.d("response", response.body()?.message ?: "null")

                        val response: CommentResponse? =response.body()
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

                override fun onFailure(call: Call<CommentResponse>, t: Throwable)
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

    private fun setData(response: List<HomePostData>?) {

        arrayPostList = response as ArrayList<HomePostData>

        var adapter = FeedsAdapter(requireContext(),arrayPostList,this)
        mView.rv_feeds.adapter = adapter
        adapter.notifyDataSetChanged()

        mView.shimmer_view_container.stopShimmer()
        mView.shimmer_view_container.visibility = View.GONE
        mView.feed_post_layout.visibility =View.VISIBLE


        mView.tv_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter?.filter?.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FeedsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onItemClick(position: Int, view: View, value: String) {


        if (value == COMMENTS)
        {
            val bundle = bundleOf(POST_ID to   arrayPostList?.get(position)?.id.toString())
            Log.d(POST_ID,  arrayPostList?.get(position)?.id.toString())

            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.commentsFragment,bundle)
        }
        else if(value == LIKES)
        {

            likeUnlike(TYPE_POST, arrayPostList?.get(position)?.id)
        }
        else if (value == PROFILE)
        {
            val bundle = bundleOf(
                USER_ID to   arrayPostList?.get(position)?.user.id.toString(),
                USER_NAME to   arrayPostList?.get(position)?.user.name.toString(),
                PROFILE to   arrayPostList?.get(position)?.user?.image?.toString()
                )


            if (!Constants.getUser().id.equals(arrayPostList?.get(position)?.user?.id))
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

        else if (value == SHARER_PROFILE)
        {
            val bundle = bundleOf(
                USER_ID to   arrayPostList?.get(position)?.shared_by.id.toString(),
                USER_NAME to   arrayPostList?.get(position)?.shared_by.name.toString(),
                PROFILE to   arrayPostList?.get(position)?.shared_by.image.toString()

            )


            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.friendProfileFragment,bundle)
        }

        else if(value == LIKES)
        {

            likeUnlike(TYPE_POST, arrayPostList?.get(position)?.id)
        }

        else if(value == CREATE_STORY)
        {

            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.createStoryFragment)
        }
        else if (value == VIEW_STORY)
        {
            val bundle = bundleOf(
                IMAGE to   arrayStoryList?.get(position-1)?.image.toString(),
            USER_NAME to   arrayStoryList?.get(position-1)?.user.name .toString(),
            USER_IMAGE to   arrayStoryList?.get(position-1)?.user?.image?.toString(),
            USER_ID to   arrayStoryList?.get(position-1)?.user.id.toString(),
            STORY_DURATION to   arrayStoryList?.get(position-1)?.duration

            )

            Log.d(STORY_DURATION, arrayStoryList?.get(position-1)?.duration.toString())

            Log.d(IMAGE, arrayStoryList?.get(position-1)?.image.toString())

            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.viewStoryFragment,bundle)

        }

        else if(value == TYPE_SHARE)
        {
            sharePost(arrayPostList?.get(position)?.id)

        }



    }

}