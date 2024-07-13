package com.seogaemo.android_adego.view.alarm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.seogaemo.android_adego.data.PlanResponse
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.databinding.ActivityAlarmBinding
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
import com.seogaemo.android_adego.util.Util
import com.seogaemo.android_adego.util.Util.fromDpToPx
import com.seogaemo.android_adego.view.auth.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val planResponse = getPlan(this@AlarmActivity)
            if (planResponse != null) {
                withContext(Dispatchers.Main) {
                    binding.friendsList.apply {
                        this.layoutManager = GridLayoutManager(this@AlarmActivity, 2)
                        this.adapter = AlarmAdapter(planResponse.users)
                        this.addItemDecoration(EdgeItemDecoration(2, 8f.fromDpToPx()))
                    }
                }
            }

        }

    }

    private suspend fun getPlan(context: Context): PlanResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getPlan("bearer ${TokenManager.accessToken}")
                if (response.isSuccessful) {
                    response.body()
                } else if (response.code() == 401) {
                    val getRefresh = Util.getRefresh()
                    if (getRefresh != null) {
                        TokenManager.refreshToken = getRefresh.refreshToken
                        TokenManager.accessToken = getRefresh.accessToken
                        getPlan(context)
                    } else if (response.code() == 404) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "약속이 없습니다", Toast.LENGTH_SHORT).show()
                        }
                        null
                    } else {
                        TokenManager.refreshToken = ""
                        TokenManager.accessToken = ""
                        startActivity(Intent(context, LoginActivity::class.java))
                        finishAffinity()
                        null
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "약속이 없습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "네트워크 에러", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

}