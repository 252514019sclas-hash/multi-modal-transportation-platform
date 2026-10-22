package com.simats.multimodaltransportationrecommendation.ui.options

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TripOptionsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TripOptionsUiState())
    val uiState: StateFlow<TripOptionsUiState> = _uiState.asStateFlow()

    fun toggleMode(mode: String) {
        val currentModes = _uiState.value.selectedModes.toMutableSet()
        if (currentModes.contains(mode)) {
            currentModes.remove(mode)
        } else {
            currentModes.add(mode)
        }
        _uiState.value = _uiState.value.copy(selectedModes = currentModes)
    }

    fun setWalkingMinutes(minutes: Float) {
        _uiState.value = _uiState.value.copy(walkingMinutes = minutes)
    }

    fun setOptimize(option: String) {
        _uiState.value = _uiState.value.copy(optimizeBy = option)
    }

    fun toggleAccessible() {
        _uiState.value = _uiState.value.copy(accessibleRoutes = !_uiState.value.accessibleRoutes)
    }
}

data class TripOptionsUiState(
    val origin: String = "Ferry Building",
    val destination: String = "California Academy of Sciences",
    val timeMode: String = "Depart at",
    val dateText: String = "Today, Oct 20",
    val timeText: String = "9:15 AM",
    val passengers: Int = 1,
    val selectedModes: Set<String> = setOf("Walk", "Cycle", "Bus", "Metro", "Rideshare"),
    val walkingMinutes: Float = 10f,
    val optimizeBy: String = "Low-carbon",
    val accessibleRoutes: Boolean = false
)
