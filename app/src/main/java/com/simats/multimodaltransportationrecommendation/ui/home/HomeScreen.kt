package com.simats.multimodaltransportationrecommendation.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.ui.notifications.NotificationsHistoryDialog

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onPlanJourney: (String, String) -> Unit = { _, _ -> },
    onOpenPlaceDetail: (TouristPlace) -> Unit = {},
    onNavigateTab: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showNotificationsDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        viewModel.useCurrentLocation(context)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        viewModel.loadExploreData(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
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
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header & Greeting
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "06 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Multi-Modal Explorer",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Weather Pill & Notification Bell
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⛅", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uiState.temperature,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Notification Icon with Unread Badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF1E293B), CircleShape)
                                .clickable { showNotificationsDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔔", fontSize = 14.sp)
                            if (uiState.unreadNotifications > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .align(Alignment.TopEnd)
                                        .background(Color(0xFFC6FF00), CircleShape)
                                )
                            }
                        }
                    }
                }

                // Greeting
                Column {
                    Text(
                        text = "Good day, ${uiState.userName}",
                        color = Color(0xFF8A99AD),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Where are you heading today?",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Multi-Modal Journey Search Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Starting Location Input
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "STARTING FROM", color = Color(0xFF8A99AD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF070B14))
                                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📍", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = uiState.origin,
                                    onValueChange = { viewModel.onOriginChange(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                                    cursorBrush = SolidColor(Color(0xFFC6FF00)),
                                    decorationBox = { inner ->
                                        if (uiState.origin.isEmpty()) {
                                            Text(text = "Enter starting location", color = Color(0xFF64748B), fontSize = 13.sp)
                                        }
                                        inner()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Current",
                                    color = Color(0xFFC6FF00),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        if (!hasFine && !hasCoarse) {
                                            locationPermissionLauncher.launch(
                                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                            )
                                        } else {
                                            viewModel.useCurrentLocation(context)
                                        }
                                    }
                                )
                            }
                        }

                        // Destination Input
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "DESTINATION", color = Color(0xFF8A99AD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF070B14))
                                    .border(1.dp, Color(0xFFC6FF00), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎯", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = uiState.destination,
                                    onValueChange = { viewModel.onDestinationChange(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                                    cursorBrush = SolidColor(Color(0xFFC6FF00)),
                                    decorationBox = { inner ->
                                        if (uiState.destination.isEmpty()) {
                                            Text(text = "Search destination or tourist place...", color = Color(0xFF64748B), fontSize = 13.sp)
                                        }
                                        inner()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                if (uiState.destination.isNotEmpty()) {
                                    Text(
                                        text = "✕",
                                        color = Color(0xFF8A99AD),
                                        fontSize = 13.sp,
                                        modifier = Modifier.clickable { viewModel.onDestinationChange("") }
                                    )
                                }
                            }
                        }

                        // Date & Time + Search Routes Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF070B14))
                                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🕒", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = uiState.dateTime, color = Color(0xFF8A99AD), fontSize = 11.sp)
                                }
                            }

                            Button(
                                onClick = {
                                    val dest = if (uiState.destination.isBlank()) "California Academy of Sciences" else uiState.destination
                                    val start = if (uiState.origin.isBlank()) "Ferry Building" else uiState.origin
                                    onPlanJourney(start, dest)
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF00))
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Search Routes", color = Color(0xFF070B14), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "→", color = Color(0xFF070B14), fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Quick Action Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val quickDestinations = listOf(
                        "🏠 Home" to "Noe Valley, SF",
                        "💼 Work" to "2nd & Howard, SoMa",
                        "🌿 Park" to "Dolores Park"
                    )
                    quickDestinations.forEach { (label, dest) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                .clickable { viewModel.setQuickDestination(dest) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Section: Tourist Attractions Discovery Module
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOURIST ATTRACTIONS & PLACES",
                                color = Color(0xFF7A8B9E),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Discover places to visit",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Category Filter Pills
                    val categories = listOf("All", "Historical", "Temple/Cultural", "Beach/Park", "Museum", "Nature")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSel = uiState.selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) Color(0xFF13201C) else Color(0xFF0F172A))
                                    .border(
                                        1.dp,
                                        if (isSel) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.selectCategory(cat, context) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) Color(0xFFC6FF00) else Color(0xFF8A99AD),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Status Messages
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

                    // Tourist Places Horizontal / List Cards
                    if (uiState.isLoadingPlaces) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFFC6FF00), modifier = Modifier.size(24.dp))
                        }
                    } else if (uiState.touristPlaces.isEmpty()) {
                        Text(
                            text = "No attractions found for this filter.",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.touristPlaces.forEach { place ->
                                TouristPlaceExploreCard(
                                    place = place,
                                    onViewDetails = { onOpenPlaceDetail(place) },
                                    onToggleSave = { viewModel.toggleSavePlace(context, place) },
                                    onPlanHere = {
                                        viewModel.setQuickDestination(place.name)
                                        val start = if (uiState.origin.isBlank()) "Ferry Building" else uiState.origin
                                        onPlanJourney(start, place.name)
                                    }
                                )
                            }
                        }
                    }
                }

                // Pick up where you left off
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "RECENT COMMUTE",
                        color = Color(0xFF7A8B9E),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .clickable {
                                onPlanJourney("Ferry Building", uiState.recentDestination)
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF162238)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🕒", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = uiState.recentDestination,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.recentLocationInfo,
                                    color = Color(0xFF8A99AD),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Text(text = "Re-route ➔", color = Color(0xFFC6FF00), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
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
                val tabs = listOf(
                    "🗺️\nExplore" to true,
                    "🎫\nMy trips" to false,
                    "🔖\nSaved" to false,
                    "👤\nProfile" to false
                )
                tabs.forEach { (tabInfo, isActive) ->
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

        if (showNotificationsDialog) {
            NotificationsHistoryDialog(
                onDismiss = {
                    showNotificationsDialog = false
                    viewModel.loadNotificationsCount(context)
                }
            )
        }
    }
}

@Composable
private fun TouristPlaceExploreCard(
    place: TouristPlace,
    onViewDetails: () -> Unit,
    onToggleSave: () -> Unit,
    onPlanHere: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0F172A))
            .border(
                1.dp,
                if (place.isRecommended) Color(0xFF1E4D2B) else Color(0xFF1E293B),
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (place.isRecommended || !place.aiReason.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF13201C))
                        .border(1.dp, Color(0xFFC6FF00), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.aiReason ?: "AI Personalized Pick",
                            color = Color(0xFFC6FF00),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(76.dp)
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
                            Text(text = "🏛️", fontSize = 28.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = place.name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF162238))
                                .clickable { onToggleSave() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (place.isSaved) "❤️" else "🤍", fontSize = 12.sp)
                        }
                    }

                    Text(
                        text = "📍 ${place.city} · ${place.category}",
                        color = Color(0xFF8A99AD),
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "★ ${place.rating}", color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "🎟️ ${place.entryFee}", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "🧭 ${place.distanceKm} km", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }
            }

            Text(
                text = place.description,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                maxLines = 2,
                lineHeight = 16.sp
            )

            // Buttons: View Details & Plan Journey Here
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onViewDetails,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162238)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "View Details", color = Color.White, fontSize = 11.sp)
                }

                Button(
                    onClick = onPlanHere,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF00)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "Plan Journey", color = Color(0xFF070B14), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
