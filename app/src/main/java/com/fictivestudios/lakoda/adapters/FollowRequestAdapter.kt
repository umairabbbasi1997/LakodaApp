package com.fictivestudios.lakoda.adapters

import android.content.Context
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.GetFollowRequest
import com.fictivestudios.lakoda.apiManager.response.GetFollowRequestData
import com.fictivestudios.lakoda.views.fragments.FollowRequestFragment
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.ACCEPT
import com.fictivestudios.ravebae.utils.Constants.Companion.PROFILE
import com.fictivestudios.ravebae.utils.Constants.Companion.REJECT
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_followers.view.*
import kotlinx.android.synthetic.main.item_followers_request.view.*
import kotlinx.android.synthetic.main.item_followers_request.view.tv_name


class FollowRequestAdapter(
    followReqList: ArrayList<GetFollowRequestData>,
    requireContext: Context,
    onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<FollowRequestAdapter.ProfileViewHolder>() {

    private var followReqList = followReqList
    private var context = requireContext
    private  var onItemClickListener = onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{


            val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_followers_request, parent, false)

        return ProfileViewHolder(view)


    }




    override fun getItemCount() = followReqList?.size ?: 0

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {

        holder.itemView.btn_confirm.setOnClickListener {
            onItemClickListener.onItemClick(position,it,ACCEPT)
        }

        holder.itemView.btn_delete.setOnClickListener {
            onItemClickListener.onItemClick(position,it, REJECT)
        }

        holder.itemView.iv_post.setOnClickListener {
            onItemClickListener.onItemClick(position,it, PROFILE)
        }

        holder.bindViews(followReqList[position],holder.itemView,context)
    }



     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        fun bindViews(model: GetFollowRequestData, itemView: View, context: Context) {
/*
            if (model.follower. .isNullOrEmpty())
            {
                itemView.iv_user.setImageResource(R.drawable.user_dp)
            }
            else
            {
                Picasso
                    .get().load(Constants.IMAGE_BASE_URL + model.image).into(itemView.iv_user)
            }*/
            itemView.tv_name.setText(model.follower.name)
            itemView.tv_followers_count.setText(model.follower.no_of_followers.toString() + " Followers")
        }

    }
}