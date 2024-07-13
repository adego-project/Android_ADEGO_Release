package com.seogaemo.android_adego.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seogaemo.android_adego.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}