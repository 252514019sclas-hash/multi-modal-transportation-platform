package com.simats.multimodaltransportationrecommendation.ui.trips

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.ai.SmartAiTransitEngine
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.trips.SaveAiItineraryRequest
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MyTripsUiState(
    val selectedTab: String = "Upcoming", // "Upcoming", "Ongoing", "Completed", "Cancelled"
    val searchQuery: String = "",
    val upcomingTrips: List<TripItem> = emptyList(),
    val ongoingTrips: List<TripItem> = emptyList(),
    val completedTrips: List<TripItem> = emptyList(),
    val cancelledTrips: List<TripItem> = emptyList(),
    val isLoading: Boolean = false,
    val statusMessage: String? = null,
    val totalCo2SavedKg: Double = 0.0,

    // AI Smart Trip Planner State
    val showAiPlanner: Boolean = false,
    val plannerOrigin: String = "Chennai Central",
    val plannerDestination: String = "Marina Beach, Chennai",
    val plannerDate: String = "Upcoming Weekend · 09:30 AM",
    val plannerDuration: String = "Half-day (3-4 hrs)",
    val plannerInterests: String = "Heritage & Temples",
    val plannerModes: List<String> = listOf("Metro", "Bus"),
    val generatedPlan: SmartAiTransitEngine.AiTripPlanResult? = null,
    val budgetBreakdown: SmartAiTransitEngine.AiBudgetBreakdown? = null,
    val isPlanning: Boolean = false,

    // AI Itinerary Assistant (Chat / Q&A)
    val showAiAssistant: Boolean = false,
    val assistantQuery: String = "",
    val assistantChat: List<Pair<String, String>> = listOf(
        "MoveSmart AI" to "Vanakkam! I am your AI Itinerary Assistant. Ask me about alternative routes, tourist stops along your corridor, or to organize your schedule."
    )
)

class MyTripsViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTripsUiState())
    val uiState: StateFlow<MyTripsUiState> = _uiState.asStateFlow()

    fun selectTab(tab: String) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun loadTrips(context: Context) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isLoading = true, statusMessage = null)

        viewModelScope.launch {
            when (val result = authRepository.fetchTrips(userId, "all")) {
                is NetworkResult.Success -> {
                    val all = result.data
                    val upcoming = all.filter { it.status.lowercase() == "upcoming" }
                    val ongoing = all.filter { it.status.lowercase() == "ongoing" }
                    val completed = all.filter { it.status.lowercase() == "completed" }
                    val cancelled = all.filter { it.status.lowercase() == "cancelled" }
                    val totalSaved = completed.sumOf { it.co2SavedKg }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        upcomingTrips = upcoming,
                        ongoingTrips = ongoing,
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

    // ==========================================
    // AI SMART TRIP PLANNER CONTROLS
    // ==========================================

    fun setAiPlannerOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(showAiPlanner = open)
    }

    fun onPlannerOriginChange(origin: String) {
        _uiState.value = _uiState.value.copy(plannerOrigin = origin)
    }

    fun onPlannerDestinationChange(dest: String) {
        _uiState.value = _uiState.value.copy(plannerDestination = dest)
    }

    fun onPlannerDateChange(date: String) {
        _uiState.value = _uiState.value.copy(plannerDate = date)
    }

    fun onPlannerDurationChange(duration: String) {
        _uiState.value = _uiState.value.copy(plannerDuration = duration)
    }

    fun onPlannerInterestsChange(interests: String) {
        _uiState.value = _uiState.value.copy(plannerInterests = interests)
    }

    fun togglePlannerMode(mode: String) {
        val current = _uiState.value.plannerModes.toMutableList()
        if (current.contains(mode)) {
            if (current.size > 1) current.remove(mode)
        } else {
            current.add(mode)
        }
        _uiState.value = _uiState.value.copy(plannerModes = current)
    }

    fun generateAiPlan() {
        val origin = _uiState.value.plannerOrigin
        val dest = _uiState.value.plannerDestination
        val date = _uiState.value.plannerDate
        val dur = _uiState.value.plannerDuration
        val modes = _uiState.value.plannerModes
        val interests = _uiState.value.plannerInterests

        val plan = SmartAiTransitEngine.generateSmartTripPlan(
            origin = origin,
            destination = dest,
            travelDate = date,
            availableTime = dur,
            preferredModes = modes,
            interests = interests
        )

        val budget = SmartAiTransitEngine.estimateBudget(
            mode = plan.recommendedMode,
            distanceKm = plan.totalEstimatedDistanceKm,
            destination = dest
        )

        _uiState.value = _uiState.value.copy(
            generatedPlan = plan,
            budgetBreakdown = budget
        )
    }

    fun saveAiPlanToBackend(context: Context, onComplete: () -> Unit = {}) {
        val plan = _uiState.value.generatedPlan ?: return
        val userId = SessionManager.getInstance(context).getUserId() ?: 1

        val req = SaveAiItineraryRequest(
            userId = userId,
            origin = _uiState.value.plannerOrigin,
            destination = _uiState.value.plannerDestination,
            mode = plan.recommendedMode,
            routeTitle = "AI Smart Itinerary (${_uiState.value.plannerInterests})",
            durationMinutes = plan.totalEstimatedDurationMinutes,
            distanceKm = plan.totalEstimatedDistanceKm,
            fare = 35.0,
            journeyDate = _uiState.value.plannerDate,
            transfers = 1,
            co2SavedKg = 0.85
        )

        viewModelScope.launch {
            when (val res = authRepository.saveAiItinerary(req)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        showAiPlanner = false,
                        statusMessage = "✓ AI Trip Plan saved to Upcoming Trips!"
                    )
                    loadTrips(context)
                    onComplete()
                }
                is NetworkResult.Error -> {
                    // Fallback to local save
                    val localTrip = TripItem(
                        journeyId = (1000..9999).random(),
                        userId = userId,
                        origin = _uiState.value.plannerOrigin,
                        destination = _uiState.value.plannerDestination,
                        mode = plan.recommendedMode,
                        routeTitle = "AI Multi-Modal Plan",
                        durationMinutes = plan.totalEstimatedDurationMinutes,
                        distanceKm = plan.totalEstimatedDistanceKm,
                        fare = 35.0,
                        fareText = "₹35",
                        transfers = 1,
                        co2SavedKg = 0.85,
                        status = "Upcoming",
                        journeyDate = _uiState.value.plannerDate,
                        bookingReference = "AI-MS-${(100..999).random()}",
                        createdAt = "MoveSmart AI Planner"
                    )
                    _uiState.value = _uiState.value.copy(
                        upcomingTrips = listOf(localTrip) + _uiState.value.upcomingTrips,
                        showAiPlanner = false,
                        statusMessage = "✓ AI Trip Plan saved to Upcoming Trips!"
                    )
                    onComplete()
                }
            }
        }
    }

    // ==========================================
    // AI ITINERARY ASSISTANT (CHAT / Q&A)
    // ==========================================

    fun setAiAssistantOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(showAiAssistant = open)
    }

    fun onAssistantQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(assistantQuery = query)
    }

    fun sendAssistantQuery(prompt: String? = null) {
        val query = (prompt ?: _uiState.value.assistantQuery).trim()
        if (query.isBlank()) return

        val userPair = "You" to query
        val response = SmartAiTransitEngine.answerItineraryQuestion(
            query = query,
            origin = _uiState.value.plannerOrigin,
            destination = _uiState.value.plannerDestination,
            mode = "Metro + Bus"
        )
        val aiPair = "MoveSmart AI" to response

        _uiState.value = _uiState.value.copy(
            assistantChat = _uiState.value.assistantChat + userPair + aiPair,
            assistantQuery = ""
        )
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }
}
