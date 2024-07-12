package com.seogaemo.android_adego.network

import com.seogaemo.android_adego.data.SignInRequest
import com.seogaemo.android_adego.data.SignInResponses
import com.seogaemo.android_adego.data.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface RetrofitAPI {
    @POST("oauth/kakao/login")
    suspend fun kakaoSignIn(
        @Body body: SignInRequest
    ): Response<SignInResponses>

    @POST("oauth/google/login")
    suspend fun googleSignIn(
        @Body body: SignInRequest
    ): Response<SignInResponses>

    @GET("user")
    suspend fun getUser(
        @Header("Authorization") authorization: String,
    ): Response<UserResponse>
}