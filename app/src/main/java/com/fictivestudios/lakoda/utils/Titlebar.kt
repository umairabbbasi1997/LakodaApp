package com.fictivestudios.lakoda.utils

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.IMAGE_BASE_URL
import com.fictivestudios.ravebae.utils.Constants.Companion.getUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_home_post.view.*
import kotlinx.android.synthetic.main.titlebar.view.*


class Titlebar : RelativeLayout {

   private lateinit var mView:View

    constructor(context: Context) : super(context) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initLayout(context)
    }

    fun initLayout(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflater.inflate(R.layout.titlebar, this, true)
    }

    fun hideTitleBar() {
        resetTitlebar()
    }

    fun showTitleBar()
    {
        mView?.rlTitlebarMainLayout?.setVisibility(View.VISIBLE)
    }

    fun resetTitlebar() {
        mView?.rlTitlebarMainLayout?.setVisibility(View.GONE)
    }



    fun setHomeTitle(titleText:String)
    {
        showTitleBar()
        MainActivity.getMainActivity?.showBottomBar()
        mView?.tvTitle?.text = titleText

        mView?.btn_left.visibility=View.GONE
        btn_user_profile_left.visibility=View.VISIBLE
        btn_user_profile_right.visibility=View.INVISIBLE
        btn_setting.visibility= View.VISIBLE

     //   mView?.btn_user_profile_left.setImageResource(R.drawable.user_dp)
        Picasso
            .get()
            .load(Constants.IMAGE_BASE_URL +getUser()?.image)
            .placeholder(R.drawable.user_dp)
            .into(  mView?.btn_user_profile_left);

        mView?.btn_setting.setImageResource(R.drawable.icon_setting)

        mView?.btn_user_profile_left!!.setOnClickListener {

            MainActivity.getMainActivity?.navControllerMain
                ?.navigate(R.id.myProfileFragment)
        }
        mView?.btn_setting!!.setOnClickListener {

            MainActivity.getMainActivity?.navControllerMain
                ?.navigate(R.id.settingsFragment)
        }

        //MainActivity.getMainActivity?.showBottomBar()
    }

    fun setChatTitle(titleText:String,imageUrl:String,rightListener: OnClickListener?)
    {        showTitleBar()
        MainActivity.getMainActivity?.hideBottomBar()

        mView?.tvTitle?.text = titleText

        btn_user_profile_right.visibility=View.VISIBLE
        mView?.btn_left.visibility=View.VISIBLE
        btn_user_profile_left.visibility=View.INVISIBLE

        btn_setting.visibility= View.INVISIBLE


        if (!imageUrl.isNullOrEmpty() || !imageUrl.equals(""))
        {
            Picasso.get().load(IMAGE_BASE_URL+imageUrl).placeholder(R.drawable.user_dp).into(  mView?.btn_user_profile_right)
        }

        mView?.btn_left.setImageResource(R.drawable.icon_btn_back)




        mView?.btn_left!!.setOnClickListener {
            MainActivity.getMainActivity?.onBackPressed()
        }
        mView?.btn_user_profile_right!!.setOnClickListener(rightListener)

    }

    fun setBtnBack( titleText:String) {

        showTitleBar()
        mView?.tvTitle?.text = titleText

        mView?.btn_left.setImageResource(R.drawable.icon_btn_back)

        mView?.btn_left.visibility=View.VISIBLE
        mView?.btn_setting.visibility=View.INVISIBLE
        mView?.btn_user_profile_left.visibility=View.INVISIBLE
        mView?.btn_user_profile_right.visibility=View.INVISIBLE

        MainActivity.getMainActivity?.hideBottomBar()

//        MainActivity.getMainActivity?.hideBottomBar()

        mView?.btn_left!!.setOnClickListener {

            if (MainActivity.getMainActivity !=null)
            {
                MainActivity.getMainActivity?.onBackPressed()
            }
            else
            {
                RegisterationActivity.getRegActivity?.onBackPressed()
            }

        }
    }

    fun setCustomTitleBar(titleText:String,
                       leftListener: OnClickListener?,
                       leftDrawable: Int,
                       rightListener: OnClickListener?,
                       rightDrawable: Int,
                       isProfile:Boolean)
    {

        showTitleBar()
        MainActivity?.getMainActivity?.showBottomBar()

        mView?.tvTitle?.text = titleText

        if (isProfile)
        {   btn_user_profile_right.visibility=View.INVISIBLE
            mView?.btn_left.visibility=View.INVISIBLE
            btn_user_profile_left.visibility=View.VISIBLE
            btn_setting.visibility= View.VISIBLE
        }
        else
        {
            btn_user_profile_right.visibility=View.INVISIBLE
            mView?.btn_left.visibility=View.VISIBLE
            btn_user_profile_left.visibility=View.INVISIBLE
            btn_setting.visibility= View.VISIBLE
        }

        mView?.btn_setting!!.setImageResource(rightDrawable)
        mView?.btn_setting!!.setOnClickListener(rightListener)
        mView?.btn_left!!.setImageResource(leftDrawable)
        mView?.btn_left!!.setOnClickListener(leftListener)

    }


    fun setTitleOnly(titleText:String)
    {
        showTitleBar()

        mView?.tvTitle?.text = titleText

        mView?.btn_left.visibility=View.GONE
        btn_user_profile_left.visibility=View.GONE
        btn_user_profile_right.visibility=View.GONE
        btn_setting.visibility= View.GONE


    }
