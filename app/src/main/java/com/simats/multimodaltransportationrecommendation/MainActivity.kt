package com.simats.multimodaltransportationrecommendation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.simats.multimodaltransportationrecommendation.ui.booking.ReviewBookingScreen
import com.simats.multimodaltransportationrecommendation.ui.compare.CompareRoutesScreen
import com.simats.multimodaltransportationrecommendation.ui.completed.JourneyCompletedScreen
import com.simats.multimodaltransportationrecommendation.ui.destination.DestinationScreen
import com.simats.multimodaltransportationrecommendation.ui.home.HomeScreen
import com.simats.multimodaltransportationrecommendation.ui.journey.OnYourWayScreen
import com.simats.multimodaltransportationrecommendation.ui.onboarding.OnboardingScreen
import com.simats.multimodaltransportationrecommendation.ui.options.TripOptionsScreen
import com.simats.multimodaltransportationrecommendation.ui.payment.PaymentMethodScreen
import com.simats.multimodaltransportationrecommendation.ui.preferences.TravelPreferencesScreen
import com.simats.multimodaltransportationrecommendation.ui.profile.ProfileScreen
import com.simats.multimodaltransportationrecommendation.ui.recommended.RecommendedRouteScreen
import com.simats.multimodaltransportationrecommendation.ui.reroute.SmarterWayScreen
import com.simats.multimodaltransportationrecommendation.ui.routes.RouteResultsScreen
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
                            currentScreen = "preferences"
                        },
                        onSignInClicked = {
                            currentScreen = "signin"
                        }
                    )
                    "preferences" -> TravelPreferencesScreen(
                        onBack = {
                            currentScreen = "signup"
                        },
                        onSavePreferences = {
                            currentScreen = "home"
                        }
                    )
                    "home" -> HomeScreen(
                        onPlanJourney = {
                            currentScreen = "destination"
                        },
                        onNavigateTab = { tab ->
                            when (tab) {
                                "My trips" -> currentScreen = "mytrips"
                                "Profile" -> currentScreen = "profile"
                            }
                        }
                    )
                    "destination" -> DestinationScreen(
                        onBack = {
                            currentScreen = "home"
                        },
                        onUseDestination = {
                            currentScreen = "tripoptions"
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
                        onBack = {
                            currentScreen = "tripoptions"
                        },
                        onViewRecommended = {
                            currentScreen = "compareroutes"
                        }
                    )
                    "compareroutes" -> CompareRoutesScreen(
                        onBack = {
                            currentScreen = "routeresults"
                        },
                        onChooseRoute = {
                            currentScreen = "recommendedroute"
                        }
                    )
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
                    "reviewbooking" -> ReviewBookingScreen(
                        onBack = {
                            currentScreen = "transitservice"
                        },
                        onConfirmAndPay = {
                            currentScreen = "paymentmethod"
                        }
                    )
                    "paymentmethod" -> PaymentMethodScreen(
                        onBack = {
                            currentScreen = "reviewbooking"
                        },
                        onPaySuccess = {
                            currentScreen = "yourticket"
                        }
                    )
                    "yourticket" -> YourTicketScreen(
                        onBack = {
                            currentScreen = "paymentmethod"
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
                        onOpenTicket = {
                            currentScreen = "yourticket"
                        },
                        onNavigateTab = { tab ->
                            when (tab) {
                                "Explore" -> currentScreen = "home"
                                "Profile" -> currentScreen = "profile"
                            }
                        }
                    )
                    "profile" -> ProfileScreen(
                        onSignOut = {
                            currentScreen = "signin"
                        },
                        onNavigateTab = { tab ->
                            when (tab) {
                                "Explore" -> currentScreen = "home"
                                "My trips" -> currentScreen = "mytrips"
                            }
                        }
                    )
                }
            }
        }
    }
}
