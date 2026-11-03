package com.simats.multimodaltransportationrecommendation.data.model.trips

import com.google.gson.annotations.SerializedName

data class TripItem(
    @SerializedName("journey_id")
    val journeyId: Int,
    @SerializedName("user_id")
    val userId: Int,
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
    @SerializedName("co2_saved_kg")
    val co2SavedKg: Double = 0.0,
    @SerializedName("co2_emitted_kg")
    val co2EmittedKg: Double = 0.0,
    @SerializedName("status")
    val status: String, // "upcoming", "completed", "cancelled"
    @SerializedName("journey_date")
    val journeyDate: String,
    @SerializedName("booking_reference")
    val bookingReference: String,
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class TripsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("trips")
    val trips: List<TripItem>? = null,
    @SerializedName("message")
    val message: String? = null
)

data class BookJourneyRequest(
    @SerializedName("user_id")
    val userId: Int,
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
    @SerializedName("transfers")
    val transfers: Int = 0,
    @SerializedName("co2_saved_kg")
    val co2SavedKg: Double,
    @SerializedName("co2_emitted_kg")
    val co2EmittedKg: Double,
    @SerializedName("journey_date")
    val journeyDate: String
)

data class BookedJourneyData(
    @SerializedName("journey_id")
    val journeyId: Int,
    @SerializedName("booking_reference")
    val bookingReference: String,
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
    @SerializedName("fare")
    val fare: Double,
    @SerializedName("fare_text")
    val fareText: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("journey_date")
    val journeyDate: String
)

data class BookJourneyResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("journey")
    val journey: BookedJourneyData? = null
)

data class UpdateTripStatusRequest(
    @SerializedName("journey_id")
    val journeyId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("status")
    val status: String // "completed" or "cancelled"
)

