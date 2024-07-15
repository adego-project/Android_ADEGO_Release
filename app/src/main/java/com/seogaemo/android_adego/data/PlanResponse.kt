package com.seogaemo.android_adego.data

data class PlanResponse(
    val id: String,
    val name: String,
    val place: Place,
    val date: String,
    val users: List<UserResponse>,
    val isAlarmAvailable: Boolean,
    val createdAt: String,
    val status: String
)