package com.simats.multimodaltransportationrecommendation.data.model.tickets

import com.google.gson.annotations.SerializedName

data class TicketItem(
    @SerializedName("ticket_id")
    val ticketId: Int,
    @SerializedName("booking_reference")
    val bookingReference: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("passenger_name")
    val passengerName: String,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("mode")
    val mode: String,
    @SerializedName("route_title")
    val routeTitle: String,
    @SerializedName("duration_minutes")
    val durationMinutes: Int,
    @SerializedName("distance_km")
    val distanceKm: Double,
    @SerializedName("fare")
    val fare: Double,
    @SerializedName("fare_text")
    val fareText: String,
    @SerializedName("transfers")
    val transfers: Int = 0,
    @SerializedName("status")
    val status: String, // "upcoming", "completed", "cancelled"
    @SerializedName("journey_date")
    val journeyDate: String,
    @SerializedName("payment_status")
    val paymentStatus: String = "successful", // "successful", "pending", "failed"
    @SerializedName("transaction_id")
    val transactionId: String = "",
    @SerializedName("payment_date")
    val paymentDate: String? = null,
    @SerializedName("verification_status")
    val verificationStatus: String = "valid", // "valid", "used", "expired", "cancelled"
    @SerializedName("verified_at")
    val verifiedAt: String? = null,
    @SerializedName("qr_data")
    val qrData: String? = null
)

data class TicketsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("total")
    val total: Int = 0,
    @SerializedName("tickets")
    val tickets: List<TicketItem>? = null,
    @SerializedName("message")
    val message: String? = null
)

data class VerifyTicketRequest(
    @SerializedName("qr_data")
    val qrData: String,
    @SerializedName("mark_used")
    val markUsed: Boolean = false
)

data class VerifyTicketResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("status")
    val status: String, // "valid", "invalid", "used", "expired", "cancelled", "error"
    @SerializedName("message")
    val message: String,
    @SerializedName("ticket")
    val ticket: TicketItem? = null
)

data class PaymentStatusResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("payment_status")
    val paymentStatus: String, // "successful", "pending", "failed"
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("booking_reference")
    val bookingReference: String,
    @SerializedName("journey_id")
    val journeyId: Int? = null,
    @SerializedName("passenger_name")
    val passengerName: String? = null,
    @SerializedName("amount")
    val amount: Double = 0.0,
    @SerializedName("amount_text")
    val amountText: String = "",
    @SerializedName("payment_date")
    val paymentDate: String? = null,
    @SerializedName("route_title")
    val routeTitle: String? = null,
    @SerializedName("origin")
    val origin: String? = null,
    @SerializedName("destination")
    val destination: String? = null,
    @SerializedName("mode")
    val mode: String? = null,
    @SerializedName("gateway_provider")
    val gatewayProvider: String = "MoveSmart Secure Transit Gateway",
    @SerializedName("message")
    val message: String
)

