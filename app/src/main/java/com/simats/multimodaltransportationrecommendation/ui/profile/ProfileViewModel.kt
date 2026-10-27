package com.simats.multimodaltransportationrecommendation.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun selectTab(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}

data class ProfileUiState(
    val userName: String = "Alex Morgan",
    val email: String = "alex.morgan@example.com",
    val userMeta: String = "San Francisco · Member since Sep 2026",
    val co2Impact: String = "18.6 kg CO₂ avoided",
    val co2Subtitle: String = "Your impact this month · Keep moving smarter",
    val travelPreferences: String = "Low-carbon · Walk, cycle, bus & metro",
    val accessibility: String = "Standard",
    val notifications: String = "On",
    val savedPlacesCount: String = "2 places",
    val paymentMethod: String = "Visa •••4242",
    val versionText: String = "MoveSmart AI 1.0",
    val selectedTab: String = "Profile"
)
