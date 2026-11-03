package com.simats.multimodaltransportationrecommendation.ui.preferences

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.preferences.UpdatePreferencesRequest
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TravelPreferencesUiState(
    val selectedModes: Set<String> = setOf("Walking", "Cycling", "Bus", "Metro"),
    val lowCarbon: Boolean = true,
    val travelPurpose: String = "Daily Commute", // Daily Commute, Tourism, Business, Adventure
    val selectedDestinationTypes: Set<String> = setOf("Parks", "Museums"), // Historical Places, Temples, Beaches, Parks, Museums, Nature and Adventure
    val distancePreference: String = "Within City", // Nearby, Within City, Long Distance
    val touristRecommendations: Boolean = true,
    val selectedBudget: String = "Moderate",
    val selectedDuration: String = "Under 45 min",
    val notificationsEnabled: Boolean = true,
    val journeyReminders: Boolean = true,
    val bookingUpdates: Boolean = true,
    val travelAlerts: Boolean = true,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class TravelPreferencesViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TravelPreferencesUiState())
    val uiState: StateFlow<TravelPreferencesUiState> = _uiState.asStateFlow()

    private var hasLoaded = false

    fun loadPreferences(context: Context) {
        if (hasLoaded) return
        hasLoaded = true

        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val result = authRepository.fetchPreferences(userId)) {
                is NetworkResult.Success -> {
                    val p = result.data
                    val parsedModes = p.modes.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                    val parsedDestTypes = p.destinationTypes.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        selectedModes = if (parsedModes.isNotEmpty()) parsedModes else setOf("Walking", "Cycling", "Bus", "Metro"),
                        lowCarbon = p.lowCarbon,
                        travelPurpose = p.travelPurpose.ifBlank { "Daily Commute" },
                        selectedDestinationTypes = if (parsedDestTypes.isNotEmpty()) parsedDestTypes else setOf("Parks", "Museums"),
                        distancePreference = p.distancePreference.ifBlank { "Within City" },
                        touristRecommendations = p.touristRecommendations,
                        selectedBudget = p.budgetTier.ifBlank { "Moderate" },
                        selectedDuration = p.maxDuration.ifBlank { "Under 45 min" },
                        notificationsEnabled = p.notificationsEnabled,
                        journeyReminders = p.journeyReminders,
                        bookingUpdates = p.bookingUpdates,
                        travelAlerts = p.travelAlerts
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun toggleMode(mode: String) {
        val current = _uiState.value.selectedModes.toMutableSet()
        if (current.contains(mode)) {
            if (current.size > 1) { // keep at least 1 mode
                current.remove(mode)
            }
        } else {
            current.add(mode)
        }
        _uiState.value = _uiState.value.copy(selectedModes = current)
    }

    fun setTravelPurpose(purpose: String) {
        _uiState.value = _uiState.value.copy(travelPurpose = purpose)
    }

    fun toggleDestinationType(type: String) {
        val current = _uiState.value.selectedDestinationTypes.toMutableSet()
        if (current.contains(type)) {
            if (current.size > 1) { // keep at least 1 destination type
                current.remove(type)
            }
        } else {
            current.add(type)
        }
        _uiState.value = _uiState.value.copy(selectedDestinationTypes = current)
    }

    fun setDistancePreference(dist: String) {
        _uiState.value = _uiState.value.copy(distancePreference = dist)
    }

    fun toggleTouristRecommendations() {
        _uiState.value = _uiState.value.copy(touristRecommendations = !_uiState.value.touristRecommendations)
    }

    fun toggleLowCarbon() {
        _uiState.value = _uiState.value.copy(lowCarbon = !_uiState.value.lowCarbon)
    }

    fun selectBudget(budget: String) {
        _uiState.value = _uiState.value.copy(selectedBudget = budget)
    }

    fun selectDuration(duration: String) {
        _uiState.value = _uiState.value.copy(selectedDuration = duration)
    }

    fun savePreferences(context: Context, onSaved: () -> Unit = {}) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, successMessage = null)

        val modesStr = _uiState.value.selectedModes.joinToString(", ")
        val destTypesStr = _uiState.value.selectedDestinationTypes.joinToString(", ")

        viewModelScope.launch {
            val req = UpdatePreferencesRequest(
                userId = userId,
                modes = modesStr,
                lowCarbon = _uiState.value.lowCarbon,
                travelPurpose = _uiState.value.travelPurpose,
                destinationTypes = destTypesStr,
                distancePreference = _uiState.value.distancePreference,
                touristRecommendations = _uiState.value.touristRecommendations,
                budgetTier = _uiState.value.selectedBudget,
                maxDuration = _uiState.value.selectedDuration,
                notificationsEnabled = _uiState.value.notificationsEnabled,
                journeyReminders = _uiState.value.journeyReminders,
                bookingUpdates = _uiState.value.bookingUpdates,
                travelAlerts = _uiState.value.travelAlerts
            )

            when (val result = authRepository.updatePreferences(req)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Travel preferences & AI tourist settings saved!"
                    )
                    onSaved()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.message ?: "Failed to save preferences"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }
}
