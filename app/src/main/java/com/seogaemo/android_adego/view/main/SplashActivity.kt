package com.seogaemo.android_adego.view.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.seogaemo.android_adego.R
import com.seogaemo.android_adego.database.SharedPreference
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.view.auth.LoginActivity
import com.seogaemo.android_adego.view.auth.ProfileNameActivity
import com.seogaemo.android_adego.view.invite.InviteActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (TokenManager.refreshToken != "") {
                if (!SharedPreference.isFirst) {
                    handleIntent(intent)
                } else {
                    startActivity(Intent(this@SplashActivity, ProfileNameActivity::class.java))
                }
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
            finishAffinity()
        }, 1500)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (TokenManager.refreshToken != "") {
            if (!SharedPreference.isFirst) {
                handleIntent(intent)
            } else {
                startActivity(Intent(this@SplashActivity, ProfileNameActivity::class.java))
            }
        } else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }
    }

    private fun handleIntent(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.scheme == "adego-by-seogaemo" && data.host == "invite") {
            val inviteId = data.getQueryParameter("id")
            if (inviteId != null) {
                val inviteIntent = Intent(this, InviteActivity::class.java).apply {
                    putExtra("inviteId", inviteId)
                }
                startActivity(inviteIntent)
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}