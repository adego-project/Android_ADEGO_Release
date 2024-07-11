package com.seogaemo.android_adego.view.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.seogaemo.android_adego.data.SignInRequest
import com.seogaemo.android_adego.data.SignInResponses
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.databinding.ActivityLoginBinding
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
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

        binding.kakaoLoginButton.setOnClickListener {
            kakaoLogin(this@LoginActivity)
        }

    }

    private fun kakaoLogin(context: Context) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(context, "카카오 로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                saveToken(context, "kakao", token.accessToken)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    saveToken(context, "kakao", token.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    private suspend fun postSignIn(context: Context, type: String, signInRequest: SignInRequest): SignInResponses? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = if (type == "kakao") retrofitAPI.kakaoSignIn(signInRequest) else retrofitAPI.googleSignIn(signInRequest)
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

    private fun saveToken(context: Context, type: String, token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val signInResponses = postSignIn(context, type, SignInRequest(token))
            if (signInResponses != null) {
                TokenManager.accessToken = signInResponses.accessToken
                TokenManager.refreshToken = signInResponses.refreshToken
            } else {
                Toast.makeText(context, "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
            withContext(Dispatchers.Main) { finishLogin(context) }
        }
    }

    private fun finishLogin(context: Context) {
        startActivity(Intent(context, MainActivity::class.java))
        finishAffinity()
    }
}