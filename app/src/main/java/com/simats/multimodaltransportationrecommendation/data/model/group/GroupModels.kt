package com.simats.multimodaltransportationrecommendation.data.model.group

import com.google.gson.annotations.SerializedName

// Group summary in dashboard / list
data class TravelGroup(
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("group_name") val groupName: String,
    @SerializedName("invitation_code") val invitationCode: String,
    @SerializedName("creator_id") val creatorId: Int,
    @SerializedName("creator_name") val creatorName: String,
    @SerializedName("destination") val destination: String,
    @SerializedName("destination_lat") val destinationLat: Double,
    @SerializedName("destination_lng") val destinationLng: Double,
    @SerializedName("common_start_location") val commonStartLocation: String? = null,
    @SerializedName("travel_date") val travelDate: String,
    @SerializedName("travel_time") val travelTime: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("privacy") val privacy: String = "invite_only",
    @SerializedName("status") val status: String = "active",
    @SerializedName("member_count") val memberCount: Int = 1,
    @SerializedName("members_preview") val membersPreview: List<MemberPreview> = emptyList(),
    @SerializedName("itinerary_stops_count") val itineraryStopsCount: Int = 0,
    @SerializedName("user_role") val userRole: String = "member",
    @SerializedName("user_starting_location") val userStartingLocation: String = "Chennai Central",
    @SerializedName("user_sharing_enabled") val userSharingEnabled: Boolean = false,
    @SerializedName("is_creator") val isCreator: Boolean = false
)

data class MemberPreview(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("initials") val initials: String = "U",
    @SerializedName("role") val role: String = "member",
    @SerializedName("starting_location") val startingLocation: String? = null,
    @SerializedName("is_sharing") val isSharing: Boolean = false,
    @SerializedName("profile_image") val profileImage: String? = null
)

// Detailed member
data class GroupMember(
    @SerializedName("member_id") val memberId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("initials") val initials: String = "U",
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("role") val role: String = "member",
    @SerializedName("starting_location") val startingLocation: String = "Chennai Central",
    @SerializedName("starting_lat") val startingLat: Double = 13.0827,
    @SerializedName("starting_lng") val startingLng: Double = 80.2707,
    @SerializedName("status") val status: String = "approved",
    @SerializedName("is_sharing") val isSharing: Boolean = false,
    @SerializedName("joined_at") val joinedAt: String? = null,
    @SerializedName("profile_image") val profileImage: String? = null
)

// Live location of member on map
data class GroupMemberLocation(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("initials") val initials: String = "U",
    @SerializedName("role") val role: String = "member",
    @SerializedName("is_self") val isSelf: Boolean = false,
    @SerializedName("is_sharing") val isSharing: Boolean = false,
    @SerializedName("starting_location") val startingLocation: String = "Chennai Central",
    @SerializedName("starting_lat") val startingLat: Double = 13.0827,
    @SerializedName("starting_lng") val startingLng: Double = 80.2707,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("accuracy") val accuracy: Float? = null,
    @SerializedName("speed") val speed: Float? = null,
    @SerializedName("battery_level") val batteryLevel: Int? = null,
    @SerializedName("location_name") val locationName: String = "Location Sharing Off",
    @SerializedName("last_updated") val lastUpdated: String? = null,
    @SerializedName("distance_to_destination_km") val distanceToDestinationKm: Double? = null,
    @SerializedName("distance_to_user_km") val distanceToUserKm: Double? = null,
    @SerializedName("profile_image") val profileImage: String? = null
)

data class GroupMeetingPoint(
    @SerializedName("custom_place_name") val placeName: String,
    @SerializedName("location_address") val address: String? = null,
    @SerializedName("stop_type") val stopType: String = "meeting_point",
    @SerializedName("planned_time") val plannedTime: String? = null,
    @SerializedName("notes") val notes: String? = null
)

