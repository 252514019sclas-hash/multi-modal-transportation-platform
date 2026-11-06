package com.simats.multimodaltransportationrecommendation.ui.destination

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.model.location.LocationSearchResult
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class DestinationUiState(
    val origin: String = "Chennai Central",
    val destination: String = "Mahabalipuram",
    val activeField: String = "none", // "origin", "destination", "none"
    val isSearching: Boolean = false,
    val suggestions: List<LocationSearchResult> = emptyList(),
    val popularDestinations: List<String> = listOf(
        "Mahabalipuram", "Ooty", "Madurai", "Rameswaram",
        "Kodaikanal", "Yercaud", "Kanyakumari", "Thanjavur"
    ),
    val recentSearches: List<Pair<String, String>> = listOf(
        "Marina Beach & Promenade" to "Chennai",
        "Shore Temple & Pancha Rathas" to "Mahabalipuram",
        "Meenakshi Amman Temple" to "Madurai",
        "Government Botanical Garden" to "Ooty"
    ),
    val isLocating: Boolean = false,
    val statusMessage: String? = null
)

class DestinationViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DestinationUiState())
    val uiState: StateFlow<DestinationUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun setInitialLocations(initialOrigin: String, initialDestination: String) {
        _uiState.value = _uiState.value.copy(
            origin = if (initialOrigin.isNotBlank()) initialOrigin else _uiState.value.origin,
            destination = if (initialDestination.isNotBlank()) initialDestination else _uiState.value.destination
        )
    }

    fun onOriginChange(newOrigin: String) {
        _uiState.value = _uiState.value.copy(
            origin = newOrigin,
            activeField = "origin"
        )
        performSearch(newOrigin)
    }

    fun onDestinationChange(newDestination: String) {
        _uiState.value = _uiState.value.copy(
            destination = newDestination,
            activeField = "destination"
        )
        performSearch(newDestination)
    }

    fun swapLocations() {
        val currentOrigin = _uiState.value.origin
        val currentDest = _uiState.value.destination
        _uiState.value = _uiState.value.copy(
            origin = currentDest,
            destination = currentOrigin,
            suggestions = emptyList(),
            activeField = "none"
        )
    }

    fun selectSuggestion(loc: LocationSearchResult) {
        if (_uiState.value.activeField == "origin") {
            _uiState.value = _uiState.value.copy(
                origin = loc.name,
                suggestions = emptyList(),
                activeField = "none"
            )
        } else {
            _uiState.value = _uiState.value.copy(
                destination = loc.name,
                suggestions = emptyList(),
                activeField = "none"
            )
        }
    }

    fun selectPopularDestination(dest: String) {
        _uiState.value = _uiState.value.copy(
            destination = dest,
            suggestions = emptyList(),
            activeField = "none"
        )
    }

    fun clearSuggestions() {
        _uiState.value = _uiState.value.copy(
            suggestions = emptyList(),
            activeField = "none"
        )
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _uiState.value = _uiState.value.copy(suggestions = emptyList(), isSearching = false)
            return
        }

        searchJob = viewModelScope.launch {
            delay(250) // Debounce typing
            _uiState.value = _uiState.value.copy(isSearching = true)
            when (val res = authRepository.searchLocations(query.trim())) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        suggestions = res.data
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        suggestions = emptyList()
                    )
                }
            }
        }
    }

    fun useCurrentLocation(context: Context) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            _uiState.value = _uiState.value.copy(statusMessage = "Location permission is required to detect GPS.")
            return
        }

        _uiState.value = _uiState.value.copy(isLocating = true, statusMessage = null)

        viewModelScope.launch {
            try {
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                if (loc != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val addr = addresses[0]
                        val placeName = addr.locality ?: addr.subLocality ?: addr.featureName ?: "Current Location"
                        _uiState.value = _uiState.value.copy(
                            origin = placeName,
                            isLocating = false,
                            statusMessage = "Detected location: $placeName"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            origin = "Chennai Central",
                            isLocating = false,
                            statusMessage = "Defaulted to Chennai Central"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        origin = "Chennai Central",
                        isLocating = false,
                        statusMessage = "Using Chennai Central as default origin"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    origin = "Chennai Central",
                    isLocating = false,
                    statusMessage = "Location error: defaulting to Chennai Central"
                )
            }
        }
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }
}
