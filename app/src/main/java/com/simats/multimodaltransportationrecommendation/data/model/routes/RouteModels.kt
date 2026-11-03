package com.simats.multimodaltransportationrecommendation.data.model.routes

import com.google.gson.annotations.SerializedName

data class RouteOption(
    @SerializedName("id")
    val id: String,
    @SerializedName("mode")
    val mode: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("duration_minutes")
    val durationMinutes: Int,
    @SerializedName("duration_text")
    val durationText: String,
    @SerializedName("distance_km")
    val distanceKm: Double,
    @SerializedName("distance_text")
    val distanceText: String,
    @SerializedName("fare")
    val fare: Double,
    @SerializedName("fare_text")
    val fareText: String,
    @SerializedName("transfers")
    val transfers: Int = 0,
    @SerializedName("co2_emitted_kg")
    val co2EmittedKg: Double = 0.0,
    @SerializedName("co2_saved_kg")
    val co2SavedKg: Double = 0.0,
    @SerializedName("steps")
    val steps: List<String> = emptyList(),
    @SerializedName("route_details")
    val routeDetails: String,
    @SerializedName("badge")
    val badge: String? = null,
    @SerializedName("is_recommended")
    val isRecommended: Boolean = false
)

data class SearchRoutesRequest(
    @SerializedName("origin")
    val origin: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("date_time")
    val dateTime: String = "Today, 9:15 AM",
    @SerializedName("user_id")
    val userId: Int = 0
)

data class SearchRoutesResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("origin")
    val origin: String? = null,
    @SerializedName("destination")
    val destination: String? = null,
    @SerializedName("date_time")
    val dateTime: String? = null,
    @SerializedName("ai_insight")
    val aiInsight: String? = null,
    @SerializedName("data_source")
    val dataSource: String? = null,
    @SerializedName("routes")
    val routes: List<RouteOption>? = null,
    @SerializedName("message")
    val message: String? = null
)

data class SaveRouteRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("mode")
    val mode: String,
    @SerializedName("duration_text")
    val durationText: String,
    @SerializedName("cost_text")
    val costText: String,
    @SerializedName("co2_text")
    val co2Text: String,
    @SerializedName("action")
    val action: String = "save",
    @SerializedName("route_id")
    val routeId: Int? = null
)

data class SavedRoute(
    @SerializedName("route_id")
    val routeId: Int,
    @SerializedName("origin")
    val origin: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("mode")
    val mode: String,
    @SerializedName("duration_text")
    val durationText: String,
    @SerializedName("cost_text")
    val costText: String,
    @SerializedName("co2_text")
    val co2Text: String,
    @SerializedName("created_at")
    val createdAt: String? = null
)

data class SavedRoutesResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("saved_routes")
    val savedRoutes: List<SavedRoute>? = null,
    @SerializedName("message")
    val message: String? = null
)

