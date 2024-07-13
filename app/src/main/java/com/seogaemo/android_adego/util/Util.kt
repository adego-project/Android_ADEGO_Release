package com.seogaemo.android_adego.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.seogaemo.android_adego.data.SignInResponse
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
}