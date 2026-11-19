package com.simats.multimodaltransportationrecommendation.data.model.facilities

import com.google.gson.annotations.SerializedName

data class TravelFacility(
    @SerializedName("facility_id")
    val facilityId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("nearby_landmark")
    val nearbyLandmark: String? = null,

    @SerializedName("category")
    val category: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("district")
    val district: String? = null,

    @SerializedName("address")
    val address: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("contact_number")
    val contactNumber: String? = null,

    @SerializedName("operating_hours")
    val operatingHours: String? = "Open 24/7",

    @SerializedName("rating")
    val rating: Double = 4.6,

    @SerializedName("amenities")
    val amenities: String? = "",

    @SerializedName("is_24_hours")
    val is24Hours: Boolean = true,

    @SerializedName("distance_km")
    val distanceKm: Double = 0.0
)

data class FacilityCategorySummary(
    @SerializedName("id")
    val id: String,

    @SerializedName("label")
    val label: String,

    @SerializedName("icon")
    val icon: String,

    @SerializedName("count")
    val count: Int
)

data class NearbyFacilitiesResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("searched_location")
    val searchedLocation: String? = null,

    @SerializedName("target_place")
    val targetPlace: String? = null,

    @SerializedName("available_landmarks")
    val availableLandmarks: List<String> = emptyList(),

    @SerializedName("total_facilities")
    val totalFacilities: Int = 0,

    @SerializedName("categories")
    val categories: List<FacilityCategorySummary> = emptyList(),

    @SerializedName("facilities")
    val facilities: List<TravelFacility> = emptyList(),

    @SerializedName("message")
    val message: String? = null
)
