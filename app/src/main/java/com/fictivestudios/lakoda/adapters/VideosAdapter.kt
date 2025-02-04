package com.fictivestudios.lakoda.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.fictivestudios.lakoda.Interface.OnItemClickListener
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.apiManager.response.HomePostData
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.LIKED
import com.fictivestudios.ravebae.utils.Constants.Companion.STATUS_UNFOLLOWED
import com.fictivestudios.ravebae.utils.Constants.Companion.UNLIKED
import com.fictivestudios.ravebae.utils.Constants.Companion.getUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_home_post.view.*
import kotlinx.android.synthetic.main.item_video.view.*


class VideosAdapter(
    var videolist: ArrayList<HomePostData>,
    requireContext: Context,
    onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<VideosAdapter.VideoViewHolder>() {


    var mVideoItems =videolist
    private var context = requireContext
    private var onItemClickListener = onItemClickListener
    var pagePostion :Int? =-1


    private var videoView: VideoView?=null

    var isClick: Boolean = false
    var row_index =-1



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {


            var view =  VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,parent,false));


            return view


    }



    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {

        try {


            if (mVideoItems.size-1 != position)
            {

                if (!mVideoItems[position].video_image.isNullOrEmpty())

                {


                    holder.itemView.tv_like_count.setText(mVideoItems[position].like_count.toString())
                    holder.itemView.tv_comment_count.setText(mVideoItems[position].comment_count.toString())
                    holder.itemView.txtTitle.setText(mVideoItems[position].user.name)
                    holder.itemView.txtDesc.setText(mVideoItems[position].description)

                    if (mVideoItems[position].follow_status == STATUS_UNFOLLOWED)
                    {
                        holder.itemView.btn_follow.visibility = View.VISIBLE

                    }
                    else{
                        holder.itemView.btn_follow.visibility = View.GONE
                    }



                    if (getUser().id.equals(mVideoItems[position].user.id))
                    {
                        holder.itemView.btn_follow.visibility = View.INVISIBLE
                        holder.itemView.btn_share.visibility = View.INVISIBLE

                    }
                    else
                    {
                       // holder.itemView.btn_follow.visibility = View.VISIBLE
                        holder.itemView.btn_share.visibility = View.VISIBLE
                    }

                    if (mVideoItems[position]?.is_liked == 1)
                    {
                        holder.itemView.btn_heart_like.setTag(LIKED)
                        holder.itemView.btn_heart_like.setBackgroundResource(R.drawable.heart_icon)
                        //    itemView.iv_heart.tint
                    }
                    else
                    {
                        holder.itemView.btn_heart_like.setTag(UNLIKED)
                        holder.itemView.btn_heart_like.setBackgroundResource(R.drawable.heart_white_icon)
                    }


                    if (! mVideoItems[position].user.image.isNullOrEmpty())
                    {
                        Picasso.get().load(Constants.IMAGE_BASE_URL + mVideoItems[position].user.image).into(holder.itemView.iv_user)
                    }

                    if (! mVideoItems[position].video_image.isNullOrEmpty())
                    {




                        holder.itemView.videoView.setVideoURI((Constants.IMAGE_BASE_URL + mVideoItems[position].video_image).toUri())

                        holder.itemView.videoView.setOnPreparedListener { mp ->
                            holder.itemView.progressBar.visibility = View.GONE
                               holder.itemView.videoView.setOnCompletionListener {

                                   holder.itemView.btn_play.visibility = View.VISIBLE
                               }

                            holder.itemView.videoView.seekTo( 1 );
                            val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
                            val screenRatio =
                                holder.itemView.videoView.width / holder.itemView.videoView.height.toFloat()
                            val scale = videoRatio / screenRatio
                            if (scale >= 1f) {
                                holder.itemView.videoView.scaleX = scale
                            } else {
                                holder.itemView.videoView.scaleY = 1f / scale
                            }
                        }

                        /*          if (pagePostion == position)
                                  {

                                      holder.itemView.videoView.start()
                                      //holder.itemView.videoView.seekTo( 1 );
                                  }
                                  else
                                  {
                                      holder.itemView.videoView.stopPlayback()
                                  }*/
                    }




                }







                holder.itemView.lay_video.setOnClickListener {

                    if (holder.itemView.btn_play.visibility == View.VISIBLE )
                    {
                        holder.itemView.videoView.start()
                        holder.itemView.btn_play.visibility = View.GONE
                    }
                    else
                    {
                        holder.itemView.videoView.pause()
                        holder.itemView.btn_play.visibility = View.VISIBLE

                    }

                }



            }
            holder.itemView.btn_share.setOnClickListener {

                    onItemClickListener.onItemClick(position,it, Constants.TYPE_SHARE)

            }

            holder.itemView.btn_follow.setOnClickListener {
             //   onItemClickListener.onItemClick(position,it, Constants.FOLLOW)
                onItemClickListener.onItemClick(position,it, Constants.PROFILE)

            }


            holder.itemView.txtTitle.setOnClickListener {
                onItemClickListener.onItemClick(position,it, Constants.PROFILE)
            }

            holder.itemView.iv_user.setOnClickListener {
                onItemClickListener.onItemClick(position,it, Constants.PROFILE)
            }
            holder.itemView.btn_comment.setOnClickListener {


                if (mVideoItems[position].shared_by!=null)
                {
                    onItemClickListener.onItemClick(position,it, Constants.SHARER_COMMENTS)
                }
                else
                {
                    onItemClickListener.onItemClick(position,it, Constants.COMMENTS)
                }
            }

            holder.itemView.btn_heart_like.setOnClickListener {

                if (holder.itemView.btn_heart_like.tag == Constants.UNLIKED)

                //(mVideoItems?.get(position)?.is_liked == 0)
                {
                    var count = mVideoItems?.get(position)?.like_count
                    if (count != null) {
                        count += 1
                        holder.itemView.tv_like_count.text = count.toString()
                        mVideoItems?.get(position)?.like_count = count
                    }

                    holder.itemView.btn_heart_like.setTag(LIKED)
                    holder.itemView.btn_heart_like.setBackground(context.getDrawable(R.drawable.heart_icon))
                }
                else
                {
                    var count = mVideoItems?.get(position)?.like_count
                    if (count != null) {
                        count -= 1
                        holder.itemView.tv_like_count.text = count.toString()
                        mVideoItems?.get(position)?.like_count = count
                    }
                    holder.itemView.btn_heart_like.setTag(UNLIKED)
                    holder.itemView.btn_heart_like.setBackground(context.getDrawable(R.drawable.heart_white_icon))
                }


                if (mVideoItems[position].shared_by!=null)
                {
                    onItemClickListener.onItemClick(position,it, Constants.SHARER_LIKES)
                }
                else
                {
                    onItemClickListener.onItemClick(position,it, Constants.LIKES)
                }
            }


        }
        catch (e:Exception)
        {
            Log.d("Exception",e.localizedMessage)
        }

    }

    override fun getItemCount(): Int {
        return videolist.size
    }

    fun updatePosition(position: Int) {
        pagePostion = position
    }


    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        }


    }


