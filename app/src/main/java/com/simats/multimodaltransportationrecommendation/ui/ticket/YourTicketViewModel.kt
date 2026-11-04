package com.simats.multimodaltransportationrecommendation.ui.ticket

import androidx.lifecycle.ViewModel
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class YourTicketUiState(
    val userName: String = "Traveler",
    val confirmationText: String = "Booking confirmed · ₹30 paid",
    val transportLine: String = "CMRL · BLUE LINE METRO",
    val passengerType: String = "1 Passenger Pass",
    val origin: String = "Chennai Central",
    val destination: String = "Marina Beach, Chennai",
    val dateTime: String = "Today, 09:30 AM",
    val ticketCode: String = "TN-MS-928174",
    val ticketValidity: String = "Valid for 4 hours across Tamil Nadu transit networks",
    val ticketNotice: String = "Present this digital QR pass at metro gates or transit conductors.",
    val durationText: String = "18 min",
    val fareText: String = "₹30"
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
