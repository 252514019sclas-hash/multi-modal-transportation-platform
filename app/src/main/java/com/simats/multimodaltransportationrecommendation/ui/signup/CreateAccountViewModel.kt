package com.simats.multimodaltransportationrecommendation.ui.signup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateAccountViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CreateAccountUiState())
    val uiState: StateFlow<CreateAccountUiState> = _uiState.asStateFlow()
}

data class CreateAccountUiState(
    val fullName: String = "Alex Morgan",
    val email: String = "alex.morgan@example.com",
    val password: String = "••••••••••",
    val passwordStrength: String = "Strong password : 12 characters"
)
