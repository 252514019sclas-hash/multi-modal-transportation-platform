package com.simats.multimodaltransportationrecommendation.ui.journey

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.location.LocationTrackerManager
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
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

data class SmartPlanStop(
    val time: String,
    val title: String,
    val type: String,
    val description: String,
    val transitHint: String
)

/**
 * Predefined Corridor Route covering major hubs and tourist spots across Tamil Nadu
 */
data class TamilNaduCorridorRoute(
    val id: String,
    val title: String,
    val transitBadge: String,
    val origin: String,
    val destination: String,
    val originDistrict: String,
    val destinationDistrict: String,
    val originLatLng: LatLng,
    val destinationLatLng: LatLng,
    val waypoints: List<LatLng>,
    val nextStopInfo: String,
    val reminderText: String,
    val upcomingSteps: List<JourneyUpcomingStep>,
    val stations: List<MapStationPoint>,
    val attractionsOnMap: List<MapAttractionPoint>
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

    // Active Route (Dynamic across Tamil Nadu)
    val selectedRouteId: String = "chennai_marina",
    val origin: String = "Chennai Central",
    val destination: String = "Marina Beach & Promenade",
    val originDistrict: String = "Chennai",
    val destinationDistrict: String = "Chennai",
    val originLatLng: LatLng = LatLng(13.0827, 80.2707),
    val destinationLatLng: LatLng = LatLng(13.0499, 80.2824),
    val routeWaypoints: List<LatLng> = listOf(
        LatLng(13.0732, 80.2608),
        LatLng(13.0645, 80.2644),
        LatLng(13.0583, 80.2544),
        LatLng(13.0673, 80.2736),
        LatLng(13.0499, 80.2824)
    ),
    val activeStatusTitle: String = "Ready to Track CMRL Blue Line",
    val nextStopInfo: String = "Next: Thousand Lights Metro · On Schedule",
    val reminderText: String = "We'll remind you 1 station before Government Estate",
    val upcomingSteps: List<JourneyUpcomingStep> = emptyList(),
    val stations: List<MapStationPoint> = emptyList(),
    val attractionsOnMap: List<MapAttractionPoint> = emptyList(),
    val availableRoutes: List<TamilNaduCorridorRoute> = emptyList(),
    val isRouteSelectorOpen: Boolean = false,

    // Map Layers
    val showStationsLayer: Boolean = true,
    val showAttractionsLayer: Boolean = true,
    val showBusStopsLayer: Boolean = false,
    val showFacilitiesLayer: Boolean = false,
    val facilitiesOnMap: List<com.simats.multimodaltransportationrecommendation.data.model.facilities.TravelFacility> = emptyList(),
    val selectedMarkerDetail: MapPointDetail? = null,

    // AI Smart Travel Assistant
    val isAssistantSheetOpen: Boolean = false,
    val assistantQueryText: String = "",
    val isAiThinking: Boolean = false,
    val assistantChat: List<AIAssistantMessage> = emptyList(),

    // Dynamic Tamil Nadu Tourist Places Discovery
    val selectedTouristCategory: String = "All",
    val touristSearchQuery: String = "",
    val isPlacesLoading: Boolean = false,
    val placesAlongRoute: List<TouristPlace> = emptyList(),
    val totalPlacesCount: Int = 0,

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
    private var searchDebounceJob: Job? = null

    // Predefined Corridor Routes across all regions of Tamil Nadu
    private val tamilNaduCorridors = listOf(
        TamilNaduCorridorRoute(
            id = "chennai_marina",
            title = "Chennai Central ➔ Marina Beach & Promenade",
            transitBadge = "CMRL Blue Line",
            origin = "Chennai Central",
            destination = "Marina Beach & Promenade",
            originDistrict = "Chennai",
            destinationDistrict = "Chennai",
            originLatLng = LatLng(13.0827, 80.2707),
            destinationLatLng = LatLng(13.0499, 80.2824),
            waypoints = listOf(
                LatLng(13.0732, 80.2608),
                LatLng(13.0645, 80.2644),
                LatLng(13.0583, 80.2544),
                LatLng(13.0673, 80.2736),
                LatLng(13.0499, 80.2824)
            ),
            nextStopInfo = "Next: Thousand Lights Metro · On Schedule",
            reminderText = "We'll remind you 1 station before Government Estate",
            upcomingSteps = listOf(
                JourneyUpcomingStep("09:38 AM", "🚇", "Arrive Thousand Lights Metro", "Station platform 2, train on schedule", false),
                JourneyUpcomingStep("09:44 AM", "🚇", "Exit at Government Estate Metro Station", "Direct interchange exit for Marina Promenade", false),
                JourneyUpcomingStep("09:48 AM", "🚶", "Arrive at Marina Beach & Promenade", "4-minute walk along Kamarajar Salai", false)
            ),
            stations = listOf(
                MapStationPoint("s1", "Chennai Central Metro", "metro", 13.0827, 80.2707, 0.15f, 0.75f, "09:30 AM"),
                MapStationPoint("s2", "LIC Metro Station", "metro", 13.0645, 80.2644, 0.35f, 0.62f, "09:34 AM"),
                MapStationPoint("s3", "Thousand Lights Metro", "metro", 13.0583, 80.2544, 0.55f, 0.50f, "09:38 AM"),
                MapStationPoint("s4", "Government Estate Metro", "metro", 13.0673, 80.2736, 0.72f, 0.35f, "09:44 AM"),
                MapStationPoint("s5", "Marina Beach Terminal", "bus", 13.0499, 80.2824, 0.88f, 0.22f, "09:48 AM")
            ),
            attractionsOnMap = listOf(
                MapAttractionPoint("a1", "Marina Beach & Promenade", "Beaches", 13.0499, 80.2824, 0.88f, 0.20f, "4.7★", "Free"),
                MapAttractionPoint("a2", "Government Museum Egmore", "Museums", 13.0700, 80.2562, 0.32f, 0.40f, "4.6★", "₹15"),
                MapAttractionPoint("a3", "Kapaleeshwarar Temple", "Temples", 13.0334, 80.2699, 0.65f, 0.68f, "4.9★", "Free"),
                MapAttractionPoint("a4", "Fort St. George & Clive", "Historical", 13.0797, 80.2872, 0.82f, 0.45f, "4.5★", "₹25")
            )
        ),
        TamilNaduCorridorRoute(
            id = "chennai_coimbatore",
            title = "Chennai Central ➔ Coimbatore Junction",
            transitBadge = "Vande Bharat / Kovai Express & NH 544",
            origin = "Chennai Central",
            destination = "Coimbatore Junction",
            originDistrict = "Chennai",
            destinationDistrict = "Coimbatore",
            originLatLng = LatLng(13.0827, 80.2707),
            destinationLatLng = LatLng(11.0016, 76.9629),
            waypoints = listOf(
                LatLng(13.0827, 80.2707),
                LatLng(13.0786, 79.6677),
                LatLng(12.9734, 79.1352),
                LatLng(12.5593, 78.5835),
                LatLng(12.1332, 78.3846),
                LatLng(11.6643, 78.1460),
                LatLng(11.4833, 77.8667),
                LatLng(11.3410, 77.7172),
                LatLng(11.1085, 77.3411),
                LatLng(11.0016, 76.9629)
            ),
            nextStopInfo = "Next: Katpadi / Vellore Junction · On Time",
            reminderText = "We'll remind you before reaching Salem & Erode Junctions",
            upcomingSteps = listOf(
                JourneyUpcomingStep("06:10 AM", "🚆", "Depart Chennai Central", "Platform 2 - Kovai Superfast Express", false),
                JourneyUpcomingStep("07:58 AM", "🚉", "Arrive Katpadi Junction (Vellore)", "Interchange for Vellore Golden Temple", false),
                JourneyUpcomingStep("10:50 AM", "⛰️", "Pass Salem Junction", "Gateway to Shevaroy & Yercaud Hills", false),
                JourneyUpcomingStep("11:50 AM", "🌊", "Pass Erode Junction", "Cauvery & Bhavani river transit corridor", false),
                JourneyUpcomingStep("01:10 PM", "🧵", "Pass Tiruppur Junction", "Knitwear industrial capital station", false),
                JourneyUpcomingStep("02:05 PM", "🏁", "Arrive Coimbatore Junction", "Western Tamil Nadu textile & transit terminal", false)
            ),
            stations = listOf(
                MapStationPoint("cc1", "Chennai Central", "train", 13.0827, 80.2707, 0.1f, 0.85f, "06:10 AM"),
                MapStationPoint("cc2", "Arakkonam Junction", "train", 13.0786, 79.6677, 0.22f, 0.78f, "07:05 AM"),
                MapStationPoint("cc3", "Katpadi (Vellore)", "train", 12.9734, 79.1352, 0.35f, 0.70f, "07:58 AM"),
                MapStationPoint("cc4", "Jolarpettai Junction", "train", 12.5593, 78.5835, 0.48f, 0.60f, "09:15 AM"),
                MapStationPoint("cc5", "Salem Junction", "train", 11.6643, 78.1460, 0.62f, 0.48f, "10:50 AM"),
                MapStationPoint("cc6", "Erode Junction", "train", 11.3410, 77.7172, 0.75f, 0.38f, "11:50 AM"),
                MapStationPoint("cc7", "Tiruppur Junction", "train", 11.1085, 77.3411, 0.85f, 0.28f, "01:10 PM"),
                MapStationPoint("cc8", "Coimbatore Junction", "train", 11.0016, 76.9629, 0.95f, 0.15f, "02:05 PM")
            ),
            attractionsOnMap = listOf(
                MapAttractionPoint("ca1", "Vellore Fort & Jalakandeswarar", "Historical", 12.9202, 79.1325, 0.36f, 0.68f, "4.6★", "Free"),
                MapAttractionPoint("ca2", "Sripuram Golden Temple", "Temples", 12.8711, 79.0886, 0.38f, 0.65f, "4.8★", "Free"),
                MapAttractionPoint("ca3", "Yercaud Emerald Lake", "Hill Stations", 11.7753, 78.2093, 0.63f, 0.50f, "4.7★", "₹100"),
                MapAttractionPoint("ca4", "Bhavani Sangameshwarar Temple", "Temples", 11.4450, 77.6830, 0.74f, 0.39f, "4.8★", "Free"),
                MapAttractionPoint("ca5", "Marudhamalai Murugan Temple", "Temples", 11.0450, 76.8520, 0.96f, 0.14f, "4.9★", "Free")
            )
        ),
        TamilNaduCorridorRoute(
            id = "cbe_ooty",
            title = "Coimbatore Junction ➔ Ooty Botanical Garden",
            transitBadge = "Nilgiri Mountain Express",
            origin = "Coimbatore Junction",
            destination = "Ooty Botanical Garden & Lake",
            originDistrict = "Coimbatore",
            destinationDistrict = "Nilgiris",
            originLatLng = LatLng(11.0018, 76.9628),
            destinationLatLng = LatLng(11.4190, 76.7110),
            waypoints = listOf(
                LatLng(11.0018, 76.9628),
                LatLng(11.1960, 76.9380),
                LatLng(11.3000, 76.8900),
                LatLng(11.3530, 76.7950),
                LatLng(11.4190, 76.7110)
            ),
            nextStopInfo = "Next: Mettupalayam Junction · Mountain Rail Transfer",
            reminderText = "We'll remind you before the Coonoor Toy Train ascent",
            upcomingSteps = listOf(
                JourneyUpcomingStep("10:15 AM", "🚂", "Board Mountain Express at Mettupalayam", "Connecting to UNESCO Nilgiri Toy Train", false),
                JourneyUpcomingStep("11:30 AM", "⛰️", "Pass Coonoor Heritage Station", "Scenic 15-minute tea break stop", false),
                JourneyUpcomingStep("12:45 PM", "🌸", "Arrive Ooty Botanical Garden", "Walk into 55-acre terraced mountain gardens", false)
            ),
            stations = listOf(
                MapStationPoint("co1", "Coimbatore Jn", "train", 11.0018, 76.9628, 0.1f, 0.8f, "09:30 AM"),
                MapStationPoint("co2", "Mettupalayam Station", "train", 11.3000, 76.8900, 0.35f, 0.6f, "10:15 AM"),
                MapStationPoint("co3", "Coonoor Station", "train", 11.3530, 76.7950, 0.65f, 0.4f, "11:30 AM"),
                MapStationPoint("co4", "Udagamandalam (Ooty)", "train", 11.4190, 76.7110, 0.9f, 0.2f, "12:45 PM")
            ),
            attractionsOnMap = listOf(
                MapAttractionPoint("oa1", "Government Botanical Garden", "Parks", 11.4190, 76.7110, 0.88f, 0.20f, "4.7★", "₹40"),
                MapAttractionPoint("oa2", "Doddabetta Peak Viewpoint", "Hill Stations", 11.4014, 76.7360, 0.75f, 0.30f, "4.6★", "₹10"),
                MapAttractionPoint("oa3", "Ooty Lake & Boathouse", "Nature", 11.4050, 76.6950, 0.6f, 0.5f, "4.6★", "₹30"),
                MapAttractionPoint("oa4", "Adiyogi Shiva & Isha Yoga", "Temples", 10.9723, 76.7406, 0.15f, 0.85f, "4.9★", "Free")
            )
        ),
        TamilNaduCorridorRoute(
            id = "madurai_rameswaram",
            title = "Madurai Junction ➔ Rameswaram Temple & Beach",
            transitBadge = "Madurai - Pamban Express",
            origin = "Madurai Junction",
            destination = "Ramanathaswamy Temple & Beach",
            originDistrict = "Madurai",
            destinationDistrict = "Ramanathapuram",
            originLatLng = LatLng(9.9195, 78.1100),
            destinationLatLng = LatLng(9.2881, 79.3174),
            waypoints = listOf(
                LatLng(9.9195, 78.1100),
                LatLng(9.6000, 78.4800),
                LatLng(9.3667, 78.8333),
                LatLng(9.2789, 79.2086),
                LatLng(9.2881, 79.3174)
            ),
            nextStopInfo = "Next: Mandapam Pamban Sea Bridge",
            reminderText = "We'll remind you 10 minutes before crossing Pamban Bridge",
            upcomingSteps = listOf(
                JourneyUpcomingStep("08:30 AM", "🚆", "Depart Madurai Junction", "Platform 4 - Pamban Express", false),
                JourneyUpcomingStep("10:45 AM", "🌊", "Cross Pamban Sea Bridge", "Over-water ocean rail crossing over Palk Strait", false),
                JourneyUpcomingStep("11:15 AM", "🛕", "Arrive Ramanathaswamy Temple", "22 holy theertham pilgrimage stop", false)
            ),
            stations = listOf(
                MapStationPoint("mr1", "Madurai Junction", "train", 9.9195, 78.1100, 0.1f, 0.8f, "08:30 AM"),
                MapStationPoint("mr2", "Manamadurai Jn", "train", 9.6000, 78.4800, 0.4f, 0.6f, "09:30 AM"),
                MapStationPoint("mr3", "Mandapam Sea Bridge", "train", 9.2789, 79.2086, 0.75f, 0.4f, "10:45 AM"),
                MapStationPoint("mr4", "Rameswaram Terminus", "train", 9.2881, 79.3174, 0.9f, 0.2f, "11:15 AM")
            ),
            attractionsOnMap = listOf(
                MapAttractionPoint("ra1", "Meenakshi Amman Temple", "Temples", 9.9195, 78.1193, 0.12f, 0.8f, "4.9★", "Free"),
                MapAttractionPoint("ra2", "Ramanathaswamy Temple", "Temples", 9.2881, 79.3174, 0.9f, 0.2f, "4.9★", "Free"),
                MapAttractionPoint("ra3", "Dhanushkodi Ghost Beach", "Beaches", 9.1783, 79.4183, 0.95f, 0.15f, "4.9★", "Free"),
                MapAttractionPoint("ra4", "Pamban Sea Bridge", "Historical", 9.2789, 79.2086, 0.75f, 0.35f, "4.8★", "Free")
            )
        ),
        TamilNaduCorridorRoute(
            id = "trichy_thanjavur",
            title = "Trichy Central ➔ Thanjavur Big Temple",
            transitBadge = "Chola Heritage Express",
            origin = "Trichy Central Junction",
            destination = "Thanjavur Brihadisvara Temple",
            originDistrict = "Tiruchirappalli",
            destinationDistrict = "Thanjavur",
            originLatLng = LatLng(10.7905, 78.6900),
            destinationLatLng = LatLng(10.7828, 79.1318),
            waypoints = listOf(
                LatLng(10.7905, 78.6900),
                LatLng(10.8333, 78.8167),
                LatLng(10.8000, 78.9500),
                LatLng(10.7828, 79.1318)
            ),
            nextStopInfo = "Next: Kallanai Grand Anicut Junction",
            reminderText = "We'll remind you before reaching Thanjavur Gate",
            upcomingSteps = listOf(
                JourneyUpcomingStep("09:10 AM", "🚆", "Depart Trichy Central", "Platform 3 - Cauvery Express", false),
                JourneyUpcomingStep("09:40 AM", "🌊", "Pass Kallanai Grand Anicut", "2nd-century Chola engineering landmark", false),
                JourneyUpcomingStep("10:15 AM", "🛕", "Arrive Brihadisvara Temple", "UNESCO World Heritage granite temple entrance", false)
            ),
            stations = listOf(
                MapStationPoint("tt1", "Trichy Central", "train", 10.7905, 78.6900, 0.1f, 0.8f, "09:10 AM"),
                MapStationPoint("tt2", "Kallanai Junction", "bus", 10.8333, 78.8167, 0.45f, 0.55f, "09:40 AM"),
                MapStationPoint("tt3", "Thanjavur Junction", "train", 10.7828, 79.1318, 0.9f, 0.2f, "10:15 AM")
            ),
            attractionsOnMap = listOf(
                MapAttractionPoint("ta1", "Brihadisvara Big Temple", "Temples", 10.7828, 79.1318, 0.88f, 0.2f, "4.9★", "Free"),
                MapAttractionPoint("ta2", "Sri Ranganathaswamy Temple", "Temples", 10.8624, 78.6901, 0.15f, 0.85f, "4.9★", "Free"),
                MapAttractionPoint("ta3", "Rockfort Pillayar Temple", "Historical", 10.8282, 78.6968, 0.25f, 0.75f, "4.8★", "Free"),
                MapAttractionPoint("ta4", "Kallanai Grand Anicut", "Lakes & Dams", 10.8333, 78.8167, 0.45f, 0.55f, "4.7★", "₹5")
            )
        ),
        TamilNaduCorridorRoute(
            id = "salem_yercaud",
            title = "Salem Junction ➔ Yercaud Emerald Lake",
            transitBadge = "Shevaroy Mountain Ghat Transit",
            origin = "Salem Junction",
            destination = "Yercaud Lake & Lady's Seat",
            originDistrict = "Salem",
            destinationDistrict = "Salem",
            originLatLng = LatLng(11.6643, 78.1460),
            destinationLatLng = LatLng(11.7753, 78.2093),
            waypoints = listOf(
                LatLng(11.6643, 78.1460),
                LatLng(11.7000, 78.1700),
                LatLng(11.7400, 78.1900),
                LatLng(11.7753, 78.2093)
            ),
            nextStopInfo = "Next: 10th Hairpin Bend Scenic Viewpoint",
            reminderText = "We'll remind you at the 20th Hairpin summit",
            upcomingSteps = listOf(
                JourneyUpcomingStep("02:00 PM", "🚌", "Board TNSTC Hill Bus 13", "Salem Hasthampatti Bus Stand", false),
                JourneyUpcomingStep("02:45 PM", "🌀", "Ascend 20 Hairpin Bends", "Misty valley panorama over Salem plains", false),
                JourneyUpcomingStep("03:15 PM", "🛶", "Arrive Yercaud Emerald Lake", "Boating & Anna Park garden stroll", false)
            ),
            stations = listOf(
                MapStationPoint("sy1", "Salem Junction", "train", 11.6643, 78.1460, 0.1f, 0.8f, "02:00 PM"),
                MapStationPoint("sy2", "Hasthampatti Stand", "bus", 11.7000, 78.1700, 0.35f, 0.6f, "02:20 PM"),
                MapStationPoint("sy3", "10th Hairpin View", "bus", 11.7400, 78.1900, 0.65f, 0.4f, "02:45 PM"),
                MapStationPoint("sy4", "Yercaud Lake Terminal", "bus", 11.7753, 78.2093, 0.9f, 0.2f, "03:15 PM")
            ),
            attractionsOnMap = listOf(
                MapAttractionPoint("ya1", "Yercaud Emerald Lake", "Hill Stations", 11.7753, 78.2093, 0.88f, 0.2f, "4.7★", "₹100"),
                MapAttractionPoint("ya2", "Mettur Dam & Ellis Park", "Lakes & Dams", 11.7964, 77.8016, 0.2f, 0.8f, "4.5★", "₹10"),
                MapAttractionPoint("ya3", "Lady's Seat Viewpoint", "Hill Stations", 11.7680, 78.2050, 0.8f, 0.25f, "4.6★", "Free")
            )
        ),
        TamilNaduCorridorRoute(
            id = "tirunelveli_kanyakumari",
            title = "Tirunelveli Junction ➔ Kanyakumari Cape",
            transitBadge = "Cape Coastal Intercity Express",
            origin = "Tirunelveli Junction",
            destination = "Vivekananda Rock & Triveni Sangam",
            originDistrict = "Tirunelveli",
            destinationDistrict = "Kanyakumari",
            originLatLng = LatLng(8.7300, 77.7000),
            destinationLatLng = LatLng(8.0780, 77.5550),
            waypoints = listOf(
                LatLng(8.7300, 77.7000),
                LatLng(8.4000, 77.6000),
                LatLng(8.1800, 77.4300),
                LatLng(8.0780, 77.5550)
            ),
            nextStopInfo = "Next: Nagercoil Town Interchange",
            reminderText = "We'll remind you before reaching Cape Ocean Terminus",
            upcomingSteps = listOf(
                JourneyUpcomingStep("07:45 AM", "🚆", "Depart Tirunelveli Junction", "Platform 1 - Kanyakumari Express", false),
                JourneyUpcomingStep("08:35 AM", "🌬️", "Pass Nagercoil Wind Farm Plains", "Scenic Western Ghats foothills view", false),
                JourneyUpcomingStep("09:20 AM", "⛵", "Arrive Kanyakumari Cape", "Poompuhar Ferry to Vivekananda Rock", false)
            ),
            stations = listOf(
                MapStationPoint("tk1", "Tirunelveli Jn", "train", 8.7300, 77.7000, 0.1f, 0.8f, "07:45 AM"),
                MapStationPoint("tk2", "Valliyur Station", "train", 8.4000, 77.6000, 0.45f, 0.55f, "08:15 AM"),
                MapStationPoint("tk3", "Nagercoil Town", "train", 8.1800, 77.4300, 0.7f, 0.35f, "08:45 AM"),
                MapStationPoint("tk4", "Kanyakumari Cape", "train", 8.0780, 77.5550, 0.9f, 0.2f, "09:20 AM")
            ),
            attractionsOnMap = listOf(
                MapAttractionPoint("ka1", "Vivekananda Rock Memorial", "Historical", 8.0780, 77.5550, 0.88f, 0.2f, "4.9★", "₹20"),
                MapAttractionPoint("ka2", "Sunset Point & Triveni Sangam", "Beaches", 8.0792, 77.5496, 0.85f, 0.25f, "4.8★", "Free"),
                MapAttractionPoint("ka3", "Nellaiappar Temple", "Temples", 8.7289, 77.6892, 0.12f, 0.8f, "4.9★", "Free"),
                MapAttractionPoint("ka4", "Courtallam Main Falls", "Waterfalls", 8.9300, 77.2694, 0.3f, 0.6f, "4.8★", "Free")
            )
        )
    )

    init {
        val defaultRoute = tamilNaduCorridors[0]
        _uiState.value = _uiState.value.copy(
            availableRoutes = tamilNaduCorridors,
            selectedRouteId = defaultRoute.id,
            origin = defaultRoute.origin,
            destination = defaultRoute.destination,
            originDistrict = defaultRoute.originDistrict,
            destinationDistrict = defaultRoute.destinationDistrict,
            originLatLng = defaultRoute.originLatLng,
            destinationLatLng = defaultRoute.destinationLatLng,
            routeWaypoints = defaultRoute.waypoints,
            activeStatusTitle = "Ready to Track ${defaultRoute.transitBadge}",
            nextStopInfo = defaultRoute.nextStopInfo,
            reminderText = defaultRoute.reminderText,
            upcomingSteps = defaultRoute.upcomingSteps,
            stations = defaultRoute.stations,
            attractionsOnMap = defaultRoute.attractionsOnMap
        )

        // Load all 38 districts Tamil Nadu tourist places from backend
        loadTamilNaduPlaces()
    }

    fun dismissNotice() {
        _uiState.value = _uiState.value.copy(userNotice = null)
    }

    // ----------------------------------------------------
    // DYNAMIC TAMIL NADU ROUTE SWITCHER
    // ----------------------------------------------------

    fun setRouteSelectorOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isRouteSelectorOpen = open)
    }

    fun selectRoute(route: TamilNaduCorridorRoute) {
        _uiState.value = _uiState.value.copy(
            selectedRouteId = route.id,
            origin = route.origin,
            destination = route.destination,
            originDistrict = route.originDistrict,
            destinationDistrict = route.destinationDistrict,
            originLatLng = route.originLatLng,
            destinationLatLng = route.destinationLatLng,
            routeWaypoints = route.waypoints,
            activeStatusTitle = "Tracking ${route.transitBadge}",
            nextStopInfo = route.nextStopInfo,
            reminderText = route.reminderText,
            upcomingSteps = route.upcomingSteps,
            stations = route.stations,
            attractionsOnMap = route.attractionsOnMap,
            isRouteSelectorOpen = false,
            userNotice = "Route changed: ${route.title}"
        )

        // Refresh places along the newly selected destination district & Tamil Nadu
        loadTamilNaduPlaces(search = route.destinationDistrict)
    }

    // ----------------------------------------------------
    // DYNAMIC TAMIL NADU TOURIST PLACES DISCOVERY & SEARCH
    // ----------------------------------------------------

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(touristSearchQuery = query)
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(350L)
            loadTamilNaduPlaces(search = query, category = _uiState.value.selectedTouristCategory)
        }
    }

    fun setTouristCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedTouristCategory = category)
        loadTamilNaduPlaces(search = _uiState.value.touristSearchQuery, category = category)
    }

    fun loadTamilNaduPlaces(search: String? = null, category: String? = null) {
        _uiState.value = _uiState.value.copy(isPlacesLoading = true)

        viewModelScope.launch {
            val q = search ?: _uiState.value.touristSearchQuery
            val cat = category ?: _uiState.value.selectedTouristCategory
            val lat = _uiState.value.userGpsLatLng?.latitude ?: _uiState.value.originLatLng.latitude
            val lng = _uiState.value.userGpsLatLng?.longitude ?: _uiState.value.originLatLng.longitude

            when (val result = authRepository.fetchPlaces(
                userId = 1,
                city = null,
                category = if (cat.equals("All", ignoreCase = true)) null else cat,
                search = if (q.isBlank()) null else q,
                lat = lat,
                lng = lng
            )) {
                is NetworkResult.Success -> {
                    val places = result.data
                    _uiState.value = _uiState.value.copy(
                        placesAlongRoute = places,
                        totalPlacesCount = places.size,
                        isPlacesLoading = false
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isPlacesLoading = false,
                        userNotice = "Places: ${result.message}"
                    )
                }
            }
        }
    }

    fun toggleSavePlace(placeId: Int, context: Context) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val currentList = _uiState.value.placesAlongRoute
        val target = currentList.firstOrNull { it.placeId == placeId }
        val newAction = if (target?.isSaved == true) "remove" else "save"

        viewModelScope.launch {
            when (val result = authRepository.savePlace(userId, placeId, newAction)) {
                is NetworkResult.Success -> {
                    val savedNow = result.data
                    val updated = currentList.map { p ->
                        if (p.placeId == placeId) p.copy(isSaved = savedNow) else p
                    }
                    val msg = if (savedNow) "✓ Added ${target?.name ?: "Place"} to My Trips!" else "Removed from My Trips"
                    _uiState.value = _uiState.value.copy(placesAlongRoute = updated, userNotice = msg)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(userNotice = result.message)
                }
            }
        }
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
            activeStatusTitle = "Live Tracking: ${_uiState.value.origin} ➔ ${_uiState.value.destination}",
            userNotice = "🟢 Real-time GPS tracking active across Tamil Nadu."
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
                if (additionalKm < 0.003) additionalKm = 0.0
            }
            lastRecordedLatLng = newLatLng

            val dest = _uiState.value.destinationLatLng
            val distToDest = LocationTrackerManager.calculateDistanceKm(
                newLatLng.latitude, newLatLng.longitude,
                dest.latitude, dest.longitude
            )

            val totalRouteDist = Math.max(5.0, distToDest * 1.2)
            val progress = ((totalRouteDist - distToDest) / totalRouteDist).coerceIn(0.10, 0.98).toFloat()

            // ETA in minutes
            val estMins = Math.max(2, (distToDest * 2.8).toInt())
            val remainingText = "${String.format(Locale.US, "%.1f", distToDest)} km remaining · ~$estMins min"

            val speedKmh = if (loc.hasSpeed()) loc.speed * 3.6f else 32.0f
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
                distToDest > 2.5 -> _uiState.value.nextStopInfo
                distToDest > 1.0 -> "Approaching ${_uiState.value.destination} area"
                else -> "Arriving at ${_uiState.value.destination} shortly"
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
        searchDebounceJob?.cancel()
    }

    // Map Layer Toggles
    fun toggleMapLayer(layer: String) {
        val s = _uiState.value
        _uiState.value = when (layer) {
            "stations" -> s.copy(showStationsLayer = !s.showStationsLayer)
            "attractions" -> s.copy(showAttractionsLayer = !s.showAttractionsLayer)
            "bus_stops" -> s.copy(showBusStopsLayer = !s.showBusStopsLayer)
            "facilities" -> {
                val newShow = !s.showFacilitiesLayer
                if (newShow && s.facilitiesOnMap.isEmpty()) {
                    loadNearbyFacilitiesForCurrentLocation()
                }
                s.copy(showFacilitiesLayer = newShow)
            }
            else -> s
        }
    }

    fun loadNearbyFacilitiesForCurrentLocation() {
        val dest = _uiState.value.destination
        val orig = _uiState.value.origin
        viewModelScope.launch {
            val res = authRepository.fetchNearbyFacilities(
                city = dest,
                origin = orig,
                destination = dest
            )
            if (res is NetworkResult.Success) {
                _uiState.value = _uiState.value.copy(facilitiesOnMap = res.data.facilities)
            }
        }
    }

    fun selectMarker(detail: MapPointDetail?) {
        _uiState.value = _uiState.value.copy(selectedMarkerDetail = detail)
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
                    "Your live GPS position is ${String.format(Locale.US, "%.4f, %.4f", coords.latitude, coords.longitude)}. Remaining distance to ${_uiState.value.destination} is ${String.format(Locale.US, "%.1f", _uiState.value.remainingDistanceKm)} km."
                } else {
                    "Live GPS is active across Tamil Nadu. Satellite fix is updating for ${_uiState.value.origin} ➔ ${_uiState.value.destination}."
                }
            }
            q.contains("tourist") || q.contains("near") || q.contains("visit") || q.contains("places") -> {
                "Along your route to ${_uiState.value.destination}, you can discover top attractions in ${_uiState.value.destinationDistrict} and across Tamil Nadu! Use the Explore Places search bar below to find beaches, hill stations, temples, and waterfalls."
            }
            q.contains("next") || q.contains("station") || q.contains("stop") -> {
                "Your next stop is ${_uiState.value.nextStopInfo}. You are tracking on time towards ${_uiState.value.destination}."
            }
            q.contains("route") || q.contains("change") || q.contains("corridor") -> {
                "You can tap 'Change Route' anytime to switch between Chennai, Coimbatore, Madurai, Salem, Trichy, Tirunelveli, Ooty, or Kanyakumari transit corridors."
            }
            else -> {
                "MoveSmart AI Tamil Nadu Transit: Tracking ${_uiState.value.origin} ➔ ${_uiState.value.destination}. ETA is ${_uiState.value.remainingTimeText}."
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
        val dest = _uiState.value.destination

        val plan = listOf(
            SmartPlanStop("09:45 AM", "$dest Sightseeing", "Attraction", "Explore prime scenic and heritage spots around $dest.", "Direct transit stop"),
            SmartPlanStop("11:30 AM", "Heritage Landmark & Museum", "Historical", "Discover ancient Chola/Pandya architecture and cultural archives.", "Short auto/bus transfer"),
            SmartPlanStop("01:15 PM", "Traditional Tamil Nadu Feast", "Dining", "Authentic regional banana leaf lunch & filter coffee.", "Nearby dining row"),
            SmartPlanStop("03:00 PM", "Scenic Viewpoint & Leisure", "Nature & Leisure", "Relax and capture photography before return transit.", "Walk from destination")
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
