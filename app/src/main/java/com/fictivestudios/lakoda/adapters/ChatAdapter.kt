package com.fictivestudios.lakoda.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.model.ReceivedLastMessage
import com.fictivestudios.ravebae.utils.Constants.Companion.IMAGE_BASE_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.THUMB_BASE_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_DOCUMENT
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_IMAGE
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_LOCATION
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_TEXT
import com.fictivestudios.ravebae.utils.Constants.Companion.TYPE_VIDEO
import com.fictivestudios.ravebae.utils.Constants.Companion.VIEW_TYPE_MESSAGE_RECEIVED
import com.fictivestudios.ravebae.utils.Constants.Companion.VIEW_TYPE_MESSAGE_SENT
import com.fictivestudios.ravebae.utils.Constants.Companion.getTime
import com.fictivestudios.ravebae.utils.Constants.Companion.getUser
import com.squareup.picasso.Picasso
import com.zerobranch.layout.SwipeLayout
import com.zerobranch.layout.SwipeLayout.SwipeActionsListener
import global.msnthrp.staticmap.core.StaticMap
import global.msnthrp.staticmap.model.LatLngZoom
import global.msnthrp.staticmap.tile.TileEssential
import kotlinx.android.synthetic.main.item_message_received.view.*
import kotlinx.android.synthetic.main.item_message_sent.view.*


