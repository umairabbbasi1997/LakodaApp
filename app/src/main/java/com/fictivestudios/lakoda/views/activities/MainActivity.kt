package com.fictivestudios.lakoda.views.activities

import android.database.DatabaseUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.databinding.ActivityMainBinding

import com.fictivestudios.lakoda.utils.Titlebar
import com.fictivestudios.lakoda.views.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {


    lateinit var navControllerMain : NavController
    private lateinit var mainBinding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*        setContentView(R.layout.activity_main)*/

        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setContentView(mainBinding.root)


        getMainActivity = this
        navControllerMain = Navigation.findNavController(this, R.id.container);

        navControllerMain.navigate(R.id.videoViewFragment)


        btn_create_post.setOnClickListener {
            navControllerMain.navigate(R.id.createPostFragment)
        }

        bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    // handle click
                    navControllerMain.navigate(R.id.videoViewFragment)

                    true }
                R.id.feeds -> {
                    // handle click

                    navControllerMain.navigate(R.id.feedsFragment)
                    true }
                R.id.notify -> {
                    // handle click

                    navControllerMain.navigate(R.id.notificationFragment)
                    true }
                R.id.chat -> {
                    // handle click

                    navControllerMain.navigate(R.id.messagesFragment)
                    true }

                else -> false
            }
        }
    }

    fun hideBottomBar()
    {
        bottomBar.visibility = View.GONE
        btn_create_post.visibility = View.GONE
    }

    fun showBottomBar()
    {
        bottomBar.visibility = View.VISIBLE
        btn_create_post.visibility = View.VISIBLE
    }
    override fun setMainFrameLayoutID() {

    }

    fun getTitlebar(): Titlebar {
        return main_title_bar
    }
    companion object{
        var getMainActivity: MainActivity? =null
    }

    override fun onBackPressed() {
         if (navControllerMain?.currentDestination?.id == R.id.videoViewFragment                                                )
        {
            finish()
            System.exit(0)
        }


        else{
            super.onBackPressed()
        }
    /*   else if (navControllerMain?.currentDestination?.id == R.id.videoViewFragment )
        {
            finish()
            System.exit(0)
        }
         else if (navControllerMain?.currentDestination?.id == R.id.notificationFragment )
         {
             finish()
             System.exit(0)
         }*/



    }
}