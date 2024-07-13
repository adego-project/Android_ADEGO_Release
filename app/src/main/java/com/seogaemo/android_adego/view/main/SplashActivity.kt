package com.seogaemo.android_adego.view.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.seogaemo.android_adego.R
import com.seogaemo.android_adego.database.SharedPreference
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.view.auth.LoginActivity
import com.seogaemo.android_adego.view.auth.ProfileNameActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (!SharedPreference.isFirst) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else if (TokenManager.refreshToken != "") {
                startActivity(Intent(this@SplashActivity, ProfileNameActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finishAffinity()
        }, 1500)

    }
}