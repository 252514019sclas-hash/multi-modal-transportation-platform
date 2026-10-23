package com.simats.multimodaltransportationrecommendation.ui.recommended

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecommendedRouteViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendedRouteUiState())
    val uiState: StateFlow<RecommendedRouteUiState> = _uiState.asStateFlow()
}

data class RecommendedRouteUiState(
    val title: String = "Walk + N Judah",
    val duration: String = "42 min",
    val timeRange: String = "9:15–9:57 AM",
    val totalFare: String = "$3.00",
    val transfers: Int = 0,
    val dateText: String = "Today, Oct 20",
    val steps: List<JourneyStep> = listOf(
        JourneyStep("9:15 AM", "🚶", "Walk to Embarcadero station", "From Ferry Building · 6 min · 0.3 mi"),
        JourneyStep("9:21 AM", "🚊", "N Judah toward Ocean Beach", "Ride 28 min to 9th Ave & Irving · $3.00"),
        JourneyStep("9:49 AM", "🚶", "Walk to the Academy", "Via 9th Ave & Music Concourse Dr · 8 min"),
        JourneyStep("9:57 AM", "📍", "California Academy of Sciences", "55 Music Concourse Dr · You've arrived")
    ),
    val footerSummary: String = "One transit service · 14 min walking · 0.4 kg CO₂"
)

data class JourneyStep(
    val time: String,
    val icon: String,
    val title: String,
    val description: String
)
