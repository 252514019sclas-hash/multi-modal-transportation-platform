package com.simats.multimodaltransportationrecommendation.data.api

import com.simats.multimodaltransportationrecommendation.data.model.auth.LoginRequest
import com.simats.multimodaltransportationrecommendation.data.model.auth.LoginResponse
import com.simats.multimodaltransportationrecommendation.data.model.auth.SignUpRequest
import com.simats.multimodaltransportationrecommendation.data.model.auth.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/signup.php")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
