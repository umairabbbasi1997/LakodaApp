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
import com.fictivestudios.lakoda.apiManager.response.BlockedListData
import com.fictivestudios.lakoda.apiManager.response.CommonResponse
import com.fictivestudios.lakoda.apiManager.response.GetFollowingData
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.getUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_blocked.view.*
import kotlinx.android.synthetic.main.item_followers.view.*
import kotlinx.android.synthetic.main.item_followers.view.iv_user
import kotlinx.android.synthetic.main.item_followers.view.tv_name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response


class BlockedListAdapter(
    blockedList: ArrayList<BlockedListData>,
    requireContext: Context,
    onItemClickListener: OnItemClickListener,
) : RecyclerView.Adapter<BlockedListAdapter.ProfileViewHolder>() {

    private var blockedList = blockedList
    private var context = requireContext
    private var onItemClickListener = onItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{


            val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_blocked, parent, false)

        return ProfileViewHolder(view)


    }




    override fun getItemCount() = blockedList?.size ?: 0

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {




        holder.itemView.btn_unblock.setOnClickListener {

            onItemClickListener.onItemClick(position,it, Constants.UNBLOCK)


        }


        holder.bindViews(blockedList[position],holder.itemView,context)

    }


     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        fun bindViews(model: BlockedListData, itemView: View, context: Context) {

            itemView.tv_name.setText(model.blockuser.name)
            if (model.blockuser.image.isNullOrEmpty())
            {
                itemView.iv_user.setImageResource(R.drawable.user_dp)
            }
            else
            {
               Picasso
                        .get().load(Constants.IMAGE_BASE_URL + model.blockuser.image).into(itemView.iv_user)
            }


        }

    }


}