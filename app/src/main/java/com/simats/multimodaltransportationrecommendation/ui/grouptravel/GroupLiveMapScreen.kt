package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.simats.multimodaltransportationrecommendation.ui.journey.CorridorStopItem
import com.simats.multimodaltransportationrecommendation.ui.journey.TamilNaduTransitResolver
import com.simats.multimodaltransportationrecommendation.ui.journey.TamilNaduTravelRoute
import com.simats.multimodaltransportationrecommendation.ui.map.MapMarkerItem
import com.simats.multimodaltransportationrecommendation.ui.map.MoveSmartGoogleMapView
import com.simats.multimodaltransportationrecommendation.ui.theme.*

/**
 * Modern Transit Route & Intermediate Stations Timeline Screen
 * Replaces the live tracking screen with authentic scheduled stops,
 * transit times, and Tamil Nadu state-wide corridor discovery.
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

    // Initialize with active group destination & start point
    LaunchedEffect(groupId, uiState.activeGroup) {
        val dest = uiState.activeGroup?.destination?.ifBlank { null }
            ?: uiState.routeDestQuery.ifBlank { null }
            ?: "Coimbatore"
        val orig = uiState.activeGroup?.commonStartLocation?.ifBlank { null }
            ?: uiState.routeOriginQuery.ifBlank { null }
            ?: "Chennai"
        viewModel.searchTravelRoute(orig, dest)
    }

    val activeRoute = uiState.activeTravelRoute
        ?: TamilNaduTransitResolver.resolveRoute(uiState.routeOriginQuery, uiState.routeDestQuery)

    // Route Search Dialog across all of Tamil Nadu
    if (isSearchModalOpen) {
        TamilNaduRouteSearchDialog(
            currentOrigin = activeRoute.origin,
            currentDestination = activeRoute.destination,
            onDismiss = { isSearchModalOpen = false },
            onApplyRoute = { orig, dest ->
                viewModel.searchTravelRoute(orig, dest)
                isSearchModalOpen = false
            }
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
                val mapMarkers = remember(activeRoute) {
                    activeRoute.intermediateStops.map { stop ->
                        MapMarkerItem(
                            id = stop.id,
                            title = "🚉 ${stop.name}",
                            snippet = "Arr: ${stop.arrivalTime} | Dep: ${stop.departureTime} · ${stop.distanceFromOriginKm} km",
                            latitude = stop.latitude,
                            longitude = stop.longitude,
                            iconColorHue = BitmapDescriptorFactory.HUE_VIOLET,
                            extraData = stop
                        )
                    }
                }

                MoveSmartGoogleMapView(
                    modifier = Modifier.fillMaxSize(),
                    originLocation = activeRoute.originLatLng,
                    destinationLocation = activeRoute.destinationLatLng,
                    routeWaypoints = activeRoute.routePolyline,
                    additionalMarkers = mapMarkers,
                    triggerFitAll = System.currentTimeMillis()
                )

                // Top Bar on Map View
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
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { showFullMapView = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = Color(0xFF1E1B4B), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF6D28D9))
                            .clickable { showFullMapView = false }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "📋 View Stops Timeline",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                    // Metrics Row: Total Distance & Expected Arrival (Live tracking removed)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
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

                    // Swap / Change Route Button
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

                        // Col 3: Departure
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(0.9f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📅", fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(text = "Departure", color = Color(0xFF64748B), fontSize = 10.sp)
                            }
                            Text(
                                text = activeRoute.departureTime,
                                color = Color(0xFF1E1B4B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                    departureTime = activeRoute.departureTime
                )

                // INTERMEDIATE STATIONS (Connected by vertical line with hollow purple circles)
                activeRoute.intermediateStops.forEachIndexed { index, stop ->
                    TimelineIntermediateRow(
                        stop = stop,
                        isLast = (index == activeRoute.intermediateStops.size - 1)
                    )
                }

                // DESTINATION STATION ROW
                TimelineDestinationCard(
                    stationName = activeRoute.destination,
                    arrivalTime = activeRoute.arrivalTime
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
    departureTime: String
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
                Column {
                    Text(
                        text = stationName,
                        color = Color(0xFF1E1B4B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Departure : $departureTime",
                        color = Color(0xFF6D28D9),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
 * Intermediate Stop Row with Connected Timeline Line & Hollow Circle Node
 */
@Composable
private fun TimelineIntermediateRow(
    stop: CorridorStopItem,
    isLast: Boolean
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
            // Continuous Vertical Line
            Canvas(modifier = Modifier.fillMaxHeight().width(2.dp)) {
                drawLine(
                    color = Color(0xFF7C3AED),
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Hollow Circle Node
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.5.dp, Color(0xFF7C3AED), CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // White Intermediate Station Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📍", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(3.dp))
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
 * Destination Node Card with Purple Theme & Destination Badge
 */
@Composable
private fun TimelineDestinationCard(
    stationName: String,
    arrivalTime: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Destination Icon Node
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF6D28D9)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "📍", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Destination Card Container
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
                Column {
                    Text(
                        text = stationName,
                        color = Color(0xFF1E1B4B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Arrival : $arrivalTime",
                        color = Color(0xFF6D28D9),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF6D28D9))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏁 Destination",
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
        "Chennai" to "Coimbatore",
        "Chennai" to "Madurai",
        "Chennai" to "Kanyakumari",
        "Coimbatore" to "Ooty",
        "Madurai" to "Rameswaram",
        "Tiruchirappalli" to "Thanjavur",
        "Salem" to "Yercaud",
        "Tirunelveli" to "Courtallam"
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
                    Column {
                        Text(
                            text = "SEARCH ROUTE ACROSS TAMIL NADU",
                            color = Color(0xFF6D28D9),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Scheduled Stops & Stations",
                            color = Color(0xFF1E1B4B),
                            fontSize = 15.sp,
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

                // From Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "STARTING FROM (ORIGIN)", color = Color(0xFF1E1B4B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = fromInput,
                        onValueChange = { fromInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("e.g. Chennai, Madurai, Salem", fontSize = 12.sp) },
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
                        placeholder = { Text("e.g. Coimbatore, Ooty, Kanyakumari", fontSize = 12.sp) },
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
                        val cleanFrom = fromInput.trim().ifBlank { "Chennai" }
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
