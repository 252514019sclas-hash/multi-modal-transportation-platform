package com.simats.multimodaltransportationrecommendation.ui.routes

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
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
import androidx.compose.ui.text.style.TextOverflow
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
    dateTime: String = "Today, 9:15 AM",
    viewModel: RouteResultsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onStartJourney: (RouteOption) -> Unit = {},
    onCompareRoutes: (RouteOption, List<RouteOption>) -> Unit = { _, _ -> },
    onOpenPlaceDetail: (TouristPlace) -> Unit = {},
    onPlanMultiDay: () -> Unit = {},
    onOpenNearbyFacilities: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(origin, destination, dateTime) {
        viewModel.searchRoutes(context, origin, destination, dateTime)
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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // 1. TOP HEADER & SEARCH SUMMARY (FIXED AT TOP)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Vehicles & Tourist Guide",
                            color = TextNavyDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Action Button: Facilities (Clean Horizontal Pill, No vertical wrapping)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFEF3C7))
                            .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(10.dp))
                            .clickable { onOpenNearbyFacilities() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalGasStation,
                                contentDescription = "Facilities",
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Facilities",
                                color = Color(0xFFB45309),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                // Clean Route Pill (Origin -> Destination)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundSoftCream)
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(modifier = Modifier.size(8.dp).background(SkyBluePrimary, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.origin.replaceFirstChar { it.uppercase() },
                                color = TextNavyDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(text = " ➔ ", color = TextMutedSlate, fontSize = 12.sp)
                            Box(modifier = Modifier.size(8.dp).background(WarmOrangeAccent, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.destination.replaceFirstChar { it.uppercase() },
                                color = TextNavyDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE0F2FE))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "No Booking · Free", color = SkyBlueDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 2. CLEAN SEGMENTED TABS: VEHICLES | TOURIST SPOTS & PLANNER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BorderSubtle)
                        .padding(3.dp)
                ) {
                    // Tab 0: Available Vehicles
                    val isVehiclesTab = uiState.selectedTab == 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isVehiclesTab) SurfaceWhite else Color.Transparent)
                            .clickable { viewModel.setSelectedTab(0) }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🚗 Vehicles (${uiState.allRoutes.size})",
                                color = if (isVehiclesTab) TextNavyDark else TextMutedSlate,
                                fontSize = 12.sp,
                                fontWeight = if (isVehiclesTab) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }

                    // Tab 1: Nearby Tourist Spots & Trip Planner
                    val isPlacesTab = uiState.selectedTab == 1
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isPlacesTab) SurfaceWhite else Color.Transparent)
                            .clickable { viewModel.setSelectedTab(1) }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🧭 Tourist Spots & Planner",
                                color = if (isPlacesTab) TextNavyDark else TextMutedSlate,
                                fontSize = 12.sp,
                                fontWeight = if (isPlacesTab) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 3. MAIN TAB CONTENT
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
                            text = "Analyzing available vehicles & tourist spots across Tamil Nadu...",
                            color = TextMutedSlate,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else if (uiState.errorMessage != null && uiState.allRoutes.isEmpty() && uiState.routePlaces.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = "⚠️", fontSize = 32.sp)
                            Text(text = "No Transit or Spots Found", color = TextNavyDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = uiState.errorMessage ?: "Please verify your starting point or destination in Tamil Nadu.",
                                color = TextMutedSlate,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.searchRoutes(context, origin, destination) },
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "Retry Search", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (uiState.selectedTab) {
                        0 -> AvailableVehiclesTabContent(
                            uiState = uiState,
                            onSelectRoute = { viewModel.selectRoute(it) },
                            onStartJourney = onStartJourney,
                            onSavePlan = { viewModel.saveVehiclePlan(context, it) },
                            onCompare = { sel -> onCompareRoutes(sel, uiState.allRoutes) }
                        )
                        1 -> TouristSpotsPlannerTabContent(
                            uiState = uiState,
                            onSelectCategory = { viewModel.setSelectedCategory(it) },
                            onOpenPlaceDetail = onOpenPlaceDetail,
                            onToggleSavePlace = { viewModel.toggleSavePlace(context, it) },
                            onPlanMultiDay = onPlanMultiDay
                        )
                    }
                }
            }

            // 4. CLEAN BOTTOM ACTION BAR (ONLY FOR TAB 1: SMART TRIP PLANNER)
            if (uiState.selectedTab == 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight)
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(PrimaryGradient)
                            .clickable { onPlanMultiDay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✨ Plan Multi-Day Tour & Stays with AI  ➔",
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

// -----------------------------------------------------------------------------
// TAB 0: AVAILABLE VEHICLES CONTENT
// -----------------------------------------------------------------------------
@Composable
private fun AvailableVehiclesTabContent(
    uiState: RouteResultsUiState,
    onSelectRoute: (RouteOption) -> Unit,
    onStartJourney: (RouteOption) -> Unit,
    onSavePlan: (RouteOption) -> Unit,
    onCompare: (RouteOption) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Available Vehicle Cards List
        items(uiState.allRoutes) { route ->
            val isSelected = uiState.selectedRoute?.id == route.id
            VehicleCard(
                route = route,
                isSelected = isSelected,
                onClick = { onSelectRoute(route) },
                onStartJourney = { onStartJourney(route) },
                onSavePlan = { onSavePlan(route) },
                onCompare = { onCompare(route) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun VehicleCard(
    route: RouteOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    onStartJourney: () -> Unit,
    onSavePlan: () -> Unit,
    onCompare: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val vehicleIcon = when (route.mode.lowercase()) {
        "bus" -> "🚌"
        "train", "rail" -> "🚆"
        "metro" -> "🚇"
        "taxi", "cab" -> "🚖"
        "auto" -> "🛺"
        else -> "🚗"
    }

    val modeColor = when (route.mode.lowercase()) {
        "bus" -> SkyBlueDark
        "train", "rail" -> Color(0xFF6B21A8) // Purple
        "metro" -> TealDark
        "taxi", "cab" -> WarmOrangeDark
        "auto" -> Color(0xFF0F766E)
        else -> SkyBluePrimary
    }

    val modeBg = when (route.mode.lowercase()) {
        "bus" -> PastelCardBlue
        "train", "rail" -> Color(0xFFF3E8FF)
        "metro" -> PastelCardTeal
        "taxi", "cab" -> Color(0xFFFEF3C7)
        "auto" -> Color(0xFFCCFBF1)
        else -> BorderSubtle
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, SkyBluePrimary) else androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Vehicle Icon, Title, Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(modeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = vehicleIcon, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = route.mode.uppercase(),
                                color = modeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            if (route.isRecommended) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFD1FAE5))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "RECOMMENDED", color = Color(0xFF065F46), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = route.title,
                            color = TextNavyDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Quick Stats Row: Duration, Distance, Fare Estimate
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(BackgroundSoftCream)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "TRAVEL TIME", color = TextMutedSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(text = route.durationText, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Column {
                    Text(text = "DISTANCE", color = TextMutedSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(text = route.distanceText, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "ESTIMATED FARE", color = TextMutedSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(text = route.fareText, color = TealDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Transit steps view toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Hide Stops ▲" else "View Transit Stops ▼",
                    color = SkyBlueDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )
            }

            // Expandable Stops
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BorderSubtle)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "Transit Stops & Route Steps:", color = TextNavyDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    route.steps.forEachIndexed { i, s ->
                        Text(text = "• Step ${i + 1}: $s", color = TextNavyBody, fontSize = 10.sp)
                    }
                    if (route.intermediateStops.isNotEmpty()) {
                        Text(
                            text = "Key Stations: ${route.intermediateStops.joinToString(" ➔ ")}",
                            color = TextMutedSlate,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Action Buttons: Save, Compare
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Save Trip Plan
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PastelCardTeal)
                        .clickable { onSavePlan() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💾 Save Trip", color = TealDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Compare
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PastelCardBlue)
                        .clickable { onCompare() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚖️ Compare", color = SkyBlueDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 1: NEARBY TOURIST SPOTS & SMART TRIP PLANNER
// -----------------------------------------------------------------------------
@Composable
private fun TouristSpotsPlannerTabContent(
    uiState: RouteResultsUiState,
    onSelectCategory: (String) -> Unit,
    onOpenPlaceDetail: (TouristPlace) -> Unit,
    onToggleSavePlace: (TouristPlace) -> Unit,
    onPlanMultiDay: () -> Unit
) {
    val categories = listOf("All", "Temples and Religious Places", "Hill Stations", "Waterfalls", "Beaches", "Historical Places", "Forts and Heritage Sites", "Nature and Wildlife", "Museums")

    val filteredPlaces = remember(uiState.routePlaces, uiState.selectedCategoryFilter) {
        if (uiState.selectedCategoryFilter == "All") {
            uiState.routePlaces
        } else {
            uiState.routePlaces.filter { it.category.equals(uiState.selectedCategoryFilter, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Category Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Tourist Spots Near ${uiState.destination.replaceFirstChar { it.uppercase() }} (${filteredPlaces.size})",
                    color = TextNavyDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSel = uiState.selectedCategoryFilter == cat
                        val displayCat = if (cat == "Temples and Religious Places") "Temples" else cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) SkyBluePrimary else SurfaceWhite)
                                .border(1.dp, if (isSel) SkyBluePrimary else BorderLight, RoundedCornerShape(12.dp))
                                .clickable { onSelectCategory(cat) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = displayCat,
                                color = if (isSel) Color.White else TextNavyDark,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // AI Suggested Day Trip Schedule (Morning -> Afternoon -> Evening)
        if (uiState.routePlaces.size >= 2) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TealLight)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📅", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Suggested 1-Day Sightseeing Planner for ${uiState.destination.replaceFirstChar { it.uppercase() }}",
                                color = TealDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Morning spot
                        val morningSpot = uiState.routePlaces.getOrNull(0)
                        morningSpot?.let { p ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🌅 Morning: ", color = SkyBlueDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${p.name} (${p.category})", color = TextNavyDark, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }

                        // Afternoon spot
                        val afternoonSpot = uiState.routePlaces.getOrNull(1)
                        afternoonSpot?.let { p ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "☀️ Afternoon: ", color = WarmOrangeDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${p.name} (${p.category})", color = TextNavyDark, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }

                        // Evening spot
                        val eveningSpot = uiState.routePlaces.getOrNull(2)
                        eveningSpot?.let { p ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🌆 Evening: ", color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${p.name} (${p.category})", color = TextNavyDark, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }

        // Tourist Place Cards
        if (filteredPlaces.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No tourist spots matching this category.", color = TextMutedSlate, fontSize = 12.sp)
                }
            }
        } else {
            items(filteredPlaces) { place ->
                TouristSpotCard(
                    place = place,
                    onViewDetails = { onOpenPlaceDetail(place) },
                    onToggleSave = { onToggleSavePlace(place) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun TouristSpotCard(
    place: TouristPlace,
    onViewDetails: () -> Unit,
    onToggleSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BorderSubtle)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(place.imageUrl)
                            .crossfade(true)
                            .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                            .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                            .build(),
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Details
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = place.name,
                            color = TextNavyDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "⭐ ${place.rating}",
                            color = WarmOrangeDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "📍 ${place.city} · ${place.category}",
                        color = TextMutedSlate,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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

                    Text(
                        text = place.description,
                        color = TextNavyBody,
                        fontSize = 10.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Transit accessibility & timings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(BackgroundSoftCream)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚗 Transit: ${place.nearbyTransit ?: "Local Bus / Auto"}",
                    color = SkyBlueDark,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "⏱️ ${place.durationVisit}",
                    color = TextNavyDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (place.isSaved) "❤️ Saved" else "🤍 Save to Favorites",
                    color = if (place.isSaved) Color(0xFFDC2626) else TextMutedSlate,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onToggleSave() }
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PastelCardBlue)
                        .clickable { onViewDetails() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "View Details →", color = SkyBlueDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// INTERACTIVE MAP DIALOG
// -----------------------------------------------------------------------------
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
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "TAMIL NADU TRANSIT CORRIDOR", color = SkyBluePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Route & Attractions Map", color = TextNavyDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                        .height(240.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val start = Offset(size.width * 0.15f, size.height * 0.2f)
                        val end = Offset(size.width * 0.85f, size.height * 0.8f)

                        val mid1 = Offset(size.width * 0.4f, size.height * 0.35f)
                        val mid2 = Offset(size.width * 0.65f, size.height * 0.6f)

                        drawLine(
                            color = Color(0xFFCBD5E1),
                            start = start,
                            end = end,
                            strokeWidth = 14f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
                        )

                        drawLine(color = SkyBluePrimary, start = start, end = mid1, strokeWidth = 6f)
                        drawLine(color = TealSecondary, start = mid1, end = mid2, strokeWidth = 6f)
                        drawLine(color = WarmOrangeAccent, start = mid2, end = end, strokeWidth = 6f)

                        drawCircle(color = Color(0xFF10B981), radius = 12f, center = start)
                        drawCircle(color = Color.White, radius = 5f, center = start)

                        drawCircle(color = TealSecondary, radius = 9f, center = mid1)
                        drawCircle(color = Color.White, radius = 4f, center = mid1)

                        drawCircle(color = TealSecondary, radius = 9f, center = mid2)
                        drawCircle(color = Color.White, radius = 4f, center = mid2)

                        drawCircle(color = Color(0xFFDC2626), radius = 14f, center = end)
                        drawCircle(color = Color.White, radius = 6f, center = end)
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = "Origin: $origin", color = TextNavyDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = "Destination: $destination", color = Color(0xFFDC2626), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Corridor spots list
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Nearby Spots to Explore:", color = TextNavyDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    places.take(3).forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BackgroundSoftCream)
                                .clickable { onSelectPlace(p) }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⭐ ${p.name}", color = TextNavyDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = p.routeNote ?: "Nearby", color = TealDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Close Map", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}
