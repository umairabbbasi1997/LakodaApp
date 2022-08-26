package com.fictivestudios.lakoda.adapters

import android.content.Context
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
 
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.HomePostData
import com.fictivestudios.lakoda.apiManager.response.Post
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.COMMENTS
import com.fictivestudios.ravebae.utils.Constants.Companion.LIKES
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_friend_post.view.*
import kotlinx.android.synthetic.main.item_friend_post.view.iv_Comment
import kotlinx.android.synthetic.main.item_friend_post.view.iv_heart
import kotlinx.android.synthetic.main.item_friend_post.view.iv_post
import kotlinx.android.synthetic.main.item_friend_post.view.tv_like
import kotlinx.android.synthetic.main.item_shared_home_post.view.*


class MyProfileFeedsAdapter(context: Context, post: List<Post>?, onItemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<MyProfileFeedsAdapter.ProfileViewHolder>() {

    private var postList: List<Post>? = post
    private var context = context
    private var onItemClickListener = onItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{


        if (viewType == Constants.VIEW_TYPE_POST)
        {
            return ProfileViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_friend_post, parent, false)
            )
        }
        else
        {
            return ProfileViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_shared_home_post, parent, false)
            )
        }




    }




    override fun getItemCount() = postList?.size ?: 0

    override fun getItemViewType(position: Int): Int {


        return postList?.get(position)?.is_post!!

        /*    if (postList?.get(position)?.type  == TYPE_POST)
                return VIEW_TYPE_POST
                else
                {
                    return VIEW_TYPE_SHARED_POST
                }*/

    }


    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {

        holder.itemView.iv_heart.setOnClickListener {


            onItemClickListener.onItemClick(position,it,LIKES)
        }


        holder.itemView.iv_Comment.setOnClickListener {

            onItemClickListener.onItemClick(position,it,COMMENTS)


        }

/*
        holder.itemView.tv_comments.setOnClickListener {

            onItemClickListener.onItemClick(position,it,COMMENTS)


        }

*/

        if ( postList?.get(position)?.is_post == Constants.VIEW_TYPE_POST)
        {
            holder.bind(position, postList?.get(position),holder.itemView,context)
        }
        else
        {
            holder.bindShared(position, postList?.get(position),holder.itemView,context)

            holder.itemView.iv_sharer_profile.setOnClickListener {

                onItemClickListener.onItemClick(position,it, Constants.SHARER_PROFILE)
            }
        }

    }


     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        fun bind(position: Int, post: Post?, itemView: View, context: Context) {


            itemView.tv_like.setText(post?.like_count.toString())
            itemView.tv_comments.setText(post?.comment_count.toString() + " comments")



            if (post?.type == "post") {
                if (!post?.video_image.isNullOrBlank()) {
                    Picasso
                        .get()
                        .load(Constants.IMAGE_BASE_URL + post?.video_image)
                        //.placeholder(R.drawable.loading_spinner)
                        .into(itemView.iv_post);
                }
            }


        }
        fun bindShared(position: Int, post: Post?, itemView: View, context: Context)
        {
            itemView.tv_username.setText(post?.user?.name)
            itemView.tv_post_description .setText(post?.description)
            itemView.tv_like.setText(post?.like_count.toString())
            itemView.tv_comment.setText(post?.comment_count.toString()+" comment")
            itemView.tv_sharer_username.setText(post?.shared_by?.name)


            if (!post?.shared_by?.image.isNullOrBlank())
            {
                Picasso
                    .get()
                    .load(Constants.IMAGE_BASE_URL +post?.shared_by?.image)
                    //.placeholder(R.drawable.loading_spinner)
                    .into(itemView.iv_sharer_profile);

            }

            if (post?.type == "post")
            {
                if (!post?.video_image.isNullOrBlank())
                {
/*                   Picasso
                        .get()
                        .load(Constants.IMAGE_BASE_URL +post?.video_image)
                        //.placeholder(R.drawable.loading_spinner)
                        .into(itemView.iv_post);*/
                    Picasso
                        .get()
                        .load(Constants.IMAGE_BASE_URL +post?.video_image)
                        .into(itemView.iv_post);
                }
            }

            if (!post?.user?.image.isNullOrBlank())
            {
                Picasso
                    .get()
                    .load(Constants.IMAGE_BASE_URL +post?.user?.image)
                    //.placeholder(R.drawable.loading_spinner)
                    .into(itemView.iv_profile);

            }


        }

    }
}
