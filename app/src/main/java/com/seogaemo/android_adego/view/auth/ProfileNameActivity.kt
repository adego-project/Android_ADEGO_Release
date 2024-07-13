package com.seogaemo.android_adego.view.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.seogaemo.android_adego.databinding.ActivityProfileNameBinding

class ProfileNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nameInput.apply {
            this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.nameLengthText.text = "사용자 이름 (${binding.nameInput.text.length}/8)"
                if (binding.nameInput.text.isNotEmpty()) {
                    binding.nextButton.visibility = View.VISIBLE
                    binding.noneButton.visibility = View.GONE
                } else {
                    binding.nextButton.visibility = View.GONE
                    binding.noneButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
            this.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    manager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    binding.nameInput.clearFocus()
                    true
                } else {
                    false
                }
            }
        }

        binding.nextButton.setOnClickListener {
            startActivity(
                Intent(this@ProfileNameActivity, ProfileImageActivity::class.java).apply
                { this.putExtra("name", binding.nameInput.text) }
            )
        }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this@ProfileNameActivity, LoginActivity::class.java))
            finishAffinity()
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(Intent(this@ProfileNameActivity, LoginActivity::class.java))
            finishAffinity()
        }
    }

}