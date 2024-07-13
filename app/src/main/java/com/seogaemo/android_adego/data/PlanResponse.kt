package com.seogaemo.android_adego.data

data class PlanResponse(
    val createdAt: String,
    val date: String,
    val id: String,
    val name: String,
    val place: String,
    val users: List<UserResponse>
)