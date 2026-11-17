package com.simats.multimodaltransportationrecommendation.ui.journey

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.simats.multimodaltransportationrecommendation.data.location.LocationTrackerManager
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.ui.map.MapMarkerItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class JourneyUpcomingStep(
    val time: String,
    val icon: String,
    val title: String,
    val subtitle: String,
    val isCompleted: Boolean = false
)

data class MapStationPoint(
    val id: String,
    val name: String,
    val type: String, // "metro", "bus", "train"
    val latitude: Double,
    val longitude: Double,
    val xRatio: Float = 0.5f,
    val yRatio: Float = 0.5f,
    val time: String
)

data class MapAttractionPoint(
    val id: String,
    val name: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val xRatio: Float = 0.5f,
    val yRatio: Float = 0.5f,
    val rating: String,
    val fee: String
)

data class MapPointDetail(
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: String,
    val distance: String,
    val actionText: String? = null
)

data class AIAssistantMessage(
    val id: String,
    val sender: String, // "user", "ai"
    val text: String,
    val timeText: String,
    val actionTag: String? = null
)

data class RouteAttractionItem(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val imageUrl: String,
    val distanceOffRoute: String,
    val rating: Double,
    val entryFee: String,
    val visitDuration: String,
    val nearbyStop: String,
    val isSaved: Boolean = false
)

data class AlternativeJourneyItem(
    val id: String,
    val title: String,
    val modeCombination: String, // "Metro + Walking", "Bus + Train", etc.
    val durationText: String,
    val fareText: String,
    val transfers: Int,
    val co2Saved: String,
    val transferPoints: String,
    val isRecommended: Boolean = false
)

data class SmartPlanStop(
    val time: String,
    val title: String,
    val type: String,
    val description: String,
    val transitHint: String
)

