package com.simats.multimodaltransportationrecommendation.ui.booking

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookJourneyRequest
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import com.simats.multimodaltransportationrecommendation.ui.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewBookingUiState(
    val dateText: String = "TODAY",
    val passengerText: String = "1 Passenger",
    val origin: String = "Chennai Central",
    val destination: String = "Marina Beach, Chennai",
    val timeRange: String = "09:30 – 09:48 AM",
    val duration: String = "18 min",
    val transfers: String = "0 transfers",
    val stepDetails: String = "Walk 2 min → CMRL Blue Line 12 min → Walk 4 min",
    val ticketItem: String = "CMRL Metro & Transit Pass × 1",
    val ticketPrice: String = "₹30",
    val bookingFee: String = "FREE (₹0)",
    val totalPayable: String = "₹30",
    val termsNote: String = "Your digital transit pass activates immediately upon booking. Valid across Tamil Nadu transit networks.",
    val routeOption: RouteOption? = null,
    val isBooking: Boolean = false,
    val errorMessage: String? = null,
    val bookedJourney: BookedJourneyData? = null
)

class ReviewBookingViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewBookingUiState())
    val uiState: StateFlow<ReviewBookingUiState> = _uiState.asStateFlow()

    fun setBookingDetails(
        route: RouteOption?,
        origin: String = "Chennai Central",
        destination: String = "Marina Beach, Chennai"
    ) {
        if (route == null) return
        val stepsStr = if (route.steps.isNotEmpty()) route.steps.joinToString(" → ") else "${route.mode} (${route.durationText})"
        _uiState.value = _uiState.value.copy(
            origin = origin,
            destination = destination,
            duration = route.durationText,
            transfers = "${route.transfers} transfers",
            stepDetails = stepsStr,
            ticketItem = "${route.title} · Tamil Nadu Transit Pass",
            ticketPrice = route.fareText,
            totalPayable = route.fareText,
            routeOption = route,
            errorMessage = null
        )
    }

    fun confirmAndBook(
        context: Context,
        onSuccess: (BookedJourneyData) -> Unit
    ) {
        val current = _uiState.value
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = current.copy(isBooking = true, errorMessage = null)

        viewModelScope.launch {
            val req = BookJourneyRequest(
                userId = userId,
                origin = current.origin.ifBlank { "Chennai Central" },
                destination = current.destination.ifBlank { "Marina Beach, Chennai" },
                mode = current.routeOption?.mode ?: "metro",
                routeTitle = current.routeOption?.title ?: "Chennai Metro Rail (CMRL)",
                durationMinutes = current.routeOption?.durationMinutes ?: 18,
                distanceKm = current.routeOption?.distanceKm ?: 4.8,
                fare = current.routeOption?.fare ?: 30.0,
                transfers = current.routeOption?.transfers ?: 0,
                co2SavedKg = current.routeOption?.co2SavedKg ?: 0.0,
                co2EmittedKg = current.routeOption?.co2EmittedKg ?: 0.0,
                journeyDate = "Today, ${java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())}"
            )

            when (val result = authRepository.bookJourney(req)) {
                is NetworkResult.Success -> {
                    val booked = result.data
                    _uiState.value = _uiState.value.copy(
                        isBooking = false,
                        bookedJourney = booked,
                        errorMessage = null
                    )
                    NotificationHelper.showNotification(
                        context = context,
                        title = "Journey Booked · ${booked.bookingReference}",
                        message = "Your ${booked.mode} journey from ${booked.origin} to ${booked.destination} is confirmed (${booked.fareText}).",
                        channelId = NotificationHelper.CHANNEL_JOURNEY
                    )
                    onSuccess(booked)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isBooking = false,
                        errorMessage = result.message ?: "Booking could not be completed"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
