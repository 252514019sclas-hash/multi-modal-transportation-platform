package com.simats.multimodaltransportationrecommendation.ui.trips

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun MyTripsScreen(
    viewModel: MyTripsViewModel = viewModel(),
    onOpenTicket: (TripItem) -> Unit = {},
    onOpenTripDetails: (TripItem) -> Unit = {},
    onPlanNewJourney: () -> Unit = {},
    onNavigateTab: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var tripToCancel by remember { mutableStateOf<TripItem?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTrips(context)
    }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearStatusMessage()
        }
    }

    // Filter trips by search query
    fun filterTrips(list: List<TripItem>): List<TripItem> {
        val q = uiState.searchQuery.trim().lowercase()
        if (q.isBlank()) return list
        return list.filter {
            it.origin.lowercase().contains(q) ||
            it.destination.lowercase().contains(q) ||
            it.routeTitle.lowercase().contains(q) ||
            it.mode.lowercase().contains(q) ||
            it.bookingReference.lowercase().contains(q)
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
            // Main Top Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TAMIL NADU JOURNEYS",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "My Trips",
                            color = TextNavyDark,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // AI Assistant Chat trigger
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(PastelCardTeal)
                                .border(1.dp, TealLight, CircleShape)
                                .clickable { viewModel.setAiAssistantOpen(true) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🤖", fontSize = 16.sp)
                        }

                        // Refresh Button
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, CircleShape)
                                .clickable { viewModel.loadTrips(context) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔄", fontSize = 14.sp)
                        }
                    }
                }

                // Short Welcome Message
                Text(
                    text = "Plan, track, and manage your Tamil Nadu eco-transit and holiday journeys.",
                    color = TextMutedSlate,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Search trips and Add New Trip Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search Field
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        placeholder = { Text("Search by city, stop, or ref...", fontSize = 12.sp, color = TextMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceWhite,
                            unfocusedContainerColor = SurfaceWhite,
                            focusedBorderColor = SkyBluePrimary,
                            unfocusedBorderColor = BorderLight
                        ),
                        leadingIcon = { Text("🔍", fontSize = 13.sp) }
                    )

                    // Add New Trip Button
                    Box(
                        modifier = Modifier
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryGradient)
                            .clickable { viewModel.setAiPlannerOpen(true) }
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "+", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "AI Plan", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4-Tab Category Selector: Upcoming, Ongoing, Completed, Cancelled
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(
                        "Upcoming" to uiState.upcomingTrips.size,
                        "Ongoing" to uiState.ongoingTrips.size,
                        "Completed" to uiState.completedTrips.size,
                        "Cancelled" to uiState.cancelledTrips.size
                    ).forEach { (tab, count) ->
                        val isSelected = uiState.selectedTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PastelCardBlue else Color.Transparent)
                                .clickable { viewModel.selectTab(tab) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$tab ($count)",
                                color = if (isSelected) SkyBluePrimary else TextMuted,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List Content Area
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SkyBluePrimary, modifier = Modifier.size(36.dp))
                    }
                } else {
                    val currentList = when (uiState.selectedTab) {
                        "Upcoming" -> filterTrips(uiState.upcomingTrips)
                        "Ongoing" -> filterTrips(uiState.ongoingTrips)
                        "Completed" -> filterTrips(uiState.completedTrips)
                        "Cancelled" -> filterTrips(uiState.cancelledTrips)
                        else -> emptyList()
                    }

                    if (currentList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = when (uiState.selectedTab) {
                                        "Ongoing" -> "🚆"
                                        "Completed" -> "🌴"
                                        "Cancelled" -> "🏷️"
                                        else -> "🎫"
                                    },
                                    fontSize = 38.sp
                                )
                                Text(
                                    text = "No ${uiState.selectedTab.lowercase()} trips found",
                                    color = TextNavyDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (uiState.searchQuery.isNotBlank()) "Try modifying your search keywords." else "Use the AI Trip Planner to create your next route.",
                                    color = TextMutedSlate,
                                    fontSize = 12.sp
                                )
                                if (uiState.selectedTab == "Upcoming" || uiState.selectedTab == "Ongoing") {
                                    Button(
                                        onClick = { viewModel.setAiPlannerOpen(true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                                        shape = RoundedCornerShape(20.dp)
                                    ) {
                                        Text(text = "✨ Create AI Trip Plan", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(currentList) { trip ->
                                ComprehensiveTripCard(
                                    trip = trip,
                                    onViewDetails = { onOpenTripDetails(trip) },
                                    onViewTicket = { onOpenTicket(trip) },
                                    onCancel = { tripToCancel = trip },
                                    onComplete = { viewModel.completeTrip(context, trip.journeyId) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(10.dp)) }
                        }
                    }
                }
            }

            // Fixed Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight)
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    "🗺️\nExplore" to false,
                    "🎫\nMy trips" to true,
                    "🔖\nSaved" to false,
                    "👤\nProfile" to false
                ).forEach { (tabInfo, isActive) ->
                    val lines = tabInfo.split("\n")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onNavigateTab(lines[1]) }
                    ) {
                        Text(text = lines[0], fontSize = 16.sp)
                        Text(
                            text = lines[1],
                            color = if (isActive) SkyBluePrimary else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Cancel Confirmation Dialog
        tripToCancel?.let { trip ->
            AlertDialog(
                onDismissRequest = { tripToCancel = null },
                containerColor = SurfaceWhite,
                shape = RoundedCornerShape(18.dp),
                title = { Text(text = "Cancel Journey?", color = TextNavyDark, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        text = "Are you sure you want to cancel journey ${trip.bookingReference} to ${trip.destination}?",
                        color = TextNavyBody,
                        fontSize = 12.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.cancelTrip(context, trip.journeyId)
                            tripToCancel = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(text = "Confirm Cancel", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { tripToCancel = null }) {
                        Text(text = "Keep Journey", color = SkyBlueDark)
                    }
                }
            )
        }

        // AI Smart Trip Planner Dialog
        if (uiState.showAiPlanner) {
            AiSmartTripPlannerDialog(
                uiState = uiState,
                onOriginChange = { viewModel.onPlannerOriginChange(it) },
                onDestChange = { viewModel.onPlannerDestinationChange(it) },
                onDateChange = { viewModel.onPlannerDateChange(it) },
                onDurationChange = { viewModel.onPlannerDurationChange(it) },
                onInterestsChange = { viewModel.onPlannerInterestsChange(it) },
                onToggleMode = { viewModel.togglePlannerMode(it) },
                onGenerate = { viewModel.generateAiPlan() },
                onSave = { viewModel.saveAiPlanToBackend(context) },
                onDismiss = { viewModel.setAiPlannerOpen(false) }
            )
        }

        // AI Itinerary Assistant Dialog
        if (uiState.showAiAssistant) {
            AiItineraryAssistantDialog(
                uiState = uiState,
                onQueryChange = { viewModel.onAssistantQueryChange(it) },
                onSend = { viewModel.sendAssistantQuery() },
                onQuickPrompt = { viewModel.sendAssistantQuery(it) },
                onDismiss = { viewModel.setAiAssistantOpen(false) }
            )
        }
    }
}

/**
 * Comprehensive Trip Card matching MoveSmart AI standards:
 * Origin, Destination, Travel Date, Starting Time, Selected Transport Modes,
 * Trip Status, Estimated Duration, Tourist Destination image where applicable,
 * and View Details action button.
 */
@Composable
private fun ComprehensiveTripCard(
    trip: TripItem,
    onViewDetails: () -> Unit,
    onViewTicket: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onViewDetails() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Mode & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val modeIcon = when {
                        trip.mode.contains("Metro", ignoreCase = true) -> "🚇"
                        trip.mode.contains("Bus", ignoreCase = true) -> "🚌"
                        trip.mode.contains("Rail", ignoreCase = true) || trip.mode.contains("Train", ignoreCase = true) -> "🚆"
                        else -> "🚶"
                    }
                    Text(text = modeIcon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = trip.mode,
                        color = SkyBlueDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                val statusColor = when (trip.status.lowercase()) {
                    "ongoing" -> TealDark
                    "completed" -> EmeraldGreen
                    "cancelled" -> Color(0xFFDC2626)
                    else -> SkyBluePrimary
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = trip.status.uppercase(),
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Route & Thumbnail Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "From: ${trip.origin}", color = TextNavyBody, fontSize = 12.sp)
                    Text(
                        text = "To: ${trip.destination}",
                        color = TextNavyDark,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Tourist Destination Thumbnail image where applicable
                val destinationThumb = when {
                    trip.destination.contains("Beach", ignoreCase = true) -> "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=400&q=75"
                    trip.destination.contains("Temple", ignoreCase = true) -> "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?auto=format&fit=crop&w=400&q=75"
                    trip.destination.contains("Museum", ignoreCase = true) -> "https://images.unsplash.com/photo-1566127444979-b3d2b654e3d7?auto=format&fit=crop&w=400&q=75"
                    else -> null
                }
                destinationThumb?.let { imgUrl ->
                    AsyncImage(
                        model = imgUrl,
                        contentDescription = trip.destination,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Date, Time, Duration & Fare Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 ${trip.journeyDate} · ⏱️ ${trip.durationMinutes} min",
                    color = TextMutedSlate,
                    fontSize = 11.sp
                )
                Text(
                    text = trip.fareText.ifBlank { "₹${trip.fare.toInt()}" },
                    color = TealDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = BorderLight)

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // View Details Button
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PastelCardBlue)
                        .clickable { onViewDetails() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "View Details 📋", color = SkyBlueDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // View Ticket Button
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGradient)
                        .clickable { onViewTicket() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "View Ticket 🎫", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Cancel Button (for upcoming or ongoing)
                if (trip.status.equals("upcoming", ignoreCase = true) || trip.status.equals("ongoing", ignoreCase = true)) {
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEE2E2))
                            .clickable { onCancel() }
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Cancel", color = Color(0xFFDC2626), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * AI SMART TRIP PLANNER DIALOG
 * Allows entering Starting location, Destination, Travel Date, Available time,
 * Preferred transport modes, and Tourist interests.
 * Employs SmartAiTransitEngine to generate Multi-Modal Route Optimizer comparison,
 * Tourist Recommendations, Travel Schedule, and Budget Estimator.
 */
@Composable
fun AiSmartTripPlannerDialog(
    uiState: MyTripsUiState,
    onOriginChange: (String) -> Unit,
    onDestChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onInterestsChange: (String) -> Unit,
    onToggleMode: (String) -> Unit,
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
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "AI Smart Trip Planner", color = TextNavyDark, fontSize = 17.sp, fontWeight = FontWeight.Bold)
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

                Text(
                    text = "Generate optimized multi-modal schedules and attraction visits tailored to your corridor.",
                    color = TextMutedSlate,
                    fontSize = 11.5.sp
                )

                // Starting Location
                OutlinedTextField(
                    value = uiState.plannerOrigin,
                    onValueChange = onOriginChange,
                    label = { Text("Starting Location", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                // Destination
                OutlinedTextField(
                    value = uiState.plannerDestination,
                    onValueChange = onDestChange,
                    label = { Text("Destination", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                // Travel Date & Starting Time
                OutlinedTextField(
                    value = uiState.plannerDate,
                    onValueChange = onDateChange,
                    label = { Text("Travel Date & Starting Time", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                // Available Time duration chips
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Available Time Duration:", fontSize = 11.sp, color = TextNavyBody, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("1-Hour Express", "Half-day (3-4 hrs)", "Full Day").forEach { dur ->
                            val isSel = uiState.plannerDuration == dur
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PastelCardTeal else BorderSubtle)
                                    .border(1.dp, if (isSel) TealSecondary else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { onDurationChange(dur) }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(text = dur, fontSize = 10.sp, color = if (isSel) TealDark else TextNavyDark, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                // Preferred Transport Modes
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Preferred Transport Modes:", fontSize = 11.sp, color = TextNavyBody, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Metro", "Bus", "Train", "Walking").forEach { mode ->
                            val isSel = uiState.plannerModes.contains(mode)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PastelCardBlue else BorderSubtle)
                                    .border(1.dp, if (isSel) SkyBluePrimary else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { onToggleMode(mode) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = mode, fontSize = 11.sp, color = if (isSel) SkyBlueDark else TextNavyDark, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                // Tourist Interests
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Tourist Interests:", fontSize = 11.sp, color = TextNavyBody, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Temples", "Beaches", "Heritage", "Nature", "Museums").forEach { intTag ->
                            val isSel = uiState.plannerInterests.contains(intTag)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) PastelCardPeach else BorderSubtle)
                                    .border(1.dp, if (isSel) WarmOrangeAccent else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { onInterestsChange(intTag) }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(text = intTag, fontSize = 10.sp, color = if (isSel) WarmOrangeAccent else TextNavyDark, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                // Generate Plan Button
                Button(
                    onClick = onGenerate,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Synthesize AI Travel Plan ✨", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Generated Plan Display
                uiState.generatedPlan?.let { plan ->
                    HorizontalDivider(color = BorderLight)

                    // 1. Multi-Modal Route Optimizer comparison
                    Text(text = "🔄 AI Multi-Modal Route Optimizer", color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        plan.multiModalCombinations.forEach { opt ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PastelCardBlue)
                                    .border(1.dp, SkyBluePrimary.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = opt.combination, color = SkyBlueDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        opt.recommendationTag?.let { tag ->
                                            Text(text = tag, color = TealDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "⏱️ ${opt.durationMinutes} min · ${opt.transfers} transfers", color = TextNavyBody, fontSize = 10.sp)
                                        Text(text = "Fare: ${opt.estimatedFare}", color = TextNavyDark, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }

                    // 2. AI Travel Budget Estimator
                    uiState.budgetBreakdown?.let { b ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(PastelCardTeal)
                                .border(1.dp, TealLight, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = "💰 AI Estimated Trip Budget", color = TealDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = b.totalEstimatedRange, color = TealDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(text = "• Transit: ${b.transitEstimate} · Entry: ${b.attractionEntryEstimate}", color = TextNavyBody, fontSize = 10.sp)
                                Text(text = "• Refreshments: ${b.refreshmentsEstimate} (${b.savingsVsCab})", color = TextNavyBody, fontSize = 10.sp)
                                Text(text = "⚠️ ${b.disclaimer}", color = TextMutedSlate, fontSize = 9.sp, lineHeight = 12.sp)
                            }
                        }
                    }

                    // 3. AI Travel Schedule Assistant
                    Text(text = "🕒 Suggested Travel Schedule", color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        plan.schedule.forEach { s ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(text = s.time, color = SkyBluePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
                                Column {
                                    Text(text = s.title, color = TextNavyDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = s.desc, color = TextMutedSlate, fontSize = 9.5.sp)
                                }
                            }
                        }
                    }

                    // Save to My Trips button
                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Save Itinerary to My Trips 💾", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

/**
 * AI ITINERARY ASSISTANT DIALOG (Chat & Questions)
 * Answers user questions:
 * - Which places can I visit during this trip?
 * - Can I find a different route?
 * - What transport options are available?
 * - Can you organize my tourist stops?
 */
@Composable
fun AiItineraryAssistantDialog(
    uiState: MyTripsUiState,
    onQueryChange: (String) -> Unit,
    onSend: () -> Unit,
    onQuickPrompt: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
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
                        Text(text = "🤖", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "AI Itinerary Assistant", color = TextNavyDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = "MoveSmart transit & tourist advisor", color = TextMutedSlate, fontSize = 10.sp)
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

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Messages
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.assistantChat) { (sender, msg) ->
                        val isUser = sender == "You"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isUser) PastelCardBlue else BackgroundSoftCream)
                                    .border(1.dp, if (isUser) SkyBluePrimary.copy(alpha = 0.3f) else BorderLight, RoundedCornerShape(12.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = sender,
                                        color = if (isUser) SkyBlueDark else TealDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = msg,
                                        color = TextNavyDark,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Prompt Suggestion Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(listOf(
                            "Which places can I visit during this trip?",
                            "Can I find a different route?",
                            "What transport options are available?",
                            "Can you organize my tourist stops?"
                        )) { prompt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(PastelCardTeal)
                                    .border(1.dp, TealLight, RoundedCornerShape(14.dp))
                                    .clickable { onQuickPrompt(prompt) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(text = prompt, color = TealDark, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.assistantQuery,
                            onValueChange = onQueryChange,
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Ask about routes, stops, places...", fontSize = 11.sp, color = TextMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryGradient)
                                .clickable { onSend() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "➤", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
