package com.simats.multimodaltransportationrecommendation.ui.destination

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DestinationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DestinationUiState())
    val uiState: StateFlow<DestinationUiState> = _uiState.asStateFlow()

    fun updateDestination(query: String) {
        _uiState.value = _uiState.value.copy(destinationQuery = query)
    }
}

data class DestinationUiState(
    val origin: String = "Ferry Building",
    val destinationQuery: String = "California Academy of Sci",
    val recentSearches: List<Pair<String, String>> = listOf(
        "California Academy of Sciences" to "Golden Gate Park",
        "Dolores Park" to "Dolores St & 19th St",
        "San Francisco Museum of Modern Art" to "151 3rd St · SoMa"
    ),
    val savedPlaces: List<Pair<String, String>> = listOf(
        "Home" to "Noe Valley",
        "Work" to "SoMa"
    )
)
