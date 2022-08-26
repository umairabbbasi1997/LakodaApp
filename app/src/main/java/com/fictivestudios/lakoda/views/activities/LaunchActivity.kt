package com.fictivestudios.lakoda.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.fictivestudios.docsvisor.apiManager.client.SessionManager

import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.PreferenceUtils

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        PreferenceUtils.init(this)
        var sessionManager = SessionManager(this)

        Handler().postDelayed({

            if (!sessionManager.fetchAuthToken().isNullOrBlank())
            {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            else
            {
                startActivity(Intent(this, RegisterationActivity::class.java))
                finish()
            }


        }, 3000)

    }
}