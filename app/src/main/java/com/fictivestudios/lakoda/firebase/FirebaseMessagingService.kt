package com.fictivestudios.lakoda.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.fictivestudios.lakoda.R
import com.fictivestudios.lakoda.utils.PreferenceUtils
import com.fictivestudios.lakoda.views.activities.MainActivity
import com.fictivestudios.ravebae.utils.Constants
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
        Log.d("onMessageReceived",message.data.toString())

        if (message.data.get("type")?.equals("message") == true)
        {

            var type = message?.data?.get("type")
            var body =  message?.data?.get("body")
            var senderId =   message?.data?.get("sender_id")
            var senderName =   message?.data?.get("user_name")


            if (type != null && body != null && senderId != null && senderName != null) {
                sendChatNotification(
                    type,
                    body,
                    senderId,
                    senderName
                )
            }
        }
        else
        {
            var type = message?.data?.get("type")
            var body =  message?.data?.get("body")


            if (type != null && body != null ) {
                sendNotification(
                    type,
                    body
                )
            }
        }
    }


    private fun getPendingIntent(destinationId: Int, bundle: Bundle? = null): PendingIntent =
        NavDeepLinkBuilder(applicationContext)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(destinationId)
            .setArguments(bundle)
            .createPendingIntent()


    private fun sendChatNotification(type:String,body:String, senderId:String,senderName:String)
    {



        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra(Constants.USER_ID , senderId)
        intent.putExtra(Constants.USER_NAME , senderName)
        intent.putExtra(Constants.NOTIFICATION_TYPE , type)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


        var pendingIntent = getActivityPendingIntent(applicationContext,1,intent,PendingIntent.FLAG_ONE_SHOT)
            //getPendingIntent(destinationId = R.id.chatFragment,bundle)

        notification(pendingIntent,type,body)

    }

    private fun sendNotification(type:String,body:String) {



        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra(Constants.NOTIFICATION_TYPE , type)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        var pendingIntent = getActivityPendingIntent(applicationContext,1,intent,PendingIntent.FLAG_ONE_SHOT)


//        var pendingIntent: PendingIntent? = null

        /*     pendingIntent = when(type){
                 FCMEnums.LIKE .value -> getPendingIntent(destinationId = R.id.notificationFragment)
                 FCMEnums.COMMENT .value -> getPendingIntent(destinationId = R.id.notificationFragment)
                 FCMEnums.FOLLOW .value -> getPendingIntent(destinationId = R.id.notificationFragment)
                 FCMEnums.FOLLOW_REQUEST .value -> getPendingIntent(destinationId = R.id.notificationFragment)
                 else -> null
             }*/

        if (pendingIntent != null) {
            notification(pendingIntent,type,body)
        }
    }

    private fun notification(pendingIntent: PendingIntent, title: String, body: String)
    {

        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, packageName)
            .setSmallIcon(R.mipmap.ic_app_icon)
            .setContentTitle("Lakoda" ?: "title")
            .setContentText(body ?: "body")
            .setAutoCancel(true)
            .setSound(alarmSound)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

//            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.not))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(packageName, title, NotificationManager.IMPORTANCE_HIGH).apply {
                description = body
                enableVibration(true)
                setShowBadge(true)
                /*   val audioAttributes = AudioAttributes.Builder()
                       .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                       .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                       .build()
                   setSound(alarmSound,audioAttributes)*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setAllowBubbles(true)
                }
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    fun getActivityPendingIntent(context: Context?, id: Int, intent: Intent?, flag: Int): PendingIntent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(context,id, intent, flag)
        }
    }
}