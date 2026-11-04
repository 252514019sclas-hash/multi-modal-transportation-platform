package com.simats.multimodaltransportationrecommendation.ui.reroute

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SmarterWayUiState(
    val delayTitle: String = "MTC Bus Route has congestion (+8 min)",
    val delayDetails: String = "Heavy traffic near Anna Salai junction. Original arrival delayed to 10:02 AM.",
    val currentRouteStatus: String = "Stay on MTC City Bus",
    val currentRouteTime: String = "10:02 AM",
    val currentRouteMeta: String = "24 min left · Road traffic",
    val aiAlternativeTitle: String = "Switch to CMRL Metro Blue Line",
    val aiAlternativeTime: String = "09:48 AM",
    val timeSaved: String = "Save 14 min",
    val alternativeDetails: String = "Board at Government Estate Metro · High-frequency train",
    val alternativePerks: String = "Air-conditioned speed · Instant transfer ticket",
    val scenarioNote: String = "MoveSmart AI real-time rerouting · Changes apply only if accepted."
)

class SmarterWayViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SmarterWayUiState())
    val uiState: StateFlow<SmarterWayUiState> = _uiState.asStateFlow()
}
