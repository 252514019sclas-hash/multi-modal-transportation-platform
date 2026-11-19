package com.simats.multimodaltransportationrecommendation.data.model.ai

import com.google.gson.annotations.SerializedName

data class AiTripPlanRequest(
    @SerializedName("user_id") val userId: Int = 1,
    @SerializedName("origin") val origin: String,
    @SerializedName("destination") val destination: String,
    @SerializedName("days") val days: Int,
    @SerializedName("trip_theme") val tripTheme: String = "Balanced",
    @SerializedName("stay_preference") val stayPreference: String = "Mid-Range"
)

data class AiTripSummary(
    @SerializedName("origin") val origin: String,
    @SerializedName("destination") val destination: String,
    @SerializedName("total_days") val totalDays: Int,
    @SerializedName("total_distance_km") val totalDistanceKm: Double,
    @SerializedName("estimated_driving_hours") val estimatedDrivingHours: Double,
    @SerializedName("trip_theme") val tripTheme: String,
    @SerializedName("stay_preference") val stayPreference: String,
    @SerializedName("total_attractions_planned") val totalAttractionsPlanned: Int,
    @SerializedName("booking_required") val bookingRequired: Boolean = false,
    @SerializedName("payment_required") val paymentRequired: Boolean = false,
    @SerializedName("advisory_notice") val advisoryNotice: String
)

data class AiScheduleSlot(
    @SerializedName("slot") val slot: String,
    @SerializedName("period") val period: String,
    @SerializedName("place_name") val placeName: String,
    @SerializedName("city") val city: String,
    @SerializedName("district") val district: String,
    @SerializedName("category") val category: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("description") val description: String,
    @SerializedName("transit_advice") val transitAdvice: String
)

data class RecommendedStay(
    @SerializedName("stay_name") val stayName: String,
    @SerializedName("city") val city: String,
    @SerializedName("district") val district: String,
    @SerializedName("stay_type") val stayType: String,
    @SerializedName("price_range") val priceRange: String,
    @SerializedName("rating") val rating: Double,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("location_address") val locationAddress: String,
    @SerializedName("amenities") val amenities: String?,
    @SerializedName("why_recommended") val whyRecommended: String?
)

data class AiDayPlan(
    @SerializedName("day_number") val dayNumber: Int,
    @SerializedName("day_title") val dayTitle: String,
    @SerializedName("travel_segment") val travelSegment: String,
    @SerializedName("hub_city") val hubCity: String,
    @SerializedName("theme_focus") val themeFocus: String,
    @SerializedName("schedule") val schedule: List<AiScheduleSlot>,
    @SerializedName("recommended_night_stay") val recommendedNightStay: RecommendedStay,
    @SerializedName("culinary_delicacy") val culinaryDelicacy: String,
    @SerializedName("enjoyment_advice") val enjoymentAdvice: String
)

data class AiTripPlanResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("trip_summary") val tripSummary: AiTripSummary? = null,
    @SerializedName("daily_itinerary") val dailyItinerary: List<AiDayPlan>? = null,
    @SerializedName("message") val message: String? = null
)
