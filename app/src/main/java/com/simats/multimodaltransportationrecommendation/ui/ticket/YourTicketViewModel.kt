package com.simats.multimodaltransportationrecommendation.ui.ticket

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class YourTicketViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(YourTicketUiState())
    val uiState: StateFlow<YourTicketUiState> = _uiState.asStateFlow()
}

data class YourTicketUiState(
    val userName: String = "Alex",
    val confirmationText: String = "Booking confirmed · $3.00 paid",
    val transportLine: String = "MUNI · N JUDAH",
    val passengerType: String = "1 adult",
    val destination: String = "California Academy of Sciences",
    val dateTime: String = "Today, Oct 20 - 9:15–9:57 AM",
    val ticketCode: String = "MS-261020-N042",
    val ticketValidity: String = "Valid for 2 hours after activation",
    val ticketNotice: String = "Keep this ticket ready for fare inspection."
)
