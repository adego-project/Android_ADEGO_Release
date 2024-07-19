package com.seogaemo.android_adego.view.invite

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.seogaemo.android_adego.data.PlanInviteResponse
import com.seogaemo.android_adego.data.PlanResponse
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.databinding.ActivityInviteBinding
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
import com.seogaemo.android_adego.util.Util.getPlan
import com.seogaemo.android_adego.util.Util.getRefresh
import com.seogaemo.android_adego.util.Util.getUser
import com.seogaemo.android_adego.util.Util.parseDateTime
import com.seogaemo.android_adego.view.auth.LoginActivity
import com.seogaemo.android_adego.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InviteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInviteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInviteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inviteId = intent.getStringExtra("inviteId").toString()
        CoroutineScope(Dispatchers.IO).launch {
            val isJoinedPlan = getPlan(this@InviteActivity) != null

            getPlanInfo(inviteId)?.let { plan ->
                withContext(Dispatchers.Main) {
                    with(plan.plan) {
                        binding.nameText.text = name

                        val (date, time) = parseDateTime(date)
                        binding.dateText.text = date
                        binding.timeText.text = time
                        binding.locationText.text = place.name
                    }

                    with(plan.user) {
                        Glide.with(this@InviteActivity)
                            .load(profileImage)
                            .into(binding.profileImage)

                        binding.profileName.text = name
                    }

                    if (isJoinedPlan) binding.nextButton.visibility = View.INVISIBLE
                    else binding.noneButton.visibility = View.INVISIBLE

                    binding.nextButton.setOnClickListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            val isSuccess = acceptPlan(inviteId) != null
                            withContext(Dispatchers.Main) {
                                if (isSuccess) {
                                    Toast.makeText(this@InviteActivity, "초대를 수락하셨습니다", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@InviteActivity, "초대 수락을 실패하셨습니다", Toast.LENGTH_SHORT).show()
                                }
                                startActivity(Intent(this@InviteActivity, MainActivity::class.java).apply {
                                    finishAffinity()
                                })
                            }
                        }
                    }

                    binding.refuseButton.setOnClickListener {
                        finish()
                    }
                }
            }

        }
    }


    private suspend fun acceptPlan(inviteId: String): PlanResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.acceptPlan("bearer ${TokenManager.accessToken}", inviteId)
                if (response.isSuccessful) {
                    response.body()
                } else if (response.code() == 401) {
                    val getRefresh = getRefresh()
                    if (getRefresh != null) {
                        TokenManager.refreshToken = getRefresh.refreshToken
                        TokenManager.accessToken = getRefresh.accessToken
                        getUser(this@InviteActivity)
                        null
                    } else {
                        TokenManager.refreshToken = ""
                        TokenManager.accessToken = ""
                        startActivity(Intent(this@InviteActivity, LoginActivity::class.java))
                        finishAffinity()
                        null
                    }
                }else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getPlanInfo(inviteId: String): PlanInviteResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getInvitePlanInfo("bearer ${TokenManager.accessToken}", inviteId)
                if (response.isSuccessful) {
                    response.body()
                } else if (response.code() == 401) {
                    val getRefresh = getRefresh()
                    if (getRefresh != null) {
                        TokenManager.refreshToken = getRefresh.refreshToken
                        TokenManager.accessToken = getRefresh.accessToken
                        getUser(this@InviteActivity)
                        null
                    } else {
                        TokenManager.refreshToken = ""
                        TokenManager.accessToken = ""
                        startActivity(Intent(this@InviteActivity, LoginActivity::class.java))
                        finishAffinity()
                        null
                    }
                }else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

}