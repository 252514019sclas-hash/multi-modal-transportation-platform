package com.simats.multimodaltransportationrecommendation.ui.aitravel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.ai.AiTripPlanRequest
import com.simats.multimodaltransportationrecommendation.data.model.ai.AiTripPlanResponse
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookJourneyRequest
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AiTravelAdvisorUiState(
    val origin: String = "Chennai",
    val destination: String = "Madurai",
    val selectedDays: Int = 5,
    val selectedTheme: String = "Heritage & Family",
    val stayPreference: String = "Mid-Range",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val tripPlan: AiTripPlanResponse? = null,
    val activeDayIndex: Int = 0,
    val userNotice: String? = null
)

class AiTravelAdvisorViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiTravelAdvisorUiState())
    val uiState: StateFlow<AiTravelAdvisorUiState> = _uiState.asStateFlow()

    init {
        // Automatically generate initial itinerary
        generateItinerary()
    }

    fun initRoute(newOrigin: String?, newDestination: String?) {
        val o = newOrigin?.trim()?.takeIf { it.isNotBlank() } ?: _uiState.value.origin
        val d = newDestination?.trim()?.takeIf { it.isNotBlank() } ?: _uiState.value.destination
        if (o != _uiState.value.origin || d != _uiState.value.destination) {
            _uiState.value = _uiState.value.copy(origin = o, destination = d)
            generateItinerary()
        }
    }

    fun setOrigin(origin: String) {
        _uiState.value = _uiState.value.copy(origin = origin)
    }

    fun setDestination(destination: String) {
        _uiState.value = _uiState.value.copy(destination = destination)
    }

    fun swapOriginDestination() {
        val o = _uiState.value.origin
        val d = _uiState.value.destination
        _uiState.value = _uiState.value.copy(origin = d, destination = o)
        generateItinerary()
    }

    fun setSelectedDays(days: Int) {
        _uiState.value = _uiState.value.copy(selectedDays = days, activeDayIndex = 0)
        generateItinerary()
    }

    fun setSelectedTheme(theme: String) {
        _uiState.value = _uiState.value.copy(selectedTheme = theme)
        generateItinerary()
    }

    fun setStayPreference(pref: String) {
        _uiState.value = _uiState.value.copy(stayPreference = pref)
        generateItinerary()
    }

    fun setActiveDayIndex(index: Int) {
        _uiState.value = _uiState.value.copy(activeDayIndex = index)
    }

    fun dismissNotice() {
        _uiState.value = _uiState.value.copy(userNotice = null)
    }

    fun generateItinerary() {
        val s = _uiState.value
        _uiState.value = s.copy(isLoading = true, userNotice = null)

        viewModelScope.launch {
            val req = AiTripPlanRequest(
                userId = 1,
                origin = s.origin,
                destination = s.destination,
                days = s.selectedDays,
                tripTheme = s.selectedTheme,
                stayPreference = s.stayPreference
            )

            when (val res = repository.generateAiTripPlan(req)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        tripPlan = res.data,
                        activeDayIndex = 0,
                        userNotice = "✨ Generated ${res.data.tripSummary?.totalDays ?: s.selectedDays}-Day Travel Plan with en-route stays & local tips!"
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userNotice = "Unable to plan itinerary: ${res.message}"
                    )
                }
            }
        }
    }

    fun saveItineraryToMyTrips(context: Context) {
        val plan = _uiState.value.tripPlan?.tripSummary ?: return
        val days = _uiState.value.selectedDays
        val userId = SessionManager.getInstance(context).getUserId() ?: 1

        _uiState.value = _uiState.value.copy(isSaving = true)

        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val dateStr = "Starts ${dateFormat.format(Date())} ($days Days)"

            val bookReq = BookJourneyRequest(
                userId = userId,
                origin = plan.origin,
                destination = plan.destination,
                mode = "AI Multi-Day Road & Rail Tour",
                routeTitle = "MoveSmart AI $days-Day ${plan.tripTheme} Tour",
                durationMinutes = (plan.estimatedDrivingHours * 60).toInt(),
                distanceKm = plan.totalDistanceKm,
                fare = 0.0, // Zero payment / Free plan
                transfers = days,
                co2SavedKg = 3.5,
                co2EmittedKg = 0.5,
                journeyDate = dateStr
            )

            when (val res = repository.bookJourney(bookReq)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        userNotice = "✓ Successfully saved ${plan.origin} to ${plan.destination} ($days Days) in My Trips!"
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        userNotice = "Saved locally. Notice: ${res.message}"
                    )
                }
            }
        }
    }
}
