package com.seogaemo.android_adego.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Base64
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.seogaemo.android_adego.R
import com.seogaemo.android_adego.data.PlanResponse
import com.seogaemo.android_adego.data.SignInResponse
import com.seogaemo.android_adego.data.UserResponse
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
import com.seogaemo.android_adego.view.auth.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object Util {
    suspend fun getRefresh (): SignInResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getRefresh("bearer ${TokenManager.refreshToken}")
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun parseDateTime(dateTimeString: String): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'H:m:ss")
        val dateTime = LocalDateTime.parse(dateTimeString, formatter)

        val date = dateTime.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
        val time = dateTime.format(DateTimeFormatter.ofPattern("a h시 m분"))

        return Pair(date, time)
    }

    fun uriToBase64(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        return inputStream?.use {
            val byteArray = it.readBytes()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    suspend fun getUser(activity: Activity, context: Context): UserResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getUser("bearer ${TokenManager.accessToken}")
                if (response.isSuccessful) {
                    response.body()
                } else if (response.code() == 401) {
                    val getRefresh = getRefresh()
                    if (getRefresh != null) {
                        TokenManager.refreshToken = getRefresh.refreshToken
                        TokenManager.accessToken = getRefresh.accessToken
                        getUser(activity, context)
                    } else {
                        TokenManager.refreshToken = ""
                        TokenManager.accessToken = ""
                        activity.startActivity(Intent(context, LoginActivity::class.java))
                        activity.finishAffinity()
                        null
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "정보 조회를 실패하였습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "정보 조회를 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    fun createDialog(context: Context, text: String, mainButtonText: String, buttonAction: (customDialog: Dialog) -> Unit) {
        val customDialog = Dialog(context)
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        customDialog.setContentView(R.layout.dialog_setting)

        customDialog.findViewById<TextView>(R.id.textView).text = text
        customDialog.findViewById<TextView>(R.id.main_button_text).text = mainButtonText
        customDialog.findViewById<CardView>(R.id.main_button).setOnClickListener {
            buttonAction(customDialog)
        }
        customDialog.findViewById<CardView>(R.id.cancel_button).setOnClickListener {
            customDialog.cancel()
        }

        customDialog.show()
    }

    fun convertDateFormat(inputDate: String, inputFormat: String = "yyyy-MM-dd", outputFormat: String = "yyyy년 M월 d일"): String {
        val formatter = DateTimeFormatter.ofPattern(inputFormat)
        val date = LocalDate.parse(inputDate, formatter)

        val outputFormatter = DateTimeFormatter.ofPattern(outputFormat)
        return date.format(outputFormatter)
    }

    fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    fun keyboardDown(activity: Activity) {
        val manager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        manager!!.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    suspend fun getPlan(activity: Activity, context: Context): PlanResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.getPlan("bearer ${TokenManager.accessToken}")
                if (response.isSuccessful) {
                    response.body()
                } else if (response.code() == 401) {
                    val getRefresh = getRefresh()
                    if (getRefresh != null) {
                        TokenManager.refreshToken = getRefresh.refreshToken
                        TokenManager.accessToken = getRefresh.accessToken
                        getPlan(activity, context)
                    } else if (response.code() == 404) {
                        null
                    } else {
                        TokenManager.refreshToken = ""
                        TokenManager.accessToken = ""
                        activity.startActivity(Intent(context, LoginActivity::class.java))
                        activity.finishAffinity()
                        null
                    }
                } else {
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