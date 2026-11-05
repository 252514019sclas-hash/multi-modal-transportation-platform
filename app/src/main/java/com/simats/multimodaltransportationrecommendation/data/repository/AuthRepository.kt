package com.simats.multimodaltransportationrecommendation.data.repository

import com.google.gson.Gson
import com.simats.multimodaltransportationrecommendation.data.api.AuthApiService
import com.simats.multimodaltransportationrecommendation.data.api.RetrofitClient
import com.simats.multimodaltransportationrecommendation.data.model.auth.BaseResponse
import com.simats.multimodaltransportationrecommendation.data.model.auth.LoginRequest
import com.simats.multimodaltransportationrecommendation.data.model.auth.LoginResponse
import com.simats.multimodaltransportationrecommendation.data.model.auth.SignUpRequest
import com.simats.multimodaltransportationrecommendation.data.model.auth.SignUpResponse
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
        search: String? = null
    ): NetworkResult<List<TouristPlace>> = withContext(Dispatchers.IO) {
        try {
            val response = authService.fetchPlaces(userId, city, category, search)
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
}
