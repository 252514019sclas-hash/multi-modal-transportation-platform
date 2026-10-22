package com.simats.multimodaltransportationrecommendation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.simats.multimodaltransportationrecommendation.ui.destination.DestinationScreen
import com.simats.multimodaltransportationrecommendation.ui.home.HomeScreen
import com.simats.multimodaltransportationrecommendation.ui.onboarding.OnboardingScreen
import com.simats.multimodaltransportationrecommendation.ui.options.TripOptionsScreen
import com.simats.multimodaltransportationrecommendation.ui.preferences.TravelPreferencesScreen
import com.simats.multimodaltransportationrecommendation.ui.signin.SignInScreen
import com.simats.multimodaltransportationrecommendation.ui.signup.CreateAccountScreen
import com.simats.multimodaltransportationrecommendation.ui.splash.SplashScreen
import com.simats.multimodaltransportationrecommendation.ui.theme.MultiModalTransportationRecommendationTheme

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
                            currentScreen = "home"
                        }
                    )
                }
            }
        }
    }
}
