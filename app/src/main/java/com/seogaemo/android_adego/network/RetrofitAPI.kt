package com.seogaemo.android_adego.network

import com.seogaemo.android_adego.data.ImageRequest
import com.seogaemo.android_adego.data.NameRequest
import com.seogaemo.android_adego.data.SignInRequest
import com.seogaemo.android_adego.data.SignInResponse
import com.seogaemo.android_adego.data.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST


interface RetrofitAPI {
    @POST("oauth/kakao/login")
    suspend fun kakaoSignIn(
        @Body body: SignInRequest
    ): Response<SignInResponse>

    @POST("oauth/google/login")
    suspend fun googleSignIn(
        @Body body: SignInRequest
    ): Response<SignInResponse>

    @GET("user")
    suspend fun getUser(
        @Header("Authorization") authorization: String,
    ): Response<UserResponse>

    @PATCH("user")
    suspend fun updateName(
        @Header("Authorization") authorization: String,
        @Body body: NameRequest
    ): Response<UserResponse>

    @POST("user/profile-image")
    suspend fun updateImage(
        @Header("Authorization") authorization: String,
        @Body body: ImageRequest
    ): Response<Unit>

    @GET("auth/refresh")
    suspend fun getRefresh(
        @Header("Authorization") authorization: String,
    ): Response<SignInResponse>
}