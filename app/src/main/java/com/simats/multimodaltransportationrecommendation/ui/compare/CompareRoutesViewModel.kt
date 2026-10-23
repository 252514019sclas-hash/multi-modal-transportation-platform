package com.simats.multimodaltransportationrecommendation.ui.compare

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompareRoutesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CompareRoutesUiState())
    val uiState: StateFlow<CompareRoutesUiState> = _uiState.asStateFlow()

    fun selectRoute(routeTitle: String) {
        _uiState.value = _uiState.value.copy(selectedRouteTitle = routeTitle)
    }
}

data class CompareRoutesUiState(
    val origin: String = "Ferry Building",
    val destination: String = "California Academy of Sciences",
    val selectedRouteTitle: String = "Walk + N Judah",
    val routes: List<ComparisonRoute> = listOf(
        ComparisonRoute("Walk + N Judah", "42 min", "$3.00", "14 min", "0.4 kg", "96%", true),
        ComparisonRoute("Muni bus - 5 Fulton", "48 min", "$3.00", "12 min", "0.6 kg", "92%", false),
        ComparisonRoute("Rideshare", "24 min", "$22.40", "2 min", "2.8 kg", "88%", false),
        ComparisonRoute("Bay Wheels bicycle", "39 min", "$4.50", "4 min", "0 kg", "94%", false)
    ),
    val aiInsight: String = "N Judah balances price and reliability with 86% less CO₂ than a rideshare.",
    val note: String = "Estimated fares and emissions. Reliability is the share of recent trips arriving within 5 minutes of schedule."
)

data class ComparisonRoute(
    val title: String,
    val time: String,
    val fare: String,
    val walk: String,
    val co2: String,
    val onTime: String,
    val isAiPick: Boolean
)
