package com.seogaemo.android_adego.database

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val NAME = "ADEGO"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val ACCESS_TOKEN = Pair("accessToken", "")
    private val REFRESH_TOKEN = Pair("refreshToken", "")


    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var accessToken: String
        get() = preferences.getString(ACCESS_TOKEN.first, ACCESS_TOKEN.second) ?: ""
        set(value) = preferences.edit {
            it.putString(ACCESS_TOKEN.first, value)
        }
    var refreshToken: String
        get() = preferences.getString(REFRESH_TOKEN.first, REFRESH_TOKEN.second) ?: ""
        set(value) = preferences.edit {
            it.putString(REFRESH_TOKEN.first, value)
        }
}