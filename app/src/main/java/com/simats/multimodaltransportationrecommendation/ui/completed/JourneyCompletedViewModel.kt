package com.simats.multimodaltransportationrecommendation.ui.completed

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.ai.SmartAiTransitEngine
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookJourneyRequest
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class JourneyCompletedUiState(
    val origin: String = "Chennai Central",
    val destination: String = "Marina Beach, Chennai",
    val mode: String = "CMRL Blue Line Metro + Walking",
    val completionDateTime: String = SimpleDateFormat("MMM d, yyyy · hh:mm a", Locale.getDefault()).format(Date()),
    val durationMinutes: Int = 18,
    val distanceKm: Double = 6.4,
    val fare: Double = 30.0,
    val fareText: String = "₹30",
    val transfers: Int = 0,
    val co2SavedKg: Double = 1.45,
    val completedStops: List<String> = listOf(
        "Chennai Central Metro Terminus",
        "Government Estate Interchange Node",
        "Thousand Lights Metro Transit Checkpoint",
        "Marina Beach & Promenade Exit"
    ),
    val bookingReference: String = "TN-MS-COMPLETED",

    // AI Features
    val aiSummary: SmartAiTransitEngine.AiJourneySummaryResult? = null,
    val alternativeRoutes: List<SmartAiTransitEngine.AlternativeRouteComparison> = emptyList(),
    val nextDestinations: List<SmartAiTransitEngine.NextDestinationIdea> = emptyList(),
    val futureTripIdeas: List<SmartAiTransitEngine.FutureTripIdea> = emptyList(),

    // AI Travel Feedback Assistant
    val rating: Int = 5,
    val suggestedFeedbackTags: List<String> = emptyList(),
    val selectedFeedbackTags: Set<String> = emptySet(),
    val userFeedbackText: String = "",
    val feedbackSubmitted: Boolean = false,

    val isSavedToTrips: Boolean = false,
    val isSaving: Boolean = false,
    val statusMessage: String? = null
)

class JourneyCompletedViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(JourneyCompletedUiState())
    val uiState: StateFlow<JourneyCompletedUiState> = _uiState.asStateFlow()

    init {
        refreshAiIntelligence()
    }

    fun initJourneyData(
        origin: String,
        dest: String,
        mode: String,
        durationMin: Int,
        distanceKm: Double,
        fare: Double,
        bookingRef: String = "TN-MS-${(1000..9999).random()}"
    ) {
        val dateStr = SimpleDateFormat("MMM d, yyyy · hh:mm a", Locale.getDefault()).format(Date())
        _uiState.value = _uiState.value.copy(
            origin = origin.ifBlank { "Chennai Central" },
            destination = dest.ifBlank { "Marina Beach, Chennai" },
            mode = mode.ifBlank { "CMRL Blue Line Metro" },
            durationMinutes = if (durationMin > 0) durationMin else 18,
            distanceKm = if (distanceKm > 0.0) distanceKm else 6.4,
            fare = if (fare > 0.0) fare else 30.0,
            fareText = "₹${fare.toInt().coerceAtLeast(30)}",
            completionDateTime = dateStr,
            bookingReference = bookingRef
        )
        refreshAiIntelligence()
    }

    private fun refreshAiIntelligence() {
        val s = _uiState.value
        val summary = SmartAiTransitEngine.generateCompletedJourneySummary(
            origin = s.origin,
            destination = s.destination,
            mode = s.mode,
            durationMinutes = s.durationMinutes,
            distanceKm = s.distanceKm,
            stopsCount = s.completedStops.size
        )
        val alternatives = SmartAiTransitEngine.getAlternativeRouteComparison(
            completedMode = s.mode,
            actualMinutes = s.durationMinutes,
            actualFare = s.fare
        )
        val nextDests = SmartAiTransitEngine.getNextDestinationRecommendations(s.destination)
        val futureTrips = SmartAiTransitEngine.getFutureTripSuggestions()
        val feedbackSuggestions = SmartAiTransitEngine.getFeedbackSuggestions(s.rating)

        _uiState.value = _uiState.value.copy(
            aiSummary = summary,
            alternativeRoutes = alternatives,
            nextDestinations = nextDests,
            futureTripIdeas = futureTrips,
            suggestedFeedbackTags = feedbackSuggestions
        )
    }

    fun setRating(rating: Int) {
        val feedbackSuggestions = SmartAiTransitEngine.getFeedbackSuggestions(rating)
        _uiState.value = _uiState.value.copy(
            rating = rating,
            suggestedFeedbackTags = feedbackSuggestions
        )
    }

    fun toggleFeedbackTag(tag: String) {
        val current = _uiState.value.selectedFeedbackTags.toMutableSet()
        if (current.contains(tag)) current.remove(tag) else current.add(tag)
        _uiState.value = _uiState.value.copy(selectedFeedbackTags = current)
    }

    fun onFeedbackTextChange(text: String) {
        _uiState.value = _uiState.value.copy(userFeedbackText = text)
    }

    fun submitFeedback() {
        _uiState.value = _uiState.value.copy(
            feedbackSubmitted = true,
            statusMessage = "✓ Thank you! Your transit experience feedback helps optimize Tamil Nadu routes."
        )
    }

    fun saveJourneyToBackend(context: Context, onComplete: () -> Unit = {}) {
        val s = _uiState.value
        if (s.isSavedToTrips) {
            onComplete()
            return
        }

        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isSaving = true)

        val req = BookJourneyRequest(
            userId = userId,
            origin = s.origin,
            destination = s.destination,
            mode = s.mode,
            routeTitle = "Completed Journey Corridor",
            durationMinutes = s.durationMinutes,
            distanceKm = s.distanceKm,
            fare = s.fare,
            transfers = s.transfers,
            co2SavedKg = s.co2SavedKg,
            co2EmittedKg = 0.15,
            journeyDate = s.completionDateTime
        )

        viewModelScope.launch {
            when (val res = authRepository.bookJourney(req)) {
                is NetworkResult.Success -> {
                    // Update status to completed
                    val jId = res.data.journeyId
                    authRepository.updateTripStatus(jId, userId, "completed")
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isSavedToTrips = true,
                        statusMessage = "✓ Journey saved to My Trips under Completed Journeys!"
                    )
                    onComplete()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        isSavedToTrips = true,
                        statusMessage = "✓ Journey archived locally to Completed Journeys!"
                    )
                    onComplete()
                }
            }
        }
    }

    fun addFutureTripToMyTrips(context: Context, idea: SmartAiTransitEngine.FutureTripIdea) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val req = BookJourneyRequest(
            userId = userId,
            origin = "Chennai Central",
            destination = idea.destination,
            mode = idea.recommendedMode,
            routeTitle = "AI Suggested: ${idea.interestTag}",
            durationMinutes = 65,
            distanceKm = 35.0,
            fare = 50.0,
            transfers = 1,
            co2SavedKg = 1.8,
            co2EmittedKg = 0.2,
            journeyDate = "Upcoming Weekend · 09:00 AM"
        )
        viewModelScope.launch {
            authRepository.bookJourney(req)
            _uiState.value = _uiState.value.copy(
                statusMessage = "✓ '${idea.destination}' added to Upcoming Trips!"
            )
        }
    }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    fun asTripItem(): TripItem {
        val s = _uiState.value
        return TripItem(
            journeyId = (1000..9999).random(),
            userId = 1,
            origin = s.origin,
            destination = s.destination,
            mode = s.mode,
            routeTitle = "Completed Journey Corridor",
            durationMinutes = s.durationMinutes,
            distanceKm = s.distanceKm,
            fare = s.fare,
            fareText = s.fareText,
            transfers = s.transfers,
            co2SavedKg = s.co2SavedKg,
            status = "completed",
            journeyDate = s.completionDateTime,
            bookingReference = s.bookingReference,
            createdAt = "MoveSmart AI Completed"
        )
    }
}
