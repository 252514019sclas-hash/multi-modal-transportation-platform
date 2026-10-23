package com.simats.multimodaltransportationrecommendation.ui.booking

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReviewBookingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewBookingUiState())
    val uiState: StateFlow<ReviewBookingUiState> = _uiState.asStateFlow()
}

data class ReviewBookingUiState(
    val dateText: String = "TODAY, OCT 20",
    val passengerText: String = "1 adult",
    val origin: String = "Ferry Building",
    val destination: String = "California Academy of Sciences",
    val timeRange: String = "9:15–9:57 AM",
    val duration: String = "42 min",
    val transfers: String = "0 transfers",
    val stepDetails: String = "Walk 6 min → N Judah 28 min → Walk 8 min",
    val ticketItem: String = "Muni adult ticket × 1",
    val ticketPrice: String = "$3.00",
    val bookingFee: String = "$0.00",
    val totalPayable: String = "$3.00",
    val cardName: String = "Visa •••• 4242",
    val cardHolder: String = "Alex Morgan · Expires 08/29",
    val termsNote: String = "Your ticket activates when you start the journey. By paying, you agree to Muni fare conditions."
)
