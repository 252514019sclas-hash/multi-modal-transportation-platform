package com.simats.multimodaltransportationrecommendation.data.api

object ApiConstants {
    /**
     * Common Base URL for all backend API endpoints.
     * Ensure there are no leading/trailing accidental spaces.
     */
    const val BASE_URL = "https://carrousel-poking-recluse.ngrok-free.dev/multimodeltrans/"

    fun getFullImageUrl(relativePath: String?): String? {
        if (relativePath.isNullOrBlank()) return null
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) return relativePath
        val base = BASE_URL.trim()
        val cleanPath = relativePath.removePrefix("/")
        return if (base.endsWith("/")) "$base$cleanPath" else "$base/$cleanPath"
    }
}
