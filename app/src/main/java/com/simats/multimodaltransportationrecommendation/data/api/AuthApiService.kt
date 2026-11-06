package com.simats.multimodaltransportationrecommendation.data.api

import com.simats.multimodaltransportationrecommendation.data.model.auth.BaseResponse
import com.simats.multimodaltransportationrecommendation.data.model.auth.LoginRequest
import com.simats.multimodaltransportationrecommendation.data.model.auth.LoginResponse
import com.simats.multimodaltransportationrecommendation.data.model.auth.SignUpRequest
import com.simats.multimodaltransportationrecommendation.data.model.auth.SignUpResponse
import com.simats.multimodaltransportationrecommendation.data.model.places.PlaceDetailsResponse
import com.simats.multimodaltransportationrecommendation.data.model.places.PlacesResponse
import com.simats.multimodaltransportationrecommendation.data.model.places.RouteRecommendationsResponse
import com.simats.multimodaltransportationrecommendation.data.model.places.SavePlaceRequest
import com.simats.multimodaltransportationrecommendation.data.model.places.SavePlaceResponse
import com.simats.multimodaltransportationrecommendation.data.model.places.SavedPlacesResponse
import com.simats.multimodaltransportationrecommendation.data.model.preferences.PreferencesResponse
import com.simats.multimodaltransportationrecommendation.data.model.preferences.UpdatePreferencesRequest
import com.simats.multimodaltransportationrecommendation.data.model.profile.ChangePasswordRequest
import com.simats.multimodaltransportationrecommendation.data.model.profile.ProfileResponse
import com.simats.multimodaltransportationrecommendation.data.model.profile.ProfileUploadResponse
import com.simats.multimodaltransportationrecommendation.data.model.profile.UpdateProfileRequest
import com.simats.multimodaltransportationrecommendation.data.model.routes.SaveRouteRequest
import com.simats.multimodaltransportationrecommendation.data.model.routes.SavedRoutesResponse
import com.simats.multimodaltransportationrecommendation.data.model.routes.SearchRoutesRequest
import com.simats.multimodaltransportationrecommendation.data.model.routes.SearchRoutesResponse
import com.simats.multimodaltransportationrecommendation.data.model.stats.StatsResponse
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookJourneyRequest
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookJourneyResponse
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripsResponse
import com.simats.multimodaltransportationrecommendation.data.model.trips.UpdateTripStatusRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    // Authentication
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/signup.php")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("auth/password_reset.php")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<BaseResponse>

    // Profile & Stats
    @GET("profile/profile_fetch.php")
    suspend fun fetchProfile(@Query("user_id") userId: Int): Response<ProfileResponse>

    @POST("profile/profile_edit.php")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<BaseResponse>

    @Multipart
    @POST("profile/profile_upload.php")
    suspend fun uploadProfileImage(
        @Part("user_id") userId: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<ProfileUploadResponse>

    @GET("profile/stats_fetch.php")
    suspend fun fetchStats(@Query("user_id") userId: Int): Response<StatsResponse>

    // Preferences & Notifications
    @GET("profile/preferences_fetch.php")
    suspend fun fetchPreferences(@Query("user_id") userId: Int): Response<PreferencesResponse>

    @POST("profile/preferences_update.php")
    suspend fun updatePreferences(@Body request: UpdatePreferencesRequest): Response<BaseResponse>

    // Tourist Places & Recommendations
    @GET("places/places_fetch.php")
    suspend fun fetchPlaces(
        @Query("user_id") userId: Int = 0,
        @Query("city") city: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null
    ): Response<PlacesResponse>

    @GET("places/place_details.php")
    suspend fun fetchPlaceDetails(
        @Query("place_id") placeId: Int,
        @Query("user_id") userId: Int = 0
    ): Response<PlaceDetailsResponse>

    @GET("places/route_recommendations.php")
    suspend fun fetchRouteRecommendations(
        @Query("origin") origin: String,
        @Query("destination") destination: String
    ): Response<RouteRecommendationsResponse>

    @POST("places/save_place.php")
    suspend fun savePlace(@Body request: SavePlaceRequest): Response<SavePlaceResponse>

    @GET("places/saved_fetch.php")
    suspend fun fetchSavedPlaces(@Query("user_id") userId: Int): Response<SavedPlacesResponse>

    // Multi-Modal Location & Routes
    @GET("routes/location_search.php")
    suspend fun searchLocations(@Query("q") query: String): Response<com.simats.multimodaltransportationrecommendation.data.model.location.LocationSearchResponse>

    @POST("routes/search_routes.php")
    suspend fun searchRoutes(@Body request: SearchRoutesRequest): Response<SearchRoutesResponse>

    @POST("routes/save_route.php")
    suspend fun saveRoute(@Body request: SaveRouteRequest): Response<BaseResponse>

    @GET("routes/saved_routes_fetch.php")
    suspend fun fetchSavedRoutes(@Query("user_id") userId: Int): Response<SavedRoutesResponse>

    // Journeys / Trips
    @GET("trips/trips_fetch.php")
    suspend fun fetchTrips(
        @Query("user_id") userId: Int,
        @Query("status") status: String = "all"
    ): Response<TripsResponse>

    @POST("trips/book_journey.php")
    suspend fun bookJourney(@Body request: BookJourneyRequest): Response<BookJourneyResponse>

    @POST("trips/update_status.php")
    suspend fun updateTripStatus(@Body request: UpdateTripStatusRequest): Response<BaseResponse>

    // Notifications
    @GET("notifications/notifications_fetch.php")
    suspend fun fetchNotifications(@Query("user_id") userId: Int): Response<com.simats.multimodaltransportationrecommendation.data.model.notifications.NotificationsResponse>

    @POST("notifications/mark_read.php")
    suspend fun markNotificationRead(@Body request: com.simats.multimodaltransportationrecommendation.data.model.notifications.MarkReadRequest): Response<BaseResponse>

    // Smart Travel Tickets & QR Scanner
    @GET("tickets/tickets_fetch.php")
    suspend fun fetchTickets(
        @Query("user_id") userId: Int,
        @Query("status") status: String = "all"
    ): Response<com.simats.multimodaltransportationrecommendation.data.model.tickets.TicketsResponse>

    @POST("tickets/ticket_verify.php")
    suspend fun verifyTicket(
        @Body request: com.simats.multimodaltransportationrecommendation.data.model.tickets.VerifyTicketRequest
    ): Response<com.simats.multimodaltransportationrecommendation.data.model.tickets.VerifyTicketResponse>

    @GET("payment/payment_status.php")
    suspend fun getPaymentStatus(
        @Query("booking_reference") bookingReference: String? = null,
        @Query("journey_id") journeyId: Int? = null,
        @Query("transaction_id") transactionId: String? = null
    ): Response<com.simats.multimodaltransportationrecommendation.data.model.tickets.PaymentStatusResponse>

    // Group Travel & Live Location Sharing
    @POST("groups/group_create.php")
    suspend fun createGroup(@Body request: com.simats.multimodaltransportationrecommendation.data.model.group.CreateGroupRequest): Response<com.simats.multimodaltransportationrecommendation.data.model.group.GroupActionResponse>

    @POST("groups/group_join.php")
    suspend fun joinGroup(@Body request: com.simats.multimodaltransportationrecommendation.data.model.group.JoinGroupRequest): Response<com.simats.multimodaltransportationrecommendation.data.model.group.GroupActionResponse>

    @GET("groups/group_fetch.php")
    suspend fun fetchGroups(@Query("user_id") userId: Int): Response<com.simats.multimodaltransportationrecommendation.data.model.group.GroupFetchResponse>

    @GET("groups/group_details.php")
    suspend fun fetchGroupDetails(
        @Query("user_id") userId: Int,
        @Query("group_id") groupId: Int
    ): Response<com.simats.multimodaltransportationrecommendation.data.model.group.GroupDetailsResponse>

    @GET("groups/group_members.php")
    suspend fun fetchGroupMembers(
        @Query("user_id") userId: Int,
        @Query("group_id") groupId: Int
    ): Response<com.simats.multimodaltransportationrecommendation.data.model.group.GroupMembersResponse>

    @POST("groups/group_members.php")
    suspend fun manageGroupMember(@Body request: com.simats.multimodaltransportationrecommendation.data.model.group.ManageMemberRequest): Response<BaseResponse>

    @POST("groups/group_location_update.php")
    suspend fun updateGroupLocation(@Body request: com.simats.multimodaltransportationrecommendation.data.model.group.UpdateGroupLocationRequest): Response<BaseResponse>

    @GET("groups/group_location_fetch.php")
    suspend fun fetchGroupLocations(
        @Query("user_id") userId: Int,
        @Query("group_id") groupId: Int
    ): Response<com.simats.multimodaltransportationrecommendation.data.model.group.GroupLocationFetchResponse>

    @POST("groups/group_location_settings.php")
    suspend fun updateLocationSettings(@Body request: com.simats.multimodaltransportationrecommendation.data.model.group.LocationSettingsRequest): Response<com.simats.multimodaltransportationrecommendation.data.model.group.LocationSettingsResponse>

    @GET("groups/group_itinerary.php")
    suspend fun fetchGroupItinerary(
        @Query("user_id") userId: Int,
        @Query("group_id") groupId: Int
    ): Response<com.simats.multimodaltransportationrecommendation.data.model.group.GroupItineraryResponse>

    @POST("groups/group_itinerary.php")
    suspend fun addItineraryStop(@Body request: com.simats.multimodaltransportationrecommendation.data.model.group.AddItineraryStopRequest): Response<BaseResponse>

    @POST("groups/group_itinerary.php")
    suspend fun removeItineraryStop(@Body request: com.simats.multimodaltransportationrecommendation.data.model.group.RemoveItineraryStopRequest): Response<BaseResponse>

    @GET("groups/group_places.php")
    suspend fun fetchGroupPlaces(
        @Query("user_id") userId: Int,
        @Query("group_id") groupId: Int,
        @Query("category") category: String? = null
    ): Response<com.simats.multimodaltransportationrecommendation.data.model.group.GroupPlacesResponse>

    @POST("groups/group_places.php")
    suspend fun voteGroupPlace(@Body request: com.simats.multimodaltransportationrecommendation.data.model.group.PlaceVoteRequest): Response<com.simats.multimodaltransportationrecommendation.data.model.group.VoteActionResponse>

    @GET("groups/group_chat.php")
    suspend fun fetchGroupChat(
        @Query("user_id") userId: Int,
        @Query("group_id") groupId: Int
    ): Response<com.simats.multimodaltransportationrecommendation.data.model.group.GroupChatResponse>

    @POST("groups/group_chat.php")
    suspend fun sendGroupMessage(@Body request: com.simats.multimodaltransportationrecommendation.data.model.group.SendGroupMessageRequest): Response<BaseResponse>

    @GET("groups/group_notifications.php")
    suspend fun fetchGroupNotifications(@Query("user_id") userId: Int): Response<com.simats.multimodaltransportationrecommendation.data.model.group.GroupNotificationsResponse>
}
