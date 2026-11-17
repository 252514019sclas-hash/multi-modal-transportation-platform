package com.simats.multimodaltransportationrecommendation.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * MoveSmart AI – Real-Time GPS Location Tracker
 * Wraps Android's Fused Location Provider Client with lifecycle safety,
 * accuracy validation, GPS provider status checks, and distance calculations.
 */
class LocationTrackerManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _lastUpdateTimestamp = MutableStateFlow<Long>(0L)
    val lastUpdateTimestamp: StateFlow<Long> = _lastUpdateTimestamp.asStateFlow()

    private var locationCallback: LocationCallback? = null

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun isGpsEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        return lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
               lm?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        intervalMs: Long = 5000L,
        onUpdate: (Location) -> Unit = {}
    ) {
        if (!hasLocationPermission()) return

        stopLocationUpdates()

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMs
        )
            .setMinUpdateIntervalMillis(intervalMs / 2)
            .setMinUpdateDistanceMeters(2.0f)
            .setWaitForAccurateLocation(false)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                _currentLocation.value = loc
                _lastUpdateTimestamp.value = System.currentTimeMillis()
                onUpdate(loc)
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            _isTracking.value = true

            // Fetch last known location immediately for instant UI response
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null && _currentLocation.value == null) {
                    _currentLocation.value = loc
                    _lastUpdateTimestamp.value = System.currentTimeMillis()
                    onUpdate(loc)
                }
            }
        } catch (_: SecurityException) {
            _isTracking.value = false
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationCallback = null
        _isTracking.value = false
    }

    companion object {
        /**
         * Haversine formula for distance in kilometers
         */
        fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val earthRadius = 6371.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            return Math.round(earthRadius * c * 100.0) / 100.0
        }

        fun formatCoordinates(lat: Double, lng: Double): String {
            val latDir = if (lat >= 0) "N" else "S"
            val lngDir = if (lng >= 0) "E" else "W"
            return String.format(Locale.US, "%.4f° %s, %.4f° %s", Math.abs(lat), latDir, Math.abs(lng), lngDir)
        }

        fun formatFreshness(timestamp: Long): String {
            if (timestamp <= 0L) return "Waiting for GPS..."
            val diffSec = (System.currentTimeMillis() - timestamp) / 1000
            return when {
                diffSec < 5 -> "Live · Just now"
                diffSec < 60 -> "Live · ${diffSec}s ago"
                else -> "${diffSec / 60}m ago"
            }
        }
    }
}
