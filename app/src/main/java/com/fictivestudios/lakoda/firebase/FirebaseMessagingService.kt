package com.fictivestudios.lakoda.firebase

import android.util.Log
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.ravebae.utils.Constants.Companion.FCM
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessaging : FirebaseMessagingService() {
     override fun onNewToken(token: String) {
         super.onNewToken(token)
         PreferenceUtils.init(this)
         PreferenceUtils.saveString(FCM,token)
         Log.d("fcmToken",token.toString())
     }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("onMessageReceived",message.toString())
    }
}