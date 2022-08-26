package com.fictivestudios.lakoda.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
 
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.GetStoryData
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.fragments.FeedsFragment
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.CREATE_STORY
import com.fictivestudios.ravebae.utils.Constants.Companion.VIEW_STORY
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_home_post.view.*
import kotlinx.android.synthetic.main.item_story.view.*
import kotlinx.android.synthetic.main.item_story.view.card_post
import kotlinx.android.synthetic.main.item_story.view.iv_post


class StoriesAdapter(
    context: Context,
    storylist: ArrayList<GetStoryData>?,
    onItemClickListener: OnItemClickListener
)  : RecyclerView.Adapter<StoriesAdapter.ProfileViewHolder>() {

    private var storyList=storylist
    private var onItemClickListener = onItemClickListener
    private var context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{


            val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)

        return ProfileViewHolder(view)


    }




    //override fun getItemCount(): Int = storyList?.size ?:  1

    override fun getItemCount(): Int {
        if (storyList?.size == 0)
        {
            return 1
        }
        else
        {

            return storyList?.size?.plus(1) ?: 1
        }
    }
    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {


        if (position==0)
        {
            holder.itemView.iv_post.setImageResource(R.drawable.add_story)
            holder.itemView.tv_name.text = "Create Story"
        }
        else
        {
            storyList?.get(position-1)?.let {
                holder.bindViews(it,holder.itemView,context)
            }

        }
        holder.itemView.card_post.setOnClickListener {
            if (position==0)
            {

                onItemClickListener.onItemClick(position,it,CREATE_STORY)

            }
            else
            {

                onItemClickListener.onItemClick(position,it, VIEW_STORY)
            }

        }



    }







     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        fun bindViews(getStoryData: GetStoryData, itemView: View, context: Context) {


            itemView.tv_name.setText(getStoryData.user.name)


            if (!getStoryData?.image.isNullOrBlank())
            {
               Picasso
                        .get()
                    .load(Constants.IMAGE_BASE_URL +getStoryData?.image)
                    //.placeholder(R.drawable.loading_spinner)
                    .into(itemView.iv_post);
            }

        }

    }
}