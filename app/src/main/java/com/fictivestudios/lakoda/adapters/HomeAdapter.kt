package com.fictivestudios.lakoda.adapters

import android.content.Intent
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.TiktokActivity
import kotlinx.android.synthetic.main.item_home_post.view.*


class HomeAdapter  : RecyclerView.Adapter<HomeAdapter.ProfileViewHolder>() {

    private var users: List<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{


            val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_post, parent, false)

        return ProfileViewHolder(view)


    }




    override fun getItemCount() = users?.size ?: 10

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {





        holder.itemView.iv_profile.setOnClickListener {

            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.myProfileFragment)
        }

        holder.itemView.tv_username.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.myProfileFragment)
        }
        holder.itemView.tv_comment.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.commentsFragment)
        }

        holder.itemView.tv_comment.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.commentsFragment)
        }

        holder.itemView.iv_Comment.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.commentsFragment)
        }

        holder.itemView.card_post.setOnClickListener {
            MainActivity.getMainActivity
                ?.navControllerMain?.navigate(R.id.videoViewFragment)        }
    }

    fun setProfiles(profiles: List<ContactsContract.Profile>) {
       /* this.profiles = profiles
        notifyDataSetChanged()*/
    }

     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {

    }
}