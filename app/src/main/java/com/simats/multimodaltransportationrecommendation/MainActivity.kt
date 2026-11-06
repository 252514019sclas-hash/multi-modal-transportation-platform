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

                var selectedTouristPlace by androidx.compose.runtime.remember { mutableStateOf<TouristPlace?>(null) }
                var selectedRouteOption by androidx.compose.runtime.remember { mutableStateOf<RouteOption?>(null) }
                var activeTicketBooking by androidx.compose.runtime.remember { mutableStateOf<BookedJourneyData?>(null) }
                var activeTicketTrip by androidx.compose.runtime.remember { mutableStateOf<TripItem?>(null) }

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
                        onNavigateTab = { tab ->
                            when (tab) {
                                "Explore" -> currentScreen = "home"
                                "My trips" -> currentScreen = "mytrips"
                                "Saved" -> currentScreen = "saved"
                                "Profile" -> currentScreen = "profile"
                            }
                        }
                    )
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
                        onViewRecommended = { route ->
                            selectedRouteOption = route
                            currentScreen = "reviewbooking"
                        },
                        onCompareRoutes = { route, _ ->
                            selectedRouteOption = route
                            currentScreen = "compareroutes"
                        },
                        onOpenPlaceDetail = { place ->
                            selectedTouristPlace = place
                            currentScreen = "placedetail"
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
                                currentScreen = "reviewbooking"
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
                        }
                    )
                    "onyourway" -> OnYourWayScreen(
                        onBack = {
                            currentScreen = "yourticket"
                        },
                        onViewTicket = {
                            currentScreen = "yourticket"
                        },
                        onSimulateDelay = {
                            currentScreen = "smarterway"
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
                            currentScreen = "smarterway"
                        },
                        onBackToHome = {
                            currentScreen = "mytrips"
                        }
                    )
                    "mytrips" -> MyTripsScreen(
                        onOpenTicket = { trip ->
                            activeTicketTrip = trip
                            activeTicketBooking = null
                            currentScreen = "yourticket"
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
