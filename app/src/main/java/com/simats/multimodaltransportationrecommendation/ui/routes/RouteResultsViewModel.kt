package com.simats.multimodaltransportationrecommendation.ui.routes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import com.simats.multimodaltransportationrecommendation.data.model.routes.SaveRouteRequest
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RouteResultsUiState(
    val origin: String = "Chennai Central",
    val destination: String = "Mahabalipuram",
    val dateTime: String = "Today, 9:15 AM",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val aiInsight: String = "Finding the optimal low-carbon multi-modal transit connection across Tamil Nadu.",
    val recommendedRoute: RouteOption? = null,
    val alternativeRoutes: List<RouteOption> = emptyList(),
    val allRoutes: List<RouteOption> = emptyList(),
    val selectedRoute: RouteOption? = null,
    val routePlaces: List<TouristPlace> = emptyList(),
    val isLoadingPlaces: Boolean = false,
    val selectedPlaceForDetails: TouristPlace? = null,
    val showMapSheet: Boolean = false,
    val saveMessage: String? = null
)

class RouteResultsViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RouteResultsUiState())
    val uiState: StateFlow<RouteResultsUiState> = _uiState.asStateFlow()

    fun searchRoutes(
        context: Context,
        origin: String,
        destination: String,
        dateTime: String = "Today, 9:15 AM"
    ) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val cleanOrigin = if (origin.isNotBlank()) origin.trim() else "Chennai Central"
        val cleanDest = if (destination.isNotBlank()) destination.trim() else "Mahabalipuram"

        _uiState.value = _uiState.value.copy(
            origin = cleanOrigin,
            destination = cleanDest,
            dateTime = dateTime,
            isLoading = true,
            isLoadingPlaces = true,
            errorMessage = null,
            saveMessage = null
        )

        // 1. Fetch Multi-Modal Transit Routes
        viewModelScope.launch {
            when (val result = authRepository.searchRoutes(cleanOrigin, cleanDest, dateTime, userId)) {
                is NetworkResult.Success -> {
                    val resp = result.data
                    val routes = resp.routes ?: emptyList()
                    val recommended = routes.firstOrNull { it.isRecommended } ?: routes.firstOrNull()
                    val alternatives = if (recommended != null) routes.filter { it.id != recommended.id } else routes

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        aiInsight = resp.aiInsight ?: _uiState.value.aiInsight,
                        recommendedRoute = recommended,
                        alternativeRoutes = alternatives,
                        allRoutes = routes,
                        selectedRoute = recommended
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "No transit routes found between these locations"
                    )
                }
            }
        }

        // 2. Fetch Tourist Places Along Route Corridor
        viewModelScope.launch {
            when (val placeRes = authRepository.fetchRouteRecommendations(cleanOrigin, cleanDest)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingPlaces = false,
                        routePlaces = placeRes.data
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoadingPlaces = false,
                        routePlaces = emptyList()
                    )
                }
            }
        }
    }

    fun selectRoute(route: RouteOption) {
        _uiState.value = _uiState.value.copy(selectedRoute = route)
    }

    fun toggleMapSheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showMapSheet = show)
    }

    fun openPlaceDetails(place: TouristPlace) {
        _uiState.value = _uiState.value.copy(selectedPlaceForDetails = place)
    }

    fun closePlaceDetails() {
        _uiState.value = _uiState.value.copy(selectedPlaceForDetails = null)
    }

    fun toggleSavePlace(context: Context, place: TouristPlace) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val newAction = if (place.isSaved) "remove" else "save"

        viewModelScope.launch {
            when (val result = authRepository.savePlace(userId, place.placeId, newAction)) {
                is NetworkResult.Success -> {
                    val isNowSaved = result.data
                    val updatedPlaces = _uiState.value.routePlaces.map {
                        if (it.placeId == place.placeId) it.copy(isSaved = isNowSaved) else it
                    }
                    _uiState.value = _uiState.value.copy(
                        routePlaces = updatedPlaces,
                        saveMessage = if (isNowSaved) "Saved to your places!" else "Removed from saved"
                    )
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun saveRoute(context: Context, route: RouteOption) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            val req = SaveRouteRequest(
                userId = userId,
                origin = _uiState.value.origin,
                destination = _uiState.value.destination,
                mode = route.mode,
                durationText = route.durationText,
                costText = route.fareText,
                co2Text = "${route.co2SavedKg} kg CO2 saved",
                action = "save"
            )
            when (val res = authRepository.saveRoute(req)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(saveMessage = "Route saved to My Trips!")
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(saveMessage = res.message ?: "Failed to save route")
                }
            }
        }
    }

    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(saveMessage = null)
    }
}
