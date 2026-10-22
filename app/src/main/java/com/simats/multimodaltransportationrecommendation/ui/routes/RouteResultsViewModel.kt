package com.simats.multimodaltransportationrecommendation.ui.routes

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RouteResultsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RouteResultsUiState())
    val uiState: StateFlow<RouteResultsUiState> = _uiState.asStateFlow()
}

data class RouteResultsUiState(
    val origin: String = "Ferry Building",
    val destination: String = "California Academy of Sciences",
    val aiInsight: String = "A low-carbon ride with no transit transfers. Just $3 and a comfortable 14-minute walk.",
    val recommendedRoute: RouteItem = RouteItem(
        title = "N Judah",
        duration = "42 min",
        cost = "$3.00",
        arrivalTime = "Arrive 9:57 AM",
        steps = listOf("Walk", "N Judah", "Walk"),
        details = "0 transfers · Walk 14 min · 0.4 kg CO₂",
        isRecommended = true
    ),
    val alternativeRoutes: List<RouteItem> = listOf(
        RouteItem(
            title = "Muni bus · 5 Fulton",
            duration = "48 min",
            cost = "$3.00",
            arrivalTime = "",
            steps = emptyList(),
            details = "0 transfers · Walk 12 min · 0.6 kg CO₂",
            isRecommended = false
        ),
        RouteItem(
            title = "Rideshare",
            duration = "24 min",
            cost = "$22.40",
            arrivalTime = "",
            steps = emptyList(),
            details = "Direct ride · Walk 2 min · 2.8 kg CO₂",
            isRecommended = false
        ),
        RouteItem(
            title = "Bay Wheels bicycle",
            duration = "39 min",
            cost = "$4.50",
            arrivalTime = "",
            steps = emptyList(),
            details = "No transfers · Walk 4 min · 0 kg CO₂",
            isRecommended = false
        )
    )
)

data class RouteItem(
    val title: String,
    val duration: String,
    val cost: String,
    val arrivalTime: String,
    val steps: List<String>,
    val details: String,
    val isRecommended: Boolean
)
