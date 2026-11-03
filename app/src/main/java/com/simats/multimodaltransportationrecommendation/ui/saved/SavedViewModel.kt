package com.simats.multimodaltransportationrecommendation.ui.saved

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.model.routes.SaveRouteRequest
import com.simats.multimodaltransportationrecommendation.data.model.routes.SavedRoute
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SavedUiState(
    val selectedTab: String = "Places", // "Places" or "Routes"
    val savedPlaces: List<TouristPlace> = emptyList(),
    val savedRoutes: List<SavedRoute> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class SavedViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    fun loadSavedData(context: Context) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            // Fetch saved places
            when (val placesRes = authRepository.fetchSavedPlaces(userId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(savedPlaces = placesRes.data)
                }
                is NetworkResult.Error -> {}
            }

            // Fetch saved routes
            when (val routesRes = authRepository.fetchSavedRoutes(userId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(savedRoutes = routesRes.data)
                }
                is NetworkResult.Error -> {}
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun selectTab(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun removeSavedPlace(context: Context, placeId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (authRepository.savePlace(userId, placeId, "remove")) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        savedPlaces = _uiState.value.savedPlaces.filter { it.placeId != placeId },
                        successMessage = "Place removed from saved"
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = "Could not remove place")
                }
            }
        }
    }

    fun removeSavedRoute(context: Context, routeId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            val req = SaveRouteRequest(
                userId = userId,
                origin = "",
                destination = "",
                mode = "",
                durationText = "",
                costText = "",
                co2Text = "",
                action = "remove",
                routeId = routeId
            )
            when (authRepository.saveRoute(req)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        savedRoutes = _uiState.value.savedRoutes.filter { it.routeId != routeId },
                        successMessage = "Route removed from saved"
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = "Could not remove route")
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}

