package com.seogaemo.android_adego.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.seogaemo.android_adego.BuildConfig
import com.seogaemo.android_adego.database.SharedPreference
import com.seogaemo.android_adego.database.TokenManager

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
        SharedPreference.init(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
    }
}