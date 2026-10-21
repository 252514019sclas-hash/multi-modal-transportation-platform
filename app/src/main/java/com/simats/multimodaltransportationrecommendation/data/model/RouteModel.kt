package com.simats.multimodaltransportationrecommendation.data.model

data class RouteModel(
    val id: String,
    val title: String,
    val mode: String,
    val durationMinutes: Int,
    val carbonSavedKg: Double
)
