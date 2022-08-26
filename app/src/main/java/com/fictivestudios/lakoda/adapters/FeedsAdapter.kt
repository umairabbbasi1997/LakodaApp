package com.fictivestudios.lakoda.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.HomePostData
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.VIEW_TYPE_POST
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_home_post.view.*
import kotlinx.android.synthetic.main.item_home_post.view.iv_heart
import kotlinx.android.synthetic.main.item_home_post.view.iv_post
import kotlinx.android.synthetic.main.item_home_post.view.iv_profile
import kotlinx.android.synthetic.main.item_home_post.view.iv_share
import kotlinx.android.synthetic.main.item_home_post.view.tv_comment
import kotlinx.android.synthetic.main.item_home_post.view.tv_like
import kotlinx.android.synthetic.main.item_home_post.view.tv_post_description
import kotlinx.android.synthetic.main.item_home_post.view.tv_username
import kotlinx.android.synthetic.main.item_shared_home_post.view.*
import java.util.*
import kotlin.collections.ArrayList


class FeedsAdapter(context: Context, post: List<HomePostData>?, onItemClickListener: OnItemClickListener)
    : RecyclerView.Adapter<FeedsAdapter.ProfileViewHolder>(), Filterable {

    var filteredList = ArrayList<HomePostData>()
    init {
        filteredList = post as ArrayList<HomePostData>
        notifyDataSetChanged()
    }

    private var postList: List<HomePostData>? = post
    private var context = context
    private var onItemClickListener = onItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{


        if (viewType == VIEW_TYPE_POST)
        {
            return ProfileViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_post, parent, false))
        }
        else
        {
            return ProfileViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_shared_home_post, parent, false))
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

        holder.itemView.iv_profile.setOnClickListener {

            onItemClickListener.onItemClick(position,it, Constants.PROFILE)

        }



        holder.itemView.tv_comment.setOnClickListener {

            onItemClickListener.onItemClick(position,it, Constants.COMMENTS)

        }
        holder.itemView.tv_like.setOnClickListener {

            if (postList?.get(position)?.is_liked == 0)
            {
                holder.itemView.iv_heart.setBackgroundResource(R.drawable.heart_icon)
                holder.itemView.tv_like.text = postList?.get(position)?.like_count?.minus(1) .toString()
            }
            else
            {

                holder.itemView.iv_heart.setBackgroundResource(R.drawable.heart_white_icon)
                holder.itemView.tv_like.text = postList?.get(position)?.like_count?.plus(1) .toString()
            }
            onItemClickListener.onItemClick(position,it, Constants.LIKES)

        }

        holder.itemView.iv_heart.setOnClickListener {

            if (postList?.get(position)?.is_liked == 0)
            {
                holder.itemView.iv_heart.setBackground(context.getDrawable(R.drawable.heart_icon))
            }
            else
            {
                holder.itemView.iv_heart.setBackground(context.getDrawable(R.drawable.heart_white_icon))
            }
            onItemClickListener.onItemClick(position,it, Constants.LIKES)

        }

        holder.itemView.tv_like.setOnClickListener {

            if (postList?.get(position)?.is_liked == 0)
            {
                holder.itemView.iv_heart.setBackgroundResource(R.drawable.heart_icon)
                holder.itemView.tv_like.text = postList?.get(position)?.like_count?.minus(1) .toString()
            }
            else
            {

                holder.itemView.iv_heart.setBackgroundResource(R.drawable.heart_white_icon)
                holder.itemView.tv_like.text = postList?.get(position)?.like_count?.plus(1) .toString()
            }
            onItemClickListener.onItemClick(position,it, Constants.LIKES)

        }

        holder.itemView.iv_heart.setOnClickListener {

            if (postList?.get(position)?.is_liked == 0)
            {
                holder.itemView.iv_heart.setBackground(context.getDrawable(R.drawable.heart_icon))
            }
            else
            {
                holder.itemView.iv_heart.setBackground(context.getDrawable(R.drawable.heart_white_icon))
            }
            onItemClickListener.onItemClick(position,it, Constants.LIKES)

        }



        holder.itemView.iv_share.setOnClickListener {

            onItemClickListener.onItemClick(position,it, Constants.TYPE_SHARE)

        }



        if ( postList?.get(position)?.is_post == VIEW_TYPE_POST)
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
        fun bind(position: Int, post: HomePostData?, itemView: View, context: Context) {

            itemView.tv_username.setText(post?.user?.name)
            itemView.tv_post_description .setText(post?.description)
            itemView.tv_like.setText(post?.like_count.toString())
            itemView.tv_comment.setText(post?.comment_count.toString()+" comment")


            if (post?.is_liked == 1)
            {
                itemView.iv_heart.setBackgroundResource(R.drawable.heart_icon)
            //    itemView.iv_heart.tint
            }
            else
            {
                itemView.iv_heart.setBackgroundResource(R.drawable.heart_white_icon)
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

        fun bindShared(position: Int, post: HomePostData?, itemView: View, context: Context)
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

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val charSearch = constraint.toString()
                if (charSearch== null || charSearch.isEmpty()) {
                    // filteredList = mList
                    filterResults.count = filteredList.size
                    filterResults.values = filteredList
                } else {
                    val resultList = ArrayList<HomePostData>()
                    for (row in filteredList) {
                        if (row.description .lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    filterResults.count = resultList.size
                    filterResults.values = resultList
                }
                return filterResults
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                postList = results?.values as ArrayList<HomePostData>
                notifyDataSetChanged()
            }
        }
    }
}