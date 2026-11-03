package com.simats.multimodaltransportationrecommendation.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.api.ApiConstants
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.preferences.UpdatePreferencesRequest
import com.simats.multimodaltransportationrecommendation.data.model.profile.ProfileData
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class ProfileUiState(
    val userId: Int = 1,
    val fullName: String = "MoveSmart Traveler",
    val email: String = "user@movesmart.ai",
    val phone: String = "",
    val gender: String = "",
    val address: String = "",
    val bio: String = "",
    val profileImageUrl: String? = null,
    val rawProfileImage: String? = null,
    val initials: String = "MS",
    val userMeta: String = "Member since 2026",
    val co2Impact: String = "0.0 kg CO₂ avoided",
    val co2Subtitle: String = "Complete journeys to start saving carbon",
    val travelPreferences: String = "Low-carbon · Walk, cycle, bus & metro",
    val accessibility: String = "Standard",
    val notifications: String = "On",
    val savedPlacesCount: String = "0 places",
    val paymentMethod: String = "None added",
    val versionText: String = "MoveSmart AI 1.0",
    val selectedTab: String = "Profile",

    // Interactive states
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingImage: Boolean = false,
    val isChangingPassword: Boolean = false,
    val isEditMode: Boolean = false,
    val showChangePasswordDialog: Boolean = false,
    val showLogoutConfirmDialog: Boolean = false,
    val showNotificationDialog: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Notification toggles
    val notificationsEnabled: Boolean = true,
    val journeyReminders: Boolean = true,
    val bookingUpdates: Boolean = true,
    val touristRecommendations: Boolean = true,
    val travelAlerts: Boolean = true,
    val isSavingNotifications: Boolean = false,

    // Edit fields
    val editFullName: String = "",
    val editPhone: String = "",
    val editGender: String = "",
    val editAddress: String = "",
    val editBio: String = "",

    // Password change fields
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val isCurrentPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val passwordErrorMessage: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var isInitialized = false

    fun initProfile(context: Context) {
        if (isInitialized) return
        isInitialized = true

        val sessionManager = SessionManager.getInstance(context)
        val savedUser = sessionManager.getUser()
        val userId = sessionManager.getUserId() ?: savedUser?.userId?.toIntOrNull() ?: 1

        if (savedUser != null) {
            _uiState.value = _uiState.value.copy(
                userId = userId,
                fullName = savedUser.fullName.ifBlank { "MoveSmart Traveler" },
                email = savedUser.email.ifBlank { "user@movesmart.ai" },
                phone = savedUser.phone ?: "",
                initials = computeInitials(savedUser.fullName)
            )
        } else {
            _uiState.value = _uiState.value.copy(userId = userId)
        }

        fetchProfile(userId, context)
        fetchStats(userId)
        fetchNotificationSettings(userId)
    }

    fun fetchProfile(userId: Int = _uiState.value.userId, context: Context? = null) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val result = authRepository.fetchProfile(userId)) {
                is NetworkResult.Success -> {
                    val profile = result.data
                    applyProfileData(profile, context)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun fetchStats(userId: Int = _uiState.value.userId) {
        viewModelScope.launch {
            when (val result = authRepository.fetchStats(userId)) {
                is NetworkResult.Success -> {
                    val stats = result.data
                    _uiState.value = _uiState.value.copy(
                        co2Impact = stats.co2Impact,
                        co2Subtitle = stats.co2Subtitle,
                        travelPreferences = stats.travelPreferencesSummary,
                        notifications = stats.notificationsText,
                        savedPlacesCount = stats.savedPlacesCount,
                        paymentMethod = stats.paymentMethod
                    )
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun fetchNotificationSettings(userId: Int = _uiState.value.userId) {
        viewModelScope.launch {
            when (val result = authRepository.fetchPreferences(userId)) {
                is NetworkResult.Success -> {
                    val p = result.data
                    _uiState.value = _uiState.value.copy(
                        notificationsEnabled = p.notificationsEnabled,
                        journeyReminders = p.journeyReminders,
                        bookingUpdates = p.bookingUpdates,
                        touristRecommendations = p.touristRecommendations,
                        travelAlerts = p.travelAlerts,
                        notifications = if (p.notificationsEnabled) "On" else "Off"
                    )
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    private fun applyProfileData(profile: ProfileData, context: Context?) {
        val fullName = profile.fullName ?: _uiState.value.fullName
        val email = profile.email ?: _uiState.value.email
        val phone = profile.phone ?: _uiState.value.phone
        val gender = profile.gender ?: ""
        val address = profile.address ?: ""
        val bio = profile.bio ?: ""
        val fullImageUrl = ApiConstants.getFullImageUrl(profile.profileImage)
        val initials = computeInitials(fullName)
        val memberMeta = if (address.isNotBlank()) "$address · Member since 2026" else "Member since 2026"

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            fullName = fullName,
            email = email,
            phone = phone,
            gender = gender,
            address = address,
            bio = bio,
            profileImageUrl = fullImageUrl,
            rawProfileImage = profile.profileImage,
            initials = initials,
            userMeta = memberMeta,
            editFullName = fullName,
            editPhone = phone,
            editGender = gender,
            editAddress = address,
            editBio = bio,
            errorMessage = null
        )

        if (context != null) {
            val sessionManager = SessionManager.getInstance(context)
            sessionManager.updateUserDetails(fullName, phone)
            sessionManager.saveProfileImage(profile.profileImage)
        }
    }

    fun enterEditMode() {
        val current = _uiState.value
        _uiState.value = current.copy(
            isEditMode = true,
            editFullName = current.fullName,
            editPhone = current.phone,
            editGender = current.gender,
            editAddress = current.address,
            editBio = current.bio,
            errorMessage = null,
            successMessage = null
        )
    }

    fun cancelEditMode() {
        val current = _uiState.value
        _uiState.value = current.copy(
            isEditMode = false,
            editFullName = current.fullName,
            editPhone = current.phone,
            editGender = current.gender,
            editAddress = current.address,
            editBio = current.bio,
            errorMessage = null
        )
    }

    fun onEditFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(editFullName = value)
    }

    fun onEditPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(editPhone = value)
    }

    fun onEditGenderChange(value: String) {
        _uiState.value = _uiState.value.copy(editGender = value)
    }

    fun onEditAddressChange(value: String) {
        _uiState.value = _uiState.value.copy(editAddress = value)
    }

    fun onEditBioChange(value: String) {
        _uiState.value = _uiState.value.copy(editBio = value)
    }

    fun saveProfileChanges(context: Context) {
        val newName = _uiState.value.editFullName.trim()
        val newPhone = _uiState.value.editPhone.trim()
        val newGender = _uiState.value.editGender.trim()
        val newAddress = _uiState.value.editAddress.trim()
        val newBio = _uiState.value.editBio.trim()

        if (newName.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Full name cannot be empty")
            return
        }

        _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            val result = authRepository.updateProfile(
                userId = _uiState.value.userId,
                fullName = newName,
                phone = newPhone.ifBlank { null },
                gender = newGender.ifBlank { null },
                address = newAddress.ifBlank { null },
                bio = newBio.ifBlank { null }
            )

            when (result) {
                is NetworkResult.Success -> {
                    val initials = computeInitials(newName)
                    val memberMeta = if (newAddress.isNotBlank()) "$newAddress · Member since 2026" else "Member since 2026"

                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isEditMode = false,
                        fullName = newName,
                        phone = newPhone,
                        gender = newGender,
                        address = newAddress,
                        bio = newBio,
                        initials = initials,
                        userMeta = memberMeta,
                        successMessage = "Profile updated successfully!",
                        errorMessage = null
                    )

                    val session = SessionManager.getInstance(context)
                    session.updateUserDetails(newName, newPhone)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    // Notification Settings
    fun openNotificationDialog() {
        _uiState.value = _uiState.value.copy(showNotificationDialog = true)
    }

    fun closeNotificationDialog() {
        _uiState.value = _uiState.value.copy(showNotificationDialog = false)
    }

    fun toggleMasterNotification(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
    }

    fun toggleJourneyReminders(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(journeyReminders = enabled)
    }

    fun toggleBookingUpdates(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(bookingUpdates = enabled)
    }

    fun toggleTouristRecommendations(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(touristRecommendations = enabled)
    }

    fun toggleTravelAlerts(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(travelAlerts = enabled)
    }

    fun saveNotificationSettings(context: Context) {
        _uiState.value = _uiState.value.copy(isSavingNotifications = true)

        viewModelScope.launch {
            val req = UpdatePreferencesRequest(
                userId = _uiState.value.userId,
                modes = _uiState.value.travelPreferences,
                lowCarbon = true,
                budgetTier = "Moderate",
                maxDuration = "Under 45 min",
                notificationsEnabled = _uiState.value.notificationsEnabled,
                journeyReminders = _uiState.value.journeyReminders,
                bookingUpdates = _uiState.value.bookingUpdates,
                touristRecommendations = _uiState.value.touristRecommendations,
                travelAlerts = _uiState.value.travelAlerts
            )

            when (authRepository.updatePreferences(req)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSavingNotifications = false,
                        showNotificationDialog = false,
                        notifications = if (_uiState.value.notificationsEnabled) "On" else "Off",
                        successMessage = "Notification settings saved!"
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSavingNotifications = false,
                        errorMessage = "Could not save notification settings"
                    )
                }
            }
        }
    }

    // Password change
    fun openChangePasswordDialog() {
        _uiState.value = _uiState.value.copy(
            showChangePasswordDialog = true,
            currentPassword = "",
            newPassword = "",
            confirmNewPassword = "",
            passwordErrorMessage = null,
            errorMessage = null,
            successMessage = null
        )
    }

    fun closeChangePasswordDialog() {
        _uiState.value = _uiState.value.copy(
            showChangePasswordDialog = false,
            currentPassword = "",
            newPassword = "",
            confirmNewPassword = "",
            passwordErrorMessage = null
        )
    }

    fun onCurrentPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(currentPassword = value, passwordErrorMessage = null)
    }

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, passwordErrorMessage = null)
    }

    fun onConfirmNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmNewPassword = value, passwordErrorMessage = null)
    }

    fun toggleCurrentPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isCurrentPasswordVisible = !_uiState.value.isCurrentPasswordVisible)
    }

    fun toggleNewPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isNewPasswordVisible = !_uiState.value.isNewPasswordVisible)
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.value = _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
    }

    fun submitChangePassword() {
        val currentPass = _uiState.value.currentPassword
        val newPass = _uiState.value.newPassword
        val confirmPass = _uiState.value.confirmNewPassword

        if (currentPass.isBlank()) {
            _uiState.value = _uiState.value.copy(passwordErrorMessage = "Please enter current password")
            return
        }

        if (newPass.length < 8) {
            _uiState.value = _uiState.value.copy(passwordErrorMessage = "New password must contain at least 8 characters")
            return
        }

        if (newPass != confirmPass) {
            _uiState.value = _uiState.value.copy(passwordErrorMessage = "New passwords do not match")
            return
        }

        _uiState.value = _uiState.value.copy(isChangingPassword = true, passwordErrorMessage = null)

        viewModelScope.launch {
            val result = authRepository.changePassword(
                userId = _uiState.value.userId,
                currentPassword = currentPass,
                newPassword = newPass
            )

            when (result) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isChangingPassword = false,
                        showChangePasswordDialog = false,
                        currentPassword = "",
                        newPassword = "",
                        confirmNewPassword = "",
                        successMessage = "Password changed successfully!",
                        errorMessage = null
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isChangingPassword = false,
                        passwordErrorMessage = result.message
                    )
                }
            }
        }
    }

    fun uploadSelectedImage(context: Context, imageUri: Uri) {
        _uiState.value = _uiState.value.copy(isUploadingImage = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            try {
                val tempFile = copyUriToTempFile(context, imageUri)
                if (tempFile == null) {
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        errorMessage = "Could not process selected image file."
                    )
                    return@launch
                }

                val result = authRepository.uploadProfileImage(_uiState.value.userId, tempFile)
                when (result) {
                    is NetworkResult.Success -> {
                        val fullUrl = ApiConstants.getFullImageUrl(result.data)
                        _uiState.value = _uiState.value.copy(
                            isUploadingImage = false,
                            profileImageUrl = fullUrl,
                            rawProfileImage = result.data,
                            successMessage = "Profile picture updated successfully!"
                        )
                        SessionManager.getInstance(context).saveProfileImage(result.data)
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isUploadingImage = false,
                            errorMessage = result.message
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploadingImage = false,
                    errorMessage = e.localizedMessage ?: "Failed to upload image"
                )
            }
        }
    }

    private fun copyUriToTempFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "temp_profile_upload_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (_: Exception) {
            null
        }
    }

    fun promptLogout() {
        _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = true)
    }

    fun dismissLogoutPrompt() {
        _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = false)
    }

    fun logout(context: Context, onSignedOut: () -> Unit) {
        SessionManager.getInstance(context).clearSession()
        _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = false)
        onSignedOut()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    private fun computeInitials(name: String?): String {
        if (name.isNullOrBlank()) return "MS"
        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            parts.size == 1 && parts[0].length >= 2 -> parts[0].take(2).uppercase()
            parts.size == 1 -> parts[0].first().uppercaseChar().toString()
            else -> "MS"
        }
    }
}