// Itinerary item
data class GroupItineraryItem(
    @SerializedName("itinerary_id") val itineraryId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("place_id") val placeId: Int? = null,
    @SerializedName("place_name") val placeName: String,
    @SerializedName("location_address") val locationAddress: String? = null,
    @SerializedName("stop_type") val stopType: String = "attraction",
    @SerializedName("planned_time") val plannedTime: String? = null,
    @SerializedName("stop_order") val stopOrder: Int = 1,
    @SerializedName("suggested_by_id") val suggestedById: Int = 0,
    @SerializedName("suggested_by_name") val suggestedByName: String = "Member",
    @SerializedName("notes") val notes: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("rating") val rating: Float = 4.7f,
    @SerializedName("entry_fee") val entryFee: String? = null,
    @SerializedName("visiting_hours") val visitingHours: String? = null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)

// Explore together place
data class GroupPlaceSuggestion(
    @SerializedName("place_id") val placeId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("city") val city: String,
    @SerializedName("category") val category: String,
    @SerializedName("description") val description: String,
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("location_address") val locationAddress: String? = null,
    @SerializedName("visiting_hours") val visitingHours: String? = null,
    @SerializedName("entry_fee") val entryFee: String? = null,
    @SerializedName("rating") val rating: Float = 4.7f,
    @SerializedName("distance_km") val distanceKm: Double? = null,
    @SerializedName("nearby_transit") val nearbyTransit: String? = null,
    @SerializedName("group_votes") val groupVotes: Int = 0,
    @SerializedName("is_voted") val isVoted: Boolean = false,
    @SerializedName("is_in_itinerary") val isInItinerary: Boolean = false
)

// Chat message
data class GroupChatMessage(
    @SerializedName("message_id") val messageId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("sender_initials") val senderInitials: String = "U",
    @SerializedName("message_type") val messageType: String = "text",
    @SerializedName("message_content") val messageContent: String,
    @SerializedName("attached_place_id") val attachedPlaceId: Int? = null,
    @SerializedName("place_name") val placeName: String? = null,
    @SerializedName("place_city") val placeCity: String? = null,
    @SerializedName("place_image") val placeImage: String? = null,
    @SerializedName("is_self") val isSelf: Boolean = false,
    @SerializedName("time_text") val timeText: String = "Just now",
    @SerializedName("created_at") val createdAt: String? = null
)

// Notification
data class GroupNotificationItem(
    @SerializedName("id") val id: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("group_name") val groupName: String,
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("time") val time: String
)

// Request payloads
data class CreateGroupRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("group_name") val groupName: String,
    @SerializedName("destination") val destination: String,
    @SerializedName("starting_location") val startingLocation: String,
    @SerializedName("travel_date") val travelDate: String,
    @SerializedName("travel_time") val travelTime: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("privacy") val privacy: String = "invite_only"
)

data class JoinGroupRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("invitation_code") val invitationCode: String,
    @SerializedName("starting_location") val startingLocation: String
)

data class UpdateGroupLocationRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("accuracy") val accuracy: Float = 0f,
    @SerializedName("speed") val speed: Float = 0f,
    @SerializedName("battery_level") val batteryLevel: Int? = null,
    @SerializedName("location_name") val locationName: String? = null
)

data class LocationSettingsRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("is_sharing") val isSharing: Int
)

data class ManageMemberRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("action") val action: String,
    @SerializedName("target_user_id") val targetUserId: Int? = null,
    @SerializedName("starting_location") val startingLocation: String? = null
)

data class AddItineraryStopRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("action") val action: String = "add_stop",
    @SerializedName("place_id") val placeId: Int? = null,
    @SerializedName("custom_place_name") val customPlaceName: String,
    @SerializedName("location_address") val locationAddress: String? = null,
    @SerializedName("stop_type") val stopType: String = "attraction",
    @SerializedName("planned_time") val plannedTime: String = "10:00 AM",
    @SerializedName("notes") val notes: String? = null
)

data class RemoveItineraryStopRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("action") val action: String = "remove_stop",
    @SerializedName("itinerary_id") val itineraryId: Int
)

data class PlaceVoteRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("place_id") val placeId: Int,
    @SerializedName("vote") val vote: Int = 1
)

