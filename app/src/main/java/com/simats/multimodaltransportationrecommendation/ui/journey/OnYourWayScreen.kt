package com.simats.multimodaltransportationrecommendation.ui.journey

import android.app.Activity
import android.content.Intent
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.simats.multimodaltransportationrecommendation.ui.theme.*
import java.util.Locale

@Composable
fun OnYourWayScreen(
    viewModel: OnYourWayViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {},
    onViewTicket: () -> Unit = {},
    onSimulateDelay: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showFullRouteSheet by remember { mutableStateOf(false) }
    var showTransitHelpSheet by remember { mutableStateOf(false) }
    var selectedPlaceDetail by remember { mutableStateOf<RouteAttractionItem?>(null) }

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
                        Column {
                            Text(
                                text = "TAMIL NADU SMART TRANSIT",
                                color = SkyBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp
                            )
                            Text(
                                text = "Live Journey Tracking",
                                color = TextNavyDark,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                    val isLive = uiState.isLiveTrackingActive
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isLive) PastelCardTeal else PastelCardPeach)
                            .border(
                                1.dp,
                                if (isLive) TealSecondary.copy(alpha = 0.45f) else WarmOrangeAccent.copy(alpha = 0.45f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.toggleLiveTracking() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (isLive) TealSecondary.copy(alpha = pulseAlpha) else Color.Gray,
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isLive) "LIVE TRACKING" else "TRACKING PAUSED",
                                color = if (isLive) TealSecondary else WarmOrangeDark,
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

                    // Canvas Map
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(PastelCardBlue)
                            .border(1.dp, BorderLight, RoundedCornerShape(18.dp))
                            .padding(10.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val gridColor = SkyBluePrimary.copy(alpha = 0.12f)
                            val cols = 6
                            val rows = 5

                            val dx = w / cols
                            val dy = h / rows

                            for (i in 1 until cols) {
                                drawLine(gridColor, Offset(i * dx, 0f), Offset(i * dx, h), 1.dp.toPx())
                            }
                            for (j in 1 until rows) {
                                drawLine(gridColor, Offset(0f, j * dy), Offset(w, j * dy), 1.dp.toPx())
                            }

                            // Transit Route Line
                            val routePath = Path().apply {
                                moveTo(w * 0.15f, h * 0.75f)
                                lineTo(w * 0.35f, h * 0.62f)
                                lineTo(w * 0.55f, h * 0.50f)
                                lineTo(w * 0.72f, h * 0.35f)
                                lineTo(w * 0.88f, h * 0.22f)
                            }
                            drawPath(routePath, SkyBluePrimary, style = Stroke(width = 4.dp.toPx()))

                            // Station Markers along line
                            if (uiState.showStationsLayer) {
                                uiState.stations.forEach { st ->
                                    drawCircle(
                                        color = Color.White,
                                        radius = 6.dp.toPx(),
                                        center = Offset(w * st.xRatio, h * st.yRatio)
                                    )
                                    drawCircle(
                                        color = SkyBlueDark,
                                        radius = 3.5.dp.toPx(),
                                        center = Offset(w * st.xRatio, h * st.yRatio)
                                    )
                                }
                            }

                            // Attraction Points
                            if (uiState.showAttractionsLayer) {
                                uiState.attractionsOnMap.forEach { at ->
                                    drawCircle(
                                        color = WarmOrangeAccent.copy(alpha = 0.25f),
                                        radius = 9.dp.toPx(),
                                        center = Offset(w * at.xRatio, h * at.yRatio)
                                    )
                                    drawCircle(
                                        color = WarmOrangeAccent,
                                        radius = 4.5.dp.toPx(),
                                        center = Offset(w * at.xRatio, h * at.yRatio)
                                    )
                                }
                            }

                            // Pulsing Vehicle Location Marker
                            val vehicleCenter = Offset(w * 0.55f, h * 0.50f)
                            drawCircle(
                                color = SkyBluePrimary.copy(alpha = 0.35f * pulseAlpha),
                                radius = 22.dp.toPx(),
                                center = vehicleCenter
                            )
                            drawCircle(
                                color = SkyBluePrimary,
                                radius = 8.dp.toPx(),
                                center = vehicleCenter
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3.5.dp.toPx(),
                                center = vehicleCenter
                            )
                        }

                        // Route Label Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clip(RoundedCornerShape(6.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${uiState.origin} ➔ Marina",
                                color = TextNavyDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
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

                        // Open full chat button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PastelCardBlue)
                                .clickable { viewModel.openAssistant(true) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "💬 Open AI Travel Chatbot",
                                color = SkyBlueDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 7. AI TOURIST PLACES ALONG MY ROUTE
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Explore Places Along Route",
                                color = TextNavyDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Attractions along the Chennai transit corridor",
                                color = TextMutedSlate,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Category filter chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Beaches", "Museums", "Religious", "Nature").forEach { cat ->
                            val isSelected = uiState.selectedTouristCategory == cat
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
                                    text = cat,
                                    color = if (isSelected) Color.White else TextNavyDark,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Horizontal cards
                    val filteredPlaces = if (uiState.selectedTouristCategory == "All") {
                        uiState.placesAlongRoute
                    } else {
                        uiState.placesAlongRoute.filter { it.category == uiState.selectedTouristCategory }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        filteredPlaces.forEach { place ->
                            TouristCorridorCard(
                                place = place,
                                onViewDetails = { selectedPlaceDetail = place },
                                onToggleSave = { viewModel.toggleSavePlace(place.id) }
                            )
                        }
                    }
                }

                // 8. AI MULTI-MODAL JOURNEY SUGGESTIONS
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "AI Multi-Modal Suggestions",
                        color = TextNavyDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.journeySuggestions.forEach { option ->
                            AlternativeRouteCard(option = option)
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

                // View Digital Ticket
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(PrimaryGradient)
                        .clickable { onViewTicket() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "View Digital Ticket",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🎫", fontSize = 13.sp)
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
                onToggleSave = { viewModel.toggleSavePlace(place.id) }
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
    place: RouteAttractionItem,
    onViewDetails: () -> Unit,
    onToggleSave: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(230.dp)
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
                    .height(110.dp)
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
                        .background(TextNavyDark.copy(alpha = 0.75f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = place.category, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                // Bookmark Icon
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "⭐ ${place.rating}", color = WarmOrangeDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = place.distanceOffRoute, color = TealSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            Text(
                text = "Stop: ${place.nearbyStop}",
                color = TextMutedSlate,
                fontSize = 10.sp,
                maxLines = 1,
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
fun AlternativeRouteCard(option: AlternativeJourneyItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (option.isRecommended) PastelCardTeal else SurfaceWhite)
            .border(
                1.dp,
                if (option.isRecommended) TealSecondary.copy(alpha = 0.5f) else BorderLight,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = option.modeCombination,
                        color = TextNavyDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (option.isRecommended) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TealSecondary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "Current", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text(
                    text = option.fareText,
                    color = TealSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = option.transferPoints,
                color = TextMutedSlate,
                fontSize = 11.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⏱️ ${option.durationText} · ${option.transfers} transfers",
                    color = TextNavyDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "🌱 ${option.co2Saved} CO₂ saved",
                    color = EmeraldGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
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
    place: RouteAttractionItem,
    onDismiss: () -> Unit,
    onToggleSave: () -> Unit
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Category: ${place.category}", color = SkyBlueDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "⭐ ${place.rating}", color = WarmOrangeDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Text(text = place.description, color = TextNavyBody, fontSize = 12.sp)

                HorizontalDivider(color = BorderLight)

                Text(text = "Entry Fee: ${place.entryFee}", color = TextMutedSlate, fontSize = 11.sp)
                Text(text = "Visit Duration: ${place.visitDuration}", color = TextMutedSlate, fontSize = 11.sp)
                Text(text = "Transit Stop: ${place.nearbyStop}", color = TealSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BorderSubtle)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Close", color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (place.isSaved) PastelCardTeal else TealSecondary)
                            .clickable { onToggleSave() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (place.isSaved) "Saved in My Trips ✓" else "+ Add to My Trip",
                            color = if (place.isSaved) TealDark else Color.White,
                            fontSize = 12.sp,
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
