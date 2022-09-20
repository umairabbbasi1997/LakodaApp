package com.fictivestudios.lakoda.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.fictivestudios.imdfitness.activities.fragments.BaseFragment
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.base.BaseActivity
import com.fictivestudios.ravebae.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_registeration.*

class RegisterationActivity : BaseActivity() {

    lateinit var navControllerReg : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeration)

        getRegActivity = this

        navControllerReg = Navigation.findNavController(this, R.id.container_reg);

        if (PreferenceUtils.getString(Constants.IS_ID_CARD_VERIFIED).equals(Constants.STATUS_REJECTED) ||
            PreferenceUtils.getString(Constants.IS_ID_CARD_VERIFIED).equals(Constants.STATUS_NOT_VERIFIED))
        {
            //Toast.makeText(this, "Please Verify ID Card", Toast.LENGTH_SHORT).show()
            navControllerReg.navigate(R.id.idVerificationFragment)

        }
        else
        {
            navControllerReg.navigate(R.id.preLoginFragment)

        }

    }

    override fun setMainFrameLayoutID() {

        mainFrameLayoutID = R.id.container_reg
    }


    override fun onBackPressed() {

        if (navControllerReg?.currentDestination?.id == R.id.loginFragment)
        {
            navControllerReg.navigate(R.id.preLoginFragment)
        }
        else if (navControllerReg?.currentDestination?.id == R.id.preLoginFragment)
        {
            finish()
            System.exit(0)
        }
        else if (navControllerReg?.currentDestination?.id == R.id.idVerificationFragment)
        {
            finish()
            System.exit(0)
        }
        else
        {
            super.onBackPressed()
        }
    }

    fun getTitlebar(): Titlebar {
        return reg_title_bar
    }
    companion object{
        var getRegActivity: RegisterationActivity? =null
    }

}