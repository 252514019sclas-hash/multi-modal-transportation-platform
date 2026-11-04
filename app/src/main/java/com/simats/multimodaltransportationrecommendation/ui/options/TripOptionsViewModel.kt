package com.simats.multimodaltransportationrecommendation.ui.options

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TripOptionsUiState(
    val origin: String = "Chennai Central",
    val destination: String = "Marina Beach, Chennai",
    val timeMode: String = "Depart at",
    val dateText: String = "Today",
    val timeText: String = "09:30 AM",
    val passengers: Int = 1,
    val selectedModes: Set<String> = setOf("Walk", "Cycle", "Bus", "Metro", "Train", "Taxi", "Car"),
    val walkingMinutes: Float = 10f,
    val optimizeBy: String = "Fastest Route",
    val accessibleRoutes: Boolean = false
)

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
