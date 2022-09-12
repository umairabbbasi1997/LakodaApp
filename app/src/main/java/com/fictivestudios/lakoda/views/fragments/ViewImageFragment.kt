package com.fictivestudios.lakoda.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.ravebae.utils.Constants
import com.igreenwood.loupe.Loupe
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_view_image.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewImageFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var mView: View

    private var mediaLink:String?=null
    private var mediaType:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun setTitlebar(titlebar: Titlebar) {
        titlebar.setBtnBack("VIEW MEDIA")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_view_image, container, false)

        mediaLink = arguments?.getString(Constants.IMAGE).toString()
        mediaType = arguments?.getString(Constants.TYPE_TEXT).toString()

        if (mediaType != null && !mediaType.equals("null") )
        {
            if (mediaType == Constants.TYPE_IMAGE)
            {
                mView.container.visibility = View.VISIBLE
                mView.videoView.visibility = View.GONE
                mView.btn_play.visibility = View.GONE

                Picasso
                    .get()
                    .load(Constants.IMAGE_BASE_URL +mediaLink)
                    .placeholder(R.color.black)
                    ?.into( mView.image)


                val loupe = Loupe.create(mView.image, mView.container) { // imageView is your ImageView
                    onViewTranslateListener = object : Loupe.OnViewTranslateListener {

                        override fun onStart(view: ImageView) {
                            // called when the view starts moving
                        }

                        override fun onViewTranslate(view: ImageView, amount: Float) {
                            // called whenever the view position changed
                        }

                        override fun onRestore(view: ImageView) {
                            // called when the view drag gesture ended
                        }

                        override fun onDismiss(view: ImageView) {
                            // called when the view drag gesture ended
                            activity?.onBackPressed()
                        }
                    }
                }
            }
            else if (mediaType == Constants.TYPE_VIDEO)
            {
                mView.container.visibility = View.GONE
                mView.videoView.visibility = View.VISIBLE
                mView.pb_vid.visibility = View.VISIBLE
             //   mView.btn_play.visibility = View.VISIBLE

                mView.videoView.setVideoURI((Constants.IMAGE_BASE_URL + mediaLink).toUri())


                val mediaController = MediaController(requireContext())
                mediaController.setAnchorView( mView.videoView)
                mView.videoView.setMediaController(mediaController)

                mView.videoView.setOnPreparedListener { mp ->
                    mView.videoView.seekTo( 1 );
                    mView.pb_vid.visibility = View.GONE
/*                    mView.videoView.setOnCompletionListener {

                      //  mView.btn_play.visibility = View.VISIBLE
                    }*/


           /*         val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
                    val screenRatio =
                        mView.videoView.width / mView.videoView.height.toFloat()
                    val scale = videoRatio / screenRatio
                    if (scale >= 1f) {
                        mView.videoView.scaleX = scale
                    } else {
                        mView.videoView.scaleY = 1f / scale
                    }*/
                }

              /*  mView.btn_play.setOnClickListener {

                    if (mView.btn_play.visibility == View.VISIBLE)
                    {
                        mView.videoView.start()
                        mView.btn_play.visibility = View.GONE
                    }
                    else{
                        mView.btn_play.visibility = View.VISIBLE
                        mView.videoView.pause()
                    }

                }*/

            }

        }

        return mView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}