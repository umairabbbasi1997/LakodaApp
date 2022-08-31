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
import com.fictivestudios.lakoda.apiManager.response.GetChatListData
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.ravebae.utils.Constants
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_messages.view.*
import java.util.*
import kotlin.collections.ArrayList


class MessagesAdapter(
    requireContext: Context,
    chatList: ArrayList<GetChatListData>,
    onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MessagesAdapter.ProfileViewHolder>(), Filterable {

    private var chatList = chatList
    private var onItemClickListener = onItemClickListener
    private  var context = requireContext


    var filteredList = ArrayList<GetChatListData>()
    init {
        filteredList = chatList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{


            val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_messages, parent, false)

        return ProfileViewHolder(view)


    }




    override fun getItemCount() = chatList?.size ?: 0

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {

        holder.itemView.setOnClickListener {

            onItemClickListener.onItemClick(position,it,"")


        }
        holder.bindViews(chatList[position],context,holder.itemView)

    }



     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        fun bindViews(model: GetChatListData, context: Context, itemView: View) {


            itemView.tv_name.setText(model.user.name)
            itemView.tv_message.setText(model.last_message)
            //itemView.tv_date.setText(model.)

            if (model.user.image.isNullOrEmpty())
            {
                itemView.iv_post.setImageResource(R.drawable.user_dp)
            }
            else
            {
                Picasso
                    .get().load(Constants.IMAGE_BASE_URL + model.user.image).into(itemView.iv_post)
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
                    val resultList = ArrayList<GetChatListData>()
                    for (row in filteredList) {
                        if (row.user.name.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))) {
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
                chatList = results?.values as ArrayList<GetChatListData>
                notifyDataSetChanged()
            }
        }
    }
}