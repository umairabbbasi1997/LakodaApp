package com.fictivestudios.lakoda.views.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.utils.PreferenceUtils


abstract class BaseActivity : AppCompatActivity() {

    var baseFragment: BaseFragment? = null
    var mainFrameLayoutID: Int = 0
  //  var preferenceHelper: BasePreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //   setContentView(R.layout.activity_base)

       // preferenceHelper = BasePreferenceHelper(this)

        // all cions

/*        GlobalScope.launch { Dispatchers.Default.apply {

            addDataToList()
        }*/

        FacebookSdk.sdkInitialize(getApplicationContext());

        PreferenceUtils.init(this)

        }




    abstract fun setMainFrameLayoutID()



    fun replaceFragment(
        frag: BaseFragment,
        tag: String,
        isAddToBackStack: Boolean,
        animate: Boolean
    ) {

        baseFragment = frag
        val transaction = supportFragmentManager.beginTransaction()

        if (animate) {
/*//            transaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out)
            transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )*/
        }
        transaction.replace(mainFrameLayoutID, frag)

        if (isAddToBackStack) {
            transaction.addToBackStack(null).commit()
        } else {
            transaction.commitAllowingStateLoss()
        }

    }

    fun AddFragment(frag: BaseFragment, tag: String, isAddToBackStack: Boolean, animate: Boolean) {

        baseFragment = frag
        val transaction = supportFragmentManager.beginTransaction()

        if (animate) {
/*//            transaction.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out)
            transaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )*/
        }
        transaction.add(mainFrameLayoutID, frag)

        if (isAddToBackStack) {
            transaction.addToBackStack(null).commit()
        } else {
            transaction.commitAllowingStateLoss()
        }
    }
}
