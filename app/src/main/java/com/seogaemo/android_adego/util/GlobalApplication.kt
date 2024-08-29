package com.seogaemo.android_adego.util

import android.app.Application
import com.seogaemo.android_adego.database.SharedPreference
import com.seogaemo.android_adego.database.TokenManager

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        SharedPreference.init(this)
    }
}