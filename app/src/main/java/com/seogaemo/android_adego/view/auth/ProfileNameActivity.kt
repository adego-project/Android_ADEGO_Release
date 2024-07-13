package com.seogaemo.android_adego.view.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.seogaemo.android_adego.data.NameRequest
import com.seogaemo.android_adego.data.UserResponse
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.databinding.ActivityProfileNameBinding
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
import com.seogaemo.android_adego.util.Util
import com.seogaemo.android_adego.view.main.SettingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isSetting = intent.getBooleanExtra("isSetting" ,false)
        if (isSetting) {
            binding.backButton.setOnClickListener {
                finish()
            }

            binding.nextButton.setOnClickListener {
                val name = binding.nameInput.text.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    val isSuccess = updateName(this@ProfileNameActivity, name) != null
                    if (isSuccess) finish()
                }
            }

        } else {
            binding.backButton.setOnClickListener {
                startActivity(Intent(this@ProfileNameActivity, LoginActivity::class.java))
                finishAffinity()
            }

            binding.nextButton.setOnClickListener {
                startActivity(
                    Intent(this@ProfileNameActivity, ProfileImageActivity::class.java).apply
                    { this.putExtra("name", binding.nameInput.text.toString()) }
                )
            }

            onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        }

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

    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            startActivity(Intent(this@ProfileNameActivity, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private suspend fun updateName(context: Context, name: String): UserResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.updateName("bearer ${TokenManager.accessToken}", NameRequest(name))
                if (response.isSuccessful) {
                    response.body()
                } else if (response.code() == 401) {
                    val getRefresh = Util.getRefresh()
                    if (getRefresh != null) {
                        TokenManager.refreshToken = getRefresh.refreshToken
                        TokenManager.accessToken = getRefresh.accessToken
                        updateName(context, name)
                    } else {
                        TokenManager.refreshToken = ""
                        TokenManager.accessToken = ""
                        startActivity(Intent(context, LoginActivity::class.java))
                        finishAffinity()
                        null
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "업데이트 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "업데이트 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

}