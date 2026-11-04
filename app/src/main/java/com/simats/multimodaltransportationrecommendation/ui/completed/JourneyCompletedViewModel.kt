package com.simats.multimodaltransportationrecommendation.ui.completed

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class JourneyCompletedUiState(
    val arrivalTitle: String = "Vanakkam, Marina Beach!",
    val arrivalSubtitle: String = "You arrived at 09:48 AM. Smooth & on time.",
    val origin: String = "Chennai Central",
    val destination: String = "Marina Beach, Chennai",
    val travelTime: String = "18 min",
    val totalFare: String = "₹30",
    val routeLine: String = "CMRL Blue Line",
    val touristTipTitle: String = "Nearby Tamil Nadu Highlights",
    val touristTipDetails: String = "Kapaleeshwarar Temple is just 3.5 km away, easily accessible via MTC City Bus 21G.",
    val selectedRating: Int = 5,
    val ratingPrompt: String = "Rate your Tamil Nadu journey experience"
)

class JourneyCompletedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(JourneyCompletedUiState())
    val uiState: StateFlow<JourneyCompletedUiState> = _uiState.asStateFlow()

    fun setRating(rating: Int) {
        _uiState.value = _uiState.value.copy(selectedRating = rating)
    }
}
