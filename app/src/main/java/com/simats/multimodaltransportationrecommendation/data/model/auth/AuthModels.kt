package com.simats.multimodaltransportationrecommendation.data.model.auth

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("phone")
    val phone: String? = null
)

data class SignUpResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("user_id")
    val userId: String? = null,
    @SerializedName("full_name")
    val fullName: String? = null,
    @SerializedName("email")
    val email: String? = null
)

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class UserData(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String? = null
)

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: UserData? = null
)

data class BaseResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)
