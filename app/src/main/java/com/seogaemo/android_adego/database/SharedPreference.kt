package com.seogaemo.android_adego.database

import android.content.Context
import android.content.SharedPreferences

object SharedPreference {
    private const val NAME = "SharedPreference"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val IS_FIRST = Pair("isFirst", true)


    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var isFirst: Boolean
        get() = preferences.getBoolean(IS_FIRST.first, IS_FIRST.second)
        set(value) = preferences.edit {
            it.putBoolean(IS_FIRST.first, value)
        }
}