class ChatAdapter(
    messageList: ArrayList<ReceivedLastMessage>,
    swipeReply: SwipeReply,
    onItemClickListener: OnItemClickListener,
    context: Context,
    tileEssential: TileEssential?
) : RecyclerView.Adapter<ChatAdapter.ProfileViewHolder>() {

    private var messageList: ArrayList<ReceivedLastMessage>? = messageList
    private var context = context
    private var mSwipeReply:SwipeReply = swipeReply
    private var onItemClickListener = onItemClickListener
    private var tileEssential =tileEssential
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ProfileViewHolder{

        val view:View

        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED)
        {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
        }
        else
        {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
        }


        return ProfileViewHolder(view)


    }



    override fun getItemViewType(position: Int): Int {
         if (getUser().id.toString().equals(messageList?.get(position)?.sender_id.toString()))
        {
            return    VIEW_TYPE_MESSAGE_SENT
        } else {
             return VIEW_TYPE_MESSAGE_RECEIVED
        }
    }



    override fun getItemCount() = messageList?.size ?: 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ProfileViewHolder, @SuppressLint("RecyclerView") position: Int) {


        if (getItemViewType(position) == VIEW_TYPE_MESSAGE_RECEIVED)
        {


            holder.bindReceivedMessage(
                holder.itemView,
                messageList?.get(position),
                position,
                mSwipeReply,
                messageList,
                context,
                onItemClickListener,
                tileEssential
            )

        }


        else if  (getItemViewType(position) == VIEW_TYPE_MESSAGE_SENT)
            {

            holder.bindSentMessage(
                holder.itemView,
                messageList?.get(position),
                position,
                mSwipeReply,
                messageList,
                context,
                onItemClickListener,
                tileEssential
            )
        }


    }

     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) /*,OnMapReadyCallback*/
    {

       //private lateinit var map: GoogleMap
       //private var latLng:LatLng? = null

        @RequiresApi(Build.VERSION_CODES.M)
        fun bindReceivedMessage(
            itemView: View,
            model: ReceivedLastMessage?,
            position: Int,
            mSwipeReply: SwipeReply,
            messageList: ArrayList<ReceivedLastMessage>?,
            context: Context,
            onItemClickListener: OnItemClickListener,
            tileEssential: TileEssential?,

            ) {



            itemView.text_ll.setOnClickListener {

                if (model?.type == TYPE_IMAGE )
                {
                 onItemClickListener.onItemClick(position,it, TYPE_IMAGE)
                }
                if (model?.type == TYPE_VIDEO)
                {
                    onItemClickListener.onItemClick(position,it, TYPE_VIDEO)
                }
            }
            itemView.btn_play_r.setOnClickListener {
                onItemClickListener.onItemClick(position,it, TYPE_VIDEO)
            }




            itemView.swipe_layout_received.setOnActionsListener(object : SwipeActionsListener {
                override fun onOpen(direction: Int, isContinuous: Boolean) {
                    if (direction == SwipeLayout.RIGHT) {

                        itemView.swipe_layout_received.close(true)

                        mSwipeReply.onSwipe(position)

                    } else if (direction == SwipeLayout.LEFT) {
                        // was executed swipe to the left
                    }
                }

                override fun onClose() {
                    // the main view has returned to the default state
                }
            })


            if (model?.created_at != null)

            {
                itemView.tv_received_time.text = model?.created_at.toString().getTime("yyyy-MM-dd'T'HH:ss:SSS","HH:ss")
            }



            Log.d("type",model?.type.toString())
            if ( model?.type == TYPE_IMAGE)
            {
                itemView.iv_media_received.visibility = View.VISIBLE
                itemView.tv_text_received.visibility = View.GONE
                itemView.tv_received_reply.visibility = View.GONE
                itemView.iv_video_received.visibility = View.GONE
                itemView.btn_play_r.visibility = View.GONE
                itemView.map_view_rec.visibility = View.GONE
                itemView.text_ll.setBackgroundColor(context.resources.getColor( R.color.black))
                Picasso
                    .get()
                    .load(IMAGE_BASE_URL+model?.message)?.into(itemView.iv_media_received)



            }
            else if ( model?.type == TYPE_VIDEO)
            {


                if (!model?.message.isNullOrBlank())
                {
                    itemView.iv_media_received.visibility = View.GONE
                    itemView.tv_text_received.visibility = View.GONE
                    itemView.tv_received_reply.visibility = View.GONE
                    itemView.btn_play_r.visibility = View.VISIBLE
                    itemView.map_view_rec.visibility = View.GONE
                    itemView.text_ll.setBackgroundColor(context.resources.getColor( R.color.black))

                    itemView.iv_video_received.visibility = View.VISIBLE

                    Picasso
                        .get()
                        .load(THUMB_BASE_URL+model?.thumbnail)?.into(itemView.iv_video_received)
                    Log.d("videofile",IMAGE_BASE_URL + model.message)

                }




            }
            else if ( model?.type == TYPE_TEXT)
            {
                itemView.tv_text_received.text = model?.message.toString()
                itemView.iv_media_received.visibility = View.GONE
                itemView.tv_text_received.visibility = View.VISIBLE
                itemView.tv_received_reply.visibility = View.GONE
                itemView.iv_video_received.visibility = View.GONE
                itemView.btn_play_r.visibility = View.GONE
                itemView.map_view_rec.visibility = View.GONE
            }


            else if ( model?.type == TYPE_LOCATION) {
                itemView.iv_media_received.visibility = View.VISIBLE
                itemView.tv_text_received.visibility = View.GONE
                itemView.map_view_rec.visibility == View.GONE
                itemView.tv_received_reply.visibility = View.GONE
                itemView.iv_video_received.visibility = View.GONE
                itemView.btn_play_r.visibility = View.GONE

                itemView.text_ll.setBackgroundColor(context.resources.getColor( R.color.black))


                val latLngZoom = LatLngZoom(model?.message.toDouble(),model?.thumbnail.toDouble(),   zoom = 14)


                val pinIcon = context.getDrawable( R.drawable.ic_baseline_location_on_24)
                with(itemView) {
                    if (tileEssential != null) {
                        StaticMap.with(tileEssential)
                            .load(latLngZoom)
                            .pin(pinIcon)
                            .clearBeforeLoading(true)
                            .into(itemView.iv_media_received)
                    }
                }


              itemView.iv_media_received.setOnClickListener {

                    val url =
                        "https://www.google.com/maps/search/?api=1&query="+model?.message.toDouble()+","+ model?.thumbnail.toDouble()

                    val gmmIntentUri: Uri = Uri.parse(url)
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    }
                }




                /*               val latLngZoom = LatLngZoom(model?.message.toDouble(),model?.thumbnail.toDouble(),   zoom = 14)


                               val pinIcon = context.getDrawable( R.drawable.ic_baseline_location_on_24)
                               with(itemView) {
                                   if (tileEssential != null) {
                                       StaticMap.with(tileEssential)
                                           .load(latLngZoom)
                                           .pin(pinIcon)
                                           .clearBeforeLoading(true)
                                           .into( itemView.map_view_rec)
                                   }
                               }


                               itemView.map_view_rec.setOnClickListener {

                                   val url =
                                       "https://www.google.com/maps/search/?api=1&query="+model?.message.toDouble()+","+ model?.thumbnail.toDouble()

                                   val gmmIntentUri: Uri = Uri.parse(url)
                                   val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                   mapIntent.setPackage("com.google.android.apps.maps")
                                   if (mapIntent.resolveActivity(context.packageManager) != null) {
                                       context.startActivity(mapIntent)
                                   }
                               }
               */
            }


            else if (model?.type?.contains("replyId") == true)
            {

                var replyId = model?.type.filter { it.isDigit() }

                var data = messageList?.find { it.id == replyId.toInt() }

                itemView.tv_received_reply.setText("Replied To: "+data?.message)

                itemView.iv_media_received.visibility = View.GONE
                itemView.tv_text_received.visibility = View.VISIBLE
                itemView.tv_received_reply.visibility = View.VISIBLE
                itemView.iv_video_received.visibility = View.GONE
                itemView.btn_play_r.visibility = View.GONE
                itemView.map_view_rec.visibility = View.GONE
                itemView.text_ll.setBackgroundColor(context.resources.getColor( R.color.black))
                itemView.tv_text_received.text = model?.message.toString()

            }


            if (model?.image?.isNullOrBlank() == true)
            {

                itemView.iv_user_rec.setBackgroundResource(R.drawable.user_dp)
            }
            else
            {
                Picasso
                    .get()
                    .load(IMAGE_BASE_URL+model?.image)?.placeholder(R.drawable.user_dp)?.into(itemView.iv_user_rec)
            }


        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun bindSentMessage(
            itemView: View,
            model: ReceivedLastMessage?,
            position: Int,
            mSwipeReply: SwipeReply,
            messageList: ArrayList<ReceivedLastMessage>?,
            context: Context,
            onItemClickListener: OnItemClickListener,
            tileEssential: TileEssential?
        ) {





            itemView.text_layout.setOnClickListener {

                if (model?.type == TYPE_IMAGE )
                {
                    onItemClickListener.onItemClick(position,it, TYPE_IMAGE)
                }
                if (model?.type == TYPE_VIDEO)
                {
                    onItemClickListener.onItemClick(position,it, TYPE_VIDEO)
                }
            }

            itemView.btn_play.setOnClickListener {
                onItemClickListener.onItemClick(position,it, TYPE_VIDEO)
            }

            itemView.swipe_layout_sent.setOnActionsListener(object : SwipeActionsListener {
                override fun onOpen(direction: Int, isContinuous: Boolean) {
                    if (direction == SwipeLayout.RIGHT) {
                        // was executed swipe to the right
                        itemView.swipe_layout_sent.close(true)
                        mSwipeReply.onSwipe(position)

                    } else if (direction == SwipeLayout.LEFT) {
                        // was executed swipe to the left
                    }
                }

                override fun onClose() {
                    // the main view has returned to the default state
                }
            })

            // set time
            if (model?.created_at != null)
            {
                itemView.tv_sent_time.text = model?.created_at.toString().getTime("yyyy-MM-dd'T'HH:ss:SSS","HH:ss")/*getTime("yyyy-MM-dd'T'HH:ss:SSS","HH:ss a")*/
            }



            if (model?.read_at != null)
            {
                itemView.iv_last_seen.visibility = View.VISIBLE
            }
            else{
                itemView.iv_last_seen.visibility = View.GONE
            }

            //set attachment image
            Log.d("type",model?.type.toString())
            if ( model?.type == TYPE_IMAGE)
            {


                if (!model?.message.isNullOrBlank())
                {
                    itemView.iv_media_sent.visibility = View.VISIBLE
                    itemView.tv_text_sent.visibility = View.GONE
                    itemView.tv_text_reply.visibility = View.GONE
                    itemView.iv_video_sent.visibility = View.GONE
                    itemView.btn_play.visibility = View.GONE
                    itemView.map_view.visibility = View.GONE
                    itemView.text_layout.setBackgroundColor(context.resources.getColor( R.color.black))
                    Picasso
                        .get()
                        .load(IMAGE_BASE_URL+model?.message)?.into(itemView.iv_media_sent)
                }


            }
            else if ( model?.type == TYPE_VIDEO)
            {


                if (!model?.message.isNullOrBlank())
                {
                    itemView.iv_media_sent.visibility = View.GONE
                    itemView.tv_text_sent.visibility = View.GONE
                    itemView.tv_text_reply.visibility = View.GONE
                    itemView.iv_video_sent.visibility = View.VISIBLE
                    itemView.map_view.visibility = View.GONE
                    itemView.text_layout.setBackgroundColor(context.resources.getColor( R.color.black))
                    itemView.btn_play.visibility = View.VISIBLE

                    Log.d("videofile",IMAGE_BASE_URL + model.message)

                    Picasso
                        .get()
                        .load(THUMB_BASE_URL+model?.thumbnail)?.into(itemView.iv_video_sent)

                }




            }
            //set  text
            else if ( model?.type == TYPE_TEXT)
            {
                itemView.iv_media_sent.visibility = View.GONE
                itemView.tv_text_sent.visibility = View.VISIBLE
                itemView.tv_text_reply.visibility = View.GONE
                itemView.iv_video_sent.visibility = View.GONE
                itemView.btn_play.visibility = View.GONE
                itemView.map_view.visibility = View.GONE

                itemView.tv_text_sent.text = model?.message?.toString()

            }

            else if ( model?.type == TYPE_LOCATION)
            {
                itemView.iv_media_sent.visibility = View.GONE
                itemView.tv_text_sent.visibility = View.GONE
                itemView.tv_text_reply.visibility = View.GONE
                itemView.iv_video_sent.visibility = View.GONE
                itemView.btn_play.visibility = View.GONE
                itemView.map_view.visibility = View.VISIBLE
              //  itemView.richLinkView.visibility = View.GONE
                itemView.text_layout.setBackgroundColor(context.resources.getColor( R.color.black))


              //  itemView.tv_text_sent.setTextColor(context.resources.getColor(R.color.fb_color))

                val latLngZoom = LatLngZoom(model?.message.toDouble(),model?.thumbnail.toDouble(),   zoom = 14)


                val pinIcon = context.getDrawable( R.drawable.ic_baseline_location_on_24)
                with(itemView) {
                    if (tileEssential != null) {
                        StaticMap.with(tileEssential)
                            .load(latLngZoom)
                            .pin(pinIcon)
                            .clearBeforeLoading(true)
                            .into( itemView.map_view)
                    }
                }


             itemView.map_view.setOnClickListener {

                val url =
                    "https://www.google.com/maps/search/?api=1&query="+model?.message.toDouble()+","+ model?.thumbnail.toDouble()

                    val gmmIntentUri: Uri = Uri.parse(url)
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    }
                }

            }

            else if ( model?.type == TYPE_DOCUMENT)
            {
                itemView.iv_media_sent.visibility = View.GONE
                itemView.tv_text_sent.visibility = View.VISIBLE
                itemView.tv_text_reply.visibility = View.GONE
                itemView.iv_video_sent.visibility = View.GONE

                //itemView.richLinkView.visibility = View.GONE
                itemView.tv_text_sent.setText(model?.message)

            }

            else if (model?.type?.contains("replyId") == true)
            {

                var replyId = model?.type.filter { it.isDigit() }

                var data = messageList?.find { it.id == replyId.toInt() }

                itemView.tv_text_reply.setText("Replied To: "+data?.message)

                itemView.iv_media_sent.visibility = View.GONE
                itemView.tv_text_sent.visibility = View.VISIBLE
                itemView.tv_text_reply.visibility = View.VISIBLE
                itemView.iv_video_sent.visibility = View.GONE
                itemView.btn_play.visibility = View.GONE
                itemView.map_view.visibility = View.GONE

                itemView.tv_text_sent.text = model?.message.toString()

            }


            //set profile
            if (model?.image?.isNullOrBlank() == true)
            {
                itemView.iv_user_sent.setBackgroundResource(R.drawable.user_dp)
            }
            else
            {
                Picasso
                    .get()
                    .load(IMAGE_BASE_URL + model?.image)?.placeholder(R.drawable.user_dp)?.into(itemView.iv_user_sent)
            }
        }


    }


}

interface SwipeReply
{
    fun onSwipe(position: Int)
}