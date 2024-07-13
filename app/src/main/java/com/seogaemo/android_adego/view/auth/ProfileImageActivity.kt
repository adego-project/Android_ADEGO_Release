package com.seogaemo.android_adego.view.auth

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.seogaemo.android_adego.data.ImageRequest
import com.seogaemo.android_adego.data.NameRequest
import com.seogaemo.android_adego.data.UserResponse
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.databinding.ActivityProfileImageBinding
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
import com.seogaemo.android_adego.util.Util
import com.seogaemo.android_adego.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream


class ProfileImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileImageBinding

    private val getContent: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val image = uriToBase64(this@ProfileImageActivity, uri)

                binding.noneButton.visibility = View.VISIBLE
                binding.nextButton.visibility = View.GONE

                if (image != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = updateImage(this@ProfileImageActivity, image)
                        withContext(Dispatchers.Main) {
                            if (response != null) {
                                Glide.with(this@ProfileImageActivity).load(it).centerCrop().into(binding.imageView)
                                binding.noneButton.visibility = View.GONE
                                binding.nextButton.visibility = View.VISIBLE
                            } else {
                                binding.noneButton.visibility = View.GONE
                                Toast.makeText(this@ProfileImageActivity, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                } else {
                    binding.noneButton.visibility = View.GONE
                    Toast.makeText(this@ProfileImageActivity, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("확인", intent.getStringExtra("name")!!)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.setImageButton.apply {
            this.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            this.setOnClickListener { getContent.launch("image/*") }
        }

        binding.nextButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val isSuccess = updateName(this@ProfileImageActivity, intent.getStringExtra("name")!!) != null
                withContext(Dispatchers.Main) {
                    if (isSuccess) {
                        startActivity(Intent(this@ProfileImageActivity, MainActivity::class.java))
                        finishAffinity()
                    } else {
                        Toast.makeText(this@ProfileImageActivity, "업데이트 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun uriToBase64(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        return inputStream?.use {
            val byteArray = it.readBytes()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    private suspend fun updateImage(context: Context, image: String): Unit? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.updateImage("bearer ${TokenManager.accessToken}", ImageRequest(image))
                if (response.isSuccessful) {
                    response.body()
                } else if (response.code() == 401) {
                    val getRefresh = Util.getRefresh()
                    if (getRefresh != null) {
                        TokenManager.refreshToken = getRefresh.refreshToken
                        TokenManager.accessToken = getRefresh.accessToken
                        updateImage(context, image)
                    } else {
                        startActivity(Intent(context, LoginActivity::class.java))
                        finishAffinity()
                        null
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.noneButton.visibility = View.GONE
                        Toast.makeText(context, "업로드를 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                binding.noneButton.visibility = View.GONE
                Toast.makeText(context, "업로드를 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
            null
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
                        startActivity(Intent(context, LoginActivity::class.java))
                        finishAffinity()
                        null
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.noneButton.visibility = View.GONE
                        Toast.makeText(context, "업데이트 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                binding.noneButton.visibility = View.GONE
                Toast.makeText(context, "업데이트 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }


}