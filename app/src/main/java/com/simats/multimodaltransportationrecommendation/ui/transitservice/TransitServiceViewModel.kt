package com.simats.multimodaltransportationrecommendation.ui.transitservice

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TransitServiceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TransitServiceUiState())
    val uiState: StateFlow<TransitServiceUiState> = _uiState.asStateFlow()

    fun selectDeparture(time: String) {
        _uiState.value = _uiState.value.copy(selectedDeparture = time)
    }
}

data class TransitServiceUiState(
    val lineName: String = "Muni N Judah",
    val direction: String = "Toward Ocean Beach",
    val status: String = "Good service · Updated just now",
    val departures: List<Pair<String, String>> = listOf(
        "9:21" to "Your trip",
        "9:31" to "In 16 min",
        "9:41" to "In 26 min"
    ),
    val selectedDeparture: String = "9:21",
    val rideDuration: String = "28-minute ride",
    val stations: List<Pair<String, String>> = listOf(
        "Embarcadero" to "Board at 9:21 AM",
        "Montgomery · Powell · Civic Center" to "Downtown subway stops",
        "Van Ness · Church & Duboce" to "Continue on N Judah",
        "Carl & Cole · UCSF Parnassus" to "Surface stops",
        "9th Ave & Irving" to "Exit at 9:49 AM"
    ),
    val fareType: String = "Adult single ride",
    val fareAmount: String = "$3.00",
    val fareDetails: String = "Accessible boarding · Priority seating · Audio stop announcements. Check elevator status before travel."
)
