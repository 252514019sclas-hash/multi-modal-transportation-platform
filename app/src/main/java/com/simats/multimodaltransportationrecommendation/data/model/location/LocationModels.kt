package com.simats.multimodaltransportationrecommendation.data.model.location

import com.google.gson.annotations.SerializedName

data class LocationSearchResult(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("state")
    val state: String = "Tamil Nadu",
    @SerializedName("category")
    val category: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)

data class LocationSearchResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("query")
    val query: String = "",
    @SerializedName("total")
    val total: Int = 0,
    @SerializedName("locations")
    val locations: List<LocationSearchResult>? = null,
    @SerializedName("message")
    val message: String? = null
)

