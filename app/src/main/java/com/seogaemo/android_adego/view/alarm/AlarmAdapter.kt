package com.seogaemo.android_adego.view.alarm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.seogaemo.android_adego.data.UserResponse
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.databinding.AlarmItemBinding
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
import com.seogaemo.android_adego.util.Util
import com.seogaemo.android_adego.view.auth.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmAdapter(private val userList: List<UserResponse>): RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    private var lastClickTime = 0L
    private val clickInterval = 3000L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val vibrator = binding.root.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        return AlarmViewHolder(binding).also { handler->
            binding.root.setOnClickListener {
                val currentTime = SystemClock.elapsedRealtime()

                if (currentTime - lastClickTime >= clickInterval) {
                    val effect = VibrationEffect.createOneShot(100L, 100)
                    vibrator.vibrate(effect)
                    lastClickTime = currentTime

                    CoroutineScope(Dispatchers.IO).launch {
                        callUser(binding.root.context, userList[handler.adapterPosition].id)
                    }
                } else {
                    Toast.makeText(binding.root.context, "3초에 한번만 보낼 수 있습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    class AlarmViewHolder(private val binding: AlarmItemBinding):
        ViewHolder(binding.root) {
        fun bind(user: UserResponse) {
            if (binding.root.context != null) {
                Glide.with(binding.root.context)
                    .load(user.profileImage)
                    .centerCrop()
                    .into(binding.imageView)
            }
            binding.nicknameText.text = user.name
        }
    }

    private suspend fun callUser(context: Context, id: String): Unit? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.callUser("bearer ${TokenManager.accessToken}", id)
                if (response.isSuccessful) {
                    response.body()
                } else if (response.code() == 401) {
                    val getRefresh = Util.getRefresh()
                    if (getRefresh != null) {
                        TokenManager.refreshToken = getRefresh.refreshToken
                        TokenManager.accessToken = getRefresh.accessToken
                        callUser(context, id)
                    } else {
                        TokenManager.refreshToken = ""
                        TokenManager.accessToken = ""
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        (context as Activity).finishAffinity()
                        null
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

}