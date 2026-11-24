package com.simats.multimodaltransportationrecommendation.data.weather

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

data class WeatherData(
    val temperatureCelsius: Int,
    val conditionTitle: String,
    val fullDisplay: String,
    val iconType: String, // "sunny", "cloudy", "rainy", "night", "breeze"
    val cityName: String,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val climateZone: String,
    val advisory: String
)

private data class OpenMeteoResponse(
    @SerializedName("current_weather")
    val currentWeather: OpenMeteoCurrent? = null
)

private data class OpenMeteoCurrent(
    val temperature: Double = 28.0,
    val windspeed: Double = 10.0,
    val winddirection: Double = 0.0,
    @SerializedName("weathercode")
    val weatherCode: Int = 0,
    @SerializedName("is_day")
    val isDay: Int = 1,
    val time: String? = null
)

object WeatherManager {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(4, TimeUnit.SECONDS)
            .build()
    }

    private val gson: Gson by lazy { Gson() }

    data class CityGeo(
        val name: String,
        val lat: Double,
        val lng: Double,
        val zone: String,
        val baseDayTemp: Int,
        val isHillStation: Boolean = false
    )

    private val cityCatalog = listOf(
        CityGeo("Chennai", 13.0827, 80.2707, "Coastal Plain", 29),
        CityGeo("Mahabalipuram", 12.6269, 80.1927, "Bay Coast", 29),
        CityGeo("Kanchipuram", 12.8342, 79.7036, "Northern Inland", 30),
        CityGeo("Vellore", 12.9165, 79.1325, "Northern Plains", 31),
        CityGeo("Tiruvannamalai", 12.2253, 79.0747, "Spiritual Foothills", 30),
        CityGeo("Pondicherry", 11.9416, 79.8083, "East Coast Promenade", 29),
        CityGeo("Cuddalore", 11.7480, 79.7714, "Coastal Maritime", 29),
        CityGeo("Salem", 11.6643, 78.1460, "Kongu Basin", 28),
        CityGeo("Yercaud", 11.7753, 78.2093, "Shevaroy Hills", 21, isHillStation = true),
        CityGeo("Coimbatore", 11.0168, 76.9558, "Kongu Plateau", 26),
        CityGeo("Tiruppur", 11.1085, 77.3411, "Kongu Plain", 28),
        CityGeo("Erode", 11.3410, 77.7172, "Kaveri Basin", 29),
        CityGeo("Ooty", 11.4102, 76.6950, "Western Ghats - Nilgiris", 16, isHillStation = true),
        CityGeo("Coonoor", 11.3530, 76.7959, "Nilgiri Mountain Range", 17, isHillStation = true),
        CityGeo("Kodaikanal", 10.2381, 77.4892, "Palani Hills", 17, isHillStation = true),
        CityGeo("Valparai", 10.3256, 76.9558, "Anamalai Rainforest", 18, isHillStation = true),
        CityGeo("Tiruchirappalli", 10.7905, 78.7047, "Cauvery Heartlands", 31),
        CityGeo("Thanjavur", 10.7870, 79.1378, "Cauvery Delta", 30),
        CityGeo("Kumbakonam", 10.9602, 79.3845, "Temple Delta", 30),
        CityGeo("Nagapattinam", 10.7672, 79.8449, "Bay of Bengal Port", 28),
        CityGeo("Madurai", 9.9252, 78.1198, "Vaigai Valley", 31),
        CityGeo("Dindigul", 10.3673, 77.9803, "Southern Foothills", 29),
        CityGeo("Rameswaram", 9.2876, 79.3129, "Island Coast", 28),
        CityGeo("Tirunelveli", 8.7139, 77.7567, "Thamirabarani Basin", 30),
        CityGeo("Kanyakumari", 8.0883, 77.5385, "Southern Cape Coast", 28)
    )

    fun resolveCity(query: String?): CityGeo {
        if (query.isNullOrBlank()) return cityCatalog.first() // Chennai default
        val clean = query.trim().lowercase(Locale.ROOT)
        return cityCatalog.firstOrNull { city ->
            clean.contains(city.name.lowercase(Locale.ROOT)) ||
            (city.name.equals("Ooty", ignoreCase = true) && (clean.contains("nilgiri") || clean.contains("udhagamandalam"))) ||
            (city.name.equals("Tiruchirappalli", ignoreCase = true) && clean.contains("trichy")) ||
            (city.name.equals("Chennai", ignoreCase = true) && (clean.contains("central") || clean.contains("egmore") || clean.contains("marina") || clean.contains("tambaram"))) ||
            (city.name.equals("Pondicherry", ignoreCase = true) && clean.contains("puducherry"))
        } ?: cityCatalog.first()
    }

    suspend fun getLiveOrClimaticWeather(locationQuery: String?, customLat: Double? = null, customLng: Double? = null): WeatherData {
        val city = resolveCity(locationQuery)
        val lat = customLat ?: city.lat
        val lng = customLng ?: city.lng

        // 1. Compute dynamic climatic baseline for fallback / immediate display
        val fallback = computeClimaticBaseline(city)

        // 2. Fetch live data from Open-Meteo
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lng&current_weather=true"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "MoveSmartAI-TamilNadu-Transit/1.0")
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank()) {
                        val parsed = gson.fromJson(bodyString, OpenMeteoResponse::class.java)
                        val cur = parsed.currentWeather
                        if (cur != null) {
                            val temp = cur.temperature.roundToInt()
                            val isDay = cur.isDay == 1
                            val (cond, icon) = interpretWeatherCode(cur.weatherCode, isDay, temp, city.isHillStation)
                            val humidity = if (city.isHillStation) 82 else if (city.zone.contains("Coast")) 74 else 62
                            val advisory = generateAdvisory(cond, temp, city)

                            return@withContext WeatherData(
                                temperatureCelsius = temp,
                                conditionTitle = cond,
                                fullDisplay = "$temp°C · $cond",
                                iconType = icon,
                                cityName = city.name,
                                humidityPercent = humidity,
                                windSpeedKmh = cur.windspeed,
                                climateZone = city.zone,
                                advisory = advisory
                            )
                        }
                    }
                }
                fallback
            } catch (_: Exception) {
                fallback
            }
        }
    }

    fun computeClimaticBaseline(city: CityGeo): WeatherData {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val isDay = hour in 6..18

        var temp = city.baseDayTemp

        // Climatic hour adjustment
        when (hour) {
            in 0..5 -> temp -= 4 // Late night chill
            in 6..8 -> temp -= 2 // Morning freshness
            in 9..11 -> temp -= 0 // Mild day
            in 12..15 -> temp += 2 // Peak warmth
            in 16..18 -> temp -= 1 // Evening cool
            else -> temp -= 3 // Night
        }

        val (cond, icon) = if (city.isHillStation) {
            if (!isDay) "Mountain Chill" to "night"
            else if (hour in 6..10) "Misty Morn" to "cloudy"
            else "Misty & Cool" to "breeze"
        } else if (city.zone.contains("Coast") || city.zone.contains("Bay")) {
            if (!isDay) "Sea Breeze" to "night"
            else if (hour in 12..15) "Warm Breeze" to "sunny"
            else "Coastal Breeze" to "breeze"
        } else {
            if (!isDay) "Clear Night" to "night"
            else if (hour in 12..15) "Warm & Sunny" to "sunny"
            else "Pleasant Weather" to "sunny"
        }

        val humidity = if (city.isHillStation) 80 else if (city.zone.contains("Coast")) 70 else 58
        val wind = if (city.zone.contains("Coast")) 14.5 else if (city.isHillStation) 8.0 else 10.5
        val advisory = generateAdvisory(cond, temp, city)

        return WeatherData(
            temperatureCelsius = temp,
            conditionTitle = cond,
            fullDisplay = "$temp°C · $cond",
            iconType = icon,
            cityName = city.name,
            humidityPercent = humidity,
            windSpeedKmh = wind,
            climateZone = city.zone,
            advisory = advisory
        )
    }

    private fun interpretWeatherCode(code: Int, isDay: Boolean, temp: Int, isHill: Boolean): Pair<String, String> {
        return when (code) {
            0 -> {
                if (!isDay) "Clear Night" to "night"
                else if (isHill) "Pleasant Mountain Air" to "breeze"
                else "Sunny & Clear" to "sunny"
            }
            1, 2 -> {
                if (!isDay) "Partly Cloudy" to "night"
                else if (isHill) "Misty & Cool" to "cloudy"
                else "Pleasant Breeze" to "breeze"
            }
            3 -> {
                if (isHill) "Foggy Overcast" to "cloudy"
                else "Overcast Skies" to "cloudy"
            }
            45, 48 -> "Misty Fog" to "cloudy"
            51, 53, 55, 61, 63 -> "Light Showers" to "rainy"
            65, 80, 81, 82 -> "Rain Showers" to "rainy"
            95, 96, 99 -> "Thunderstorm" to "rainy"
            else -> {
                if (!isDay) "Clear Night" to "night"
                else if (isHill) "Misty & Cool" to "breeze"
                else "Pleasant Day" to "sunny"
            }
        }
    }

    private fun generateAdvisory(condition: String, temp: Int, city: CityGeo): String {
        return when {
            condition.contains("Rain", ignoreCase = true) || condition.contains("Shower", ignoreCase = true) ->
                "Passing monsoon showers in ${city.name}. Carry an umbrella and prefer metro or covered bus transit."
            condition.contains("Misty", ignoreCase = true) || city.isHillStation ->
                "Scenic cool mountain climate in ${city.name} ($temp°C). Light woolens or jackets recommended for morning tours."
            temp >= 32 ->
                "Warm tropical afternoon in ${city.name}. Stay hydrated; AC buses, metro or shaded routes are recommended."
            condition.contains("Night", ignoreCase = true) ->
                "Pleasant night conditions in ${city.name} ($temp°C). Great climate for evening promenades and transit."
            else ->
                "Comfortable climatic conditions in ${city.name} ($temp°C). Perfect for sightseeing and multi-modal travel."
        }
    }
}
