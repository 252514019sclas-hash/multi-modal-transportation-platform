package com.simats.multimodaltransportationrecommendation.data.model.stats

import com.google.gson.annotations.SerializedName

data class UserStatsData(
    @SerializedName("co2_impact")
    val co2Impact: String = "0.0 kg CO₂ avoided",
    @SerializedName("co2_subtitle")
    val co2Subtitle: String = "Complete journeys to start saving carbon",
    @SerializedName("total_co2_kg")
    val totalCo2Kg: Double = 0.0,
    @SerializedName("total_distance_km")
    val totalDistanceKm: Double = 0.0,
    @SerializedName("completed_trips")
    val completedTrips: Int = 0,
    @SerializedName("upcoming_trips")
    val upcomingTrips: Int = 0,
    @SerializedName("saved_places_count")
    val savedPlacesCount: String = "0 places",
    @SerializedName("saved_routes_count")
    val savedRoutesCount: Int = 0,
    @SerializedName("payment_method")
    val paymentMethod: String = "None added",
    @SerializedName("travel_preferences_summary")
    val travelPreferencesSummary: String = "Low-carbon · Walk, cycle, bus & metro",
    @SerializedName("notifications_text")
    val notificationsText: String = "On"
)

data class StatsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("stats")
    val stats: UserStatsData? = null,
    @SerializedName("message")
    val message: String? = null
)

