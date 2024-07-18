package com.seogaemo.android_adego.view.invite

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.seogaemo.android_adego.databinding.ActivityInviteBinding

class InviteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInviteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInviteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inviteId = intent.getStringExtra("inviteId")
        Toast.makeText(this@InviteActivity, inviteId.toString(), Toast.LENGTH_SHORT).show()
    }
}