/*    fun makeTitleTransparent()
    {
        mView.rlTitlebarMainLayout.setBackgroundColor(resources.getColor(R.color.transparent))
    }*/
    /*
    fun setProfileBtn(titleText:String)
    {
        mView?.tvTitle?.text = titleText
        mView?.btn_left.setImageResource(R.drawable.back_arrow_icon)
        mView?.btn_profile.visibility=View.GONE
        mView?.btn_user_profile_left.visibility=View.VISIBLE

        mView?.btn_left!!.setOnClickListener {

            MainActivity.getMainActivity?.onBackPressed()
        }
        mView?.btn_user_profile_left!!.setOnClickListener {

            MainActivity.getMainActivity?.navController
                ?.navigate(R.id.userProfileFragment)
        }

        MainActivity.getMainActivity?.hideBottomBar()

    }*/
/*    fun setBtnBack( listener: OnClickListener?) {

            mView?.ivBack!!.visibility = View.VISIBLE
            mView?.ivProfile!!.visibility = View.GONE
           // mView?.ivBack!!.setImageResource(drawable)
            mView?.ivBack!!.setOnClickListener(listener)
    }

    fun setBtnProfile(*//*drawable: Int,*//* listener: OnClickListener?) {

            mView?.ivBack!!.visibility = View.GONE
            mView?.ivProfile!!.visibility = View.VISIBLE
           // mView?.ivProfile!!.setImageResource(drawable)
            mView?.ivProfile!!.setOnClickListener(listener)


    }

    fun setBtnRight(drawable: Int, listener: OnClickListener?) {
        mView?.iv_notification!!.visibility = View.VISIBLE
        mView?.ivProfileRight!!.visibility = View.GONE
        mView?.iv_notification!!.setImageResource(drawable)
        mView?.iv_notification!!.setOnClickListener(listener)

    }

    fun setBtnRightUser(drawable: Int, listener: OnClickListener?) {
        mView?.iv_notification!!.visibility = View.GONE
        mView?.ivProfileRight!!.visibility = View.VISIBLE
        mView?.ivProfileRight!!.setImageResource(drawable)
        mView?.ivProfileRight!!.setOnClickListener(listener)

    }


    fun setTitle(getActivityContext: MainActivity, title: String) {
        mView?.rlTitlebarMainLayout?.setVisibility(View.VISIBLE)
        mView?.tvTitle?.text = title
     *//*   mView?.ivBack?.visibility = View.GONE

        mView?.iv_notification?.visibility = View.VISIBLE*//*


    }*/



/*    fun setHideTitle() {
        resetTitlebar()
        mView?.rlTitlebarMainLayout?.setVisibility(View.VISIBLE)
        mView?.btnMenu?.visibility = View.INVISIBLE
    }*/
}