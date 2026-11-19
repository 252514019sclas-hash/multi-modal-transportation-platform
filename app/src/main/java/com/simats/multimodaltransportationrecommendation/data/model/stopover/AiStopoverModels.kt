package com.simats.multimodaltransportationrecommendation.data.model.stopover

data class AiStopoverItem(
    val id: String,
    val name: String,
    val type: String, // "start", "stop", "destination"
    val badgeText: String, // "Start Hub", "Spot #1 • 1h", "Destination Attraction"
    val shortDescription: String,
    val timeWindowText: String,
    val durationMinutes: Int,
    val distanceFromOriginKm: Double,
    val distanceBadgeText: String, // "0 km", "12 km", "72 km"
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
    val summaryText: String = "",
    val peakHours: String = "",
    val localAdvice: String = ""
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
    val totalSpotsCount: Int = 0,
    val stops: List<AiStopoverItem>,
    val detourOptions: List<AiDetourOption> = emptyList(),
    val insights: TravelInsightsData? = null,
    val specialOffers: List<SpecialOfferItem> = emptyList(),
    val routeOptimizationNote: String = "AI Route Optimization: All tourist spots sequenced chronologically from starting point to destination to minimize travel detour."
)
