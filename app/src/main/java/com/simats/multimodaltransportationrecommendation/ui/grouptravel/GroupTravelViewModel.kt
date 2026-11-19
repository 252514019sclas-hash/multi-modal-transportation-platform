package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.simats.multimodaltransportationrecommendation.data.location.LocationTrackerManager
import com.simats.multimodaltransportationrecommendation.data.model.group.*
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.ui.journey.CorridorStopItem
import com.simats.multimodaltransportationrecommendation.ui.journey.TamilNaduTransitResolver
import com.simats.multimodaltransportationrecommendation.ui.journey.TamilNaduTravelRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GroupTravelUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Dashboard
    val myGroups: List<TravelGroup> = emptyList(),
    val upcomingTrips: List<TravelGroup> = emptyList(),
    val completedTrips: List<TravelGroup> = emptyList(),

    // Active Group
    val activeGroup: GroupDetailsData? = null,
    val selectedTab: Int = 0, // 0=Overview, 1=Map, 2=Itinerary, 3=Explore, 4=Chat, 5=Settings

    // Live Map
    val destinationMapPoint: DestinationMapPoint? = null,
    val liveLocations: List<GroupMemberLocation> = emptyList(),
    val meetingPoints: List<GroupMeetingPoint> = emptyList(),
    val isSharingActive: Boolean = false,
    val isSharingPaused: Boolean = false,
    val activeSharingCount: Int = 0,
    val userGpsLatLng: LatLng? = null,
    val gpsAccuracy: Float? = null,
    val gpsSpeedKmh: Float? = null,
    val gpsLastUpdated: String = "Location sharing off",
    val showTouristPlacesOnMap: Boolean = true,
    val showMeetingPointsOnMap: Boolean = true,
    val cameraFocusLatLng: LatLng? = null,
    val mapRecenterTrigger: Long = 0L,
    val mapFitAllTrigger: Long = 0L,

    // Tamil Nadu Travel Route & In-Between Places State
    val activeTravelRoute: TamilNaduTravelRoute? = null,
    val routeOriginQuery: String = "Chennai Central",
    val routeDestQuery: String = "Coimbatore",
    val isRouteSearchSheetOpen: Boolean = false,
    val selectedEnRouteFilter: String = "ALL", // "ALL", "STATIONS", "PLACES"
    val selectedEnRouteStop: CorridorStopItem? = null,
    val showTravelRouteOnMap: Boolean = true,
    val showIntermediateStopsOnMap: Boolean = true,

    // Itinerary & Explore
    val itinerary: List<GroupItineraryItem> = emptyList(),
    val explorePlaces: List<GroupPlaceSuggestion> = emptyList(),
    val selectedExploreCategory: String = "All",

    // Chat
    val chatMessages: List<GroupChatMessage> = emptyList(),
    val chatInput: String = "",

    // Create / Join forms
    val formGroupName: String = "",
    val formDestination: String = "",
    val formStartLocation: String = "Chennai Central",
    val formTravelDate: String = "",
    val formTravelTime: String = "08:00 AM",
    val formDescription: String = "",
    val formPrivacy: String = "invite_only",
    val formInviteCode: String = "",
    val joinInviteCode: String = "",
    val joinStartLocation: String = "Chennai Central",

    // Action feedback
    val newlyCreatedGroup: TravelGroup? = null
)

class GroupTravelViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupTravelUiState())
    val uiState: StateFlow<GroupTravelUiState> = _uiState.asStateFlow()

    private var pollJob: Job? = null

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun setSelectedTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex)
    }

    // ----------------------------------------------------
    // DASHBOARD & GROUP FETCH
    // ----------------------------------------------------

    fun loadGroups(context: Context) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val res = repository.fetchGroups(userId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        myGroups = res.data.myGroups,
                        upcomingTrips = res.data.upcomingTrips,
                        completedTrips = res.data.completedTrips
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = res.message
                    )
                }
            }
        }
    }

    // ----------------------------------------------------
    // CREATE GROUP
    // ----------------------------------------------------

    fun onFormGroupNameChange(v: String) { _uiState.value = _uiState.value.copy(formGroupName = v) }
    fun onFormDestinationChange(v: String) { _uiState.value = _uiState.value.copy(formDestination = v) }
    fun onFormStartLocationChange(v: String) { _uiState.value = _uiState.value.copy(formStartLocation = v) }
    fun onFormTravelDateChange(v: String) { _uiState.value = _uiState.value.copy(formTravelDate = v) }
    fun onFormTravelTimeChange(v: String) { _uiState.value = _uiState.value.copy(formTravelTime = v) }
    fun onFormDescriptionChange(v: String) { _uiState.value = _uiState.value.copy(formDescription = v) }
    fun onFormPrivacyChange(v: String) { _uiState.value = _uiState.value.copy(formPrivacy = v) }

    fun createGroup(context: Context, onSuccess: (TravelGroup) -> Unit) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val s = _uiState.value

        if (s.formGroupName.isBlank() || s.formDestination.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter group name and destination.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val res = repository.createGroup(
                userId = userId,
                groupName = s.formGroupName.trim(),
                destination = s.formDestination.trim(),
                startingLocation = s.formStartLocation.ifBlank { "Chennai Central" },
                travelDate = s.formTravelDate.ifBlank { "2026-11-15" },
                travelTime = s.formTravelTime.ifBlank { "08:00 AM" },
                description = s.formDescription.trim().ifBlank { null },
                privacy = s.formPrivacy
            )

            when (res) {
                is NetworkResult.Success -> {
                    val group = res.data.group
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Group created! Code: ${group?.invitationCode}",
                        newlyCreatedGroup = group
                    )
                    loadGroups(context)
                    if (group != null) {
                        onSuccess(group)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
            }
        }
    }

    // ----------------------------------------------------
    // JOIN GROUP
    // ----------------------------------------------------

    fun onJoinInviteCodeChange(v: String) { _uiState.value = _uiState.value.copy(joinInviteCode = v.uppercase()) }
    fun onJoinStartLocationChange(v: String) { _uiState.value = _uiState.value.copy(joinStartLocation = v) }

    fun joinGroup(context: Context, onSuccess: () -> Unit) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val s = _uiState.value

        if (s.joinInviteCode.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter an invitation code.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val res = repository.joinGroup(userId, s.joinInviteCode.trim(), s.joinStartLocation.ifBlank { "Chennai Central" })) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = res.data.message
                    )
                    loadGroups(context)
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
            }
        }
    }

    // ----------------------------------------------------
    // ACTIVE GROUP DETAILS & POLLING
    // ----------------------------------------------------

    fun loadGroupDetails(context: Context, groupId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            when (val res = repository.fetchGroupDetails(userId, groupId)) {
                is NetworkResult.Success -> {
                    val g = res.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        activeGroup = g,
                        isSharingActive = g.userMembership?.isSharing == true
                    )
                    // Concurrently load live locations, itinerary, places, chat
                    loadLiveLocations(context, groupId)
                    loadItinerary(context, groupId)
                    loadExplorePlaces(context, groupId, "All")
                    loadChat(context, groupId)

                    startLivePolling(context, groupId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = res.message)
                }
            }
        }
    }

    private fun startLivePolling(context: Context, groupId: Int) {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (isActive) {
                delay(12000) // Poll every 12 seconds for low battery impact
                val userId = SessionManager.getInstance(context).getUserId() ?: 1
                // Silent refresh of locations and chat
                val locRes = repository.fetchGroupLocations(userId, groupId)
                if (locRes is NetworkResult.Success) {
                    _uiState.value = _uiState.value.copy(
                        liveLocations = locRes.data.members,
                        activeSharingCount = locRes.data.activeSharingCount
                    )
                }
                val chatRes = repository.fetchGroupChat(userId, groupId)
                if (chatRes is NetworkResult.Success) {
                    _uiState.value = _uiState.value.copy(chatMessages = chatRes.data)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollJob?.cancel()
        locationTracker?.stopLocationUpdates()
    }

    // ----------------------------------------------------
    // LIVE LOCATION CONTROLS & PRIVACY
    // ----------------------------------------------------

    private var locationTracker: LocationTrackerManager? = null

    private fun getTracker(context: Context): LocationTrackerManager {
        if (locationTracker == null) {
            locationTracker = LocationTrackerManager(context.applicationContext)
        }
        return locationTracker!!
    }

    fun startLocationSharing(context: Context, groupId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val tracker = getTracker(context)

        if (!tracker.hasLocationPermission()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Location permission is required to share your live GPS position.")
            return
        }

        viewModelScope.launch {
            repository.updateLocationSettings(userId, groupId, true)
            _uiState.value = _uiState.value.copy(
                isSharingActive = true,
                isSharingPaused = false,
                successMessage = "🟢 Live GPS location sharing started."
            )

            tracker.startLocationUpdates(intervalMs = 8000L) { loc ->
                val newLatLng = LatLng(loc.latitude, loc.longitude)
                val freshness = LocationTrackerManager.formatFreshness(System.currentTimeMillis())
                val speed = if (loc.hasSpeed()) loc.speed * 3.6f else 0f

                _uiState.value = _uiState.value.copy(
                    userGpsLatLng = newLatLng,
                    gpsAccuracy = loc.accuracy,
                    gpsSpeedKmh = speed,
                    gpsLastUpdated = freshness
                )

                // Push actual coordinates to backend API
                viewModelScope.launch {
                    repository.updateGroupLocation(
                        userId = userId,
                        groupId = groupId,
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        accuracy = loc.accuracy,
                        speed = speed,
                        locationName = "Live GPS (${String.format(java.util.Locale.US, "%.3f, %.3f", loc.latitude, loc.longitude)})"
                    )
                    // Refresh group locations silently
                    val locRes = repository.fetchGroupLocations(userId, groupId)
                    if (locRes is NetworkResult.Success) {
                        _uiState.value = _uiState.value.copy(
                            liveLocations = locRes.data.members,
                            activeSharingCount = locRes.data.activeSharingCount
                        )
                    }
                }
            }
        }
    }

    fun pauseLocationSharing(context: Context, groupId: Int) {
        locationTracker?.stopLocationUpdates()
        _uiState.value = _uiState.value.copy(
            isSharingPaused = true,
            gpsLastUpdated = "Paused (Frozen)",
            successMessage = "⏸️ Location sharing paused. Marker is frozen."
        )
    }

    fun resumeLocationSharing(context: Context, groupId: Int) {
        startLocationSharing(context, groupId)
        _uiState.value = _uiState.value.copy(
            isSharingPaused = false,
            successMessage = "▶️ Location sharing resumed."
        )
    }

    fun stopLocationSharing(context: Context, groupId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        locationTracker?.stopLocationUpdates()
        _uiState.value = _uiState.value.copy(
            isSharingActive = false,
            isSharingPaused = false,
            userGpsLatLng = null,
            gpsLastUpdated = "Location sharing off",
            successMessage = "🛑 Stopped location sharing. Your marker is removed."
        )

        viewModelScope.launch {
            repository.updateLocationSettings(userId, groupId, false)
            loadLiveLocations(context, groupId)
        }
    }

    fun toggleLocationSharing(context: Context, groupId: Int, enable: Boolean) {
        if (enable) {
            startLocationSharing(context, groupId)
        } else {
            stopLocationSharing(context, groupId)
        }
    }

    fun leaveGroup(context: Context, groupId: Int, onLeft: () -> Unit) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        locationTracker?.stopLocationUpdates()
        _uiState.value = _uiState.value.copy(
            isSharingActive = false,
            isSharingPaused = false
        )
        viewModelScope.launch {
            repository.updateLocationSettings(userId, groupId, false)
            when (val res = repository.manageGroupMember(userId, groupId, "leave")) {
                is NetworkResult.Success<String> -> {
                    _uiState.value = _uiState.value.copy(successMessage = "Left group successfully.")
                    loadGroups(context)
                    onLeft()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = res.message)
                }
            }
        }
    }

    // Map Toolbar Handlers
    fun viewAllMembers() {
        _uiState.value = _uiState.value.copy(
            mapFitAllTrigger = System.currentTimeMillis(),
            successMessage = "Fitting all active group members"
        )
    }

    fun viewMyLocation() {
        val loc = _uiState.value.userGpsLatLng
        if (loc != null) {
            _uiState.value = _uiState.value.copy(
                cameraFocusLatLng = loc,
                mapRecenterTrigger = System.currentTimeMillis()
            )
        } else {
            _uiState.value = _uiState.value.copy(errorMessage = "Location sharing is not active yet.")
        }
    }

    fun viewGroupDestination() {
        val dest = _uiState.value.destinationMapPoint
        if (dest != null) {
            _uiState.value = _uiState.value.copy(
                cameraFocusLatLng = LatLng(dest.latitude, dest.longitude),
                mapRecenterTrigger = System.currentTimeMillis(),
                successMessage = "Focused on Destination: ${dest.name}"
            )
        }
    }

    fun recenterMap() {
        _uiState.value = _uiState.value.copy(
            mapRecenterTrigger = System.currentTimeMillis()
        )
    }

    fun toggleNearbyTouristPlaces() {
        val cur = _uiState.value.showTouristPlacesOnMap
        _uiState.value = _uiState.value.copy(showTouristPlacesOnMap = !cur)
    }

    fun toggleMeetingPoints() {
        val cur = _uiState.value.showMeetingPointsOnMap
        _uiState.value = _uiState.value.copy(showMeetingPointsOnMap = !cur)
    }

    fun loadLiveLocations(context: Context, groupId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = repository.fetchGroupLocations(userId, groupId)) {
                is NetworkResult.Success -> {
                    val destName = res.data.destination.name.ifBlank { "Coimbatore" }
                    val selfMember = res.data.members.firstOrNull { it.isSelf }
                    val originName = selfMember?.startingLocation?.ifBlank { null }
                        ?: _uiState.value.activeGroup?.commonStartLocation?.ifBlank { null }
                        ?: _uiState.value.routeOriginQuery.ifBlank { null }
                        ?: "Chennai Central"

                    val resolvedRoute = _uiState.value.activeTravelRoute
                        ?: TamilNaduTransitResolver.resolveRoute(originName, destName)

                    _uiState.value = _uiState.value.copy(
                        destinationMapPoint = res.data.destination,
                        liveLocations = res.data.members,
                        meetingPoints = res.data.meetingPoints,
                        activeSharingCount = res.data.activeSharingCount,
                        isSharingActive = res.data.userSharingActive,
                        activeTravelRoute = resolvedRoute,
                        routeOriginQuery = resolvedRoute.origin,
                        routeDestQuery = resolvedRoute.destination
                    )
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    // ----------------------------------------------------
    // EN-ROUTE STATIONS, PLACES & CORRIDOR SEARCH CONTROLS
    // ----------------------------------------------------

    fun searchTravelRoute(origin: String, destination: String) {
        val route = TamilNaduTransitResolver.resolveRoute(origin, destination)
        _uiState.value = _uiState.value.copy(
            activeTravelRoute = route,
            routeOriginQuery = route.origin,
            routeDestQuery = route.destination,
            selectedEnRouteStop = null,
            cameraFocusLatLng = null,
            isRouteSearchSheetOpen = false,
            mapFitAllTrigger = System.currentTimeMillis()
        )
    }

    fun swapRouteOriginDest() {
        val curOrigin = _uiState.value.routeOriginQuery
        val curDest = _uiState.value.routeDestQuery
        searchTravelRoute(curDest, curOrigin)
    }

    fun selectIntermediateStop(stop: CorridorStopItem?) {
        _uiState.value = _uiState.value.copy(
            selectedEnRouteStop = stop,
            cameraFocusLatLng = stop?.let { LatLng(it.latitude, it.longitude) }
        )
    }

    fun setEnRouteFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedEnRouteFilter = filter)
    }

    fun toggleTravelRouteOnMap() {
        val cur = _uiState.value.showTravelRouteOnMap
        _uiState.value = _uiState.value.copy(showTravelRouteOnMap = !cur)
    }

    fun toggleIntermediateStopsOnMap() {
        val cur = _uiState.value.showIntermediateStopsOnMap
        _uiState.value = _uiState.value.copy(showIntermediateStopsOnMap = !cur)
    }

    fun openRouteSearchSheet(open: Boolean) {
        _uiState.value = _uiState.value.copy(isRouteSearchSheetOpen = open)
    }

    fun fitEntireRoute() {
        _uiState.value = _uiState.value.copy(
            cameraFocusLatLng = null,
            selectedEnRouteStop = null,
            mapFitAllTrigger = System.currentTimeMillis()
        )
    }

    // ----------------------------------------------------
    // ITINERARY MANAGEMENT
    // ----------------------------------------------------

    fun loadItinerary(context: Context, groupId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = repository.fetchGroupItinerary(userId, groupId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(itinerary = res.data)
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun addItineraryStop(
        context: Context,
        groupId: Int,
        placeId: Int? = null,
        name: String,
        address: String? = null,
        stopType: String = "attraction",
        plannedTime: String = "10:00 AM",
        notes: String? = null
    ) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = repository.addItineraryStop(userId, groupId, placeId, name, address, stopType, plannedTime, notes)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(successMessage = res.data)
                    loadItinerary(context, groupId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = res.message)
                }
            }
        }
    }

    fun removeItineraryStop(context: Context, groupId: Int, itineraryId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = repository.removeItineraryStop(userId, groupId, itineraryId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(successMessage = res.data)
                    loadItinerary(context, groupId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = res.message)
                }
            }
        }
    }

    // ----------------------------------------------------
    // EXPLORE TOGETHER & VOTING
    // ----------------------------------------------------

    fun setExploreCategory(context: Context, groupId: Int, category: String) {
        _uiState.value = _uiState.value.copy(selectedExploreCategory = category)
        loadExplorePlaces(context, groupId, category)
    }

    fun loadExplorePlaces(context: Context, groupId: Int, category: String = "All") {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = repository.fetchGroupPlaces(userId, groupId, if (category == "All") null else category)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(explorePlaces = res.data.places)
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun togglePlaceVote(context: Context, groupId: Int, placeId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = repository.voteGroupPlace(userId, groupId, placeId)) {
                is NetworkResult.Success -> {
                    val updated = _uiState.value.explorePlaces.map { p ->
                        if (p.placeId == placeId) {
                            p.copy(
                                isVoted = res.data.isVoted,
                                groupVotes = res.data.totalVotes
                            )
                        } else p
                    }
                    _uiState.value = _uiState.value.copy(
                        explorePlaces = updated,
                        successMessage = res.data.message
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = res.message)
                }
            }
        }
    }

    // ----------------------------------------------------
    // GROUP CHAT
    // ----------------------------------------------------

    fun onChatInputChange(v: String) { _uiState.value = _uiState.value.copy(chatInput = v) }

    fun loadChat(context: Context, groupId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = repository.fetchGroupChat(userId, groupId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(chatMessages = res.data)
                }
                is NetworkResult.Error -> {}
            }
        }
    }

    fun sendChatMessage(context: Context, groupId: Int, type: String = "text", attachedPlaceId: Int? = null) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val content = _uiState.value.chatInput.trim()
        if (content.isBlank() && type == "text") return

        val textToSend = if (content.isBlank() && type == "meeting_point") "Meeting point announced!" else content

        viewModelScope.launch {
            when (val res = repository.sendGroupMessage(userId, groupId, textToSend, type, attachedPlaceId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(chatInput = "")
                    loadChat(context, groupId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = res.message)
                }
            }
        }
    }

    fun sharePlaceInChat(context: Context, groupId: Int, place: GroupPlaceSuggestion) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        val content = "Let's visit ${place.name} in ${place.city}! (★ ${place.rating})"

        viewModelScope.launch {
            when (val res = repository.sendGroupMessage(userId, groupId, content, "place_share", place.placeId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(successMessage = "Shared ${place.name} to group chat!")
                    loadChat(context, groupId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = res.message)
                }
            }
        }
    }

    // ----------------------------------------------------
    // MEMBER MANAGEMENT
    // ----------------------------------------------------

    fun approveMember(context: Context, groupId: Int, targetUserId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = repository.manageGroupMember(userId, groupId, "approve", targetUserId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(successMessage = res.data)
                    loadGroupDetails(context, groupId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = res.message)
                }
            }
        }
    }

    fun removeMember(context: Context, groupId: Int, targetUserId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = repository.manageGroupMember(userId, groupId, "remove", targetUserId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(successMessage = res.data)
                    loadGroupDetails(context, groupId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = res.message)
                }
            }
        }
    }
}
