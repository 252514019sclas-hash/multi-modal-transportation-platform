package com.simats.multimodaltransportationrecommendation.ui.completed

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JourneyCompletedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(JourneyCompletedUiState())
    val uiState: StateFlow<JourneyCompletedUiState> = _uiState.asStateFlow()

    fun setRating(rating: Int) {
        _uiState.value = _uiState.value.copy(selectedRating = rating)
    }
}

data class JourneyCompletedUiState(
    val arrivalTitle: String = "Hello, Golden Gate Park.",
    val arrivalSubtitle: String = "You arrived at 9:57 AM. Right on time.",
    val origin: String = "Ferry Building",
    val destination: String = "California Academy of Sciences",
    val travelTime: String = "42 min",
    val totalFare: String = "$3.00",
    val routeLine: String = "N Judah",
    val co2Saved: String = "2.4 kg CO₂ avoided",
    val co2Details: String = "86% less than a rideshare. Your trip emitted an estimated 0.4 kg CO₂.",
    val selectedRating: Int = 4,
    val ratingPrompt: String = "Great trip · Tap a star to rate"
)
