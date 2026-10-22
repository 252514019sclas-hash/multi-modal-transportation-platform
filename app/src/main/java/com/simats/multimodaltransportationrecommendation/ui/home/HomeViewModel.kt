package com.simats.multimodaltransportationrecommendation.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun selectTab(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}

data class HomeUiState(
    val userName: String = "Alex",
    val location: String = "Ferry Building · Current location",
    val temperature: String = "17° SF",
    val recentDestination: String = "California Academy of Sciences",
    val recentLocationInfo: String = "Golden Gate Park · 7.2 mi away",
    val selectedTab: String = "Explore"
)
