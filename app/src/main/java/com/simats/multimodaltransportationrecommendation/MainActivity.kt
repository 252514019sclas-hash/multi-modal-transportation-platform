package com.simats.multimodaltransportationrecommendation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.ui.booking.ReviewBookingScreen
import com.simats.multimodaltransportationrecommendation.ui.booking.ReviewBookingViewModel
import com.simats.multimodaltransportationrecommendation.ui.compare.CompareRoutesScreen
import com.simats.multimodaltransportationrecommendation.ui.compare.CompareRoutesViewModel
import com.simats.multimodaltransportationrecommendation.ui.completed.JourneyCompletedScreen
import com.simats.multimodaltransportationrecommendation.ui.destination.DestinationScreen
import com.simats.multimodaltransportationrecommendation.ui.home.HomeScreen
import com.simats.multimodaltransportationrecommendation.ui.journey.OnYourWayScreen
import com.simats.multimodaltransportationrecommendation.ui.onboarding.OnboardingScreen
import com.simats.multimodaltransportationrecommendation.ui.options.TripOptionsScreen
import com.simats.multimodaltransportationrecommendation.ui.places.TouristPlaceDetailScreen
import com.simats.multimodaltransportationrecommendation.ui.preferences.TravelPreferencesScreen
import com.simats.multimodaltransportationrecommendation.ui.profile.ProfileScreen
import com.simats.multimodaltransportationrecommendation.ui.recommended.RecommendedRouteScreen
import com.simats.multimodaltransportationrecommendation.ui.reroute.SmarterWayScreen
import com.simats.multimodaltransportationrecommendation.ui.routes.RouteResultsScreen
import com.simats.multimodaltransportationrecommendation.ui.saved.SavedScreen
import com.simats.multimodaltransportationrecommendation.ui.signin.SignInScreen
import com.simats.multimodaltransportationrecommendation.ui.signup.CreateAccountScreen
import com.simats.multimodaltransportationrecommendation.ui.splash.SplashScreen
import com.simats.multimodaltransportationrecommendation.ui.theme.MultiModalTransportationRecommendationTheme
import com.simats.multimodaltransportationrecommendation.ui.ticket.YourTicketScreen
import com.simats.multimodaltransportationrecommendation.ui.transitservice.TransitServiceScreen
import com.simats.multimodaltransportationrecommendation.ui.trips.MyTripsScreen
import com.simats.multimodaltransportationrecommendation.ui.trips.TripDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiModalTransportationRecommendationTheme {
                var currentScreen by rememberSaveable { mutableStateOf("splash") }
                var searchOrigin by rememberSaveable { mutableStateOf("Chennai Central") }
                var searchDestination by rememberSaveable { mutableStateOf("Marina Beach & Promenade") }
                var preferencesReturnScreen by rememberSaveable { mutableStateOf("home") }
                var activeGroupId by rememberSaveable { mutableStateOf(1) }

                var selectedTouristPlace by androidx.compose.runtime.remember { mutableStateOf<TouristPlace?>(null) }
                var selectedRouteOption by androidx.compose.runtime.remember { mutableStateOf<RouteOption?>(null) }
                var activeTicketBooking by androidx.compose.runtime.remember { mutableStateOf<BookedJourneyData?>(null) }
                var activeTicketTrip by androidx.compose.runtime.remember { mutableStateOf<TripItem?>(null) }
                var selectedFacilityCategory by androidx.compose.runtime.remember { mutableStateOf<String?>(null) }
                var selectedFacilityCity by androidx.compose.runtime.remember { mutableStateOf<String?>(null) }

                when (currentScreen) {
                    "splash" -> SplashScreen(
                        onGetStarted = {
                            currentScreen = "onboarding"
                        }
                    )
                    "onboarding" -> OnboardingScreen(
                        onGetStarted = {
                            currentScreen = "signin"
                        },
                        onSignIn = {
                            currentScreen = "signin"
                        }
                    )
                    "signin" -> SignInScreen(
                        onBack = {
                            currentScreen = "onboarding"
                        },
                        onSignInSuccess = {
                            currentScreen = "home"
                        },
                        onCreateAccount = {
                            currentScreen = "signup"
                        }
                    )
                    "signup" -> CreateAccountScreen(
                        onBack = {
                            currentScreen = "signin"
                        },
                        onAccountCreated = {
                            preferencesReturnScreen = "home"
                            currentScreen = "preferences"
                        },
                        onSignInClicked = {
                            currentScreen = "signin"
                        }
                    )
                    "preferences" -> TravelPreferencesScreen(
                        onBack = {
                            currentScreen = preferencesReturnScreen
                        },
                        onSavePreferences = {
                            currentScreen = preferencesReturnScreen
                        }
                    )
                    "home" -> HomeScreen(
                        onPlanJourney = { origin, dest ->
                            searchOrigin = origin
                            searchDestination = dest
                            currentScreen = "routeresults"
                        },
                        onOpenPlaceDetail = { place ->
                            selectedTouristPlace = place
                            currentScreen = "placedetail"
                        },
                        onOpenGroupTravel = {
                            currentScreen = "grouptravel"
                        },
                        onOpenAiTravelAdvisor = {
                            currentScreen = "aitraveladvisor"
                        },
                        onNavigateTab = { tab ->
                            when (tab) {
                                "Explore" -> currentScreen = "home"
                                "My trips" -> currentScreen = "mytrips"
                                "Saved" -> currentScreen = "saved"
                                "Profile" -> currentScreen = "profile"
                            }
                        }
                    )
                    "grouptravel" -> {
                        val groupVm: com.simats.multimodaltransportationrecommendation.ui.grouptravel.GroupTravelViewModel = viewModel()
                        com.simats.multimodaltransportationrecommendation.ui.grouptravel.GroupTravelDashboardScreen(
                            viewModel = groupVm,
                            onBack = { currentScreen = "home" },
                            onCreateGroup = { currentScreen = "creategroup" },
                            onJoinGroup = { currentScreen = "joingroup" },
                            onOpenGroupDetails = { gId ->
                                activeGroupId = gId
                                currentScreen = "groupdetails"
                            }
                        )
                    }
                    "creategroup" -> {
                        val groupVm: com.simats.multimodaltransportationrecommendation.ui.grouptravel.GroupTravelViewModel = viewModel()
                        com.simats.multimodaltransportationrecommendation.ui.grouptravel.CreateGroupScreen(
                            viewModel = groupVm,
                            onBack = { currentScreen = "grouptravel" },
                            onGroupCreated = { group ->
                                activeGroupId = group.groupId
                                currentScreen = "groupdetails"
                            }
                        )
                    }
                    "joingroup" -> {
                        val groupVm: com.simats.multimodaltransportationrecommendation.ui.grouptravel.GroupTravelViewModel = viewModel()
                        com.simats.multimodaltransportationrecommendation.ui.grouptravel.JoinGroupScreen(
                            viewModel = groupVm,
                            onBack = { currentScreen = "grouptravel" },
                            onJoinedSuccess = {
                                currentScreen = "grouptravel"
                            }
                        )
                    }
                    "groupdetails" -> {
                        val groupVm: com.simats.multimodaltransportationrecommendation.ui.grouptravel.GroupTravelViewModel = viewModel()
                        com.simats.multimodaltransportationrecommendation.ui.grouptravel.GroupDetailsScreen(
                            viewModel = groupVm,
                            groupId = activeGroupId,
                            onBack = { currentScreen = "grouptravel" },
                            onViewTransitRoute = { origin, dest ->
                                searchOrigin = origin
                                searchDestination = dest
                                currentScreen = "routeresults"
                            }
                        )
                    }
                    "groupmembers" -> {
                        val groupVm: com.simats.multimodaltransportationrecommendation.ui.grouptravel.GroupTravelViewModel = viewModel()
                        com.simats.multimodaltransportationrecommendation.ui.grouptravel.GroupMembersScreen(
                            viewModel = groupVm,
                            groupId = activeGroupId,
                            onBack = { currentScreen = "groupdetails" }
                        )
                    }
                    "grouplocationsettings" -> {
                        val groupVm: com.simats.multimodaltransportationrecommendation.ui.grouptravel.GroupTravelViewModel = viewModel()
                        com.simats.multimodaltransportationrecommendation.ui.grouptravel.GroupLocationSettingsScreen(
                            viewModel = groupVm,
                            groupId = activeGroupId,
                            onBack = { currentScreen = "groupdetails" }
                        )
                    }
                    "placedetail" -> TouristPlaceDetailScreen(
                        initialPlace = selectedTouristPlace,
                        onBack = {
                            currentScreen = "home"
                        },
                        onPlanJourneyToPlace = { destinationName ->
                            searchOrigin = "Chennai Central"
                            searchDestination = destinationName
                            currentScreen = "routeresults"
                        }
                    )
                    "aitraveladvisor" -> com.simats.multimodaltransportationrecommendation.ui.aitravel.AiTravelAdvisorScreen(
                        initialOrigin = searchOrigin,
                        initialDestination = searchDestination,
                        onBack = { currentScreen = "routeresults" },
                        onOpenPlaceDetail = { place ->
                            selectedTouristPlace = place
                            currentScreen = "placedetail"
                        },
                        onViewMyTrips = { currentScreen = "mytrips" },
                        onOpenNearbyFacilities = { origin, dest, targetCity, category ->
                            searchOrigin = origin
                            searchDestination = dest
                            selectedFacilityCity = targetCity ?: dest
                            selectedFacilityCategory = category
                            currentScreen = "nearbyfacilities"
                        }
                    )
                    "nearbyfacilities" -> com.simats.multimodaltransportationrecommendation.ui.facilities.NearbyFacilitiesScreen(
                        origin = searchOrigin,
                        destination = searchDestination,
                        initialCity = selectedFacilityCity ?: searchDestination,
                        initialCategory = selectedFacilityCategory,
                        onBack = { currentScreen = "aitraveladvisor" },
                        onOpenStopover = { currentScreen = "aismartstopover" }
                    )
                    "aismartstopover" -> com.simats.multimodaltransportationrecommendation.ui.stopover.AiSmartStopoverScreen(
                        origin = searchOrigin,
                        destination = searchDestination,
                        onBack = { currentScreen = "nearbyfacilities" },
                        onViewRouteMap = { currentScreen = "routeresults" },
                        onStartJourney = { currentScreen = "onyourway" },
                        onViewSummary = { currentScreen = "tripsummary" }
                    )
                    "tripsummary" -> com.simats.multimodaltransportationrecommendation.ui.stopover.TripSummaryScreen(
                        origin = searchOrigin,
                        destination = searchDestination,
                        onBack = { currentScreen = "aismartstopover" }
                    )
                    "destination" -> DestinationScreen(
                        initialOrigin = searchOrigin,
                        initialDestination = searchDestination,
                        onBack = {
                            currentScreen = "home"
                        },
                        onSearchRoutes = { origin, dest ->
                            searchOrigin = origin
                            searchDestination = dest
                            currentScreen = "routeresults"
                        }
                    )
                    "tripoptions" -> TripOptionsScreen(
                        onBack = {
                            currentScreen = "destination"
                        },
                        onShowRoutes = {
                            currentScreen = "routeresults"
                        }
                    )
                    "routeresults" -> RouteResultsScreen(
                        origin = searchOrigin,
                        destination = searchDestination,
                        onBack = {
                            currentScreen = "home"
                        },
                        onStartJourney = { route ->
                            selectedRouteOption = route
                            currentScreen = "onyourway"
                        },
                        onCompareRoutes = { route, _ ->
                            selectedRouteOption = route
                            currentScreen = "compareroutes"
                        },
                        onOpenPlaceDetail = { place ->
                            selectedTouristPlace = place
                            currentScreen = "placedetail"
                        },
                        onPlanMultiDay = {
                            currentScreen = "aitraveladvisor"
                        },
                        onOpenNearbyFacilities = {
                            selectedFacilityCategory = null
                            currentScreen = "nearbyfacilities"
                        }
                    )
                    "compareroutes" -> {
                        val compareVm: CompareRoutesViewModel = viewModel()
                        LaunchedEffect(selectedRouteOption) {
                            compareVm.setRoutes(searchOrigin, searchDestination, emptyList(), selectedRouteOption)
                        }
                        CompareRoutesScreen(
                            viewModel = compareVm,
                            onBack = {
                                currentScreen = "routeresults"
                            },
                            onChooseRoute = { route ->
                                if (route != null) selectedRouteOption = route
                                currentScreen = "onyourway"
                            }
                        )
                    }
                    "recommendedroute" -> RecommendedRouteScreen(
                        onBack = {
                            currentScreen = "compareroutes"
                        },
                        onContinue = {
                            currentScreen = "transitservice"
                        }
                    )
                    "transitservice" -> TransitServiceScreen(
                        onBack = {
                            currentScreen = "recommendedroute"
                        },
                        onContinueToBooking = {
                            currentScreen = "reviewbooking"
                        }
                    )
                    "reviewbooking" -> {
                        val reviewVm: ReviewBookingViewModel = viewModel()
                        LaunchedEffect(selectedRouteOption) {
                            reviewVm.setBookingDetails(selectedRouteOption, searchOrigin, searchDestination)
                        }
                        ReviewBookingScreen(
                            viewModel = reviewVm,
                            onBack = {
                                currentScreen = "routeresults"
                            },
                            onBookingSuccess = { booked ->
                                activeTicketBooking = booked
                                activeTicketTrip = null
                                currentScreen = "yourticket"
                            },
                            onConfirmAndPay = { route ->
                                if (route != null) selectedRouteOption = route
                            }
                        )
                    }

                    "yourticket" -> YourTicketScreen(
                        bookedJourney = activeTicketBooking,
                        trip = activeTicketTrip,
                        onBack = {
                            currentScreen = "mytrips"
                        },
                        onStartJourney = {
                            currentScreen = "onyourway"
                        },
                        onOpenStopover = {
                            currentScreen = "aismartstopover"
                        }
                    )
                    "onyourway" -> OnYourWayScreen(
                        onBack = {
                            currentScreen = "routeresults"
                        },
                        onViewTicket = {
                            currentScreen = "routeresults"
                        },
                        onSimulateDelay = {
                            currentScreen = "smarterway"
                        },
                        onCompleteJourney = {
                            currentScreen = "journeycompleted"
                        },
                        onViewPlaceDetails = { place ->
                            selectedTouristPlace = place
                            currentScreen = "placedetail"
                        },
                        onOpenAiTravelAdvisor = {
                            currentScreen = "aitraveladvisor"
                        }
                    )
                    "smarterway" -> SmarterWayScreen(
                        onBack = {
                            currentScreen = "onyourway"
                        },
                        onAcceptRoute = {
                            currentScreen = "journeycompleted"
                        },
                        onKeepRoute = {
                            currentScreen = "journeycompleted"
                        }
                    )
                    "journeycompleted" -> JourneyCompletedScreen(
                        onBack = {
                            currentScreen = "onyourway"
                        },
                        onViewJourneyDetails = { trip ->
                            activeTicketTrip = trip
                            currentScreen = "tripdetails"
                        },
                        onViewTicket = { trip ->
                            activeTicketTrip = trip
                            activeTicketBooking = null
                            currentScreen = "yourticket"
                        },
                        onExploreNearbyPlaces = { place ->
                            selectedTouristPlace = place
                            currentScreen = "placedetail"
                        },
                        onBackToMyTrips = {
                            currentScreen = "mytrips"
                        },
                        onGoToExplore = {
                            currentScreen = "home"
                        }
                    )
                    "mytrips" -> MyTripsScreen(
                        onOpenTicket = { trip ->
                            activeTicketTrip = trip
                            activeTicketBooking = null
                            currentScreen = "yourticket"
                        },
                        onOpenTripDetails = { trip ->
                            activeTicketTrip = trip
                            currentScreen = "tripdetails"
                        },
                        onPlanNewJourney = {
                            currentScreen = "home"
                        },
                        onNavigateTab = { tab ->
                            when (tab) {
                                "Explore" -> currentScreen = "home"
                                "My trips" -> currentScreen = "mytrips"
                                "Saved" -> currentScreen = "saved"
                                "Profile" -> currentScreen = "profile"
                            }
                        }
                    )
                    "tripdetails" -> {
                        val currentTrip = activeTicketTrip ?: TripItem(
                            journeyId = 5,
                            userId = 1,
                            origin = "Chennai Central",
                            destination = "Marina Beach, Chennai",
                            mode = "CMRL Blue Line Metro",
                            routeTitle = "Corridor Express",
                            durationMinutes = 18,
                            distanceKm = 6.4,
                            fare = 30.0,
                            fareText = "₹30",
                            status = "completed",
                            journeyDate = "Today · 09:30 AM",
                            bookingReference = "TN-MS-126F13"
                        )
                        TripDetailScreen(
                            trip = currentTrip,
                            onBack = {
                                currentScreen = "mytrips"
                            },
                            onViewTicket = {
                                activeTicketTrip = currentTrip
                                activeTicketBooking = null
                                currentScreen = "yourticket"
                            },
                            onOpenPlaceDetail = { place ->
                                selectedTouristPlace = place
                                currentScreen = "placedetail"
                            }
                        )
                    }
                    "saved" -> SavedScreen(
                        onOpenPlaceDetail = { place ->
                            selectedTouristPlace = place
                            currentScreen = "placedetail"
                        },
                        onPlanRoute = { origin, dest ->
                            searchOrigin = origin
                            searchDestination = dest
                            currentScreen = "routeresults"
                        },
                        onExploreMore = {
                            currentScreen = "home"
                        },
                        onNavigateTab = { tab ->
                            when (tab) {
                                "Explore" -> currentScreen = "home"
                                "My trips" -> currentScreen = "mytrips"
                                "Saved" -> currentScreen = "saved"
                                "Profile" -> currentScreen = "profile"
                            }
                        }
                    )
                    "profile" -> ProfileScreen(
                        onSignOut = {
                            currentScreen = "signin"
                        },
                        onOpenTravelPreferences = {
                            preferencesReturnScreen = "profile"
                            currentScreen = "preferences"
                        },
                        onOpenSavedPlaces = {
                            currentScreen = "saved"
                        },

                        onNavigateTab = { tab ->
                            when (tab) {
                                "Explore" -> currentScreen = "home"
                                "My trips" -> currentScreen = "mytrips"
                                "Saved" -> currentScreen = "saved"
                                "Profile" -> currentScreen = "profile"
                            }
                        }
                    )
                }
            }
        }
    }
}
