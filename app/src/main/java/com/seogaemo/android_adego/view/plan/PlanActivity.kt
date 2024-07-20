package com.seogaemo.android_adego.view.plan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.seogaemo.android_adego.R
import com.seogaemo.android_adego.data.PlanRequest
import com.seogaemo.android_adego.data.PlanResponse
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.databinding.ActivityPlanBinding
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
import com.seogaemo.android_adego.util.Util
import com.seogaemo.android_adego.view.auth.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlanBinding

    lateinit var planName: String
    lateinit var planDate: String
    lateinit var planTime: String
    lateinit var planPlace: String
    lateinit var planAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            if (supportFragmentManager.fragments.size == 1) {
                finish()
                overridePendingTransition(R.anim.anim_slide_in_from_left_fade_in, R.anim.anim_fade_out)
            } else {
                this.supportFragmentManager.popBackStack()
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, PlanNameFragment())
                .commit()
        }
    }

    fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.anim_slide_in_from_left_fade_in, R.anim.anim_fade_out)
            .add(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    suspend fun setPlan(context: Context): PlanResponse? {
        val planRequest = PlanRequest(planDate+"T"+planTime, planAddress, planName)
        if (checkPropertiesInitialized()) {
            return try {
                withContext(Dispatchers.IO) {
                    val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                    val response = retrofitAPI.setPlan("bearer ${TokenManager.accessToken}", planRequest)
                    if (response.isSuccessful) {
                        response.body()
                    } else if (response.code() == 401) {
                        val getRefresh = Util.getRefresh()
                        if (getRefresh != null) {
                            TokenManager.refreshToken = getRefresh.refreshToken
                            TokenManager.accessToken = getRefresh.accessToken
                            setPlan(context)
                        } else {
                            TokenManager.refreshToken = ""
                            TokenManager.accessToken = ""
                            startActivity(Intent(context, LoginActivity::class.java))
                            finishAffinity()
                            overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
                            null
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "약속 생성을 실패하였습니다", Toast.LENGTH_SHORT).show()
                        }
                        null
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "약속 생성을 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
                null
            }
        } else {
            Toast.makeText(context, "약속 생성을 실패하였습니다", Toast.LENGTH_SHORT).show()
            return null
        }
    }

    private fun checkPropertiesInitialized(): Boolean {
        return this::planName.isInitialized &&
                this::planDate.isInitialized &&
                this::planTime.isInitialized &&
                this::planPlace.isInitialized
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_in_from_left_fade_in, R.anim.anim_fade_out)
    }


}