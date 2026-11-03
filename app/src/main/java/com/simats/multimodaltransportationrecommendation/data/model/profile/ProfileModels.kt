package com.simats.multimodaltransportationrecommendation.data.model.profile

import com.google.gson.annotations.SerializedName

data class ProfileData(
    @SerializedName("user_id")
    val userId: String? = null,
    @SerializedName("full_name")
    val fullName: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("profile_id")
    val profileId: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("bio")
    val bio: String? = null,
    @SerializedName("profile_image")
    val profileImage: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

data class ProfileResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("profile")
    val profile: ProfileData? = null
)

data class UpdateProfileRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("bio")
    val bio: String? = null
)

data class ChangePasswordRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String
)

data class ProfileUploadResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("profile_image")
    val profileImage: String? = null
)

