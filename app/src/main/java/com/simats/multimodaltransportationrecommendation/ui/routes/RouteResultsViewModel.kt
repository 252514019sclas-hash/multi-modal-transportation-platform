package com.simats.multimodaltransportationrecommendation.ui.routes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import com.simats.multimodaltransportationrecommendation.data.model.routes.SaveRouteRequest
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RouteResultsUiState(
    val origin: String = "Ferry Building",
    val destination: String = "California Academy of Sciences",
    val dateTime: String = "Today, 9:15 AM",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val aiInsight: String = "A low-carbon ride with no transit transfers. Minimal carbon footprint and a comfortable walk.",
    val recommendedRoute: RouteOption? = null,
    val alternativeRoutes: List<RouteOption> = emptyList(),
    val allRoutes: List<RouteOption> = emptyList(),
    val selectedRoute: RouteOption? = null,
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
        _uiState.value = _uiState.value.copy(
            origin = origin,
            destination = destination,
            dateTime = dateTime,
            isLoading = true,
            errorMessage = null,
            saveMessage = null
        )

        viewModelScope.launch {
            when (val result = authRepository.searchRoutes(origin, destination, dateTime, userId)) {
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
                        errorMessage = result.message ?: "Failed to find routes"
                    )
                }
            }
        }
    }

    fun selectRoute(route: RouteOption) {
        _uiState.value = _uiState.value.copy(selectedRoute = route)
    }

    fun saveRoute(
        context: Context,
        route: RouteOption,
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            val req = SaveRouteRequest(
                userId = userId,
                origin = _uiState.value.origin,
                destination = _uiState.value.destination,
                mode = route.mode,
                durationText = route.durationText,
                costText = route.fareText,
                co2Text = "${route.co2EmittedKg} kg CO₂",
                action = "save"
            )
            when (val res = authRepository.saveRoute(req)) {
                is NetworkResult.Success -> {
                    val msg = res.data.ifBlank { "Route saved to your Saved list!" }
                    _uiState.value = _uiState.value.copy(saveMessage = msg)
                    onComplete(true, msg)
                }
                is NetworkResult.Error -> {
                    val msg = res.message ?: "Could not save route"
                    _uiState.value = _uiState.value.copy(saveMessage = msg)
                    onComplete(false, msg)
                }
            }
        }
    }

    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(saveMessage = null)
    }
}
