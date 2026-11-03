package com.simats.multimodaltransportationrecommendation.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.model.routes.SavedRoute

@Composable
fun SavedScreen(
    viewModel: SavedViewModel = viewModel(),
    onOpenPlaceDetail: (TouristPlace) -> Unit = {},
    onPlanRoute: (String, String) -> Unit = { _, _ -> },
    onExploreMore: () -> Unit = {},
    onNavigateTab: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadSavedData(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SAVED ITEMS / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Your Saved Collection",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Refresh Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), CircleShape)
                            .clickable { viewModel.loadSavedData(context) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔄", fontSize = 13.sp)
                    }
                }

                // Tab Selector (Places vs Routes)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val tabs = listOf("Places", "Routes")
                    tabs.forEach { tab ->
                        val isSel = uiState.selectedTab == tab
                        val count = if (tab == "Places") uiState.savedPlaces.size else uiState.savedRoutes.size
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) Color(0xFF162238) else Color.Transparent)
                                .clickable { viewModel.selectTab(tab) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$tab ($count)",
                                color = if (isSel) Color.White else Color(0xFF8A99AD),
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Messages
                if (!uiState.successMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x22C6FF00))
                            .border(1.dp, Color(0xFFC6FF00), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "✓ ${uiState.successMessage}",
                            color = Color(0xFFC6FF00),
                            fontSize = 11.sp
                        )
                    }
                }

                // Content List
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFC6FF00))
                    }
                } else if (uiState.selectedTab == "Places") {
                    if (uiState.savedPlaces.isEmpty()) {
                        EmptySavedState(
                            title = "No saved places yet",
                            subtitle = "Explore top destinations and tap the heart to save them for quick planning.",
                            buttonText = "Explore Destinations",
                            onAction = onExploreMore
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(uiState.savedPlaces, key = { it.placeId }) { place ->
                                SavedPlaceCard(
                                    place = place,
                                    onOpen = { onOpenPlaceDetail(place) },
                                    onRemove = { viewModel.removeSavedPlace(context, place.placeId) }
                                )
                            }
                        }
                    }
                } else {
                    if (uiState.savedRoutes.isEmpty()) {
                        EmptySavedState(
                            title = "No saved travel routes yet",
                            subtitle = "Search for a route on Explore and save your favorite multi-modal commutes.",
                            buttonText = "Search Routes",
                            onAction = onExploreMore
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(uiState.savedRoutes, key = { it.routeId }) { route ->
                                SavedRouteCard(
                                    route = route,
                                    onPlan = { onPlanRoute(route.origin, route.destination) },
                                    onRemove = { viewModel.removeSavedRoute(context, route.routeId) }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B111E))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val navTabs = listOf(
                    "🗺️\nExplore" to false,
                    "🎫\nMy trips" to false,
                    "🔖\nSaved" to true,
                    "👤\nProfile" to false
                )
                navTabs.forEach { (tabInfo, isActive) ->
                    val lines = tabInfo.split("\n")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onNavigateTab(lines[1])
                        }
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
    }
}

@Composable
private fun SavedPlaceCard(
    place: TouristPlace,
    onOpen: () -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
            .clickable { onOpen() }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF162238))
            ) {
                if (!place.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(place.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "🏛️", fontSize = 24.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "📍 ${place.city} · ${place.category}",
                    color = Color(0xFF8A99AD),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "★ ${place.rating}", color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "🎟️ ${place.entryFee}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }

            // Remove heart button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0x33EF4444))
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🗑️", fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun SavedRouteCard(
    route: SavedRoute,
    onPlan: () -> Unit,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF13201C))
                            .border(1.dp, Color(0xFF1E4D2B), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = route.mode, color = Color(0xFFC6FF00), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = route.durationText, color = Color(0xFF8A99AD), fontSize = 11.sp)
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x33EF4444))
                        .clickable { onRemove() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🗑️", fontSize = 12.sp)
                }
            }

            Text(
                text = "${route.origin} → ${route.destination}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💵 ${route.costText}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "🌿 ${route.co2Text}", color = Color(0xFFC6FF00), fontSize = 11.sp)
                }
                Button(
                    onClick = { onPlan() },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF00)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = "Plan Journey", color = Color(0xFF070B14), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EmptySavedState(
    title: String,
    subtitle: String,
    buttonText: String,
    onAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🔖", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = Color(0xFF8A99AD),
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF00))
            ) {
                Text(text = buttonText, color = Color(0xFF070B14), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

