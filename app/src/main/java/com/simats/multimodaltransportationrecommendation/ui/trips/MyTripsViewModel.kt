package com.simats.multimodaltransportationrecommendation.ui.trips

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyTripsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyTripsUiState())
    val uiState: StateFlow<MyTripsUiState> = _uiState.asStateFlow()

    fun selectTab(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}

data class MyTripsUiState(
    val selectedTab: String = "Upcoming / active",
    val activeTrip: ActiveTrip = ActiveTrip(
        badge = "READY TO START",
        dateText = "Today, Oct 20",
        origin = "Ferry Building",
        destination = "California Academy of Sciences",
        timeRange = "9:15–9:57 AM - 42 min",
        paidText = "$3.00 paid"
    ),
    val recentTrips: List<PastTrip> = listOf(
        PastTrip("Work · SoMa", "Mon, Oct 18", "$3.00", "Noe Valley → 2Nd & Howard", "J Church + Walk · 35 min", "Completed"),
        PastTrip("Dolores Park", "Sun, Oct 17", "$4.50", "Ferry Building → Dolores Park", "Bay Wheels bicycle · 28 min", "Completed"),
        PastTrip("Home - Noe Valley", "Sat, Oct 16", "$3.00", "SFMOMA → Noe Valley", "Muni bus · 24 · 15 min", "Completed")
    )
)

data class ActiveTrip(
    val badge: String,
    val dateText: String,
    val origin: String,
    val destination: String,
    val timeRange: String,
    val paidText: String
)

data class PastTrip(
    val title: String,
    val date: String,
    val cost: String,
    val route: String,
    val details: String,
    val status: String
)
