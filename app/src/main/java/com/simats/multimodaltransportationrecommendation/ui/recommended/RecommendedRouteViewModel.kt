package com.simats.multimodaltransportationrecommendation.ui.recommended

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RecommendedRouteUiState(
    val title: String = "Walk + CMRL Metro Blue Line",
    val duration: String = "18 min",
    val timeRange: String = "09:30 – 09:48 AM",
    val totalFare: String = "₹30",
    val transfers: Int = 0,
    val dateText: String = "Today",
    val steps: List<JourneyStep> = listOf(
        JourneyStep("09:30 AM", "🚶", "Walk to Chennai Central Metro Station", "From Central Concourse · 2 min · 150m"),
        JourneyStep("09:32 AM", "🚇", "CMRL Blue Line toward Government Estate", "Ride 12 min to Government Estate · ₹30"),
        JourneyStep("09:44 AM", "🚶", "Walk to Marina Beach Promenade", "Via Kamarajar Salai · 4 min"),
        JourneyStep("09:48 AM", "📍", "Marina Beach & Promenade", "Kamarajar Salai, Chennai · You've arrived!")
    ),
    val footerSummary: String = "High speed CMRL Metro · 6 min walk · Quick & convenient"
)

data class JourneyStep(
    val time: String,
    val icon: String,
    val title: String,
    val description: String
)

class RecommendedRouteViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendedRouteUiState())
    val uiState: StateFlow<RecommendedRouteUiState> = _uiState.asStateFlow()
}
