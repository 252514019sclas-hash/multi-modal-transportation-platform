package com.simats.multimodaltransportationrecommendation.ui.destination

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DestinationUiState(
    val origin: String = "Chennai Central",
    val destinationQuery: String = "Marina Beach, Chennai",
    val recentSearches: List<Pair<String, String>> = listOf(
        "Marina Beach & Promenade" to "Kamarajar Salai, Chennai",
        "Meenakshi Amman Temple" to "Madurai, Tamil Nadu",
        "Kapaleeshwarar Temple" to "Mylapore, Chennai"
    ),
    val savedPlaces: List<Pair<String, String>> = listOf(
        "Chennai Central" to "Kannappar Thidal, Periyamet",
        "Marina Beach" to "Triplicane, Chennai"
    )
)

class DestinationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DestinationUiState())
    val uiState: StateFlow<DestinationUiState> = _uiState.asStateFlow()

    fun updateDestination(query: String) {
        _uiState.value = _uiState.value.copy(destinationQuery = query)
    }
}
