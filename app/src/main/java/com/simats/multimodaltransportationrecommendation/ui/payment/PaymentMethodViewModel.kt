package com.simats.multimodaltransportationrecommendation.ui.payment

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PaymentMethodViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentMethodUiState())
    val uiState: StateFlow<PaymentMethodUiState> = _uiState.asStateFlow()

    fun selectMethod(method: String) {
        _uiState.value = _uiState.value.copy(selectedMethod = method)
    }
}

data class PaymentMethodUiState(
    val amount: String = "$3.00",
    val subtitle: String = "1 adult · Muni N Judah · Today",
    val selectedMethod: String = "Visa",
    val cardBrand: String = "VISA",
    val cardNumber: String = "•••• •••• •••• 4242",
    val cardHolder: String = "ALEX MORGAN",
    val cardExpiry: String = "08/29",
    val securityNotice: String = "Your payment is encrypted. We never store your full card number or security code."
)
