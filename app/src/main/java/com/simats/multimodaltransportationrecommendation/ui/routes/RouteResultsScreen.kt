package com.simats.multimodaltransportationrecommendation.ui.routes

import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun RouteResultsScreen(
    origin: String = "Chennai Central",
    destination: String = "Mahabalipuram",
    viewModel: RouteResultsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onViewRecommended: (RouteOption) -> Unit = {},
    onCompareRoutes: (RouteOption, List<RouteOption>) -> Unit = { _, _ -> },
    onOpenPlaceDetail: (TouristPlace) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(origin, destination) {
        viewModel.searchRoutes(context, origin, destination)
    }

    LaunchedEffect(uiState.saveMessage) {
        uiState.saveMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearSaveMessage()
        }
    }

    // Interactive Map Dialog
    if (uiState.showMapSheet) {
        RouteMapDialog(
            origin = uiState.origin,
            destination = uiState.destination,
            selectedRoute = uiState.selectedRoute,
            places = uiState.routePlaces,
            onClose = { viewModel.toggleMapSheet(false) },
            onSelectPlace = { place ->
                viewModel.toggleMapSheet(false)
                onOpenPlaceDetail(place)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, CircleShape)
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "←", color = TextNavyDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "TAMIL NADU TRANSIT ROUTES",
                                color = SkyBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "Available Transportation",
                                color = TextNavyDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Action Shortcuts: Map View & Compare
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(PastelCardBlue)
                                .border(1.dp, SkyBlueSoft, RoundedCornerShape(10.dp))
                                .clickable { viewModel.toggleMapSheet(true) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🗺️ Map", color = SkyBluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(PastelCardTeal)
                                .border(1.dp, TealLight, RoundedCornerShape(10.dp))
                                .clickable {
                                    uiState.selectedRoute?.let { sel ->
                                        onCompareRoutes(sel, uiState.allRoutes)
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⚖️ Compare", color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Origin -> Destination Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(SkyBluePrimary, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "From: ${uiState.origin}",
                                color = TextNavyDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(WarmOrangeAccent, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "To: ${uiState.destination}",
                                color = TextNavyDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // AI Insight
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PastelCardTeal)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "💡 ${uiState.aiInsight}",
                        color = TealDark,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.isLoading) {
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
                            CircularProgressIndicator(color = SkyBluePrimary, modifier = Modifier.size(36.dp))
                            Text(
                                text = "Searching verified Tamil Nadu multi-modal transit...",
                                color = TextMutedSlate,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else if (uiState.errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = "⚠️", fontSize = 32.sp)
                                Text(
                                    text = "No Verified Route Found",
                                    color = TextNavyDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.errorMessage ?: "Please verify your starting point and destination.",
                                    color = TextMutedSlate,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = { viewModel.searchRoutes(context, origin, destination) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(text = "Try Another Search", color = Color.White, fontSize = 12.sp)
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
                        // Section: Multi-Modal Transit Routes
                        items(uiState.allRoutes) { route ->
                            val isSelected = uiState.selectedRoute?.id == route.id
                            MultiModalRouteCard(
                                route = route,
                                isSelected = isSelected,
                                onClick = { viewModel.selectRoute(route) },
                                onSave = { viewModel.saveRoute(context, route) }
                            )
                        }

                        // Section 5: Explore Places Along Your Route
                        if (uiState.routePlaces.isNotEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, bottom = 6.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "🧭", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(
                                                    text = "Explore Places Along Your Route",
                                                    color = TextNavyDark,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Attractions along the ${uiState.destination} transit corridor",
                                                    color = TextMutedSlate,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        items(uiState.routePlaces) { place ->
                                            RoutePlaceMiniCard(
                                                place = place,
                                                onViewDetails = { onOpenPlaceDetail(place) },
                                                onToggleSave = { viewModel.toggleSavePlace(context, place) }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }

            // Bottom CTA: Proceed with Selected Route
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                uiState.selectedRoute?.let { selRoute ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(PrimaryGradient)
                            .clickable { onViewRecommended(selRoute) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select ${selRoute.mode} & Review Journey  ➔",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Multi-Modal Route Item Card Component
@Composable
private fun MultiModalRouteCard(
    route: RouteOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    onSave: () -> Unit
) {
    var expandedStops by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isSelected) 4.dp else 2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(2.dp, TealSecondary, RoundedCornerShape(16.dp))
                else Modifier.border(1.dp, BorderLight, RoundedCornerShape(16.dp))
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) PastelCardTeal else SurfaceWhite)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Badge & Mode Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (route.mode.lowercase()) {
                                    "train" -> PastelCardBlue
                                    "bus" -> PastelCardPeach
                                    "taxi" -> PastelCardPurple
                                    else -> PastelCardTeal
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (route.mode.lowercase()) {
                                "metro" -> "🚇"
                                "bus" -> "🚌"
                                "train" -> "🚆"
                                "taxi" -> "🚕"
                                "walking" -> "🚶"
                                else -> "🚀"
                            },
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        route.badge?.let { b ->
                            Text(
                                text = b,
                                color = if (route.isRecommended) TealDark else SkyBluePrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = route.title,
                            color = TextNavyDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bookmark / Save
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, CircleShape)
                        .clickable { onSave() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🔖", fontSize = 12.sp)
                }
            }

            // Duration, Distance, Fare Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BackgroundSoftCream)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "DURATION", color = TextMutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(text = route.durationText, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "DISTANCE", color = TextMutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(text = route.distanceText, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text(text = "OFFICIAL FARE", color = TextMutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(text = route.fareText, color = EmeraldGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "CO2 SAVED", color = TextMutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${route.co2SavedKg} kg", color = TealSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Route Details Summary
            Text(
                text = route.routeDetails,
                color = TextNavyBody,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            // Step-by-Step Transit Instructions
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                route.steps.forEachIndexed { idx, step ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "${idx + 1}.",
                            color = SkyBluePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(18.dp)
                        )
                        Text(
                            text = step,
                            color = TextNavyDark,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Intermediate Stops Toggle
            if (route.intermediateStops.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { expandedStops = !expandedStops }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expandedStops) "▲ Hide Intermediate Stops" else "▼ View ${route.intermediateStops.size} Intermediate Stops",
                        color = SkyBluePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (expandedStops) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            route.intermediateStops.forEachIndexed { i, stop ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(6.dp).background(TealSecondary, CircleShape))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = stop, color = TextNavyDark, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Data Source Badge
            route.sourceAgency?.let { agency ->
                Text(
                    text = "Source: $agency",
                    color = TextMutedSlate,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Attraction Mini Card along Route
@Composable
private fun RoutePlaceMiniCard(
    place: TouristPlace,
    onViewDetails: () -> Unit,
    onToggleSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clickable { onViewDetails() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
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

                // Category pill
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(text = place.category, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                // Bookmark icon
                Box(
                    modifier = Modifier
                        .padding(6.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .clickable { onToggleSave() }
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (place.isSaved) "❤️" else "🤍", fontSize = 11.sp)
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = place.name,
                    color = TextNavyDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                place.routeNote?.let { note ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PastelCardTeal)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = note, color = TealDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⭐ ${place.rating}",
                        color = TextNavyDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = place.entryFee ?: "Free",
                        color = EmeraldGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Interactive Map Dialog
@Composable
private fun RouteMapDialog(
    origin: String,
    destination: String,
    selectedRoute: RouteOption?,
    places: List<TouristPlace>,
    onClose: () -> Unit,
    onSelectPlace: (TouristPlace) -> Unit
) {
    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "TAMIL NADU TRANSIT CORRIDOR", color = SkyBluePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Interactive Route Map", color = TextNavyDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(BorderLight)
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Visual Map Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val start = Offset(size.width * 0.15f, size.height * 0.2f)
                        val end = Offset(size.width * 0.85f, size.height * 0.8f)

                        // Main route curve
                        val mid1 = Offset(size.width * 0.4f, size.height * 0.35f)
                        val mid2 = Offset(size.width * 0.65f, size.height * 0.6f)

                        // Draw background road corridor
                        drawLine(
                            color = Color(0xFFCBD5E1),
                            start = start,
                            end = end,
                            strokeWidth = 14f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
                        )

                        // Draw transit line
                        drawLine(
                            color = SkyBluePrimary,
                            start = start,
                            end = mid1,
                            strokeWidth = 6f
                        )
                        drawLine(
                            color = TealSecondary,
                            start = mid1,
                            end = mid2,
                            strokeWidth = 6f
                        )
                        drawLine(
                            color = WarmOrangeAccent,
                            start = mid2,
                            end = end,
                            strokeWidth = 6f
                        )

                        // Origin node (Green)
                        drawCircle(color = Color(0xFF10B981), radius = 12f, center = start)
                        drawCircle(color = Color.White, radius = 5f, center = start)

                        // Intermediate nodes
                        drawCircle(color = TealSecondary, radius = 9f, center = mid1)
                        drawCircle(color = Color.White, radius = 4f, center = mid1)

                        drawCircle(color = TealSecondary, radius = 9f, center = mid2)
                        drawCircle(color = Color.White, radius = 4f, center = mid2)

                        // Destination node (Red)
                        drawCircle(color = Color(0xFFDC2626), radius = 14f, center = end)
                        drawCircle(color = Color.White, radius = 6f, center = end)
                    }

                    // Floating labels
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Origin: $origin", color = TextNavyDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Destination: $destination", color = Color(0xFFDC2626), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Corridor stops list
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Attractions Along This Corridor:",
                        color = TextNavyDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    places.take(3).forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BackgroundSoftCream)
                                .clickable { onSelectPlace(p) }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⭐", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = p.name, color = TextNavyDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Text(text = p.routeNote ?: "Along route", color = TealDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Back to Routes", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}
