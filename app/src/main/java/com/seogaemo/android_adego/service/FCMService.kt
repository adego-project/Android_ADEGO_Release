package com.seogaemo.android_adego.service

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.seogaemo.android_adego.data.FCMRequest
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.util.Util.setFCMToken
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
}