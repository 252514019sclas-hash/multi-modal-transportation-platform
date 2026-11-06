package com.simats.multimodaltransportationrecommendation.data.repository

import com.google.gson.Gson
import com.simats.multimodaltransportationrecommendation.data.api.AuthApiService
import com.simats.multimodaltransportationrecommendation.data.api.RetrofitClient
import com.simats.multimodaltransportationrecommendation.data.model.auth.BaseResponse
import com.simats.multimodaltransportationrecommendation.data.model.auth.LoginRequest
import com.simats.multimodaltransportationrecommendation.data.model.auth.LoginResponse
import com.simats.multimodaltransportationrecommendation.data.model.auth.SignUpRequest
import com.simats.multimodaltransportationrecommendation.data.model.auth.SignUpResponse
import com.simats.multimodaltransportationrecommendation.data.model.location.LocationSearchResult
import com.simats.multimodaltransportationrecommendation.data.model.places.SavePlaceRequest
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.model.preferences.PreferencesData
import com.simats.multimodaltransportationrecommendation.data.model.preferences.UpdatePreferencesRequest
import com.simats.multimodaltransportationrecommendation.data.model.profile.ChangePasswordRequest
import com.simats.multimodaltransportationrecommendation.data.model.profile.ProfileData
import com.simats.multimodaltransportationrecommendation.data.model.profile.UpdateProfileRequest
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import com.simats.multimodaltransportationrecommendation.data.model.routes.SaveRouteRequest
import com.simats.multimodaltransportationrecommendation.data.model.routes.SavedRoute
import com.simats.multimodaltransportationrecommendation.data.model.routes.SearchRoutesRequest
import com.simats.multimodaltransportationrecommendation.data.model.routes.SearchRoutesResponse
import com.simats.multimodaltransportationrecommendation.data.model.stats.UserStatsData
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookJourneyRequest
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.data.model.trips.UpdateTripStatusRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String) : NetworkResult<Nothing>()
}

