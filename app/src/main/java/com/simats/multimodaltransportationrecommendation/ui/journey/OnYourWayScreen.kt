package com.simats.multimodaltransportationrecommendation.ui.journey

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.ui.map.MapMarkerItem
import com.simats.multimodaltransportationrecommendation.ui.map.MoveSmartGoogleMapView
import com.simats.multimodaltransportationrecommendation.ui.theme.*
import java.util.Locale

@Composable
fun OnYourWayScreen(
    viewModel: OnYourWayViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {},
    onViewTicket: () -> Unit = {},
    onSimulateDelay: () -> Unit = {},
    onCompleteJourney: () -> Unit = {},
    onViewPlaceDetails: (TouristPlace) -> Unit = {},
    onOpenAiTravelAdvisor: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val isTracking = uiState.trackingStatus == "TRACKING"
    val isPaused = uiState.trackingStatus == "PAUSED"

    var showFullRouteSheet by remember { mutableStateOf(false) }
    var showTransitHelpSheet by remember { mutableStateOf(false) }
    var selectedPlaceDetail by remember { mutableStateOf<TouristPlace?>(null) }

    // Pulsing animation for live badge & map marker
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Voice recognition launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.sendAssistantQuery(spokenText)
                viewModel.openAssistant(true)
            }
        }
    }

    fun launchVoiceRecognition() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask MoveSmart AI about your journey...")
            }
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Voice input not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val fine = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fine || coarse) {
            viewModel.startJourneyTracking(context)
        } else {
            Toast.makeText(context, "Location permission is required for real-time journey tracking", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkDeviceStatus(context)
    }

    // Prepare map markers for stations and attractions
    val stationMarkers = remember(uiState.stations, uiState.showStationsLayer) {
        if (!uiState.showStationsLayer) emptyList()
        else uiState.stations.map { st ->
            MapMarkerItem(
                id = st.id,
                title = st.name,
                snippet = "${st.type.replaceFirstChar { it.uppercase() }} · Arrival ${st.time}",
                latitude = st.latitude,
                longitude = st.longitude,
                iconColorHue = BitmapDescriptorFactory.HUE_AZURE
            )
        }
    }

    val attractionMarkers = remember(uiState.attractionsOnMap, uiState.showAttractionsLayer) {
        if (!uiState.showAttractionsLayer) emptyList()
        else uiState.attractionsOnMap.map { at ->
            MapMarkerItem(
                id = at.id,
                title = at.name,
                snippet = "${at.category} · ${at.rating} · ${at.fee}",
                latitude = at.latitude,
                longitude = at.longitude,
                isTouristPlace = true,
                iconColorHue = BitmapDescriptorFactory.HUE_ORANGE
            )
        }
    }

    val facilityMarkers = remember(uiState.facilitiesOnMap, uiState.showFacilitiesLayer) {
        if (!uiState.showFacilitiesLayer) emptyList()
        else uiState.facilitiesOnMap.map { fc ->
            MapMarkerItem(
                id = fc.facilityId.toString(),
                title = fc.name,
                snippet = "${fc.category} · ${if (fc.is24Hours) "Open 24/7" else fc.operatingHours}",
                latitude = fc.latitude,
                longitude = fc.longitude,
                iconColorHue = BitmapDescriptorFactory.HUE_YELLOW
            )
        }
    }

    val allMapMarkers = remember(stationMarkers, attractionMarkers, facilityMarkers) {
        stationMarkers + attractionMarkers + facilityMarkers
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. TOP BAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, CircleShape)
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "←",
                                color = TextNavyDark,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Live Journey Tracking",
                            color = TextNavyDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Options menu button (Opens Smart Notification settings)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, CircleShape)
                            .clickable { viewModel.setNotificationsDialogOpen(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "···",
                            color = TextNavyDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 2. LIVE TIME & TRACKING BADGE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = uiState.currentTime,
                            color = TextNavyDark,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.remainingTimeText,
                            color = TealSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Live Pulsing Badge (Clickable to pause / resume)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isTracking -> PastelCardTeal
                                    isPaused -> PastelCardPeach
                                    else -> PastelCardBlue
                                }
                            )
                            .border(
                                1.dp,
                                when {
                                    isTracking -> TealSecondary.copy(alpha = 0.45f)
                                    isPaused -> WarmOrangeAccent.copy(alpha = 0.45f)
                                    else -> BorderLight
                                },
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.toggleLiveTracking(context) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        when {
                                            isTracking -> TealSecondary.copy(alpha = pulseAlpha)
                                            isPaused -> WarmOrangeAccent
                                            else -> TextMuted
                                        },
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    isTracking -> "LIVE TRACKING ON"
                                    isPaused -> "TRACKING PAUSED"
                                    else -> "TRACKING OFF"
                                },
                                color = when {
                                    isTracking -> TealSecondary
                                    isPaused -> WarmOrangeDark
                                    else -> TextNavyDark
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // 3. SMART EXPLORE MAP CARD WITH LAYERS
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Map Layer Selector Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MapFilterChip(
                            label = "Stations",
                            icon = "🚇",
                            selected = uiState.showStationsLayer,
                            onClick = { viewModel.toggleMapLayer("stations") }
                        )
                        MapFilterChip(
                            label = "Attractions",
                            icon = "🏛️",
                            selected = uiState.showAttractionsLayer,
                            onClick = { viewModel.toggleMapLayer("attractions") }
                        )
                        MapFilterChip(
                            label = "Bus Stops",
                            icon = "🚏",
                            selected = uiState.showBusStopsLayer,
                            onClick = { viewModel.toggleMapLayer("bus_stops") }
                        )
                        MapFilterChip(
                            label = "Facilities",
                            icon = "ℹ️",
                            selected = uiState.showFacilitiesLayer,
                            onClick = { viewModel.toggleMapLayer("facilities") }
                        )
                    }

                    // GPS Disabled Warning Banner
                    if (!uiState.isGpsProviderEnabled) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PastelCardPeach)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "⚠️", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Device GPS is Disabled",
                                            color = WarmOrangeDark,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Please turn on device location services to view real-time tracking.",
                                            color = TextNavyDark,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        try {
                                            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                        } catch (_: Exception) {}
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = WarmOrangeAccent),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Turn On", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Location Permission Banner
                    if (!uiState.hasLocationPermission) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PastelCardBlue)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "📍", fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Location Permission Needed",
                                            color = SkyBlueDark,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Allow GPS location access to display your live marker along the route.",
                                            color = TextNavyDark,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                                Button(
                                    onClick = {
                                        locationPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Allow", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Google Maps View Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .shadow(2.dp, RoundedCornerShape(18.dp))
                            .clip(RoundedCornerShape(18.dp))
                            .background(PastelCardBlue)
                            .border(1.dp, BorderLight, RoundedCornerShape(18.dp))
                    ) {
                        MoveSmartGoogleMapView(
                            modifier = Modifier.fillMaxSize(),
                            userLocation = uiState.userGpsLatLng,
                            destinationLocation = uiState.destinationLatLng,
                            routeWaypoints = uiState.routeWaypoints,
                            additionalMarkers = allMapMarkers,
                            onRecenterClick = { viewModel.recenterMap() },
                            onMarkerClick = { mItem ->
                                viewModel.selectMarker(
                                    MapPointDetail(
                                        title = mItem.title,
                                        subtitle = mItem.snippet ?: "Along Transit Route",
                                        badge = if (mItem.isTouristPlace) "Tourist Spot" else "Transit Stop",
                                        icon = if (mItem.isTouristPlace) "🏛️" else "🚇",
                                        distance = "On Corridor"
                                    )
                                )
                            }
                        )

                        // Dynamic Route Badge & Corridor Switcher
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setRouteSelectorOpen(true) }
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${uiState.origin} ➔ ${uiState.destination}",
                                    color = TextNavyDark,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.widthIn(max = 175.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "⇄", color = SkyBluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Recenter Button
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(34.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, CircleShape)
                                .clickable {
                                    viewModel.selectMarker(
                                        MapPointDetail(
                                            title = "Current Transit Position",
                                            subtitle = "Approaching Thousand Lights Metro",
                                            badge = "On Schedule",
                                            icon = "🚇",
                                            distance = "0 km"
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎯", fontSize = 14.sp)
                        }
                    }

                    // Live GPS Telemetry Chip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🛰️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = uiState.userGpsLatLng?.let {
                                        "GPS: ${String.format(Locale.US, "%.4f° N, %.4f° E", it.latitude, it.longitude)}"
                                    } ?: "GPS: Acquiring satellite fix...",
                                    color = TextNavyDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Accuracy: ${uiState.gpsAccuracyMeters?.toInt()?.let { "±${it}m" } ?: "Standby"} · Speed: ${uiState.gpsSpeedKmh?.toInt()?.let { "${it} km/h" } ?: "0 km/h"}",
                                    color = TextMutedSlate,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Text(
                            text = uiState.gpsLastUpdatedText,
                            color = if (isTracking) EmeraldGreen else WarmOrangeDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Real-Time Journey Tracking Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (uiState.trackingStatus) {
                            "STOPPED" -> {
                                Button(
                                    onClick = {
                                        val hasPerm = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (!hasPerm) {
                                            locationPermissionLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        } else {
                                            viewModel.startJourneyTracking(context)
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                                ) {
                                    Text(
                                        text = "▶  Start Journey Tracking",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            "TRACKING" -> {
                                Button(
                                    onClick = { viewModel.pauseTracking() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = WarmOrangeAccent)
                                ) {
                                    Text(
                                        text = "⏸  Pause Tracking",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(
                                    onClick = { viewModel.stopTracking() },
                                    modifier = Modifier
                                        .weight(0.7f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TextMutedSlate)
                                ) {
                                    Text(
                                        text = "⏹  Stop",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                OutlinedButton(
                                    onClick = { viewModel.recenterMap() },
                                    modifier = Modifier.height(44.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "🎯 Recenter", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            "PAUSED" -> {
                                Button(
                                    onClick = { viewModel.resumeTracking(context) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                                ) {
                                    Text(
                                        text = "▶  Resume Tracking",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Button(
                                    onClick = { viewModel.stopTracking() },
                                    modifier = Modifier
                                        .weight(0.7f)
                                        .height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TextMutedSlate)
                                ) {
                                    Text(
                                        text = "⏹  Stop",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                OutlinedButton(
                                    onClick = { viewModel.recenterMap() },
                                    modifier = Modifier.height(44.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "🎯 Recenter", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Selected Marker Detail Popup Card
                    uiState.selectedMarkerDetail?.let { detail ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, SkyBluePrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = detail.icon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = detail.title,
                                            color = TextNavyDark,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${detail.subtitle} · ${detail.distance}",
                                            color = TextMutedSlate,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(BorderSubtle)
                                        .clickable { viewModel.selectMarker(null) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✕", fontSize = 10.sp, color = TextNavyDark)
                                }
                            }
                        }
                    }
                }

                // 4. ACTIVE TRANSIT STATUS CARD
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = uiState.activeStatusTitle,
                                color = TextNavyDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.nextStopInfo,
                                color = TextMutedSlate,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔔", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = uiState.reminderText,
                                    color = TealSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(PastelCardTeal, CircleShape)
                                .border(1.dp, TealSecondary.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🚇", fontSize = 22.sp)
                        }
                    }
                }

                // 5. WHAT'S AHEAD SECTION
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "What's Ahead",
                            color = TextNavyDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Full route",
                            color = SkyBluePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showFullRouteSheet = true }
                        )
                    }

                    // Upcoming Steps
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.upcomingSteps.forEach { step ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = step.time,
                                    color = TextMutedSlate,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(62.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(PastelCardBlue, CircleShape)
                                        .border(1.dp, SkyBluePrimary.copy(alpha = 0.3f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = step.icon, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = step.title,
                                        color = TextNavyDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = step.subtitle,
                                        color = TextMutedSlate,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. AI SMART TRAVEL ASSISTANT CARD
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(PastelCardTeal, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✨", fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "AI Smart Travel Assistant",
                                        color = TextNavyDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Voice & text transit inquiry",
                                        color = TextMutedSlate,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Voice mic button
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SkyBlueLight)
                                    .clickable { launchVoiceRecognition() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🎙️", fontSize = 16.sp)
                            }
                        }

                        // Quick prompt suggestion chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "Tourist spots near destination",
                                "Next metro station",
                                "Alternative routes",
                                "Delay status"
                            ).forEach { prompt ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(BackgroundSoftCream)
                                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                                        .clickable {
                                            viewModel.sendAssistantQuery(prompt)
                                            viewModel.openAssistant(true)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = prompt,
                                        color = SkyBlueDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Open chat and AI Multi-day Planner buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PastelCardBlue)
                                    .clickable { viewModel.openAssistant(true) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "💬 AI Assistant",
                                    color = SkyBlueDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PastelCardTeal)
                                    .clickable { onOpenAiTravelAdvisor() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🤖 AI Multi-Day & Stays",
                                    color = TealDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 7. DYNAMIC TOURIST PLACES DISCOVERY ACROSS ALL 38 TAMIL NADU DISTRICTS
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Explore Places Along Route",
                                color = TextNavyDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Discover tourist destinations across all 38 districts of Tamil Nadu",
                                color = TextMutedSlate,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(PastelCardTeal)
                                .clickable { onOpenAiTravelAdvisor() }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "Plan Stays ➔",
                                color = TealDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Dynamic Search Bar
                    OutlinedTextField(
                        value = uiState.touristSearchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceWhite),
                        placeholder = {
                            Text(
                                text = "Search district, city, place, or category (e.g. Madurai, Ooty)...",
                                fontSize = 12.sp,
                                color = TextMutedSlate
                            )
                        },
                        leadingIcon = {
                            Text(text = "🔍", fontSize = 14.sp)
                        },
                        trailingIcon = {
                            if (uiState.touristSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Text(text = "✕", fontSize = 12.sp, color = TextMutedSlate)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SkyBluePrimary,
                            unfocusedBorderColor = BorderLight,
                            cursorColor = SkyBluePrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    // 11 Required Tourist Categories Filter Chips
                    val tnCategories = listOf(
                        "All",
                        "Beaches",
                        "Hill Stations",
                        "Historical Places",
                        "Temples and Religious Places",
                        "Waterfalls",
                        "Nature and Wildlife",
                        "Parks and Gardens",
                        "Forts and Heritage Sites",
                        "Adventure and Trekking",
                        "Lakes and Dams"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tnCategories.forEach { cat ->
                            val isSelected = uiState.selectedTouristCategory.equals(cat, ignoreCase = true)
                            val displayLabel = when (cat) {
                                "Temples and Religious Places" -> "Temples & Religious"
                                "Nature and Wildlife" -> "Nature & Wildlife"
                                "Parks and Gardens" -> "Parks & Gardens"
                                "Forts and Heritage Sites" -> "Forts & Heritage"
                                "Adventure and Trekking" -> "Adventure & Trekking"
                                "Lakes and Dams" -> "Lakes & Dams"
                                else -> cat
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) SkyBluePrimary else SurfaceWhite)
                                    .border(
                                        1.dp,
                                        if (isSelected) SkyBluePrimary else BorderLight,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.setTouristCategory(cat) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = displayLabel,
                                    color = if (isSelected) Color.White else TextNavyDark,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Dynamic Horizontal Cards with Real-time GPS distance
                    if (uiState.isPlacesLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = SkyBluePrimary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Discovering top Tamil Nadu destinations...",
                                    color = TextMutedSlate,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    } else if (uiState.placesAlongRoute.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "🏛️", fontSize = 24.sp)
                                Text(
                                    text = "No destinations found for '${uiState.touristSearchQuery}'",
                                    color = TextNavyDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Try searching by district name (e.g. Madurai, Salem, Nilgiris) or choose 'All' category.",
                                    color = TextMutedSlate,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            uiState.placesAlongRoute.forEach { place ->
                                TouristCorridorCard(
                                    place = place,
                                    onViewDetails = {
                                        onViewPlaceDetails(place)
                                    },
                                    onToggleSave = {
                                        viewModel.toggleSavePlace(place.placeId, context)
                                    }
                                )
                            }
                        }
                    }
                }

                // 9. ITINERARY BUILDER BANNER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryGradient)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "✨ Create Smart Travel Plan",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Build a personalized day itinerary with transit routes and timings based on your interests.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SurfaceWhite)
                                .clickable { viewModel.setItineraryBuilderOpen(true) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Build Smart Plan →",
                                color = SkyBlueDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 10. ARRIVED AT DESTINATION / COMPLETE JOURNEY ACTION
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(PastelCardTeal)
                        .border(1.dp, TealSecondary, RoundedCornerShape(16.dp))
                        .clickable { onCompleteJourney() }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceWhite),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🏁", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Arrived at Destination?",
                                    color = TealDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap to finish journey & view AI summary",
                                    color = TextNavyDark,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(TealDark)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Finish 🎉",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // BOTTOM ACTION BAR (Preserved from original design)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Transit Help
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .shadow(2.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(24.dp))
                        .clickable { showTransitHelpSheet = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Transit Help",
                            color = TextNavyDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🎧", fontSize = 13.sp)
                    }
                }

                // Complete Journey Button (Replacing ticket booking)
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(PrimaryGradient)
                        .clickable { onCompleteJourney() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Finish Journey",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🏁", fontSize = 13.sp)
                    }
                }
            }
        }

        // Floating Notice / Toast feedback
        uiState.userNotice?.let { msg ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 76.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TextNavyDark.copy(alpha = 0.92f))
                    .clickable { viewModel.dismissNotice() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = msg,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        // MODAL DIALOGS
        if (uiState.isAssistantSheetOpen) {
            AIAssistantDialog(
                chatMessages = uiState.assistantChat,
                queryText = uiState.assistantQueryText,
                isThinking = uiState.isAiThinking,
                onQueryChange = { viewModel.onAssistantQueryChange(it) },
                onSend = { viewModel.sendAssistantQuery() },
                onVoiceClick = { launchVoiceRecognition() },
                onDismiss = { viewModel.openAssistant(false) }
            )
        }

        if (uiState.isNotificationsSheetOpen) {
            NotificationsSettingsDialog(
                uiState = uiState,
                onToggle = { viewModel.toggleNotification(it) },
                onDismiss = { viewModel.setNotificationsDialogOpen(false) }
            )
        }

        if (uiState.isItineraryBuilderOpen) {
            ItineraryBuilderDialog(
                uiState = uiState,
                onDurationChange = { viewModel.setItineraryDuration(it) },
                onInterestChange = { viewModel.setItineraryInterest(it) },
                onGenerate = { viewModel.generateSmartItinerary() },
                onSave = { viewModel.saveItineraryToMyTrips(context) },
                onDismiss = { viewModel.setItineraryBuilderOpen(false) }
            )
        }

        selectedPlaceDetail?.let { place ->
            AttractionDetailDialog(
                place = place,
                onDismiss = { selectedPlaceDetail = null },
                onToggleSave = { viewModel.toggleSavePlace(place.placeId, context) },
                onOpenFullDetails = {
                    selectedPlaceDetail = null
                    onViewPlaceDetails(place)
                }
            )
        }

        if (uiState.isRouteSelectorOpen) {
            RouteSelectorDialog(
                routes = uiState.availableRoutes,
                selectedRouteId = uiState.selectedRouteId,
                onSelectRoute = { route -> viewModel.selectRoute(route) },
                onDismiss = { viewModel.setRouteSelectorOpen(false) }
            )
        }

        if (showFullRouteSheet) {
            FullRouteDialog(
                stations = uiState.stations,
                onDismiss = { showFullRouteSheet = false }
            )
        }

        if (showTransitHelpSheet) {
            TransitHelpDialog(
                onDismiss = { showTransitHelpSheet = false },
                onSimulateDelay = {
                    showTransitHelpSheet = false
                    onSimulateDelay()
                }
            )
        }
    }
}

// ----------------- SUB-COMPONENTS -----------------

@Composable
fun MapFilterChip(
    label: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) SkyBluePrimary else SurfaceWhite)
            .border(1.dp, if (selected) SkyBluePrimary else BorderLight, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = if (selected) Color.White else TextNavyDark,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun TouristCorridorCard(
    place: TouristPlace,
    onViewDetails: () -> Unit,
    onToggleSave: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(240.dp)
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Place Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(BorderSubtle)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(place.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Category Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(TextNavyDark.copy(alpha = 0.8f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = place.category,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Bookmark / Save Icon
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite.copy(alpha = 0.9f))
                        .clickable { onToggleSave() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (place.isSaved) "❤️" else "🤍", fontSize = 12.sp)
                }
            }

            Text(
                text = place.name,
                color = TextNavyDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // District & Location
            val locationText = if (!place.district.isNullOrBlank()) "${place.district} · ${place.city}" else place.city
            Text(
                text = "📍 $locationText",
                color = TextMutedSlate,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "⭐ ${place.rating}", color = WarmOrangeDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                val distStr = if (place.distanceKm < 0.5) "At Destination" else "${String.format(Locale.US, "%.1f", place.distanceKm)} km away"
                Text(text = distStr, color = TealSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            Text(
                text = place.description,
                color = TextNavyBody,
                fontSize = 10.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PastelCardBlue)
                        .clickable { onViewDetails() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Details", color = SkyBlueDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (place.isSaved) PastelCardTeal else TealSecondary)
                        .clickable { onToggleSave() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (place.isSaved) "Saved ✓" else "+ Add",
                        color = if (place.isSaved) TealDark else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RouteSelectorDialog(
    routes: List<TamilNaduCorridorRoute>,
    selectedRouteId: String,
    onSelectRoute: (TamilNaduCorridorRoute) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceWhite)
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Select Tamil Nadu Route",
                            color = TextNavyDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Dynamic transit corridors across all 38 districts",
                            color = TextMutedSlate,
                            fontSize = 11.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(BorderSubtle)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", fontSize = 12.sp, color = TextNavyDark)
                    }
                }

                HorizontalDivider(color = BorderLight)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    routes.forEach { route ->
                        val isSelected = route.id == selectedRouteId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) PastelCardTeal else SurfaceWhite)
                                .border(
                                    1.dp,
                                    if (isSelected) TealSecondary else BorderLight,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectRoute(route) }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = route.origin,
                                            color = TextNavyDark,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(text = " ➔ ", color = SkyBluePrimary, fontSize = 13.sp)
                                        Text(
                                            text = route.destination,
                                            color = TextNavyDark,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "${route.transitBadge} · ${route.originDistrict} to ${route.destinationDistrict}",
                                        color = TextMutedSlate,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isSelected) {
                                    Text(text = "✓ Active", color = TealDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Text(text = "Select →", color = SkyBluePrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------- DIALOGS & SHEETS -----------------

@Composable
fun AIAssistantDialog(
    chatMessages: List<AIAssistantMessage>,
    queryText: String,
    isThinking: Boolean,
    onQueryChange: (String) -> Unit,
    onSend: () -> Unit,
    onVoiceClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceWhite)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "MoveSmart AI Assistant",
                                color = TextNavyDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tamil Nadu transit & tourism help",
                                color = TealSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(BorderSubtle)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", fontSize = 12.sp, color = TextNavyDark)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)

                // Chat Messages List
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chatMessages.forEach { msg ->
                        val isUser = msg.sender == "user"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.82f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isUser) SkyBluePrimary else PastelCardTeal)
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = msg.text,
                                        color = if (isUser) Color.White else TextNavyDark,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = msg.timeText,
                                        color = if (isUser) Color.White.copy(alpha = 0.7f) else TextMutedSlate,
                                        fontSize = 9.sp,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    if (isThinking) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "MoveSmart AI is typing...", color = TextMutedSlate, fontSize = 11.sp)
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextField(
                        value = queryText,
                        onValueChange = onQueryChange,
                        placeholder = { Text("Ask about stations, spots, buses...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = BackgroundSoftCream,
                            unfocusedContainerColor = BackgroundSoftCream,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SkyBlueLight)
                            .clickable { onVoiceClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🎙️", fontSize = 16.sp)
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SkyBluePrimary)
                            .clickable { onSend() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "➔", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsSettingsDialog(
    uiState: OnYourWayUiState,
    onToggle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceWhite)
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Smart Journey Alerts",
                    color = TextNavyDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                NotificationToggleRow(
                    title = "Upcoming Station Alert",
                    subtitle = "Alert 1 station before destination / interchange",
                    checked = uiState.notifyUpcomingStation,
                    onCheckedChange = { onToggle("station") }
                )

                NotificationToggleRow(
                    title = "Destination Arrival",
                    subtitle = "Ring when within 400m of final stop",
                    checked = uiState.notifyDestinationApproaching,
                    onCheckedChange = { onToggle("destination") }
                )

                NotificationToggleRow(
                    title = "Transit Delays & Disruptions",
                    subtitle = "Live CMRL / MTC alert notifications",
                    checked = uiState.notifyPossibleDelays,
                    onCheckedChange = { onToggle("delays") }
                )

                NotificationToggleRow(
                    title = "Attractions Along Route",
                    subtitle = "Notify when passing famous tourist landmarks",
                    checked = uiState.notifyNearbyAttractions,
                    onCheckedChange = { onToggle("attractions") }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SkyBluePrimary)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Save Preferences", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = TextMutedSlate, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = TealSecondary
            )
        )
    }
}

@Composable
fun ItineraryBuilderDialog(
    uiState: OnYourWayUiState,
    onDurationChange: (String) -> Unit,
    onInterestChange: (String) -> Unit,
    onGenerate: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceWhite)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✨ AI Travel Itinerary Builder",
                        color = TextNavyDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(BorderSubtle)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", fontSize = 12.sp, color = TextNavyDark)
                    }
                }

                Text(
                    text = "Select your travel time & preferences to create a customized stop-by-stop schedule:",
                    color = TextMutedSlate,
                    fontSize = 12.sp
                )

                // Duration Selector
                Text(text = "Available Time", color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1 - 2 hours", "2 - 3 hours", "Full Day").forEach { dur ->
                        val isSelected = uiState.itineraryDurationSelected == dur
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SkyBluePrimary else BackgroundSoftCream)
                                .border(1.dp, if (isSelected) SkyBluePrimary else BorderLight, RoundedCornerShape(10.dp))
                                .clickable { onDurationChange(dur) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dur,
                                color = if (isSelected) Color.White else TextNavyDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Interest Selector
                Text(text = "Theme & Interests", color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "Beaches & Culture",
                        "Heritage & Temples",
                        "Nature & Wildlife",
                        "Food & Shopping"
                    ).forEach { interest ->
                        val isSelected = uiState.itineraryInterestSelected == interest
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PastelCardTeal else SurfaceWhite)
                                .border(
                                    1.dp,
                                    if (isSelected) TealSecondary else BorderLight,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onInterestChange(interest) }
                                .padding(10.dp)
                        ) {
                            Text(
                                text = interest,
                                color = if (isSelected) TealDark else TextNavyDark,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Generate Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SkyBluePrimary)
                        .clickable { onGenerate() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Generate Itinerary", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                // Render Generated Plan
                uiState.generatedPlan?.let { plan ->
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = BorderLight)
                    Text(
                        text = "Generated Day Plan:",
                        color = TextNavyDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    plan.forEach { stop ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = stop.time,
                                color = TealSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(62.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stop.title,
                                    color = TextNavyDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = stop.description, color = TextMutedSlate, fontSize = 11.sp)
                                Text(
                                    text = "Transit: ${stop.transitHint}",
                                    color = SkyBlueDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TealSecondary)
                            .clickable { onSave() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Save Itinerary to My Trips ✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AttractionDetailDialog(
    place: TouristPlace,
    onDismiss: () -> Unit,
    onToggleSave: () -> Unit,
    onOpenFullDetails: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceWhite)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BorderSubtle)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(place.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", fontSize = 12.sp, color = TextNavyDark)
                    }
                }

                Text(
                    text = place.name,
                    color = TextNavyDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                val locInfo = if (!place.district.isNullOrBlank()) "${place.district} · ${place.city}" else place.city
                Text(
                    text = "📍 $locInfo",
                    color = TextMutedSlate,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Category: ${place.category}", color = SkyBlueDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "⭐ ${place.rating}", color = WarmOrangeDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Text(text = place.description, color = TextNavyBody, fontSize = 12.sp)

                HorizontalDivider(color = BorderLight)

                Text(text = "Entry Fee: ${place.entryFee ?: "Free"}", color = TextMutedSlate, fontSize = 11.sp)
                Text(text = "Visit Duration: ${place.durationVisit ?: "2-3 hours"}", color = TextMutedSlate, fontSize = 11.sp)
                Text(text = "Transit Stop: ${place.nearbyTransit ?: place.locationAddress}", color = TealSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PastelCardBlue)
                            .clickable { onOpenFullDetails() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "View Details Page →", color = SkyBlueDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (place.isSaved) PastelCardTeal else TealSecondary)
                            .clickable { onToggleSave() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (place.isSaved) "Saved in Trips ✓" else "+ Add to Journey",
                            color = if (place.isSaved) TealDark else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FullRouteDialog(
    stations: List<MapStationPoint>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceWhite)
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Full Corridor Route",
                        color = TextNavyDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(BorderSubtle)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", fontSize = 11.sp, color = TextNavyDark)
                    }
                }

                Text(
                    text = "CMRL Blue Line Corridor (Chennai Central ➔ Marina)",
                    color = SkyBlueDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                stations.forEachIndexed { index, st ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = st.time,
                            color = TextMutedSlate,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(62.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(if (index <= 2) TealSecondary else BorderLight, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = st.name,
                            color = TextNavyDark,
                            fontSize = 12.sp,
                            fontWeight = if (index == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SkyBluePrimary)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Close", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TransitHelpDialog(
    onDismiss: () -> Unit,
    onSimulateDelay: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceWhite)
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Transit Assistance & Helplines",
                    color = TextNavyDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Need help or experiencing a service delay?",
                    color = TextMutedSlate,
                    fontSize = 12.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    HelpLineItem("CMRL Customer Care", "1800-425-1515")
                    HelpLineItem("MTC Bus Support", "044-23455801")
                    HelpLineItem("Southern Railway Assistance", "139")
                    HelpLineItem("Women Safety Helpline", "1091")
                }

                HorizontalDivider(color = BorderLight)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WarmOrangeLight)
                        .border(1.dp, WarmOrangeAccent, RoundedCornerShape(10.dp))
                        .clickable { onSimulateDelay() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚠️ Check Delay / Smart Reroute",
                        color = WarmOrangeDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BorderSubtle)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Done", color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HelpLineItem(title: String, number: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextNavyDark, fontSize = 12.sp)
        Text(text = number, color = SkyBlueDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
