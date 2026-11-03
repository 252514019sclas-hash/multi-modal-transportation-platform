package com.simats.multimodaltransportationrecommendation.ui.payment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.payment.AddPaymentMethodRequest
import com.simats.multimodaltransportationrecommendation.data.model.payment.DeletePaymentMethodRequest
import com.simats.multimodaltransportationrecommendation.data.model.payment.PaymentMethodItem
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookJourneyRequest
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PaymentMethodUiState(
    val amount: String = "$3.00",
    val subtitle: String = "1 adult · Muni N Judah · Today",
    val selectedMethodId: Int? = null,
    val paymentMethods: List<PaymentMethodItem> = emptyList(),
    val isLoading: Boolean = false,
    val isBooking: Boolean = false,
    val statusMessage: String? = null,
    val bookedJourney: BookedJourneyData? = null,
    val securityNotice: String = "Your payment details are encrypted according to PCI-DSS standards. We never store raw card numbers or security CVV codes."
)

class PaymentMethodViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentMethodUiState())
    val uiState: StateFlow<PaymentMethodUiState> = _uiState.asStateFlow()

    fun loadPaymentMethods(context: Context) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null)

        viewModelScope.launch {
            when (val result = authRepository.fetchPaymentMethods(userId)) {
                is NetworkResult.Success -> {
                    val methods = result.data
                    val defaultMethod = methods.firstOrNull { it.isDefault } ?: methods.firstOrNull()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        paymentMethods = methods,
                        selectedMethodId = defaultMethod?.paymentId
                    )
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

    fun setBookingDetails(amount: String, subtitle: String) {
        _uiState.value = _uiState.value.copy(amount = amount, subtitle = subtitle)
    }

    fun selectMethod(paymentId: Int) {
        _uiState.value = _uiState.value.copy(selectedMethodId = paymentId)
    }

    fun addPaymentMethod(
        context: Context,
        methodType: String,
        title: String,
        subtitle: String?,
        onSuccess: () -> Unit = {}
    ) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val result = authRepository.addPaymentMethod(userId, methodType, title, subtitle)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(statusMessage = "Payment method added securely.")
                    loadPaymentMethods(context)
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(statusMessage = result.message)
                }
            }
        }
    }

    fun deletePaymentMethod(context: Context, paymentId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val result = authRepository.deletePaymentMethod(paymentId, userId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(statusMessage = "Payment method removed.")
                    loadPaymentMethods(context)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(statusMessage = result.message)
                }
            }
        }
    }

    fun payAndBook(
        context: Context,
        route: RouteOption?,
        origin: String,
        destination: String,
        onSuccess: (BookedJourneyData) -> Unit
    ) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isBooking = true, statusMessage = null)

        viewModelScope.launch {
            val req = BookJourneyRequest(
                userId = userId,
                origin = origin.ifBlank { "Ferry Building" },
                destination = destination.ifBlank { "California Academy of Sciences" },
                mode = route?.mode ?: "metro",
                routeTitle = route?.title ?: "Muni N Judah",
                durationMinutes = route?.durationMinutes ?: 42,
                distanceKm = route?.distanceKm ?: 7.5,
                fare = route?.fare ?: 3.00,
                transfers = route?.transfers ?: 0,
                co2SavedKg = route?.co2SavedKg ?: 1.2,
                co2EmittedKg = route?.co2EmittedKg ?: 0.4,
                journeyDate = "Today, 9:15 AM"
            )

            when (val result = authRepository.bookJourney(req)) {
                is NetworkResult.Success -> {
                    val booked = result.data
                    _uiState.value = _uiState.value.copy(
                        isBooking = false,
                        bookedJourney = booked,
                        statusMessage = "Payment confirmed! Journey booked."
                    )
                    com.simats.multimodaltransportationrecommendation.ui.notifications.NotificationHelper.showNotification(
                        context = context,
                        title = "Journey Booked · ${booked.bookingReference}",
                        message = "Your ${booked.mode} journey from ${booked.origin} to ${booked.destination} is confirmed (${booked.fareText}).",
                        channelId = com.simats.multimodaltransportationrecommendation.ui.notifications.NotificationHelper.CHANNEL_JOURNEY
                    )
                    onSuccess(booked)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isBooking = false,
                        statusMessage = result.message ?: "Booking failed"
                    )
                }
            }
        }
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }
}
