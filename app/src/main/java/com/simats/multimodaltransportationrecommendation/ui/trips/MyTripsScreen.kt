package com.simats.multimodaltransportationrecommendation.ui.trips

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem

@Composable
fun MyTripsScreen(
    viewModel: MyTripsViewModel = viewModel(),
    onOpenTicket: (TripItem) -> Unit = {},
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Main Top Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "19 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "My trips",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Refresh Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), CircleShape)
                            .clickable { viewModel.loadTrips(context) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔄", fontSize = 14.sp)
                    }
                }

                // 3-Tab Segment Selector (Upcoming, Completed, Cancelled)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Upcoming", "Completed", "Cancelled").forEach { tab ->
                        val isSelected = uiState.selectedTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF162238) else Color.Transparent)
                                .clickable { viewModel.selectTab(tab) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF8A99AD),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFC6FF00), modifier = Modifier.size(36.dp))
                    }
                } else {
                    when (uiState.selectedTab) {
                        "Upcoming" -> {
                            if (uiState.upcomingTrips.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(text = "🎫", fontSize = 36.sp)
                                        Text(text = "No upcoming journeys", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "Plan your next eco-friendly multi-modal ride now.",
                                            color = Color(0xFF8A99AD),
                                            fontSize = 11.sp
                                        )
                                        Button(
                                            onClick = onPlanNewJourney,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF00)),
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(text = "Plan New Journey", color = Color(0xFF070B14), fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                                    items(uiState.upcomingTrips) { trip ->
                                        UpcomingTripCard(
                                            trip = trip,
                                            onOpenTicket = { onOpenTicket(trip) },
                                            onCancel = { tripToCancel = trip },
                                            onComplete = { viewModel.completeTrip(context, trip.journeyId) }
                                        )
                                    }
                                }
                            }
                        }

                        "Completed" -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Impact summary card
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFF102015))
                                            .border(1.dp, Color(0xFF1E4D2B), RoundedCornerShape(14.dp))
                                            .padding(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "🌿", fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "${uiState.totalCo2SavedKg} kg CO₂ saved in total",
                                                    color = Color(0xFFC6FF00),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${uiState.completedTrips.size} low-carbon journeys completed",
                                                    color = Color(0xFF8A99AD),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                if (uiState.completedTrips.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 40.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "No completed trips yet.", color = Color(0xFF8A99AD), fontSize = 12.sp)
                                        }
                                    }
                                } else {
                                    items(uiState.completedTrips) { trip ->
                                        PastTripCard(trip = trip)
                                    }
                                }
                            }
                        }

                        "Cancelled" -> {
                            if (uiState.cancelledTrips.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "No cancelled journeys.", color = Color(0xFF8A99AD), fontSize = 12.sp)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(uiState.cancelledTrips) { trip ->
                                        CancelledTripCard(trip = trip)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf("🗺️\nExplore" to false, "🎫\nMy trips" to true, "🔖\nSaved" to false, "👤\nProfile" to false)
                tabs.forEach { (tabInfo, isActive) ->
                    val lines = tabInfo.split("\n")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onNavigateTab(lines[1]) }
                    ) {
                        Text(text = lines[0], fontSize = 16.sp)
                        Text(
                            text = lines[1],
                            color = if (isActive) Color(0xFFC6FF00) else Color(0xFF64748B),
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
                containerColor = Color(0xFF0F172A),
                title = {
                    Text(text = "Cancel Journey?", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(
                        text = "Are you sure you want to cancel journey ${trip.bookingReference} to ${trip.destination}?",
                        color = Color(0xFF8A99AD),
                        fontSize = 12.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.cancelTrip(context, trip.journeyId)
                            tripToCancel = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                    ) {
                        Text(text = "Cancel Journey", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { tripToCancel = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text(text = "Keep Journey", color = Color(0xFF8A99AD))
                    }
                }
            )
        }
    }
}

@Composable
private fun UpcomingTripCard(
    trip: TripItem,
    onOpenTicket: () -> Unit,
    onCancel: () -> Unit,
    onComplete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF112017))
            .border(1.5.dp, Color(0xFF1E4D2B), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1A3824))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "READY · ${trip.bookingReference}",
                        color = Color(0xFFC6FF00),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = trip.journeyDate,
                    color = Color(0xFF8A99AD),
                    fontSize = 11.sp
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).background(Color(0xFFC6FF00), CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = trip.origin, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(7.dp).background(Color(0xFF38BDF8), CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = trip.destination, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${trip.routeTitle} · ${trip.durationMinutes} min",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = trip.fareText,
                    color = Color(0xFFC6FF00),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenTicket,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp),
                    shape = RoundedCornerShape(19.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF00))
                ) {
                    Text(text = "Open ticket 🎫", color = Color(0xFF070B14), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(19.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text(text = "Complete", color = Color.White, fontSize = 10.sp)
                }

                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .weight(0.9f)
                        .height(38.dp),
                    shape = RoundedCornerShape(19.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D1619))
                ) {
                    Text(text = "Cancel", color = Color(0xFFFF5252), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PastTripCard(trip: TripItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.weight(1f)) {
            Text(text = trip.journeyDate.uppercase(), color = Color(0xFF8A99AD), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(text = trip.routeTitle, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = "${trip.origin} → ${trip.destination}", color = Color(0xFF8A99AD), fontSize = 10.sp)
            Text(text = "${trip.durationMinutes} min · ${trip.distanceKm} km · ${trip.co2SavedKg} kg CO₂ saved", color = Color(0xFF38BDF8), fontSize = 9.sp)
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF102015))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = "Completed", color = Color(0xFFC6FF00), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = trip.fareText, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CancelledTripCard(trip: TripItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0C101B))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.weight(1f)) {
            Text(text = trip.journeyDate.uppercase(), color = Color(0xFF64748B), fontSize = 9.sp)
            Text(text = trip.routeTitle, color = Color(0xFF8A99AD), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = "${trip.origin} → ${trip.destination}", color = Color(0xFF64748B), fontSize = 10.sp)
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF2D1619))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = "Cancelled", color = Color(0xFFFF5252), fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = trip.fareText, color = Color(0xFF64748B), fontSize = 12.sp)
        }
    }
}
