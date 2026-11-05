package com.simats.multimodaltransportationrecommendation.ui.ticket

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.tickets.PaymentStatusResponse
import com.simats.multimodaltransportationrecommendation.data.model.tickets.TicketItem
import com.simats.multimodaltransportationrecommendation.data.model.tickets.VerifyTicketResponse
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class YourTicketUiState(
    val selectedTab: String = "Upcoming", // "Upcoming", "Completed", "Cancelled"
    val tickets: List<TicketItem> = emptyList(),
    val selectedTicket: TicketItem? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val scannerActive: Boolean = false,
    val isVerifying: Boolean = false,
    val scanResult: VerifyTicketResponse? = null,
    val scanErrorMessage: String? = null,
    val isCheckingPayment: Boolean = false,
    val paymentStatusResult: PaymentStatusResponse? = null,
    val statusMessage: String? = null
)

class YourTicketViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(YourTicketUiState())
    val uiState: StateFlow<YourTicketUiState> = _uiState.asStateFlow()

    fun loadTickets(context: Context) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null)

        viewModelScope.launch {
            when (val result = authRepository.fetchTickets(userId, "all")) {
                is NetworkResult.Success -> {
                    val list = result.data
                    val currentSelected = _uiState.value.selectedTicket
                    val newSelected = if (currentSelected != null) {
                        list.find { it.bookingReference == currentSelected.bookingReference } ?: currentSelected
                    } else {
                        list.firstOrNull { it.status == "upcoming" } ?: list.firstOrNull()
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tickets = list,
                        selectedTicket = newSelected
                    )
                    // If selected ticket exists, load its backend payment status
                    newSelected?.let {
                        checkPaymentStatus(it.bookingReference)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        statusMessage = result.message
                    )
                }
            }
        }
    }

    fun selectTab(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun selectTicket(ticket: TicketItem) {
        _uiState.value = _uiState.value.copy(selectedTicket = ticket)
        checkPaymentStatus(ticket.bookingReference)
    }

    fun toggleScanner(active: Boolean) {
        _uiState.value = _uiState.value.copy(
            scannerActive = active,
            scanResult = if (!active) null else _uiState.value.scanResult,
            scanErrorMessage = if (!active) null else _uiState.value.scanErrorMessage
        )
    }

    fun verifyScannedCode(rawCode: String, markUsed: Boolean = false) {
        if (rawCode.isBlank()) return
        _uiState.value = _uiState.value.copy(isVerifying = true, scanErrorMessage = null)

        viewModelScope.launch {
            when (val result = authRepository.verifyTicket(rawCode.trim(), markUsed)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    _uiState.value = _uiState.value.copy(
                        isVerifying = false,
                        scanResult = response,
                        scanErrorMessage = if (!response.success && response.status == "error") response.message else null
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isVerifying = false,
                        scanErrorMessage = result.message ?: "Could not verify ticket with MoveSmart server"
                    )
                }
            }
        }
    }

    fun checkPaymentStatus(bookingReference: String) {
        if (bookingReference.isBlank()) return
        _uiState.value = _uiState.value.copy(isCheckingPayment = true)

        viewModelScope.launch {
            when (val result = authRepository.getPaymentStatus(bookingReference = bookingReference)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isCheckingPayment = false,
                        paymentStatusResult = result.data
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isCheckingPayment = false
                    )
                }
            }
        }
    }

    fun setTicketFromBooking(booking: BookedJourneyData) {
        val synthetic = TicketItem(
            ticketId = booking.journeyId,
            bookingReference = booking.bookingReference,
            userId = 1,
            passengerName = "MoveSmart Traveler",
            origin = booking.origin,
            destination = booking.destination,
            mode = booking.mode,
            routeTitle = booking.routeTitle,
            durationMinutes = booking.durationMinutes,
            distanceKm = 5.0,
            fare = booking.fare,
            fareText = booking.fareText,
            transfers = 0,
            status = booking.status,
            journeyDate = booking.journeyDate,
            paymentStatus = "successful",
            transactionId = "TXN-" + booking.bookingReference,
            verificationStatus = "valid"
        )
        _uiState.value = _uiState.value.copy(
            selectedTicket = synthetic,
            selectedTab = "Upcoming"
        )
        checkPaymentStatus(booking.bookingReference)
    }

    fun setTicketFromTrip(trip: TripItem) {
        val synthetic = TicketItem(
            ticketId = trip.journeyId,
            bookingReference = trip.bookingReference,
            userId = trip.userId,
            passengerName = "MoveSmart Traveler",
            origin = trip.origin,
            destination = trip.destination,
            mode = trip.mode,
            routeTitle = trip.routeTitle,
            durationMinutes = trip.durationMinutes,
            distanceKm = trip.distanceKm,
            fare = trip.fare,
            fareText = trip.fareText,
            transfers = trip.transfers,
            status = trip.status,
            journeyDate = trip.journeyDate,
            paymentStatus = "successful",
            transactionId = "TXN-" + trip.bookingReference,
            verificationStatus = if (trip.status == "completed") "used" else "valid"
        )
        _uiState.value = _uiState.value.copy(
            selectedTicket = synthetic,
            selectedTab = when (trip.status) {
                "completed" -> "Completed"
                "cancelled" -> "Cancelled"
                else -> "Upcoming"
            }
        )
        checkPaymentStatus(trip.bookingReference)
    }

    fun clearScanResult() {
        _uiState.value = _uiState.value.copy(
            scanResult = null,
            scanErrorMessage = null
        )
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }
}