class AuthRepository(
    private val authService: AuthApiService = RetrofitClient.authService,
    private val gson: Gson = Gson()
) {

    // Auth
    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        phone: String? = null
    ): NetworkResult<SignUpResponse> = withContext(Dispatchers.IO) {
        try {
            val request = SignUpRequest(
                fullName = fullName,
                email = email,
                password = password,
                phone = phone
            )
            val response = authService.signUp(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(body.message ?: "Signup failed")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Network connection error. Check server.")
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): NetworkResult<LoginResponse> = withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(email = email, password = password)
            val response = authService.login(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(body.message ?: "Login failed")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Network connection error. Check server.")
        }
    }

    suspend fun changePassword(
        userId: Int,
        currentPassword: String,
        newPassword: String
    ): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val request = ChangePasswordRequest(
                userId = userId,
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            val response = authService.changePassword(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body.message)
                } else {
                    NetworkResult.Error(body.message)
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to update password.")
        }
    }

    // Profile & Stats
    suspend fun fetchProfile(userId: Int): NetworkResult<ProfileData> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.profile != null) {
                    NetworkResult.Success(body.profile)
                } else {
                    NetworkResult.Error(body.message.ifBlank { "Could not fetch profile" })
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load profile from server.")
        }
    }

    suspend fun updateProfile(
        userId: Int,
        fullName: String,
        phone: String?,
        gender: String? = null,
        address: String? = null,
        bio: String? = null
    ): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val request = UpdateProfileRequest(
                userId = userId,
                fullName = fullName,
                phone = phone,
                gender = gender,
                address = address,
                bio = bio
            )
            val response = authService.updateProfile(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body.message)
                } else {
                    NetworkResult.Error(body.message)
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to update profile on server.")
        }
    }

    suspend fun uploadProfileImage(
        userId: Int,
        imageFile: File
    ): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val response = authService.uploadProfileImage(userIdBody, imagePart)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.profileImage != null) {
                    NetworkResult.Success(body.profileImage)
                } else {
                    NetworkResult.Error(body.message)
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to upload profile picture.")
        }
    }

    suspend fun fetchStats(userId: Int): NetworkResult<UserStatsData> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchStats(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.stats != null) {
                    NetworkResult.Success(body.stats)
                } else {
                    NetworkResult.Error(body.message ?: "Failed to load stats")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load travel statistics.")
        }
    }

    // Preferences & Notifications
    suspend fun fetchPreferences(userId: Int): NetworkResult<PreferencesData> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchPreferences(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.preferences != null) {
                    NetworkResult.Success(body.preferences)
                } else {
                    NetworkResult.Error(body.message ?: "Failed to fetch preferences")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load travel preferences.")
        }
    }

    suspend fun updatePreferences(request: UpdatePreferencesRequest): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = authService.updatePreferences(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body.message)
                } else {
                    NetworkResult.Error(body.message)
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to save preferences.")
        }
    }

    // Tourist Places
    suspend fun fetchPlaces(
        userId: Int = 0,
        city: String? = null,
        category: String? = null,
        search: String? = null,
        lat: Double? = null,
        lng: Double? = null
    ): NetworkResult<List<TouristPlace>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchPlaces(userId, city, category, search, lat, lng)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.places != null) {
                    NetworkResult.Success(body.places)
                } else {
                    NetworkResult.Error(body.message ?: "Failed to fetch places")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load tourist attractions.")
        }
    }

    suspend fun fetchPlaceDetails(placeId: Int, userId: Int = 0): NetworkResult<TouristPlace> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchPlaceDetails(placeId, userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.place != null) {
                    NetworkResult.Success(body.place)
                } else {
                    NetworkResult.Error(body.message ?: "Failed to fetch place details")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load destination details.")
        }
    }

    suspend fun savePlace(userId: Int, placeId: Int, action: String = "toggle"): NetworkResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = authService.savePlace(SavePlaceRequest(userId, placeId, action))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body.isSaved)
                } else {
                    NetworkResult.Error(body.message)
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to update saved place.")
        }
    }

    suspend fun fetchSavedPlaces(userId: Int): NetworkResult<List<TouristPlace>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchSavedPlaces(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.savedPlaces != null) {
                    NetworkResult.Success(body.savedPlaces)
                } else {
                    NetworkResult.Error(body.message ?: "Failed to load saved places")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load saved places.")
        }
    }

    // Routes
    suspend fun searchRoutes(
        origin: String,
        destination: String,
        dateTime: String = "Today, 9:15 AM",
        userId: Int = 0
    ): NetworkResult<SearchRoutesResponse> = withContext(Dispatchers.IO) {
        try {
            val request = SearchRoutesRequest(origin, destination, dateTime, userId)
            val response = authService.searchRoutes(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error(body.message ?: "Failed to search routes")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to calculate routes.")
        }
    }

    suspend fun saveRoute(request: SaveRouteRequest): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = authService.saveRoute(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body.message)
                } else {
                    NetworkResult.Error(body.message)
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to save route.")
        }
    }

    suspend fun fetchSavedRoutes(userId: Int): NetworkResult<List<SavedRoute>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchSavedRoutes(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.savedRoutes != null) {
                    NetworkResult.Success(body.savedRoutes)
                } else {
                    NetworkResult.Error(body.message ?: "Failed to fetch saved routes")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load saved routes.")
        }
    }

    // Location Search & Autocomplete
    suspend fun searchLocations(query: String): NetworkResult<List<LocationSearchResult>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.searchLocations(query)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.locations != null) {
                    NetworkResult.Success(body.locations)
                } else {
                    NetworkResult.Error(body.message ?: "No matching locations found")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to search locations.")
        }
    }

    // Route Recommendations
    suspend fun fetchRouteRecommendations(origin: String, destination: String): NetworkResult<List<TouristPlace>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchRouteRecommendations(origin, destination)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.places != null) {
                    NetworkResult.Success(body.places)
                } else {
                    NetworkResult.Error(body.message ?: "No attractions found along this route")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load route recommendations.")
        }
    }

    // Journeys / Trips
    suspend fun fetchTrips(userId: Int, status: String = "all"): NetworkResult<List<TripItem>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchTrips(userId, status)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.trips != null) {
                    NetworkResult.Success(body.trips)
                } else {
                    NetworkResult.Error(body.message ?: "Failed to fetch trips")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load journey history.")
        }
    }

    suspend fun bookJourney(request: BookJourneyRequest): NetworkResult<BookedJourneyData> = withContext(Dispatchers.IO) {
        try {
            val response = authService.bookJourney(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.journey != null) {
                    NetworkResult.Success(body.journey)
                } else {
                    NetworkResult.Error(body.message)
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to confirm booking.")
        }
    }

    suspend fun updateTripStatus(journeyId: Int, userId: Int, status: String): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = authService.updateTripStatus(UpdateTripStatusRequest(journeyId, userId, status))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body.message)
                } else {
                    NetworkResult.Error(body.message)
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to update trip status.")
        }
    }

    // Notifications
    suspend fun fetchNotifications(userId: Int): NetworkResult<Pair<Int, List<com.simats.multimodaltransportationrecommendation.data.model.notifications.NotificationItem>>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchNotifications(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(Pair(body.unreadCount, body.notifications ?: emptyList()))
                } else {
                    NetworkResult.Error(body.message ?: "Failed to fetch notifications")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load notifications.")
        }
    }

    suspend fun markNotificationRead(userId: Int, notificationId: Int? = null, markAll: Boolean = false): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.notifications.MarkReadRequest(userId, notificationId, markAll)
            val response = authService.markNotificationRead(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body.message)
                } else {
                    NetworkResult.Error(body.message)
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to update notification.")
        }
    }

    private fun <T> parseErrorMessage(response: Response<T>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val parsed = gson.fromJson(errorBody, BaseResponse::class.java)
                parsed.message.ifBlank { "Request failed with code ${response.code()}" }
            } else {
                "Request failed with code ${response.code()}"
            }
        } catch (_: Exception) {
            "Request failed with code ${response.code()}"
        }
    }

    // Smart Travel Tickets & QR Verification
    suspend fun fetchTickets(userId: Int, status: String = "all"): NetworkResult<List<com.simats.multimodaltransportationrecommendation.data.model.tickets.TicketItem>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchTickets(userId, status)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    NetworkResult.Success(body.tickets ?: emptyList())
                } else {
                    NetworkResult.Error(body.message ?: "Failed to fetch tickets")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load travel tickets.")
        }
    }

    suspend fun verifyTicket(qrData: String, markUsed: Boolean = false): NetworkResult<com.simats.multimodaltransportationrecommendation.data.model.tickets.VerifyTicketResponse> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.tickets.VerifyTicketRequest(qrData, markUsed)
            val response = authService.verifyTicket(req)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to verify ticket.")
        }
    }

    suspend fun getPaymentStatus(bookingReference: String? = null, journeyId: Int? = null, transactionId: String? = null): NetworkResult<com.simats.multimodaltransportationrecommendation.data.model.tickets.PaymentStatusResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authService.getPaymentStatus(bookingReference, journeyId, transactionId)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to fetch payment status.")
        }
    }

    // ==========================================
    // GROUP TRAVEL & LIVE LOCATION SHARING
    // ==========================================

    suspend fun createGroup(
        userId: Int,
        groupName: String,
        destination: String,
        startingLocation: String,
        travelDate: String,
        travelTime: String,
        description: String? = null,
        privacy: String = "invite_only"
    ): NetworkResult<com.simats.multimodaltransportationrecommendation.data.model.group.GroupActionResponse> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.group.CreateGroupRequest(
                userId = userId,
                groupName = groupName,
                destination = destination,
                startingLocation = startingLocation,
                travelDate = travelDate,
                travelTime = travelTime,
                description = description,
                privacy = privacy
            )
            val response = authService.createGroup(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body) else NetworkResult.Error(body.message)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to create travel group.")
        }
    }

    suspend fun joinGroup(
        userId: Int,
        invitationCode: String,
        startingLocation: String
    ): NetworkResult<com.simats.multimodaltransportationrecommendation.data.model.group.GroupActionResponse> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.group.JoinGroupRequest(
                userId = userId,
                invitationCode = invitationCode,
                startingLocation = startingLocation
            )
            val response = authService.joinGroup(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body) else NetworkResult.Error(body.message)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to join group.")
        }
    }

    suspend fun fetchGroups(userId: Int): NetworkResult<com.simats.multimodaltransportationrecommendation.data.model.group.GroupFetchResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchGroups(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body) else NetworkResult.Error("Failed to fetch groups")
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load travel groups.")
        }
    }

    suspend fun fetchGroupDetails(userId: Int, groupId: Int): NetworkResult<com.simats.multimodaltransportationrecommendation.data.model.group.GroupDetailsData> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchGroupDetails(userId, groupId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success && body.group != null) {
                    NetworkResult.Success(body.group)
                } else {
                    NetworkResult.Error(body.message ?: "Failed to load group details")
                }
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load group details.")
        }
    }

    suspend fun fetchGroupMembers(userId: Int, groupId: Int): NetworkResult<List<com.simats.multimodaltransportationrecommendation.data.model.group.GroupMember>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchGroupMembers(userId, groupId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body.members) else NetworkResult.Error("Failed to fetch members")
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load group members.")
        }
    }

    suspend fun manageGroupMember(
        userId: Int,
        groupId: Int,
        action: String,
        targetUserId: Int? = null,
        startingLocation: String? = null
    ): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.group.ManageMemberRequest(
                userId = userId,
                groupId = groupId,
                action = action,
                targetUserId = targetUserId,
                startingLocation = startingLocation
            )
            val response = authService.manageGroupMember(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body.message) else NetworkResult.Error(body.message)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to update member.")
        }
    }

    suspend fun updateGroupLocation(
        userId: Int,
        groupId: Int,
        latitude: Double,
        longitude: Double,
        accuracy: Float = 0f,
        speed: Float = 0f,
        batteryLevel: Int? = null,
        locationName: String? = null
    ): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.group.UpdateGroupLocationRequest(
                userId = userId,
                groupId = groupId,
                latitude = latitude,
                longitude = longitude,
                accuracy = accuracy,
                speed = speed,
                batteryLevel = batteryLevel,
                locationName = locationName
            )
            val response = authService.updateGroupLocation(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body.message) else NetworkResult.Error(body.message)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to update live location.")
        }
    }

    suspend fun fetchGroupLocations(userId: Int, groupId: Int): NetworkResult<com.simats.multimodaltransportationrecommendation.data.model.group.GroupLocationFetchResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchGroupLocations(userId, groupId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body) else NetworkResult.Error("Failed to fetch live locations")
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load live locations.")
        }
    }

    suspend fun updateLocationSettings(userId: Int, groupId: Int, isSharing: Boolean): NetworkResult<com.simats.multimodaltransportationrecommendation.data.model.group.LocationSettingsResponse> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.group.LocationSettingsRequest(
                userId = userId,
                groupId = groupId,
                isSharing = if (isSharing) 1 else 0
            )
            val response = authService.updateLocationSettings(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body) else NetworkResult.Error(body.message)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to update location settings.")
        }
    }

    suspend fun fetchGroupItinerary(userId: Int, groupId: Int): NetworkResult<List<com.simats.multimodaltransportationrecommendation.data.model.group.GroupItineraryItem>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchGroupItinerary(userId, groupId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body.itinerary) else NetworkResult.Error("Failed to load itinerary")
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load itinerary.")
        }
    }

    suspend fun addItineraryStop(
        userId: Int,
        groupId: Int,
        placeId: Int? = null,
        customPlaceName: String,
        locationAddress: String? = null,
        stopType: String = "attraction",
        plannedTime: String = "10:00 AM",
        notes: String? = null
    ): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.group.AddItineraryStopRequest(
                userId = userId,
                groupId = groupId,
                placeId = placeId,
                customPlaceName = customPlaceName,
                locationAddress = locationAddress,
                stopType = stopType,
                plannedTime = plannedTime,
                notes = notes
            )
            val response = authService.addItineraryStop(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body.message) else NetworkResult.Error(body.message)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to add stop to itinerary.")
        }
    }

    suspend fun removeItineraryStop(userId: Int, groupId: Int, itineraryId: Int): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.group.RemoveItineraryStopRequest(
                userId = userId,
                groupId = groupId,
                itineraryId = itineraryId
            )
            val response = authService.removeItineraryStop(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body.message) else NetworkResult.Error(body.message)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to remove stop.")
        }
    }

    suspend fun fetchGroupPlaces(userId: Int, groupId: Int, category: String? = null): NetworkResult<com.simats.multimodaltransportationrecommendation.data.model.group.GroupPlacesResponse> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchGroupPlaces(userId, groupId, category)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body) else NetworkResult.Error("Failed to fetch places")
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load tourist places.")
        }
    }

    suspend fun voteGroupPlace(userId: Int, groupId: Int, placeId: Int, vote: Int = 1): NetworkResult<com.simats.multimodaltransportationrecommendation.data.model.group.VoteActionResponse> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.group.PlaceVoteRequest(
                userId = userId,
                groupId = groupId,
                placeId = placeId,
                vote = vote
            )
            val response = authService.voteGroupPlace(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body) else NetworkResult.Error(body.message)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to record vote.")
        }
    }

    suspend fun fetchGroupChat(userId: Int, groupId: Int): NetworkResult<List<com.simats.multimodaltransportationrecommendation.data.model.group.GroupChatMessage>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchGroupChat(userId, groupId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body.messages) else NetworkResult.Error("Failed to load chat")
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load group chat.")
        }
    }

    suspend fun sendGroupMessage(
        userId: Int,
        groupId: Int,
        messageContent: String,
        messageType: String = "text",
        attachedPlaceId: Int? = null
    ): NetworkResult<String> = withContext(Dispatchers.IO) {
        try {
            val req = com.simats.multimodaltransportationrecommendation.data.model.group.SendGroupMessageRequest(
                userId = userId,
                groupId = groupId,
                messageType = messageType,
                messageContent = messageContent,
                attachedPlaceId = attachedPlaceId
            )
            val response = authService.sendGroupMessage(req)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body.message) else NetworkResult.Error(body.message)
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Failed to send message.")
        }
    }

    suspend fun fetchGroupNotifications(userId: Int): NetworkResult<List<com.simats.multimodaltransportationrecommendation.data.model.group.GroupNotificationItem>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchGroupNotifications(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) NetworkResult.Success(body.notifications) else NetworkResult.Error("Failed to load notifications")
            } else {
                NetworkResult.Error(parseErrorMessage(response))
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Unable to load group alerts.")
        }
    }
}
