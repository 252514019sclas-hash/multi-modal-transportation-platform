package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.simats.multimodaltransportationrecommendation.ui.journey.CorridorStopItem
import com.simats.multimodaltransportationrecommendation.ui.journey.TamilNaduTransitResolver
import com.simats.multimodaltransportationrecommendation.ui.journey.TamilNaduTravelRoute
import com.simats.multimodaltransportationrecommendation.ui.map.MapMarkerItem
import com.simats.multimodaltransportationrecommendation.ui.map.MoveSmartGoogleMapView
import com.simats.multimodaltransportationrecommendation.ui.notifications.NotificationHelper
import com.simats.multimodaltransportationrecommendation.ui.theme.*

/**
 * Modern Transit Route & Intermediate Stations Timeline Screen
 * Provides authentic scheduled stops, dynamic departure times entered by user,
 * waypoint reached tracking, interactive map exploration, and notification settings
 * for each stop arrival and final stopping arrival.
 */
@Composable
fun GroupLiveMapScreen(
    viewModel: GroupTravelViewModel,
    groupId: Int,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var isSearchModalOpen by remember { mutableStateOf(false) }
    var showFullMapView by remember { mutableStateOf(false) }
    var showDepartureTimeDialog by remember { mutableStateOf(false) }
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }

    // Initialize with active group destination & start point and user-entered departure time
    LaunchedEffect(groupId, uiState.activeGroup) {
        val dest = uiState.activeGroup?.destination?.ifBlank { null }
            ?: uiState.routeDestQuery.ifBlank { null }
            ?: "Coimbatore"
        val orig = uiState.activeGroup?.commonStartLocation?.ifBlank { null }
            ?: uiState.activeGroup?.userMembership?.startingLocation?.ifBlank { null }
            ?: uiState.routeOriginQuery.ifBlank { null }
            ?: "Kanchipuram"
        val depTime = uiState.activeGroup?.travelTime?.ifBlank { null }
            ?: uiState.userEnteredDepartureTime
        viewModel.searchTravelRoute(orig, dest, depTime)
    }

    val currentDepTime = uiState.activeGroup?.travelTime?.ifBlank { null } ?: uiState.userEnteredDepartureTime
    val activeRoute = uiState.activeTravelRoute
        ?: TamilNaduTransitResolver.resolveRoute(
            uiState.routeOriginQuery.ifBlank { "Kanchipuram" },
            uiState.routeDestQuery.ifBlank { "Coimbatore" },
            currentDepTime
        )

    // Route Search Dialog across all of Tamil Nadu
    if (isSearchModalOpen) {
        TamilNaduRouteSearchDialog(
            currentOrigin = activeRoute.origin,
            currentDestination = activeRoute.destination,
            onDismiss = { isSearchModalOpen = false },
            onApplyRoute = { orig, dest ->
                viewModel.searchTravelRoute(orig, dest, activeRoute.departureTime)
                isSearchModalOpen = false
            }
        )
    }

    // Dynamic Departure Time Selection Dialog
    if (showDepartureTimeDialog) {
        DepartureTimeSelectionDialog(
            currentTime = activeRoute.departureTime,
            origin = activeRoute.origin,
            destination = activeRoute.destination,
            onDismiss = { showDepartureTimeDialog = false },
            onConfirm = { newTime ->
                viewModel.updateRouteDepartureTime(newTime)
                showDepartureTimeDialog = false
            }
        )
    }

    // Transit & Stop Arrival Notification Settings Dialog
    if (showNotificationSettingsDialog) {
        TransitNotificationSettingsDialog(
            context = context,
            route = activeRoute,
            onDismiss = { showNotificationSettingsDialog = false },
            onTestStop = { viewModel.testStopNotification(context) },
            onTestDestination = { viewModel.testDestinationNotification(context) }
        )
    }

    // Full Interactive Google Map Dialog View when user clicks "View Map"
    if (showFullMapView) {
        Dialog(
            onDismissRequest = { showFullMapView = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                val isDestReachedOnMap = uiState.destinationReached || uiState.reachedStopIds.contains("final_destination")

                // Collect all markers: Origin, Stops (with reached status), Destination, and Live Member GPS
                val mapMarkers = remember(activeRoute, uiState.reachedStopIds, uiState.isSharingActive, uiState.userGpsLatLng, uiState.liveLocations, isDestReachedOnMap) {
                    val markers = mutableListOf<MapMarkerItem>()

                    // 1. Origin Marker
                    markers.add(
                        MapMarkerItem(
                            id = "origin_stop",
                            title = "🏁 Start: ${activeRoute.origin}",
                            snippet = "Departure: ${activeRoute.departureTime}",
                            latitude = activeRoute.originLatLng.latitude,
                            longitude = activeRoute.originLatLng.longitude,
                            iconColorHue = BitmapDescriptorFactory.HUE_AZURE
                        )
                    )

                    // 2. Intermediate Corridor Stops
                    activeRoute.intermediateStops.forEach { stop ->
                        val isReached = uiState.reachedStopIds.contains(stop.id)
                        markers.add(
                            MapMarkerItem(
                                id = stop.id,
                                title = if (isReached) "✓ ${stop.name} (Reached)" else "🚉 ${stop.name}",
                                snippet = "Arr: ${stop.arrivalTime} | Dep: ${stop.departureTime} · ${stop.distanceFromOriginKm} km" +
                                        if (isReached) " · Reached ✓" else "",
                                latitude = stop.latitude,
                                longitude = stop.longitude,
                                iconColorHue = if (isReached) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_VIOLET,
                                extraData = stop
                            )
                        )
                    }

                    // 3. Destination Marker
                    markers.add(
                        MapMarkerItem(
                            id = "dest_stop",
                            title = if (isDestReachedOnMap) "✓ Destination Reached: ${activeRoute.destination}" else "🎯 Destination: ${activeRoute.destination}",
                            snippet = "Expected Arrival: ${activeRoute.arrivalTime} (${activeRoute.totalDistanceKm.toInt()} km)" +
                                    if (isDestReachedOnMap) " · Completed ✓" else "",
                            latitude = activeRoute.destinationLatLng.latitude,
                            longitude = activeRoute.destinationLatLng.longitude,
                            iconColorHue = if (isDestReachedOnMap) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_RED
                        )
                    )

                    // 4. Live User GPS / Group Member Markers
                    if (uiState.isSharingActive || (uiState.activeGroup?.sharingCount ?: 0) > 0) {
                        val selfLoc = uiState.userGpsLatLng ?: LatLng(12.8342, 79.7036) // Fallback
                        val userName = uiState.activeGroup?.creatorName?.ifBlank { null }
                            ?: uiState.activeGroup?.approvedMembers?.firstOrNull()?.fullName
                            ?: "laxmi"
                        markers.add(
                            MapMarkerItem(
                                id = "self_live_location",
                                title = "🟢 $userName (Live GPS)",
                                snippet = "Live location active · Starting from ${activeRoute.origin}",
                                latitude = selfLoc.latitude,
                                longitude = selfLoc.longitude,
                                iconColorHue = BitmapDescriptorFactory.HUE_CYAN
                            )
                        )

                        uiState.liveLocations.filter { !it.isSelf && it.isSharing }.forEach { mem ->
                            val mLat = mem.latitude ?: mem.startingLat
                            val mLng = mem.longitude ?: mem.startingLng
                            markers.add(
                                MapMarkerItem(
                                    id = "mem_${mem.userId}",
                                    title = "🟢 ${mem.fullName} (Live)",
                                    snippet = "Starting: ${mem.startingLocation} · Live Sharing",
                                    latitude = mLat,
                                    longitude = mLng,
                                    iconColorHue = BitmapDescriptorFactory.HUE_ORANGE
                                )
                            )
                        }
                    }

                    markers
                }

                MoveSmartGoogleMapView(
                    modifier = Modifier.fillMaxSize(),
                    originLocation = activeRoute.originLatLng,
                    destinationLocation = activeRoute.destinationLatLng,
                    routeWaypoints = activeRoute.routePolyline,
                    additionalMarkers = mapMarkers,
                    triggerFitAll = System.currentTimeMillis()
                )

                // Top Navigation Bar on Map View
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { showFullMapView = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = Color(0xFF1E1B4B), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { showNotificationSettingsDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔔", fontSize = 18.sp)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF6D28D9))
                                .clickable { showFullMapView = false }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "📋 Stops Timeline",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Interactive Bottom Overlay Sheet on Map View
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .shadow(12.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val reachedCount = activeRoute.intermediateStops.count { uiState.reachedStopIds.contains(it.id) }
                        val totalCount = activeRoute.intermediateStops.size

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${activeRoute.origin} ➔ ${activeRoute.destination}",
                                    color = Color(0xFF1E1B4B),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Dep: ${activeRoute.departureTime} · Arr: ${activeRoute.arrivalTime} (${activeRoute.totalDistanceKm.toInt()} km)",
                                    color = Color(0xFF6D28D9),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF0FDF4))
                                    .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "✓ $reachedCount/$totalCount Reached",
                                    color = Color(0xFF059669),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Horizontal Scrollable Stops for Rapid Reached Marking & Alert Trigger
                        Text(
                            text = "TAP STOP TO MARK REACHED & SEND NOTIFICATION:",
                            color = Color(0xFF64748B),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(activeRoute.intermediateStops) { stop ->
                                val isReached = uiState.reachedStopIds.contains(stop.id)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isReached) Color(0xFF10B981) else Color(0xFFF3E8FF))
                                        .border(
                                            1.dp,
                                            if (isReached) Color(0xFF059669) else Color(0xFFDDD6FE),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { viewModel.toggleStopReached(context, stop.id, false) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if (isReached) "✓ ${stop.name}" else stop.name,
                                            color = if (isReached) Color.White else Color(0xFF1E1B4B),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Arr: ${stop.arrivalTime}",
                                            color = if (isReached) Color(0xFFDCFCE7) else Color(0xFF6D28D9),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            // Final Destination Stopping in Horizontal Row
                            item {
                                val isDestReached = uiState.destinationReached || uiState.reachedStopIds.contains("final_destination")
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isDestReached) Color(0xFF10B981) else Color(0xFFEDE9FE))
                                        .border(
                                            1.dp,
                                            if (isDestReached) Color(0xFF059669) else Color(0xFF8B5CF6),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { viewModel.toggleStopReached(context, "final_destination", true) }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if (isDestReached) "✓ ${activeRoute.destination} (Final)" else "🏁 ${activeRoute.destination} (Final)",
                                            color = if (isDestReached) Color.White else Color(0xFF5B21B6),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Arr: ${activeRoute.arrivalTime}",
                                            color = if (isDestReached) Color(0xFFDCFCE7) else Color(0xFF6D28D9),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Bottom Actions: Open in Google Maps & Notification Settings
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse(
                                        "https://www.google.com/maps/dir/?api=1&origin=${activeRoute.originLatLng.latitude},${activeRoute.originLatLng.longitude}&destination=${activeRoute.destinationLatLng.latitude},${activeRoute.destinationLatLng.longitude}&travelmode=transit"
                                    )
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (_: Exception) {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
                                    }
                                },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text(
                                    text = "🧭 Google Maps",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { showNotificationSettingsDialog = true },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6D28D9))
                            ) {
                                Text(
                                    text = "🔔 Alerts",
                                    color = Color(0xFF6D28D9),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFFAF5FF), // Soft background tint
        bottomBar = {
            // Bottom Summary & View Map Action Footer
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)),
                shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Metrics Row: Total Distance & Expected Arrival
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Total Distance
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🛣️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Total Distance",
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${activeRoute.totalDistanceKm.toInt()} km",
                                    color = Color(0xFF1E1B4B),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Vertical Separator
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(Color(0xFFE2E8F0))
                        )

                        // Expected Arrival Time
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showDepartureTimeDialog = true }
                        ) {
                            Text(text = "🕒", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Expected Arrival",
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = activeRoute.arrivalTime,
                                    color = Color(0xFF1E1B4B),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // View Map Outlined Button
                    Button(
                        onClick = { showFullMapView = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF6D28D9)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📍", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "View Map",
                                color = Color(0xFF6D28D9),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Top App Bar Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = Color(0xFF1E1B4B), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    // Centered Route Title & Updates Status
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isSearchModalOpen = true }
                            .padding(horizontal = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = activeRoute.origin,
                                color = Color(0xFF1E1B4B),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "➔", color = Color(0xFF6D28D9), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = activeRoute.destination,
                                color = Color(0xFF1E1B4B),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Live Journey Updates",
                                color = Color(0xFF64748B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Notification Settings Bell & Route Change Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                                .clickable { showNotificationSettingsDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔔", fontSize = 16.sp)
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                                .clickable { isSearchModalOpen = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔄", fontSize = 16.sp)
                        }
                    }
                }
            }

            // 2. Scenic Tamil Nadu Heritage & Transit Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))
                        )
                    )
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(activeRoute.bannerImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Scenic Tamil Nadu Train Corridor",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle Bottom Gradient Fade
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x55000000), Color(0x99000000))
                            )
                        )
                )

                // Scenic Location Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏛️ Tamil Nadu Scenic Transit Corridor",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 3. Service Details Info Card (Soft Purple Lavender)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Col 1: Train/Service
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF6D28D9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🚆", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = activeRoute.serviceNumber,
                                    color = Color(0xFF1E1B4B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = activeRoute.serviceName,
                                    color = Color(0xFF6D28D9),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(Color(0xFFDDD6FE))
                        )

                        // Col 2: Travel Time
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(0.9f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🕒", fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = "Travel Time", color = Color(0xFF64748B), fontSize = 10.sp)
                            }
                            Text(
                                text = activeRoute.totalDurationText,
                                color = Color(0xFF1E1B4B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .height(32.dp)
                                .width(1.dp)
                                .background(Color(0xFFDDD6FE))
                        )

                        // Col 3: Departure (Interactive & Dynamic)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(0.9f)
                                .clickable { showDepartureTimeDialog = true }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📅", fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = "Departure", color = Color(0xFF64748B), fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = "✏️", fontSize = 8.sp)
                            }
                            Text(
                                text = activeRoute.departureTime,
                                color = Color(0xFF6D28D9),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Notification Status Quick Banner above Stops Timeline
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text(text = "🔔", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Stop & Destination Arrival Alerts: Active",
                                color = Color(0xFF065F46),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Notifications fire for each scheduled stop & final stopping",
                                color = Color(0xFF047857),
                                fontSize = 9.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF10B981))
                            .clickable { showNotificationSettingsDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Settings ⚙️",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 4. Vertical Stops & Timeline Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                // START STATION ROW
                TimelineStartCard(
                    stationName = activeRoute.origin,
                    departureTime = activeRoute.departureTime,
                    onEditTime = { showDepartureTimeDialog = true }
                )

                // INTERMEDIATE STATIONS (Connected by vertical line with node checkmarks)
                activeRoute.intermediateStops.forEachIndexed { index, stop ->
                    val isReached = uiState.reachedStopIds.contains(stop.id)
                    TimelineIntermediateRow(
                        stop = stop,
                        isReached = isReached,
                        isLast = (index == activeRoute.intermediateStops.size - 1),
                        onToggleReached = {
                            viewModel.toggleStopReached(context, stop.id, false)
                        }
                    )
                }

                // DESTINATION STATION ROW (With Reached Marker & Alert Support)
                val isDestReached = uiState.destinationReached || uiState.reachedStopIds.contains("final_destination")
                TimelineDestinationCard(
                    stationName = activeRoute.destination,
                    arrivalTime = activeRoute.arrivalTime,
                    isReached = isDestReached,
                    onToggleReached = {
                        viewModel.toggleStopReached(context, "final_destination", true)
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Start Node Card with Purple Theme & Start Badge
 */
@Composable
private fun TimelineStartCard(
    stationName: String,
    departureTime: String,
    onEditTime: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Node Icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF6D28D9)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🚆", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Start Card Container
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.clickable { onEditTime() }) {
                    Text(
                        text = stationName,
                        color = Color(0xFF1E1B4B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Departure : $departureTime",
                            color = Color(0xFF6D28D9),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "✏️", fontSize = 9.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF6D28D9))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏁 Start",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Intermediate Stop Row with Connected Timeline Line, Reached Checkmark & Status Badge
 */
@Composable
private fun TimelineIntermediateRow(
    stop: CorridorStopItem,
    isReached: Boolean,
    isLast: Boolean,
    onToggleReached: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vertical Timeline Track on the Left
        Box(
            modifier = Modifier
                .width(36.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            // Continuous Vertical Line (Green if reached, else Purple)
            Canvas(modifier = Modifier.fillMaxHeight().width(2.5.dp)) {
                drawLine(
                    color = if (isReached) Color(0xFF10B981) else Color(0xFF7C3AED),
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = 2.5.dp.toPx()
                )
            }

            // Circle Node: Emerald Green with Checkmark if Reached, else Hollow Purple Circle
            if (isReached) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981))
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.5.dp, Color(0xFF7C3AED), CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Station Card with Reached State & Interactive Toggle
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isReached) Color(0xFFF0FDF4) else Color.White
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isReached) Color(0xFFA7F3D0) else Color(0xFFE2E8F0)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stop.name,
                        color = Color(0xFF1E1B4B),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Arr : ${stop.arrivalTime}  |  Dep : ${stop.departureTime}",
                        color = if (isReached) Color(0xFF047857) else Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Interactive Reached Status Pill / Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isReached) Color(0xFF10B981) else Color(0xFFF3E8FF))
                        .border(
                            1.dp,
                            if (isReached) Color(0xFF059669) else Color(0xFFDDD6FE),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onToggleReached() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isReached) "✓ Reached" else "⚪ Mark Reached",
                        color = if (isReached) Color.White else Color(0xFF6D28D9),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📍", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${stop.distanceFromOriginKm.toInt()} km",
                        color = Color(0xFF1E1B4B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Destination Node Card with Reached Marker & Final Destination Arrival Alert
 */
@Composable
private fun TimelineDestinationCard(
    stationName: String,
    arrivalTime: String,
    isReached: Boolean,
    onToggleReached: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Destination Icon Node: Emerald Green with Checkmark if Reached, else Purple
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isReached) Color(0xFF10B981) else Color(0xFF6D28D9)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (isReached) "✓" else "🎯", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Destination Card Container
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isReached) Color(0xFFF0FDF4) else Color(0xFFF3E8FF)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isReached) Color(0xFFA7F3D0) else Color(0xFFE9D5FF)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stationName,
                        color = Color(0xFF1E1B4B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Arrival : $arrivalTime",
                        color = if (isReached) Color(0xFF047857) else Color(0xFF6D28D9),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Mark Reached button for Final Stopping
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isReached) Color(0xFF10B981) else Color.White)
                            .border(
                                1.dp,
                                if (isReached) Color(0xFF059669) else Color(0xFF8B5CF6),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onToggleReached() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isReached) "✓ Reached" else "⚪ Mark Reached",
                            color = if (isReached) Color.White else Color(0xFF6D28D9),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isReached) Color(0xFF059669) else Color(0xFF6D28D9))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isReached) "🎉 Arrived" else "🏁 Destination",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Transit & Stop Arrival Notification Settings Dialog
 */
