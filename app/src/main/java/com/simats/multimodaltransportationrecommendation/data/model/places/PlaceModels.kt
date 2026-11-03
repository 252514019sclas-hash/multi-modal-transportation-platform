package com.simats.multimodaltransportationrecommendation.data.model.places

import com.google.gson.annotations.SerializedName

data class TouristPlace(
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("location_address")
    val locationAddress: String,
    @SerializedName("visiting_hours")
    val visitingHours: String? = "9:00 AM - 6:00 PM",
    @SerializedName("entry_fee")
    val entryFee: String? = "Free",
    @SerializedName("rating")
    val rating: Double = 4.8,
    @SerializedName("distance_km")
    val distanceKm: Double = 2.5,
    @SerializedName("duration_visit")
    val durationVisit: String? = "2-3 hours",
    @SerializedName("is_saved")
    val isSaved: Boolean = false,
    @SerializedName("is_recommended")
    val isRecommended: Boolean = false,
    @SerializedName("ai_reason")
    val aiReason: String? = null
)

data class PlacesResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("places")
    val places: List<TouristPlace>? = null,
    @SerializedName("message")
    val message: String? = null
)

data class PlaceDetailsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("place")
    val place: TouristPlace? = null,
    @SerializedName("message")
    val message: String? = null
)

data class SavePlaceRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("action")
    val action: String = "toggle" // "save", "remove", "toggle"
)

data class SavePlaceResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("is_saved")
    val isSaved: Boolean,
    @SerializedName("message")
    val message: String
)

data class SavedPlacesResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("saved_places")
    val savedPlaces: List<TouristPlace>? = null,
    @SerializedName("message")
    val message: String? = null
)

