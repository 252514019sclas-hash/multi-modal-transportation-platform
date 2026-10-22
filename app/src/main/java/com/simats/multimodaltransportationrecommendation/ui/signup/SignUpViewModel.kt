package com.simats.multimodaltransportationrecommendation.ui.signup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()
}

data class SignUpUiState(
    val fullName: String = "Alex Morgan",
    val email: String = "alex.morgan@example.com",
    val password: String = "••••••••••"
)
