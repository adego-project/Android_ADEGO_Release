package com.seogaemo.android_adego.network

import com.seogaemo.android_adego.data.SignInRequest
import com.seogaemo.android_adego.data.SignInResponses
import retrofit2.Response
import retrofit2.http.Body
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
}