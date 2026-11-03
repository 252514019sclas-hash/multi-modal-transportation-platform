package com.simats.multimodaltransportationrecommendation.ui.trips

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MyTripsUiState(
    val selectedTab: String = "Upcoming", // "Upcoming", "Completed", "Cancelled"
    val upcomingTrips: List<TripItem> = emptyList(),
    val completedTrips: List<TripItem> = emptyList(),
    val cancelledTrips: List<TripItem> = emptyList(),
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val totalCo2SavedKg: Double = 0.0
)

class MyTripsViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTripsUiState())
    val uiState: StateFlow<MyTripsUiState> = _uiState.asStateFlow()

    fun selectTab(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun loadTrips(context: Context) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null)

        viewModelScope.launch {
            when (val result = authRepository.fetchTrips(userId, "all")) {
                is NetworkResult.Success -> {
                    val all = result.data
                    val upcoming = all.filter { it.status.lowercase() == "upcoming" }
                    val completed = all.filter { it.status.lowercase() == "completed" }
                    val cancelled = all.filter { it.status.lowercase() == "cancelled" }
                    val totalSaved = completed.sumOf { it.co2SavedKg }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        upcomingTrips = upcoming,
                        completedTrips = completed,
                        cancelledTrips = cancelled,
                        totalCo2SavedKg = Math.round(totalSaved * 10.0) / 10.0
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        statusMessage = result.message
                    )
                }
            }
        }
    }

    fun cancelTrip(context: Context, journeyId: Int, onComplete: () -> Unit = {}) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = authRepository.updateTripStatus(journeyId, userId, "cancelled")) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(statusMessage = "Journey cancelled.")
                    loadTrips(context)
                    onComplete()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(statusMessage = res.message ?: "Failed to cancel")
                }
            }
        }
    }

    fun completeTrip(context: Context, journeyId: Int, onComplete: () -> Unit = {}) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = authRepository.updateTripStatus(journeyId, userId, "completed")) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(statusMessage = "Journey marked as completed! CO₂ savings recorded.")
                    loadTrips(context)
                    onComplete()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(statusMessage = res.message ?: "Failed to update trip")
                }
            }
        }
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }
}
