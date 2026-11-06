package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import android.content.Context
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.multimodaltransportationrecommendation.data.model.group.*
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
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
    val activeSharingCount: Int = 0,

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
    }

    // ----------------------------------------------------
    // LIVE LOCATION CONTROLS & PRIVACY
    // ----------------------------------------------------

    fun toggleLocationSharing(context: Context, groupId: Int, enable: Boolean) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1

        viewModelScope.launch {
            when (val res = repository.updateLocationSettings(userId, groupId, enable)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSharingActive = res.data.isSharing,
                        successMessage = res.data.message
                    )
                    if (enable) {
                        pushDeviceLocation(context, groupId)
                    }
                    loadLiveLocations(context, groupId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(errorMessage = res.message)
                }
            }
        }
    }

    fun pushDeviceLocation(context: Context, groupId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        var lat = 13.0827
        var lng = 80.2707
        var locName = "Current GPS Position"

        try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            if (lm != null) {
                val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (loc != null) {
                    lat = loc.latitude
                    lng = loc.longitude
                    locName = "En Route (${String.format("%.3f", lat)}, ${String.format("%.3f", lng)})"
                }
            }
        } catch (_: Exception) {}

        viewModelScope.launch {
            repository.updateGroupLocation(
                userId = userId,
                groupId = groupId,
                latitude = lat,
                longitude = lng,
                accuracy = 10f,
                speed = 35f,
                locationName = locName
            )
            loadLiveLocations(context, groupId)
        }
    }

    fun loadLiveLocations(context: Context, groupId: Int) {
        val userId = SessionManager.getInstance(context).getUserId() ?: 1
        viewModelScope.launch {
            when (val res = repository.fetchGroupLocations(userId, groupId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        destinationMapPoint = res.data.destination,
                        liveLocations = res.data.members,
                        meetingPoints = res.data.meetingPoints,
                        activeSharingCount = res.data.activeSharingCount,
                        isSharingActive = res.data.userSharingActive
                    )
                }
                is NetworkResult.Error -> {}
            }
        }
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
