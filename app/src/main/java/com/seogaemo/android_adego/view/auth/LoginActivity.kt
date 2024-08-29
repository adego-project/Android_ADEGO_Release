package com.seogaemo.android_adego.view.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import com.seogaemo.android_adego.R
import com.seogaemo.android_adego.data.FCMRequest
import com.seogaemo.android_adego.data.SignInResponse
import com.seogaemo.android_adego.database.SharedPreference
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.databinding.ActivityLoginBinding
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
import com.seogaemo.android_adego.util.Util.getUser
import com.seogaemo.android_adego.util.Util.setFCMToken
import com.seogaemo.android_adego.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            saveToken(this@LoginActivity)
        }

    }

    private suspend fun postSignIn(context: Context): SignInResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.testSignIn()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    private fun saveToken(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val signInResponses = postSignIn(context)

            withContext(Dispatchers.Main) {
                if (signInResponses != null) {
                    TokenManager.accessToken = signInResponses.accessToken
                    TokenManager.refreshToken = signInResponses.refreshToken
                    finishLogin(context)
                } else {
                    Toast.makeText(context, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun finishLogin(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val isFirst = getUser(this@LoginActivity)?.name.isNullOrEmpty()
            val token = fetchFcmToken()

            val isSuccess = token?.let { setFCMToken(FCMRequest(token)) } != null

            withContext(Dispatchers.Main) {
                if (!isSuccess) {
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                    TokenManager.accessToken = ""
                    TokenManager.refreshToken = ""
                } else {
                    val nextActivity = if (isFirst) {
                        ProfileNameActivity::class.java
                    } else {
                        SharedPreference.isFirst = false
                        MainActivity::class.java
                    }
                    startActivity(Intent(context, nextActivity))
                    overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
                    finishAffinity()
                }
            }
        }
    }

    private suspend fun fetchFcmToken(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val task = FirebaseMessaging.getInstance().token
                val token = Tasks.await(task)
                token
            } catch (e: Exception) {
                null
            }
        }
    }

}