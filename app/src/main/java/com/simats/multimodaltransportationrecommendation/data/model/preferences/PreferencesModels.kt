package com.simats.multimodaltransportationrecommendation.data.model.preferences

import com.google.gson.annotations.SerializedName

data class PreferencesData(
    @SerializedName("modes")
    val modes: String = "Walking, Cycling, Bus, Metro",
    @SerializedName("low_carbon")
    val lowCarbon: Boolean = true,
    @SerializedName("travel_purpose")
    val travelPurpose: String = "Daily Commute",
    @SerializedName("destination_types")
    val destinationTypes: String = "Parks, Museums",
    @SerializedName("distance_preference")
    val distancePreference: String = "Within City",
    @SerializedName("tourist_recommendations")
    val touristRecommendations: Boolean = true,
    @SerializedName("budget_tier")
    val budgetTier: String = "Moderate",
    @SerializedName("max_duration")
    val maxDuration: String = "Under 45 min",
    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean = true,
    @SerializedName("journey_reminders")
    val journeyReminders: Boolean = true,
    @SerializedName("booking_updates")
    val bookingUpdates: Boolean = true,
    @SerializedName("travel_alerts")
    val travelAlerts: Boolean = true
)

data class PreferencesResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("preferences")
    val preferences: PreferencesData? = null,
    @SerializedName("message")
    val message: String? = null
)

data class UpdatePreferencesRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("modes")
    val modes: String,
    @SerializedName("low_carbon")
    val lowCarbon: Boolean,
    @SerializedName("travel_purpose")
    val travelPurpose: String = "Daily Commute",
    @SerializedName("destination_types")
    val destinationTypes: String = "Parks, Museums",
    @SerializedName("distance_preference")
    val distancePreference: String = "Within City",
    @SerializedName("tourist_recommendations")
    val touristRecommendations: Boolean = true,
    @SerializedName("budget_tier")
    val budgetTier: String = "Moderate",
    @SerializedName("max_duration")
    val maxDuration: String = "Under 45 min",
    @SerializedName("notifications_enabled")
    val notificationsEnabled: Boolean = true,
    @SerializedName("journey_reminders")
    val journeyReminders: Boolean = true,
    @SerializedName("booking_updates")
    val bookingUpdates: Boolean = true,
    @SerializedName("travel_alerts")
    val travelAlerts: Boolean = true
)
