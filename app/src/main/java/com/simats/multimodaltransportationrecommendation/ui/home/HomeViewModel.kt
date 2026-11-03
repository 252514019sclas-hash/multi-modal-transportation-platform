package com.simats.multimodaltransportationrecommendation.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class HomeUiState(
    val userName: String = "Traveler",
    val origin: String = "Ferry Building",
    val destination: String = "",
    val dateTime: String = "Today, 9:15 AM",
    val location: String = "Ferry Building · Current location",
    val temperature: String = "17°C · Sunny",
    val recentDestination: String = "California Academy of Sciences",
    val recentLocationInfo: String = "Golden Gate Park · 6.8 km away",
    val selectedCategory: String = "All",
    val touristPlaces: List<TouristPlace> = emptyList(),
    val isLoadingPlaces: Boolean = false,
    val selectedTab: String = "Explore",
    val unreadNotifications: Int = 0,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var isLoaded = false

    fun loadExploreData(context: Context) {
        val session = SessionManager.getInstance(context)
        val user = session.getUser()
        val userId = session.getUserId() ?: 1
        val name = user?.fullName?.split(" ")?.firstOrNull() ?: "Traveler"

        _uiState.value = _uiState.value.copy(
            userName = name
        )

        if (!isLoaded) {
            isLoaded = true
            fetchPlaces(userId, _uiState.value.selectedCategory)
            loadNotificationsCount(context)
        }
    }

    fun loadNotificationsCount(context: Context) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = authRepository.fetchNotifications(userId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(unreadNotifications = res.data.first)
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun fetchPlaces(userId: Int, category: String) {
        _uiState.value = _uiState.value.copy(isLoadingPlaces = true, errorMessage = null)

        viewModelScope.launch {
            val catParam = if (category.equals("All", ignoreCase = true)) null else category
            when (val result = authRepository.fetchPlaces(userId, city = null, category = catParam)) {
                is NetworkResult.Success -> {
                    val places = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoadingPlaces = false,
                        touristPlaces = places
                    )
                    if (places.isNotEmpty() && _uiState.value.destination.isEmpty()) {
                        val topPlace = places.firstOrNull { it.isRecommended } ?: places.first()
                        _uiState.value = _uiState.value.copy(
                            recentDestination = topPlace.name,
                            recentLocationInfo = "${topPlace.city} · ${topPlace.distanceKm} km away"
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingPlaces = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun onOriginChange(origin: String) {
        _uiState.value = _uiState.value.copy(origin = origin)
    }

    fun onDestinationChange(destination: String) {
        _uiState.value = _uiState.value.copy(destination = destination)
    }

    fun useCurrentLocation(context: Context) {
        var detected = "Market St & 4th, SF (GPS)"
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            if (locationManager != null) {
                val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                if (hasFine || hasCoarse) {
                    val lastGps = if (hasFine) locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) else null
                    val lastNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    val loc = lastGps ?: lastNet
                    if (loc != null) {
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val list = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                            if (!list.isNullOrEmpty()) {
                                val addr = list[0]
                                val street = addr.thoroughfare ?: addr.featureName ?: addr.locality
                                detected = "${street ?: "Current GPS"}, ${addr.locality ?: "SF"}"
                            } else {
                                detected = "Current Location (${String.format(Locale.US, "%.3f", loc.latitude)}, ${String.format(Locale.US, "%.3f", loc.longitude)})"
                            }
                        } catch (_: Exception) {
                            detected = "Current GPS Location"
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        _uiState.value = _uiState.value.copy(
            origin = detected,
            location = "$detected · GPS Active"
        )
    }

    fun selectCategory(category: String, context: Context) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        fetchPlaces(userId, category)
    }

    fun toggleSavePlace(context: Context, place: TouristPlace) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val newAction = if (place.isSaved) "remove" else "save"

        viewModelScope.launch {
            when (val result = authRepository.savePlace(userId, place.placeId, newAction)) {
                is NetworkResult.Success -> {
                    val isNowSaved = result.data
                    val updatedList = _uiState.value.touristPlaces.map {
                        if (it.placeId == place.placeId) it.copy(isSaved = isNowSaved) else it
                    }
                    _uiState.value = _uiState.value.copy(
                        touristPlaces = updatedList,
                        successMessage = if (isNowSaved) "Added to Saved Places!" else "Removed from Saved Places"
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = result.message)
                }
            }
        }
    }

    fun setQuickDestination(dest: String) {
        _uiState.value = _uiState.value.copy(destination = dest)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
