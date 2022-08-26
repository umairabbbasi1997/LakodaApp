package com.fictivestudios.lakoda.adapters

import android.content.Context
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.GetFollowingData
import com.fictivestudios.lakoda.apiManager.response.GetNotificationData
import com.fictivestudios.lakoda.views.fragments.NotificationFragment
import com.fictivestudios.ravebae.utils.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_followers.view.*
import kotlinx.android.synthetic.main.item_notification.view.*
import kotlinx.android.synthetic.main.item_notification.view.tv_name


class NotificationAdapter(
    notificationList: ArrayList<GetNotificationData>,
    requireContext: Context,
    onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<NotificationAdapter.ProfileViewHolder>() {

    private var notificationList=notificationList
    private var context=requireContext
    private var onItemClickListener = onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{


            val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)

        return ProfileViewHolder(view)


    }




    override fun getItemCount() = notificationList?.size ?: 0

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {




        holder.bindViews(notificationList[position],holder.itemView,context)

        if (position == 0)
        {

            holder.itemView.date_ll.visibility=View.VISIBLE
        }

        if (position == 3)
        {

            holder.itemView.date_ll.visibility=View.VISIBLE
            holder.itemView.tv_date.text = "this week"
        }
    }


     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        fun bindViews(model: GetNotificationData, itemView: View, context: Context) {

            itemView.tv_name.setText(model.sender.name)
            itemView.tv_notification.setText(model.message)

            if (model.sender.image.isNullOrEmpty())
            {
                itemView.iv_post.setImageResource(R.drawable.user_dp)
            }
            else
            {
                Picasso
                    .get().load(Constants.IMAGE_BASE_URL + model.sender.image).into(itemView.iv_post)
            }


        }

    }
}