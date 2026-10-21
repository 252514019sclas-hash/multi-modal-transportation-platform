package com.simats.multimodaltransportationrecommendation.ui.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
}

data class OnboardingUiState(
    val title: String = "Your city. Your pace. Your best route.",
    val subtitle: String = "Walk, ride or roll. AI connects the best ways to travel, all in one personal journey."
)
