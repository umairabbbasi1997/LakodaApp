package com.fictivestudios.lakoda.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.GetCommentsData
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.PROFILE
import com.fictivestudios.ravebae.utils.Constants.Companion.getTime
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_comment.view.iv_user
import kotlinx.android.synthetic.main.item_comment.view.tv_name
import kotlinx.android.synthetic.main.item_followers.view.*


class CommentsAdapter(
    commentsList: ArrayList<GetCommentsData>?,
    requireContext: Context,
    onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CommentsAdapter.ProfileViewHolder>() {

    private var commentsList = commentsList
    private var context = requireContext
    private var onItemClickListener = onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{


            val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)

        return ProfileViewHolder(view)


    }




    override fun getItemCount() = commentsList?.size ?: 0

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {

        holder.itemView.iv_user.setOnClickListener {


            onItemClickListener.onItemClick(position,it, PROFILE)
        }

        commentsList?.get(position)?.let { holder.bindViews(it,holder.itemView,context) }
    }



     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        fun bindViews(model: GetCommentsData, itemView: View, context: Context) {

            itemView.tv_name.setText(model.user.name)
            itemView.tv_comment.setText(model.comment)
            itemView.tv_time.setText(model.created_at.getTime("yyyy-MM-dd'T'HH:ss:SSS","yyyy-MM-dd"))
            if (model.user.image.isNullOrEmpty())
            {
                itemView.iv_user.setImageResource(R.drawable.user_dp)
            }
            else
            {
               Picasso
                        .get().load(Constants.IMAGE_BASE_URL + model.user.image).into(itemView.iv_user)
            }

        }

    }
}