data class SendGroupMessageRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("message_type") val messageType: String = "text",
    @SerializedName("message_content") val messageContent: String,
    @SerializedName("attached_place_id") val attachedPlaceId: Int? = null
)

// Response wrappers
data class GroupFetchResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("total_groups") val totalGroups: Int = 0,
    @SerializedName("my_groups") val myGroups: List<TravelGroup> = emptyList(),
    @SerializedName("upcoming_trips") val upcomingTrips: List<TravelGroup> = emptyList(),
    @SerializedName("completed_trips") val completedTrips: List<TravelGroup> = emptyList()
)

data class GroupDetailsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("group") val group: GroupDetailsData? = null,
    @SerializedName("message") val message: String? = null
)

data class GroupDetailsData(
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("group_name") val groupName: String,
    @SerializedName("invitation_code") val invitationCode: String,
    @SerializedName("creator_id") val creatorId: Int,
    @SerializedName("creator_name") val creatorName: String,
    @SerializedName("destination") val destination: String,
    @SerializedName("destination_lat") val destinationLat: Double,
    @SerializedName("destination_lng") val destinationLng: Double,
    @SerializedName("common_start_location") val commonStartLocation: String? = null,
    @SerializedName("travel_date") val travelDate: String,
    @SerializedName("travel_time") val travelTime: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("privacy") val privacy: String = "invite_only",
    @SerializedName("status") val status: String = "active",
    @SerializedName("is_creator") val isCreator: Boolean = false,
    @SerializedName("members_count") val membersCount: Int = 1,
    @SerializedName("sharing_count") val sharingCount: Int = 0,
    @SerializedName("itinerary_count") val itineraryCount: Int = 0,
    @SerializedName("messages_count") val messagesCount: Int = 0,
    @SerializedName("user_membership") val userMembership: UserMembershipInfo? = null,
    @SerializedName("approved_members") val approvedMembers: List<GroupMember> = emptyList(),
    @SerializedName("pending_members") val pendingMembers: List<GroupMember> = emptyList()
)

data class UserMembershipInfo(
    @SerializedName("role") val role: String = "member",
    @SerializedName("starting_location") val startingLocation: String = "Chennai Central",
    @SerializedName("starting_lat") val startingLat: Double = 13.0827,
    @SerializedName("starting_lng") val startingLng: Double = 80.2707,
    @SerializedName("is_sharing") val isSharing: Boolean = false
)

data class GroupMembersResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("members") val members: List<GroupMember> = emptyList()
)

data class GroupLocationFetchResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("group_name") val groupName: String,
    @SerializedName("destination") val destination: DestinationMapPoint,
    @SerializedName("user_sharing_active") val userSharingActive: Boolean = false,
    @SerializedName("total_members") val totalMembers: Int = 0,
    @SerializedName("active_sharing_count") val activeSharingCount: Int = 0,
    @SerializedName("members") val members: List<GroupMemberLocation> = emptyList(),
    @SerializedName("meeting_points") val meetingPoints: List<GroupMeetingPoint> = emptyList()
)

data class DestinationMapPoint(
    @SerializedName("name") val name: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

data class GroupItineraryResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("itinerary") val itinerary: List<GroupItineraryItem> = emptyList()
)

data class GroupPlacesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("destination") val destination: String,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("places") val places: List<GroupPlaceSuggestion> = emptyList()
)

data class GroupChatResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("messages") val messages: List<GroupChatMessage> = emptyList()
)

data class GroupNotificationsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("notifications") val notifications: List<GroupNotificationItem> = emptyList()
)

data class GroupActionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: String? = null,
    @SerializedName("group") val group: TravelGroup? = null
)

data class LocationSettingsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("is_sharing") val isSharing: Boolean,
    @SerializedName("message") val message: String
)

data class VoteActionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("place_id") val placeId: Int,
    @SerializedName("is_voted") val isVoted: Boolean,
    @SerializedName("total_votes") val totalVotes: Int,
    @SerializedName("message") val message: String
)

