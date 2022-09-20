package com.fictivestudios.lakoda.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.os.bundleOf
import com.fictivestudios.docsvisor.apiManager.client.SessionManager
import com.fictivestudios.lakoda.Enum.FCMEnums

import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.ravebae.utils.Constants
import com.fictivestudios.ravebae.utils.Constants.Companion.IS_ID_CARD_VERIFIED
import com.fictivestudios.ravebae.utils.Constants.Companion.IS_PUSH
import com.fictivestudios.ravebae.utils.Constants.Companion.STATUS_NOT_VERIFIED
import com.fictivestudios.ravebae.utils.Constants.Companion.STATUS_REJECTED
import com.fictivestudios.ravebae.utils.Constants.Companion.STATUS_VERIFIED

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        PreferenceUtils.init(this)
        var sessionManager = SessionManager(this)

        Handler().postDelayed({

            if (!sessionManager.fetchAuthToken().isNullOrBlank() && PreferenceUtils.getString(IS_ID_CARD_VERIFIED).equals(STATUS_VERIFIED)) {

                if (intent?.extras?.getString("type") != null) {
                    IS_PUSH = true
                    Log.e("extras", "" + intent?.extras?.getString("title"))


                    if (intent?.extras?.getString("type").toString() == FCMEnums.MESSAGE.value)
                    {

                        var senderId = intent?.extras?.getString("sender_id").toString()
                        var senderName = intent?.extras?.getString("user_name").toString()
                        var type = intent?.extras?.getString("type").toString()


                        val intent = Intent(this,MainActivity::class.java)
                        intent.putExtra(Constants.USER_ID , senderId)
                        intent.putExtra(Constants.USER_NAME , senderName)
                        intent.putExtra(Constants.NOTIFICATION_TYPE , type)

                        startActivity(intent)
                        finish()
                    }
                    else
                    {

                        var type = Constants.NOTIFICATION_TYPE
                        val intent = Intent(this,MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra(Constants.NOTIFICATION_TYPE , type)
                        startActivity(intent)
                        finish()
                    }
                }
                else
                {
                    IS_PUSH = false
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }



                }


            else if (PreferenceUtils.getString(IS_ID_CARD_VERIFIED).equals(STATUS_NOT_VERIFIED) ||
                PreferenceUtils.getString(IS_ID_CARD_VERIFIED).equals(STATUS_REJECTED)
            )
            {
                startActivity(Intent(this, RegisterationActivity::class.java))
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