@Composable
private fun TransitNotificationSettingsDialog(
    context: Context,
    route: TamilNaduTravelRoute,
    onDismiss: () -> Unit,
    onTestStop: () -> Unit,
    onTestDestination: () -> Unit
) {
    var notifyStops by remember { mutableStateOf(NotificationHelper.isStopArrivalNotificationEnabled(context)) }
    var notifyDest by remember { mutableStateOf(NotificationHelper.isDestinationNotificationEnabled(context)) }
    var soundVibrate by remember { mutableStateOf(NotificationHelper.isSoundVibrateEnabled(context)) }
    var statusBar by remember { mutableStateOf(NotificationHelper.isStatusBarNotificationEnabled(context)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, "Notification permission granted!", Toast.LENGTH_SHORT).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
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
                        Text(text = "🔔", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Notification Settings",
                            color = Color(0xFF1E1B4B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "✕",
                        color = Color(0xFF64748B),
                        fontSize = 16.sp,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Text(
                    text = "Configure push notifications to be alerted whenever traveler reaches each intermediate stop and the final destination.",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                // Android 13+ Runtime Permission Notice
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPerm = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!hasPerm) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚠️ Notification permission needed",
                                    color = Color(0xFF92400E),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Button(
                                    onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Allow", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Toggle 1: Notify on Each Stop
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🚉 Notify on Each Stop",
                                color = Color(0xFF1E1B4B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Receive alert when person reaches each intermediate stop along route",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                        Switch(
                            checked = notifyStops,
                            onCheckedChange = {
                                notifyStops = it
                                NotificationHelper.setStopArrivalNotificationEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF6D28D9))
                        )
                    }
                }

                // Toggle 2: Notify on Final Destination
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🎯 Notify on Final Destination",
                                color = Color(0xFF1E1B4B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Receive completion alert when person reaches ${route.destination}",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                        Switch(
                            checked = notifyDest,
                            onCheckedChange = {
                                notifyDest = it
                                NotificationHelper.setDestinationNotificationEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF6D28D9))
                        )
                    }
                }

                // Toggle 3: Sound & Vibration
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔊 Sound & Vibration",
                                color = Color(0xFF1E1B4B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Play alert tone and vibrate phone on stop arrival",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = soundVibrate,
                            onCheckedChange = {
                                soundVibrate = it
                                NotificationHelper.setSoundVibrateEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF6D28D9))
                        )
                    }
                }

                // Toggle 4: System Status Bar Tray
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📲 Android Notification Tray",
                                color = Color(0xFF1E1B4B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Display notification banner in Android status bar",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = statusBar,
                            onCheckedChange = {
                                statusBar = it
                                NotificationHelper.setStatusBarNotificationEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF6D28D9))
                        )
                    }
                }

                // Test Notification Buttons
                Text(
                    text = "TEST NOTIFICATIONS",
                    color = Color(0xFF1E1B4B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onTestStop,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6D28D9))
                    ) {
                        Text("🧪 Test Stop Alert", fontSize = 10.sp, color = Color(0xFF6D28D9), fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onTestDestination,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                    ) {
                        Text("🎉 Test Final Alert", fontSize = 10.sp, color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                    }
                }

                // Done Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9))
                ) {
                    Text("Save & Close Settings ✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Dynamic Departure Time Selection Dialog
 */
@Composable
private fun DepartureTimeSelectionDialog(
    currentTime: String,
    origin: String,
    destination: String,
    onDismiss: () -> Unit,
    onConfirm: (newTime: String) -> Unit
) {
    var selectedTime by remember { mutableStateOf(currentTime) }
    var customTimeInput by remember { mutableStateOf(currentTime) }

    val quickOptions = listOf(
        "08:45 PM",
        "06:00 AM",
        "08:00 AM",
        "11:30 AM",
        "02:15 PM",
        "05:45 PM",
        "10:00 PM"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Set Departure Time",
                        color = Color(0xFF1E1B4B),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✕",
                        color = Color(0xFF64748B),
                        fontSize = 16.sp,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Text(
                    text = "Arrival and departure times for all stops along $origin ➔ $destination will automatically update based on your start time.",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                // Quick Option Chips
                Text(
                    text = "POPULAR DEPARTURE TIMES",
                    color = Color(0xFF1E1B4B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickOptions.chunked(3).forEach { rowList ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowList.forEach { opt ->
                                val isSelected = selectedTime.equals(opt, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF6D28D9) else Color(0xFFF3E8FF))
                                        .border(
                                            1.dp,
                                            if (isSelected) Color(0xFF5B21B6) else Color(0xFFE9D5FF),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedTime = opt
                                            customTimeInput = opt
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = opt,
                                        color = if (isSelected) Color.White else Color(0xFF6D28D9),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Custom Time Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "OR ENTER CUSTOM TIME",
                        color = Color(0xFF1E1B4B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = customTimeInput,
                        onValueChange = {
                            customTimeInput = it
                            selectedTime = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("e.g. 08:45 PM, 07:15 AM", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Apply Button
                Button(
                    onClick = {
                        val finalTime = customTimeInput.trim().ifBlank { selectedTime }
                        onConfirm(finalTime)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9))
                ) {
                    Text(
                        text = "Apply Departure Time ➔",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Route Search Dialog across all over Tamil Nadu
 */
@Composable
private fun TamilNaduRouteSearchDialog(
    currentOrigin: String,
    currentDestination: String,
    onDismiss: () -> Unit,
    onApplyRoute: (origin: String, destination: String) -> Unit
) {
    var fromInput by remember { mutableStateOf(currentOrigin) }
    var toInput by remember { mutableStateOf(currentDestination) }

    val popularCorridors = listOf(
        "Kanchipuram" to "Trichy",
        "Kanchipuram" to "Coimbatore",
        "Kanchipuram" to "Salem",
        "Chennai" to "Coimbatore",
        "Chennai" to "Madurai",
        "Chennai" to "Kanyakumari",
        "Coimbatore" to "Ooty",
        "Madurai" to "Rameswaram"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Scheduled Stops & Stations",
                        color = Color(0xFF1E1B4B),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✕",
                        color = Color(0xFF64748B),
                        fontSize = 16.sp,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                // From Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "STARTING FROM (ORIGIN)", color = Color(0xFF1E1B4B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = fromInput,
                        onValueChange = { fromInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("e.g. Kanchipuram, Chennai, Salem", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // To Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "DESTINATION", color = Color(0xFF1E1B4B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = toInput,
                        onValueChange = { toInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("e.g. Trichy, Coimbatore, Madurai", fontSize = 12.sp) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Popular Corridor Shortcuts
                Text(
                    text = "POPULAR TAMIL NADU ROUTES",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    popularCorridors.chunked(2).forEach { pairList ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            pairList.forEach { (cFrom, cTo) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF3E8FF))
                                        .border(1.dp, Color(0xFFE9D5FF), RoundedCornerShape(8.dp))
                                        .clickable {
                                            fromInput = cFrom
                                            toInput = cTo
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "$cFrom ➔ $cTo",
                                        color = Color(0xFF6D28D9),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Apply Route Button
                Button(
                    onClick = {
                        val cleanFrom = fromInput.trim().ifBlank { "Kanchipuram" }
                        val cleanTo = toInput.trim().ifBlank { "Coimbatore" }
                        onApplyRoute(cleanFrom, cleanTo)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9))
                ) {
                    Text(
                        text = "View Route & Stops Timeline ➔",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
