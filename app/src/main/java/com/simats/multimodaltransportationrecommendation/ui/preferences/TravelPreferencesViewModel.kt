package com.simats.multimodaltransportationrecommendation.ui.preferences

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TravelPreferencesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TravelPreferencesUiState())
    val uiState: StateFlow<TravelPreferencesUiState> = _uiState.asStateFlow()

    fun toggleMode(mode: String) {
        val currentModes = _uiState.value.selectedModes.toMutableSet()
        if (currentModes.contains(mode)) {
            currentModes.remove(mode)
        } else {
            currentModes.add(mode)
        }
        _uiState.value = _uiState.value.copy(selectedModes = currentModes)
    }

    fun setPriority(priority: String) {
        _uiState.value = _uiState.value.copy(selectedPriority = priority)
    }

    fun toggleStepFree() {
        _uiState.value = _uiState.value.copy(stepFree = !_uiState.value.stepFree)
    }
}

data class TravelPreferencesUiState(
    val selectedModes: Set<String> = setOf("Walk", "Cycle", "Bus", "Metro"),
    val selectedPriority: String = "Low-carbon",
    val stepFree: Boolean = false
)
