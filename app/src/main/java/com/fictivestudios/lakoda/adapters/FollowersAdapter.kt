package com.fictivestudios.lakoda.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.docsvisor.apiManager.client.ApiClient

import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.apiManager.response.GetFollowingData
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.getUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_followers.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response


class FollowersAdapter(
    followersList: ArrayList<GetFollowingData>,
    requireContext: Context,
    onItemClickListener: OnItemClickListener,
    var isShow:Boolean
) : RecyclerView.Adapter<FollowersAdapter.ProfileViewHolder>() {

    private var followersList = followersList
    private var context = requireContext
    private var onItemClickListener = onItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{


            val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_followers, parent, false)

        return ProfileViewHolder(view)


    }




    override fun getItemCount() = followersList?.size ?: 0

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {


        if (isShow)
        {
            holder.itemView.btn_unfollow.visibility = View.VISIBLE
        }
        else{
            holder.itemView.btn_unfollow.visibility = View.GONE

        }

        holder.itemView.btn_unfollow.text =  MainActivity?.getMainActivity?.getString(R.string.remove)

        holder.itemView.setOnClickListener {

            onItemClickListener.onItemClick(position,it, Constants.PROFILE)


        }
        holder.itemView.btn_unfollow.setOnClickListener {

                if (holder.itemView.btn_unfollow.text.toString().toLowerCase() == MainActivity?.getMainActivity?.getString(R.string.remove))
            {
                removeFollow(followersList?.get(position)?.id,holder.itemView,context,followersList?.get(position),holder)
                    //holder.itemView.btn_unfollow.text = MainActivity?.getMainActivity?.getString(R.string.follow)
                // onItemClickListener.onItemClick(position,it, UNFOLLOW)

            }
                else if (holder.itemView.btn_unfollow.text.toString().toLowerCase() == MainActivity?.getMainActivity?.getString(R.string.follow))
            {
                followRequest(followersList?.get(position)?.id,holder.itemView,context)

                // holder.itemView.btn_unfollow.text =  MainActivity?.getMainActivity?.getString(R.string.cancel_request)

                //onItemClickListener.onItemClick(position,it, FOLLOW)

            }
            else if (holder.itemView.btn_unfollow.text.toString().toLowerCase() == MainActivity?.getMainActivity?.getString(R.string.cancel_request))
            {
                followRequest(followersList?.get(position)?.id,holder.itemView,context)
                //  holder.itemView.btn_unfollow.text =  MainActivity?.getMainActivity?.getString(R.string.follow)

                //onItemClickListener.onItemClick(position,it, FOLLOW)

            }



            /*if (holder.itemView.btn_unfollow.text == MainActivity?.getMainActivity?.getString(R.string.remove))
            {
                holder.itemView.btn_unfollow.text = MainActivity?.getMainActivity?.getString(R.string.follow)
                onItemClickListener.onItemClick(position,it, Constants.REMOVE_FOLLOW)
            }
            else if (holder.itemView.btn_unfollow.text == MainActivity?.getMainActivity?.getString(R.string.follow))
            {
                holder.itemView.btn_unfollow.text = MainActivity?.getMainActivity?.getString(R.string.remove)
                onItemClickListener.onItemClick(position,it, Constants.FOLLOW)
            }*/
        }

        holder.bindViews(followersList[position],holder.itemView,context)

    }


     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        fun bindViews(model: GetFollowingData, itemView: View, context: Context) {

            itemView.tv_name.setText(model.name)
            if (model.image.isNullOrEmpty())
            {
                itemView.iv_user.setImageResource(R.drawable.user_dp)
            }
            else
            {
               Picasso
                        .get().load(Constants.IMAGE_BASE_URL + model.image).into(itemView.iv_user)
            }


        }

    }

    private fun followRequest(userID: Int, itemView: View, context: Context)
    {

        itemView.btn_unfollow.isEnabled = false

        val apiClient = ApiClient.RetrofitInstance.getApiService(context)
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
                            context?.startActivity(Intent(context, RegisterationActivity::class.java))
                            (context as Activity)?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(context, "Login expired please login again", Toast.LENGTH_SHORT).show()
                            })
                        }

                        if (response.body()?.status==1)
                        {
                            (context as Activity)?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(context, "" + response?.body()?.message, Toast.LENGTH_SHORT).show()
                                itemView.btn_unfollow.setText("unfollow")
                                itemView.btn_unfollow.isEnabled = true

                                /*if (userID != null)
                                {
                                    getProfile(userID)
                                }*/

                            })



                        }
                        else
                        {
                            (context as Activity)?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(context, ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                itemView.btn_unfollow.setText("follow")
                                itemView.btn_unfollow.isEnabled = true
                            })
                        }


                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        (context as Activity)?.runOnUiThread(java.lang.Runnable {
                            //itemView.pb_pofile.visibility=View.GONE
/*                            itemView.shimmer_following.stopShimmer()
                            itemView.shimmer_following.visibility = View.GONE*/
                            Toast.makeText(context, "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                            itemView.btn_unfollow.setText("follow")
                            itemView.btn_unfollow.isEnabled = true
                        })
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    (context as Activity)?.runOnUiThread(java.lang.Runnable {
                        //itemView.pb_pofile.visibility=View.GONE
/*                        itemView.shimmer_following.stopShimmer()
                        itemView.shimmer_following.visibility = View.GONE
                        itemView.rv_followers.visibility =View.VISIBLE*/
                        Toast.makeText(context, ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                        itemView.btn_unfollow.setText("follow")
                        itemView.btn_unfollow.isEnabled = true
                    })
                }
            })

        }


    }

    private fun removeFollow(
        userID: Int,
        itemView: View,
        context: Context,
        item: GetFollowingData,
        holder: ProfileViewHolder
    )
    {
        itemView.btn_unfollow.setText("removing...")
        itemView.btn_unfollow.isEnabled = false

        val apiClient = ApiClient.RetrofitInstance.getApiService(context)
        GlobalScope.launch(Dispatchers.IO)
        {

            apiClient.removeFollowRequest(userID).enqueue(object: retrofit2.Callback<CommonResponse> {
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
                            context?.startActivity(Intent(context, RegisterationActivity::class.java))
                            (context as Activity)?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(context, "Login expired please login again", Toast.LENGTH_SHORT).show()
                            })
                        }

                        if (response.body()?.status==1)
                        {
                            (context as Activity)?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(context, "" + response?.body()?.message, Toast.LENGTH_SHORT).show()
                                itemView.btn_unfollow.setText("follow")
                                itemView.btn_unfollow.isEnabled = true

                                followersList.remove(item)
                                notifyItemRemoved(holder.adapterPosition)


                                /*if (userID != null)
                                {
                                    getProfile(userID)
                                }*/

                            })



                        }
                        else
                        {
                            (context as Activity)?.runOnUiThread(java.lang.Runnable {
                                Toast.makeText(context, ""+response.body()?.message, Toast.LENGTH_SHORT).show()
                                itemView.btn_unfollow.setText("remove")
                                itemView.btn_unfollow.isEnabled = true
                            })
                        }


                    }
                    catch (e:Exception)
                    {
                        //Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        (context as Activity)?.runOnUiThread(java.lang.Runnable {
                            //itemView.pb_pofile.visibility=View.GONE
/*                            itemView.shimmer_following.stopShimmer()
                            itemView.shimmer_following.visibility = View.GONE*/
                            Toast.makeText(context, "msg: "+e.message, Toast.LENGTH_SHORT).show()
                            Log.d("execption","msg: "+e.localizedMessage)
                            itemView.btn_unfollow.setText("remove")
                            itemView.btn_unfollow.isEnabled = true
                        })
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable)
                {

                    (context as Activity)?.runOnUiThread(java.lang.Runnable {
                        //itemView.pb_pofile.visibility=View.GONE
/*                        itemView.shimmer_following.stopShimmer()
                        itemView.shimmer_following.visibility = View.GONE
                        itemView.rv_followers.visibility =View.VISIBLE*/
                        Toast.makeText(context, ""+t.localizedMessage, Toast.LENGTH_SHORT).show()
                        itemView.btn_unfollow.setText("remove")
                        itemView.btn_unfollow.isEnabled = true
                    })
                }
            })

        }


    }
}