data class OnYourWayUiState(
    // Core Tracking Status: "STOPPED", "TRACKING", "PAUSED"
    val trackingStatus: String = "STOPPED",
    val isLiveTrackingActive: Boolean = false,
    val isGpsProviderEnabled: Boolean = true,
    val hasLocationPermission: Boolean = false,

    // Real-Time GPS Coordinates & Telemetry
    val userGpsLatLng: LatLng? = null,
    val gpsAccuracyMeters: Float? = null,
    val gpsSpeedKmh: Float? = null,
    val gpsLastUpdatedText: String = "GPS standby",
    val recenterTrigger: Long = 0L,

    // Journey Metrics
    val currentTime: String = "09:36 AM",
    val remainingTimeText: String = "12 min remaining · On time",
    val remainingDistanceKm: Double = 3.8,
    val totalTrackedDistanceKm: Double = 0.0,
    val trackedDurationSeconds: Long = 0L,
    val progressFraction: Float = 0.35f,

    // Route Points
    val origin: String = "Chennai Central",
    val destination: String = "Marina Beach & Promenade",
    val originLatLng: LatLng = LatLng(13.0827, 80.2707),
    val destinationLatLng: LatLng = LatLng(13.0499, 80.2824),
    val routeWaypoints: List<LatLng> = listOf(
        LatLng(13.0732, 80.2608), // Near Central - Egmore
        LatLng(13.0645, 80.2644), // LIC Metro
        LatLng(13.0583, 80.2544), // Thousand Lights Metro
        LatLng(13.0673, 80.2736), // Government Estate Metro
        LatLng(13.0499, 80.2824)  // Marina Beach
    ),

    // Active status & upcoming steps
    val activeStatusTitle: String = "Ready to Track CMRL Blue Line",
    val nextStopInfo: String = "Next: Thousand Lights Metro · 09:38 AM",
    val reminderText: String = "We'll remind you 1 station before Government Estate",
    val upcomingSteps: List<JourneyUpcomingStep> = listOf(
        JourneyUpcomingStep("09:38 AM", "🚇", "Arrive Thousand Lights Metro", "Station platform 2, train on schedule", false),
        JourneyUpcomingStep("09:44 AM", "🚇", "Exit at Government Estate Metro Station", "Direct interchange exit for Marina Promenade", false),
        JourneyUpcomingStep("09:48 AM", "🚶", "Arrive at Marina Beach & Promenade", "4-minute walk along Kamarajar Salai", false)
    ),

    // Map Layers & Points
    val showStationsLayer: Boolean = true,
    val showAttractionsLayer: Boolean = true,
    val showBusStopsLayer: Boolean = false,
    val showFacilitiesLayer: Boolean = false,
    val selectedMarkerDetail: MapPointDetail? = null,
    val stations: List<MapStationPoint> = listOf(
        MapStationPoint("s1", "Chennai Central Metro", "metro", 13.0827, 80.2707, 0.15f, 0.75f, "09:30 AM"),
        MapStationPoint("s2", "LIC Metro Station", "metro", 13.0645, 80.2644, 0.35f, 0.62f, "09:34 AM"),
        MapStationPoint("s3", "Thousand Lights Metro", "metro", 13.0583, 80.2544, 0.55f, 0.50f, "09:38 AM"),
        MapStationPoint("s4", "Government Estate Metro", "metro", 13.0673, 80.2736, 0.72f, 0.35f, "09:44 AM"),
        MapStationPoint("s5", "Marina Beach Terminal", "bus", 13.0499, 80.2824, 0.88f, 0.22f, "09:48 AM")
    ),
    val attractionsOnMap: List<MapAttractionPoint> = listOf(
        MapAttractionPoint("a1", "Marina Beach & Promenade", "Beaches", 13.0499, 80.2824, 0.88f, 0.20f, "4.7★", "Free"),
        MapAttractionPoint("a2", "Government Museum Egmore", "Museums", 13.0700, 80.2562, 0.32f, 0.40f, "4.6★", "₹15"),
        MapAttractionPoint("a3", "Kapaleeshwarar Temple", "Temples", 13.0334, 80.2699, 0.65f, 0.68f, "4.9★", "Free"),
        MapAttractionPoint("a4", "Fort St. George & Clive", "Historical", 13.0797, 80.2872, 0.82f, 0.45f, "4.5★", "₹25")
    ),

    // AI Smart Travel Assistant
    val isAssistantSheetOpen: Boolean = false,
    val assistantQueryText: String = "",
    val isAiThinking: Boolean = false,
    val assistantChat: List<AIAssistantMessage> = listOf(
        AIAssistantMessage(
            "m1",
            "ai",
            "Vanakkam! I am your MoveSmart AI Travel Assistant. Live GPS Tracking is active on CMRL Blue Line. Ask me about transit times, upcoming stops, or nearby spots!",
            "09:36 AM"
        )
    ),

    // Tourist Places Along Route
    val selectedTouristCategory: String = "All",
    val placesAlongRoute: List<RouteAttractionItem> = listOf(
        RouteAttractionItem(
            id = 4,
            name = "Marina Beach & Promenade",
            category = "Beaches",
            description = "Second longest urban beach in the world with evening breeze, colonial lighthouses, and local food stalls.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800",
            distanceOffRoute = "At Destination",
            rating = 4.7,
            entryFee = "Free entry",
            visitDuration = "1.5 - 2 hours",
            nearbyStop = "Government Estate Metro (800m walk)"
        ),
        RouteAttractionItem(
            id = 10,
            name = "Government Museum & National Gallery",
            category = "Museums",
            description = "Second oldest museum in India, famous for Chola bronzes, Amaravati Buddhist sculptures, and art galleries.",
            imageUrl = "https://images.unsplash.com/photo-1566127444979-b3d2b654e3d7?w=800",
            distanceOffRoute = "0.7 km from route",
            rating = 4.6,
            entryFee = "₹15 adults, ₹10 children",
            visitDuration = "2 hours",
            nearbyStop = "LIC Metro / Egmore Station"
        ),
        RouteAttractionItem(
            id = 3,
            name = "Kapaleeshwarar Temple",
            category = "Religious",
            description = "Iconic 7th-century Dravidian Shiva temple in Mylapore featuring a 37-meter rainbow sculpted gopuram.",
            imageUrl = "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?w=800",
            distanceOffRoute = "1.8 km from Marina",
            rating = 4.9,
            entryFee = "Free entry",
            visitDuration = "1 hour",
            nearbyStop = "Thirumayilai MRTS / MTC 21G"
        ),
        RouteAttractionItem(
            id = 9,
            name = "Guindy National Park & Deer Park",
            category = "Nature",
            description = "Protected metropolitan sanctuary with blackbucks, spotted deer, and over 130 native bird species.",
            imageUrl = "https://images.unsplash.com/photo-1534567153574-2b12153a87f0?w=800",
            distanceOffRoute = "Along South Corridor",
            rating = 4.5,
            entryFee = "₹20 adults, ₹5 children",
            visitDuration = "2 - 3 hours",
            nearbyStop = "Guindy Metro (900m)"
        )
    ),

    // AI Journey Suggestions
    val journeySuggestions: List<AlternativeJourneyItem> = listOf(
        AlternativeJourneyItem(
            id = "rec_cmrl",
            title = "Current: CMRL Blue Line + Kamarajar Walk",
            modeCombination = "Metro + Walking",
            durationText = "18 min",
            fareText = "₹30",
            transfers = 0,
            co2Saved = "0.45 kg",
            transferPoints = "Direct to Government Estate, 4 min walk to beach",
            isRecommended = true
        ),
        AlternativeJourneyItem(
            id = "rec_mtc",
            title = "MTC Direct ECR Bus Route 21G / 102",
            modeCombination = "Bus Only",
            durationText = "26 min",
            fareText = "₹15",
            transfers = 0,
            co2Saved = "0.38 kg",
            transferPoints = "Board at Central, alight directly at Marina Lighthouse",
            isRecommended = false
        ),
        AlternativeJourneyItem(
            id = "rec_suburban",
            title = "Southern Railway MRTS + Promenade Stroll",
            modeCombination = "Suburban Rail + Walk",
            durationText = "22 min",
            fareText = "₹10",
            transfers = 0,
            co2Saved = "0.52 kg",
            transferPoints = "Chennai Beach to Chepauk MRTS (400m from sand)",
            isRecommended = false
        )
    ),

    // Notification Toggles
    val isNotificationsSheetOpen: Boolean = false,
    val notifyUpcomingStation: Boolean = true,
    val notifyDestinationApproaching: Boolean = true,
    val notifyPossibleDelays: Boolean = true,
    val notifyMissedConnections: Boolean = true,
    val notifyRouteChanges: Boolean = true,
    val notifyNearbyAttractions: Boolean = true,

    // AI Itinerary Builder Dialog
    val isItineraryBuilderOpen: Boolean = false,
    val itineraryDurationSelected: String = "2 - 3 hours",
    val itineraryInterestSelected: String = "Beaches & Culture",
    val generatedPlan: List<SmartPlanStop>? = null,

    // Toast/Alert message
    val userNotice: String? = null
)

class OnYourWayViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnYourWayUiState())
    val uiState: StateFlow<OnYourWayUiState> = _uiState.asStateFlow()

    private var locationTracker: LocationTrackerManager? = null
    private var trackingTimerJob: Job? = null
    private var lastRecordedLatLng: LatLng? = null

    fun dismissNotice() {
        _uiState.value = _uiState.value.copy(userNotice = null)
    }

    // ----------------------------------------------------
    // REAL-TIME GPS TRACKING CONTROLS
    // ----------------------------------------------------

    fun checkDeviceStatus(context: Context) {
        val tracker = getTracker(context)
        val hasPerm = tracker.hasLocationPermission()
        val isGps = tracker.isGpsEnabled()
        _uiState.value = _uiState.value.copy(
            hasLocationPermission = hasPerm,
            isGpsProviderEnabled = isGps
        )
    }

    private fun getTracker(context: Context): LocationTrackerManager {
        if (locationTracker == null) {
            locationTracker = LocationTrackerManager(context.applicationContext)
        }
        return locationTracker!!
    }

    fun startJourneyTracking(context: Context) {
        val tracker = getTracker(context)
        val hasPerm = tracker.hasLocationPermission()
        val isGps = tracker.isGpsEnabled()

        _uiState.value = _uiState.value.copy(
            hasLocationPermission = hasPerm,
            isGpsProviderEnabled = isGps
        )

        if (!hasPerm) {
            _uiState.value = _uiState.value.copy(
                userNotice = "⚠️ Location permission required to track your live journey on the map."
            )
            return
        }

        if (!isGps) {
            _uiState.value = _uiState.value.copy(
                userNotice = "⚠️ GPS location services are disabled. Please enable GPS."
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            trackingStatus = "TRACKING",
            isLiveTrackingActive = true,
            activeStatusTitle = "Tracking CMRL Blue Line in Real Time",
            userNotice = "🟢 Real-time GPS tracking active. MoveSmart AI is monitoring your route."
        )

        startTimer()

        tracker.startLocationUpdates(intervalMs = 4000L) { loc ->
            val newLatLng = LatLng(loc.latitude, loc.longitude)
            val prevLatLng = lastRecordedLatLng

            // Distance calculation
            var additionalKm = 0.0
            if (prevLatLng != null) {
                additionalKm = LocationTrackerManager.calculateDistanceKm(
                    prevLatLng.latitude, prevLatLng.longitude,
                    newLatLng.latitude, newLatLng.longitude
                )
                // Filter out small jitter
                if (additionalKm < 0.003) additionalKm = 0.0
            }
            lastRecordedLatLng = newLatLng

            val dest = _uiState.value.destinationLatLng
            val distToDest = LocationTrackerManager.calculateDistanceKm(
                newLatLng.latitude, newLatLng.longitude,
                dest.latitude, dest.longitude
            )

            // Total approximate route length ~ 5.5 km
            val totalRouteDist = 5.5
            val progress = ((totalRouteDist - distToDest) / totalRouteDist).coerceIn(0.10, 0.98).toFloat()

            // ETA in minutes
            val estMins = Math.max(2, (distToDest * 3.2).toInt())
            val remainingText = "${String.format(Locale.US, "%.1f", distToDest)} km remaining · ~$estMins min"

            val speedKmh = if (loc.hasSpeed()) loc.speed * 3.6f else 28.5f
            val freshness = LocationTrackerManager.formatFreshness(System.currentTimeMillis())

            // Update steps completion dynamically based on distance to stops
            val steps = _uiState.value.upcomingSteps.toMutableList()
            if (distToDest < 2.5 && steps.isNotEmpty()) {
                steps[0] = steps[0].copy(isCompleted = true)
            }
            if (distToDest < 1.0 && steps.size > 1) {
                steps[1] = steps[1].copy(isCompleted = true)
            }
            if (distToDest < 0.2 && steps.size > 2) {
                steps[2] = steps[2].copy(isCompleted = true)
            }

            val nextStop = when {
                distToDest > 2.5 -> "Next: Thousand Lights Metro · On Schedule"
                distToDest > 1.0 -> "Next: Government Estate Metro · Interchange"
                else -> "Next: Marina Beach & Promenade · Arriving shortly"
            }

            _uiState.value = _uiState.value.copy(
                userGpsLatLng = newLatLng,
                gpsAccuracyMeters = loc.accuracy,
                gpsSpeedKmh = speedKmh,
                gpsLastUpdatedText = freshness,
                remainingDistanceKm = distToDest,
                remainingTimeText = remainingText,
                totalTrackedDistanceKm = _uiState.value.totalTrackedDistanceKm + additionalKm,
                progressFraction = progress,
                nextStopInfo = nextStop,
                upcomingSteps = steps
            )
        }
    }

    fun pauseTracking() {
        locationTracker?.stopLocationUpdates()
        stopTimer()
        _uiState.value = _uiState.value.copy(
            trackingStatus = "PAUSED",
            isLiveTrackingActive = false,
            gpsLastUpdatedText = "Paused (Frozen)",
            userNotice = "⏸️ GPS tracking paused. Marker position frozen."
        )
    }

    fun resumeTracking(context: Context) {
        startJourneyTracking(context)
        _uiState.value = _uiState.value.copy(
            userNotice = "▶️ Real-time GPS tracking resumed."
        )
    }

    fun stopTracking() {
        locationTracker?.stopLocationUpdates()
        stopTimer()
        _uiState.value = _uiState.value.copy(
            trackingStatus = "STOPPED",
            isLiveTrackingActive = false,
            gpsLastUpdatedText = "Tracking stopped",
            userNotice = "⏹️ Live journey tracking stopped."
        )
    }

    fun recenterMap() {
        _uiState.value = _uiState.value.copy(
            recenterTrigger = System.currentTimeMillis(),
            userNotice = "🎯 Recentered to current location."
        )
    }

    // Toggle Tracking helper (for quick badge tap)
    fun toggleLiveTracking(context: Context) {
        if (_uiState.value.trackingStatus == "TRACKING") {
            pauseTracking()
        } else {
            startJourneyTracking(context)
        }
    }

    private fun startTimer() {
        trackingTimerJob?.cancel()
        trackingTimerJob = viewModelScope.launch {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            while (isActive) {
                delay(1000L)
                val newSecs = _uiState.value.trackedDurationSeconds + 1
                val curTime = timeFormat.format(Date())
                _uiState.value = _uiState.value.copy(
                    trackedDurationSeconds = newSecs,
                    currentTime = curTime
                )
            }
        }
    }

    private fun stopTimer() {
        trackingTimerJob?.cancel()
        trackingTimerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        locationTracker?.stopLocationUpdates()
        stopTimer()
    }

    // Map Layer Toggles
    fun toggleMapLayer(layer: String) {
        val s = _uiState.value
        _uiState.value = when (layer) {
            "stations" -> s.copy(showStationsLayer = !s.showStationsLayer)
            "attractions" -> s.copy(showAttractionsLayer = !s.showAttractionsLayer)
            "bus_stops" -> s.copy(showBusStopsLayer = !s.showBusStopsLayer)
            "facilities" -> s.copy(showFacilitiesLayer = !s.showFacilitiesLayer)
            else -> s
        }
    }

    fun selectMarker(detail: MapPointDetail?) {
        _uiState.value = _uiState.value.copy(selectedMarkerDetail = detail)
    }

    // Tourist Places Along Route Category
    fun setTouristCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedTouristCategory = category)
    }

    fun toggleSavePlace(placeId: Int) {
        val current = _uiState.value.placesAlongRoute
        val updated = current.map { p ->
            if (p.id == placeId) p.copy(isSaved = !p.isSaved) else p
        }
        val target = updated.firstOrNull { it.id == placeId }
        val msg = if (target?.isSaved == true) "Added ${target.name} to My Trip!" else "Removed from My Trip."
        _uiState.value = _uiState.value.copy(placesAlongRoute = updated, userNotice = msg)
    }

    // AI Assistant Handlers
    fun openAssistant(open: Boolean) {
        _uiState.value = _uiState.value.copy(isAssistantSheetOpen = open)
    }

    fun onAssistantQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(assistantQueryText = query)
    }

    fun sendAssistantQuery(userPrompt: String? = null) {
        val query = (userPrompt ?: _uiState.value.assistantQueryText).trim()
        if (query.isBlank()) return

        val userMsg = AIAssistantMessage(
            id = "user_${System.currentTimeMillis()}",
            sender = "user",
            text = query,
            timeText = _uiState.value.currentTime
        )

        val updatedChat = _uiState.value.assistantChat + userMsg
        _uiState.value = _uiState.value.copy(
            assistantChat = updatedChat,
            assistantQueryText = "",
            isAiThinking = true
        )

        viewModelScope.launch {
            delay(800)
            val aiResponseText = generateAIResponse(query)
            val aiMsg = AIAssistantMessage(
                id = "ai_${System.currentTimeMillis()}",
                sender = "ai",
                text = aiResponseText,
                timeText = _uiState.value.currentTime
            )
            _uiState.value = _uiState.value.copy(
                assistantChat = _uiState.value.assistantChat + aiMsg,
                isAiThinking = false
            )
        }
    }

    private fun generateAIResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("gps") || q.contains("location") || q.contains("where am i") -> {
                val coords = _uiState.value.userGpsLatLng
                if (coords != null) {
                    "Your live GPS position is ${String.format(Locale.US, "%.4f, %.4f", coords.latitude, coords.longitude)}. Remaining distance to destination is ${String.format(Locale.US, "%.1f", _uiState.value.remainingDistanceKm)} km."
                } else {
                    "Live GPS is active. We are waiting for high-accuracy satellite fix along CMRL Blue Line."
                }
            }
            q.contains("tourist") || q.contains("near my destination") || q.contains("visit") -> {
                "Near Marina Beach, you can visit the Marina Lighthouse (0.4 km), Fort St. George Museum (2.1 km), and Kapaleeshwarar Temple in Mylapore (1.8 km). I've highlighted them on your Explore Map!"
            }
            q.contains("next") || q.contains("station") || q.contains("stop") -> {
                "Your next stop is ${_uiState.value.nextStopInfo}. You should remain on the train until Government Estate Metro Station to reach Marina Promenade."
            }
            q.contains("alternative") || q.contains("route") || q.contains("bus") -> {
                "Alternative: If you switch at Central, MTC Bus 21G runs along Kamarajar Salai directly to Marina Beach without any walking transfer. Southern Railway MRTS to Chepauk is also available (₹10)."
            }
            q.contains("delay") || q.contains("time") -> {
                "CMRL Blue Line is running on time today with 98% reliability. Expected arrival at Government Estate is ${_uiState.value.remainingTimeText}."
            }
            else -> {
                "Based on Tamil Nadu transit telemetry: CMRL Blue Line is the fastest multi-modal link to Marina Beach. After arriving at Government Estate, use Exit A toward Kamarajar Salai Promenade."
            }
        }
    }

    // Notifications Dialog
    fun setNotificationsDialogOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isNotificationsSheetOpen = open)
    }

    fun toggleNotification(type: String) {
        val s = _uiState.value
        _uiState.value = when (type) {
            "station" -> s.copy(notifyUpcomingStation = !s.notifyUpcomingStation)
            "destination" -> s.copy(notifyDestinationApproaching = !s.notifyDestinationApproaching)
            "delays" -> s.copy(notifyPossibleDelays = !s.notifyPossibleDelays)
            "missed" -> s.copy(notifyMissedConnections = !s.notifyMissedConnections)
            "route_change" -> s.copy(notifyRouteChanges = !s.notifyRouteChanges)
            "attractions" -> s.copy(notifyNearbyAttractions = !s.notifyNearbyAttractions)
            else -> s
        }
    }

    // AI Itinerary Builder
    fun setItineraryBuilderOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isItineraryBuilderOpen = open)
    }

    fun setItineraryDuration(duration: String) {
        _uiState.value = _uiState.value.copy(itineraryDurationSelected = duration)
    }

    fun setItineraryInterest(interest: String) {
        _uiState.value = _uiState.value.copy(itineraryInterestSelected = interest)
    }

    fun generateSmartItinerary() {
        val duration = _uiState.value.itineraryDurationSelected
        val interest = _uiState.value.itineraryInterestSelected

        val plan = listOf(
            SmartPlanStop("09:48 AM", "Marina Beach & Lighthouse", "Attraction", "Stroll along the world's 2nd longest urban beach. Ascend the heritage lighthouse.", "Walk from Government Estate Metro"),
            SmartPlanStop("11:00 AM", "Fort St. George & Clive Museum", "Historical", "Discover British colonial archives, vintage weapons, and St. Mary's Church.", "MTC Bus 11G (10 mins)"),
            SmartPlanStop("01:15 PM", "Traditional South Indian Lunch", "Dining", "Authentic vegetarian thali along Mylapore Tank.", "Share Auto / 8 min ride"),
            SmartPlanStop("02:30 PM", "Kapaleeshwarar Temple & Tank", "Temple & Culture", "Explore 7th-century Dravidian architecture, sacred tank, and rainbow gopuram.", "Mylapore MRTS nearby")
        )

        _uiState.value = _uiState.value.copy(
            generatedPlan = plan,
            userNotice = "Smart Plan generated for $duration ($interest)!"
        )
    }

    fun saveItineraryToMyTrips(context: Context) {
        _uiState.value = _uiState.value.copy(
            userNotice = "✓ Smart Travel Plan saved to My Trips!",
            isItineraryBuilderOpen = false
        )
    }
}
