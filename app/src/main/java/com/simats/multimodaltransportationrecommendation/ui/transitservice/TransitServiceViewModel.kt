package com.simats.multimodaltransportationrecommendation.ui.transitservice

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TransitServiceUiState(
    val lineName: String = "Chennai Metro Rail (CMRL) Blue Line",
    val direction: String = "Toward Government Estate / Marina",
    val status: String = "High frequency service · Running every 5 mins",
    val departures: List<Pair<String, String>> = listOf(
        "09:32" to "Your trip",
        "09:38" to "In 6 min",
        "09:44" to "In 12 min"
    ),
    val selectedDeparture: String = "09:32",
    val rideDuration: String = "12-minute rapid transit ride",
    val stations: List<Pair<String, String>> = listOf(
        "Chennai Central Metro" to "Board at Platform 2 (09:32 AM)",
        "Government Estate Metro" to "Station Interchange · 09:40 AM",
        "Marina Promenade Exit" to "Exit towards beach corridor (09:44 AM)"
    ),
    val fareType: String = "CMRL QR Transit Pass",
    val fareAmount: String = "₹30",
    val fareDetails: String = "Air-conditioned coaches · Contactless digital entry · Direct accessibility."
)

class TransitServiceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TransitServiceUiState())
    val uiState: StateFlow<TransitServiceUiState> = _uiState.asStateFlow()

    fun selectDeparture(time: String) {
        _uiState.value = _uiState.value.copy(selectedDeparture = time)
    }
}
