package com.fictivestudios.imdfitness.activities.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity
import com.fictivestudios.lakoda.views.activities.RegisterationActivity.Companion.getRegActivity

abstract class BaseFragment : Fragment() {

    var getActivityContext: MainActivity? = null
    var getRegActivityContext: RegisterationActivity? = null
  //  var getHomeContext: HomeActivity? = null
//    var getSplashContext: SplashActivity? = null
    //var preferenceHelper: BasePreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*
        setPreferenceHelper()
        setHomePreferenceHelper()*/
    }

    abstract fun setTitlebar(titlebar: Titlebar)

  //  abstract fun setHomeTitlebar(titlebar: TitlebarMain)

    fun getActivityContext(): Activity? {
        return getActivityContext
    }


 /*   fun setPreferenceHelper() {
        if (preferenceHelper == null) {
            preferenceHelper = BasePreferenceHelper(getActivityContext)
        }
    }

    fun setHomePreferenceHelper() {
        if (preferenceHelper == null) {
            preferenceHelper = BasePreferenceHelper(getHomeContext)
        }
    }
*/
//    fun setSplashPreferenceHelper() {
//        if (preferenceHelper == null) {
//            preferenceHelper = BasePreferenceHelper(getSplashContext)
//        }
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            val contex = context as MainActivity?
            if (contex != null)
                getActivityContext = context
        }
        else if(context is RegisterationActivity)
        {
            val contex = context as RegisterationActivity?
            if (contex != null)
                getRegActivityContext = context
        }
//        else
//        {
//            val contex = context as SplashActivity?
//            if (contex != null)
//                getSplashContext = context
//        }
    }

    override fun onResume() {
        super.onResume()
        if (getActivityContext != null) {
            setTitlebar(getActivityContext!!.getTitlebar())
        }
        else if (getRegActivityContext != null){
            setTitlebar(getRegActivityContext!!.getTitlebar())
        }
        }







}
