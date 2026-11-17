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

data class TripStop(
    @SerializedName("stop_name")
    val stopName: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("arrival_time")
    val arrivalTime: String,
    @SerializedName("departure_time")
    val departureTime: String,
    @SerializedName("platform")
    val platform: String = "Platform 1"
)

data class TripAttraction(
    @SerializedName("place_id")
    val placeId: Any?, // String or Int from JSON
    @SerializedName("name")
    val name: String,
    @SerializedName("city")
    val city: String = "Tamil Nadu",
    @SerializedName("category")
    val category: String = "Attraction",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("rating")
    val rating: Any? = 4.8,
    @SerializedName("distance_km")
    val distanceKm: Any? = 2.5,
    @SerializedName("entry_fee")
    val entryFee: String? = "Free",
    @SerializedName("duration_visit")
    val durationVisit: String? = "1-2 hrs"
)

data class TripScheduleItem(
    @SerializedName("time")
    val time: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("desc")
    val desc: String
)

data class TripDetailData(
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
    @SerializedName("status")
    val status: String,
    @SerializedName("journey_date")
    val journeyDate: String,
    @SerializedName("booking_reference")
    val bookingReference: String,
    @SerializedName("ticket_code")
    val ticketCode: String = "",
    @SerializedName("payment_status")
    val paymentStatus: String = "successful",
    @SerializedName("transaction_id")
    val transactionId: String = "",
    @SerializedName("intermediate_stops")
    val intermediateStops: List<TripStop> = emptyList(),
    @SerializedName("tourist_places")
    val touristPlaces: List<TripAttraction> = emptyList(),
    @SerializedName("travel_schedule")
    val travelSchedule: List<TripScheduleItem> = emptyList()
)

data class TripDetailResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("journey")
    val journey: TripDetailData? = null
)

data class SaveAiItineraryRequest(
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
    @SerializedName("journey_date")
    val journeyDate: String,
    @SerializedName("transfers")
    val transfers: Int = 1,
    @SerializedName("co2_saved_kg")
    val co2SavedKg: Double = 0.85
)


