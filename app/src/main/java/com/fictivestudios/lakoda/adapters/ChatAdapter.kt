package com.fictivestudios.lakoda.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.model.receivedMessageData
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.IMAGE_BASE_URL
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
import io.github.ponnamkarthik.richlinkpreview.ViewListener
import kotlinx.android.synthetic.main.item_message_received.view.*
import kotlinx.android.synthetic.main.item_message_received.view.tv_text_received
import kotlinx.android.synthetic.main.item_message_sent.view.*
import java.lang.Exception


class ChatAdapter(messageList: ArrayList<receivedMessageData>, swipeReply: SwipeReply,) : RecyclerView.Adapter<ChatAdapter.ProfileViewHolder>() {

    private var messageList: ArrayList<receivedMessageData>? = messageList

    private var mSwipeReply:SwipeReply = swipeReply
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

    override fun onBindViewHolder(holder: ProfileViewHolder, @SuppressLint("RecyclerView") position: Int) {


        if (getItemViewType(position) == VIEW_TYPE_MESSAGE_RECEIVED)
        {


            holder.bindReceivedMessage(
                holder.itemView,
                messageList?.get(position),
                position,
                mSwipeReply,
                messageList
            )

        }


        else if  (getItemViewType(position) == VIEW_TYPE_MESSAGE_SENT)
            {

            holder.bindSentMessage(
                holder.itemView,
                messageList?.get(position),
                position,
                mSwipeReply,
                messageList
            )
        }


    }

     class ProfileViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)
    {
        fun bindReceivedMessage(
            itemView: View,
            model: receivedMessageData?,
            position: Int,
            mSwipeReply: SwipeReply,
            messageList: ArrayList<receivedMessageData>?,

            ) {


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


                    itemView.iv_video_received.visibility = View.VISIBLE

                    itemView.iv_video_received.setVideoURI((IMAGE_BASE_URL + model.message).toUri())

                    Log.d("videofile",IMAGE_BASE_URL + model.message)
                    itemView.iv_video_received.setOnPreparedListener { mp ->


                        itemView.iv_video_received.setOnCompletionListener {
                            itemView.iv_video_received.start()
                            // itemView.btn_play_sen.visibility = View.VISIBLE
                        }


                        /*       val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
                               val screenRatio =
                                   itemView.iv_video_received.width / itemView.iv_video_received.height.toFloat()
                               val scale = videoRatio / screenRatio
                               if (scale >= 1f) {
                                   itemView.iv_video_received.scaleX = scale
                               } else {
                                   itemView.iv_video_received.scaleY = 1f / scale
                               }
       */
                        itemView.iv_video_received.start()
                        /*mp.s*/
                    }
                }




            }
            else if ( model?.type == TYPE_TEXT)
            {
                itemView.tv_text_received.text = model?.message.toString()
                itemView.iv_media_received.visibility = View.GONE
                itemView.tv_text_received.visibility = View.VISIBLE
                itemView.tv_received_reply.visibility = View.GONE
                itemView.iv_video_received.visibility = View.GONE
            }
            else if ( model?.type == TYPE_LOCATION)
            {
                itemView.iv_media_received.visibility = View.GONE
                itemView.tv_text_received.visibility = View.VISIBLE
                itemView.tv_received_reply.visibility = View.GONE
                itemView.iv_video_received.visibility = View.GONE



                itemView.tv_text_received.setText(model?.message)

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

        fun bindSentMessage(
            itemView: View,
            model: receivedMessageData?,
            position: Int,
            mSwipeReply: SwipeReply,
            messageList: ArrayList<receivedMessageData>?
        ) {
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

                    itemView.iv_video_sent.setVideoURI((IMAGE_BASE_URL + model.message).toUri())

                    Log.d("videofile",IMAGE_BASE_URL + model.message)
                    itemView.iv_video_sent.setOnPreparedListener { mp ->


                        itemView.iv_video_sent.setOnCompletionListener {
                            itemView.iv_video_sent.start()
                            // itemView.btn_play_sen.visibility = View.VISIBLE
                        }


                 /*       val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
                        val screenRatio =
                            itemView.iv_video_sent.width / itemView.iv_video_sent.height.toFloat()
                        val scale = videoRatio / screenRatio
                        if (scale >= 1f) {
                            itemView.iv_video_sent.scaleX = scale
                        } else {
                            itemView.iv_video_sent.scaleY = 1f / scale
                        }
*/
                        itemView.iv_video_sent.start()
                        /*mp.s*/
                    }
                }




            }
            //set  text
            else if ( model?.type == TYPE_TEXT)
            {
                itemView.iv_media_sent.visibility = View.GONE
                itemView.tv_text_sent.visibility = View.VISIBLE
                itemView.tv_text_reply.visibility = View.GONE
                itemView.iv_video_sent.visibility = View.GONE

                itemView.tv_text_sent.text = model?.message.toString()

            }

            else if ( model?.type == TYPE_LOCATION)
            {
                itemView.iv_media_sent.visibility = View.GONE
                itemView.tv_text_sent.visibility = View.VISIBLE
                itemView.tv_text_reply.visibility = View.GONE
                itemView.iv_video_sent.visibility = View.GONE

              //  itemView.richLinkView.visibility = View.GONE
                itemView.tv_text_sent.setText(model?.message)

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