package com.seogaemo.android_adego.data

data class UserResponse(
    val id: String,
    val name: String,
    val planId: String,
    val profileImage: String,
    val provider: String,
    val providerId: String
)