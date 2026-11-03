package com.simats.multimodaltransportationrecommendation.ui.ticket

import androidx.lifecycle.ViewModel
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class YourTicketUiState(
    val userName: String = "Alex",
    val confirmationText: String = "Booking confirmed · $3.00 paid",
    val transportLine: String = "MUNI · N JUDAH",
    val passengerType: String = "1 adult",
    val origin: String = "Ferry Building",
    val destination: String = "California Academy of Sciences",
    val dateTime: String = "Today, Oct 20 - 9:15–9:57 AM",
    val ticketCode: String = "MS-261020-N042",
    val ticketValidity: String = "Valid for 2 hours after activation",
    val ticketNotice: String = "Keep this digital ticket ready for fare inspection.",
    val durationText: String = "42 min",
    val fareText: String = "$3.00"
)

class YourTicketViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(YourTicketUiState())
    val uiState: StateFlow<YourTicketUiState> = _uiState.asStateFlow()

    fun setTicketFromBooking(booking: BookedJourneyData) {
        _uiState.value = _uiState.value.copy(
            confirmationText = "Booking confirmed · ${booking.fareText} paid",
            transportLine = "${booking.mode.uppercase()} · ${booking.routeTitle.uppercase()}",
            origin = booking.origin,
            destination = booking.destination,
            ticketCode = booking.bookingReference,
            dateTime = booking.journeyDate,
            durationText = "${booking.durationMinutes} min",
            fareText = booking.fareText
        )
    }

    fun setTicketFromTrip(trip: TripItem) {
        _uiState.value = _uiState.value.copy(
            confirmationText = "Confirmed Ticket · ${trip.fareText}",
            transportLine = "${trip.mode.uppercase()} · ${trip.routeTitle.uppercase()}",
            origin = trip.origin,
            destination = trip.destination,
            ticketCode = trip.bookingReference,
            dateTime = trip.journeyDate,
            durationText = "${trip.durationMinutes} min",
            fareText = trip.fareText
        )
    }
}
