package com.simats.multimodaltransportationrecommendation.ui.journey

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OnYourWayUiState(
    val currentTime: String = "09:36 AM",
    val remainingTimeText: String = "12 min remaining · On time",
    val activeStatusTitle: String = "You're on CMRL Blue Line",
    val nextStopInfo: String = "Next: Thousand Lights Metro · 09:38 AM",
    val reminderText: String = "We'll remind you 1 station before Government Estate",
    val upcomingSteps: List<JourneyUpcomingStep> = listOf(
        JourneyUpcomingStep("09:44 AM", "🚇", "Exit at Government Estate Metro Station", "Direct interchange exit for Marina Promenade"),
        JourneyUpcomingStep("09:48 AM", "🚶", "Arrive at Marina Beach & Promenade", "4-minute walk along Kamarajar Salai")
    )
)

data class JourneyUpcomingStep(
    val time: String,
    val icon: String,
    val title: String,
    val subtitle: String
)

class OnYourWayViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnYourWayUiState())
    val uiState: StateFlow<OnYourWayUiState> = _uiState.asStateFlow()
}
