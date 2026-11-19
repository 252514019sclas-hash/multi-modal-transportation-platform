package com.simats.multimodaltransportationrecommendation.data.model.stopover

data class AiStopoverItem(
    val id: String,
    val name: String,
    val type: String, // "start", "stop", "destination"
    val badgeText: String, // "Start", "Stop 1 • 1h", "Stop 2 • 1h 30m", "Destination"
    val shortDescription: String,
    val timeWindowText: String, // "1h (09:45 AM – 10:45 AM)" or "Departure: 09:00 AM"
    val durationMinutes: Int,
    val distanceFromOriginKm: Double,
    val distanceBadgeText: String, // "0 km", "12 km", "20 km"
    val imageUrl: String,
    val rating: Double = 4.8,
    val category: String = "Attraction",
    val city: String = "Tamil Nadu",
    val latitude: Double = 13.0827,
    val longitude: Double = 80.2707,
    val isDetour: Boolean = false
)

data class AiDetourOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val extraTimeMinutes: Int,
    val extraDistanceKm: Double,
    val places: List<AiStopoverItem>,
    val aiBenefit: String,
    val isApplied: Boolean = false
)

data class TravelInsightsData(
    val temperature: String = "30°C",
    val weatherCondition: String = "Sunny",
    val crowdLevel: String = "Moderate Crowd",
    val overallRating: String = "4.6 / 5",
    val bestTimeVisit: String = "Nov – Feb",
    val summaryText: String = "Ideal weather conditions with gentle breeze and smooth highway traffic flow.",
    val peakHours: String = "11:30 AM – 03:00 PM",
    val localAdvice: String = "Carry light cotton clothing and stay hydrated. Temple visits are best before 11 AM."
)

data class SpecialOfferItem(
    val id: String,
    val code: String,
    val title: String,
    val discount: String,
    val description: String,
    val validUntil: String = "Valid today",
    val category: String = "Sightseeing"
)

data class AiSmartStopoverPlan(
    val origin: String,
    val destination: String,
    val transitMode: String = "Bus & Train",
    val totalTravelTimeText: String = "~ 5h 30m",
    val totalDistanceKm: Double = 28.0,
    val stops: List<AiStopoverItem>,
    val detourOptions: List<AiDetourOption>,
    val insights: TravelInsightsData,
    val specialOffers: List<SpecialOfferItem>,
    val routeOptimizationNote: String = "AI Optimized: Fastest scenic route saving 45 mins with 2 en-route attractions."
)
