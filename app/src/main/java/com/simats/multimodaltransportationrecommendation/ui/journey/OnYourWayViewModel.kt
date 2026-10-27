package com.simats.multimodaltransportationrecommendation.ui.journey

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnYourWayViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnYourWayUiState())
    val uiState: StateFlow<OnYourWayUiState> = _uiState.asStateFlow()
}

data class OnYourWayUiState(
    val currentTime: String = "9:57 AM",
    val remainingTimeText: String = "22 min remaining - On time",
    val activeStatusTitle: String = "You're on N Judah",
    val nextStopInfo: String = "Next: Carl & Cole · 9:38 AM",
    val reminderText: String = "We'll remind you at 8th Ave & Irving",
    val upcomingSteps: List<JourneyUpcomingStep> = listOf(
        JourneyUpcomingStep("9:49 AM", "🚊", "Exit at 9th Ave & Irving", "14 min left on N Judah"),
        JourneyUpcomingStep("9:57 AM", "🚶", "Arrive at California Academy of Sciences", "8-minute walk through Golden Gate Park")
    )
)

data class JourneyUpcomingStep(
    val time: String,
    val icon: String,
    val title: String,
    val subtitle: String
)
