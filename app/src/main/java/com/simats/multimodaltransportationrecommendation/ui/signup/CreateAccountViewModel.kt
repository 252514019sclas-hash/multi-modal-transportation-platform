package com.simats.multimodaltransportationrecommendation.ui.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.auth.UserData
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateAccountUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val passwordStrength: String = "Enter at least 8 characters",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val accountCreatedSuccess: Boolean = false
)

class CreateAccountViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState: StateFlow<CreateAccountUiState> = _uiState.asStateFlow()

    fun onFullNameChanged(fullName: String) {
        _uiState.value = _uiState.value.copy(fullName = fullName, errorMessage = null)
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChanged(password: String) {
        val strength = calculatePasswordStrength(password)
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordStrength = strength,
            errorMessage = null
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun calculatePasswordStrength(password: String): String {
        return when {
            password.isEmpty() -> "Enter at least 8 characters"
            password.length < 8 -> "Password too short (${password.length}/8 chars)"
            password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() } ->
                "Strong password : ${password.length} characters"
            password.any { it.isDigit() } ->
                "Good password : ${password.length} characters"
            else ->
                "Fair password : ${password.length} characters"
        }
    }

    fun createAccount(context: Context? = null, onSuccess: () -> Unit = {}) {
        val fullName = _uiState.value.fullName.trim()
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (fullName.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your full name")
            return
        }

        if (email.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your email address")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid email address")
            return
        }

        if (password.length < 8) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must contain at least 8 characters")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val result = authRepository.signUp(
                fullName = fullName,
                email = email,
                password = password
            )) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (context != null && response.userId != null) {
                        val user = UserData(
                            userId = response.userId,
                            fullName = response.fullName ?: fullName,
                            email = response.email ?: email,
                            phone = null
                        )
                        SessionManager.getInstance(context).saveUserSession(user)
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        accountCreatedSuccess = true,
                        errorMessage = null
                    )
                    onSuccess()
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
}
