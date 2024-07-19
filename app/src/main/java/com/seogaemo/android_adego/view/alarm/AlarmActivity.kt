package com.seogaemo.android_adego.view.alarm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.seogaemo.android_adego.databinding.ActivityAlarmBinding
import com.seogaemo.android_adego.util.Util.getPlan
import com.seogaemo.android_adego.util.Util.getUser
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
            val myId = getUser(this@AlarmActivity)?.id.toString()

            if (planResponse != null) {
                val alarmList = planResponse.users.filter { it.id != myId }
                withContext(Dispatchers.Main) {
                    binding.friendsList.apply {
                        this.layoutManager = GridLayoutManager(this@AlarmActivity, 2)
                        this.adapter = AlarmAdapter(alarmList)
                    }
                }
            }

        }

    }

}