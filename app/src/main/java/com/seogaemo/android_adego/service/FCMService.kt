package com.seogaemo.android_adego.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.seogaemo.android_adego.R
import com.seogaemo.android_adego.data.FCMRequest
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.util.Util.setFCMToken
import com.seogaemo.android_adego.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        if (TokenManager.refreshToken != "") {
            CoroutineScope(Dispatchers.Main).launch {
                val response = setFCMToken(FCMRequest(token))
                if (response == null) {
                    val loginIntent = Intent("ACTION_LOGIN_REQUIRED")
                    LocalBroadcastManager.getInstance(this@FCMService).sendBroadcast(loginIntent)
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            val title = it.title.toString()
            val body = it.body.toString()
            sendNotification(title, body)
        }

        remoteMessage.data.isNotEmpty().let {
            sendNotification("친구분이 알림을 울렸어요!", "빨리 약속에 참석해주세요!")
        }
    }

    private fun sendNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "adego_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.image_logo)
            .setColor(resources.getColor(R.color.black))
            .setBadgeIconType(R.drawable.image_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "ADEGO",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(0, notificationBuilder.build())
    }


}