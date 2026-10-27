package com.simats.multimodaltransportationrecommendation.ui.reroute

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SmarterWayViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SmarterWayUiState())
    val uiState: StateFlow<SmarterWayUiState> = _uiState.asStateFlow()
}

data class SmarterWayUiState(
    val delayTitle: String = "N Judah is delayed by 7 min",
    val delayDetails: String = "Signal issue near Carl & Cole. Your original 9:57 AM arrival is now 10:04 AM.",
    val currentRouteStatus: String = "Stay on N Judah",
    val currentRouteTime: String = "10:04 AM",
    val currentRouteMeta: String = "29 min left - 0 transfers",
    val aiAlternativeTitle: String = "N Judah → 7 bus → Walk",
    val aiAlternativeTime: String = "10:00",
    val timeSaved: String = "Save 4 min",
    val alternativeDetails: String = "Exit at Carl & Cole · 1 transfer · Arrive 10:00 AM",
    val alternativePerks: String = "No additional fare · Your ticket stays valid",
    val scenarioNote: String = "Alternate live-delay scenario · 9:35 AM update. Your route changes only if you accept."
)
