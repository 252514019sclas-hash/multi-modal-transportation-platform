package com.simats.multimodaltransportationrecommendation.data.repository

import com.google.gson.Gson
import com.simats.multimodaltransportationrecommendation.data.api.AuthApiService
import com.simats.multimodaltransportationrecommendation.data.api.RetrofitClient
import com.simats.multimodaltransportationrecommendation.data.model.auth.BaseResponse
import com.simats.multimodaltransportationrecommendation.data.model.auth.LoginRequest
import com.simats.multimodaltransportationrecommendation.data.model.auth.LoginResponse
import com.simats.multimodaltransportationrecommendation.data.model.auth.SignUpRequest
import com.simats.multimodaltransportationrecommendation.data.model.auth.SignUpResponse
import com.simats.multimodaltransportationrecommendation.data.model.auth.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String) : NetworkResult<Nothing>()
}

class AuthRepository(
    private val authService: AuthApiService = RetrofitClient.authService,
    private val gson: Gson = Gson()
) {

    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        phone: String? = null
    ): NetworkResult<SignUpResponse> = withContext(Dispatchers.IO) {
        try {
            val request = SignUpRequest(
                fullName = fullName,
                email = email,
                password = password,
                phone = phone
            )
            val response = authService.signUp(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(body.message ?: "Signup failed")
                }
            } else {
                val errorMessage = parseErrorMessage(response)
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Network connection error. Check server and base URL.")
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): NetworkResult<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(email = email, password = password)
            val response = authService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(body.message ?: "Login failed")
                }
            } else {
                val errorMessage = parseErrorMessage(response)
                NetworkResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Network connection error. Check server and base URL.")
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, BaseResponse::class.java)
                parsed.message.ifBlank { "Request failed with code ${response.code()}" }
            } else {
                "Request failed with code ${response.code()}"
            }
        } catch (_: Exception) {
            "Request failed with code ${response.code()}"
        }
    }
}
