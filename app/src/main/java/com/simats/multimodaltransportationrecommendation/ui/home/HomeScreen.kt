package com.simats.multimodaltransportationrecommendation.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import com.simats.multimodaltransportationrecommendation.ui.theme.*

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

    // Filter tourist places based on search query and category
    val displayedPlaces = remember(uiState.touristPlaces, uiState.searchQuery) {
        if (uiState.searchQuery.isBlank()) {
            uiState.touristPlaces
        } else {
            val q = uiState.searchQuery.trim().lowercase()
            uiState.touristPlaces.filter {
                it.name.lowercase().contains(q) ||
                it.city.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.description.lowercase().contains(q)
            }
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
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "06 / MOVESMART AI",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Tamil Nadu Explorer",
                            color = TextNavyDark,
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
                                .background(PastelCardBlue)
                                .border(1.dp, SkyBlueSoft, RoundedCornerShape(16.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⛅", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uiState.temperature,
                                    color = SkyBlueDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Notification Icon with Unread Badge
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, CircleShape)
                                .clickable { showNotificationsDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔔", fontSize = 15.sp)
                            if (uiState.unreadNotifications > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-2).dp, y = 2.dp)
                                        .background(WarmOrangeAccent, CircleShape)
                                )
                            }
                        }
                    }
                }

                // Greeting & Subtitle
                Column {
                    Text(
                        text = "Vanakkam, ${uiState.userName} 🙏",
                        color = TextNavyDark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Where would you like to explore across Tamil Nadu today?",
                        color = TextNavyBody,
                        fontSize = 13.sp
                    )
                }

                // Explore Tamil Nadu Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(HeroBannerGradient)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌴", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ENCHANTING TAMIL NADU",
                                color = SurfaceWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Temples · Beaches · Hill Stations · Waterfalls",
                            color = SurfaceWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Connected by Chennai Metro, MTC, TNSTC & Southern Railway",
                            color = SurfaceWhite.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Multi-Modal Journey Search Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Starting Location Input
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "STARTING FROM", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PastelCardBlue)
                                    .border(1.dp, SkyBlueSoft, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📍", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = uiState.origin,
                                    onValueChange = { viewModel.onOriginChange(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp),
                                    cursorBrush = SolidColor(SkyBluePrimary),
                                    decorationBox = { inner ->
                                        if (uiState.origin.isEmpty()) {
                                            Text(text = "Enter starting location (e.g. Chennai Central)", color = TextLight, fontSize = 13.sp)
                                        }
                                        inner()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "GPS",
                                    color = SkyBluePrimary,
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

                        // Swap Button Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(PastelCardBlue)
                                    .border(1.dp, SkyBlueSoft, CircleShape)
                                    .clickable { viewModel.swapLocations() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "⇅",
                                    color = SkyBluePrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Destination Input
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "DESTINATION / TOURIST PLACE", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceWhite)
                                    .border(1.dp, SkyBluePrimary, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎯", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = uiState.destination,
                                    onValueChange = { viewModel.onDestinationChange(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp),
                                    cursorBrush = SolidColor(SkyBluePrimary),
                                    decorationBox = { inner ->
                                        if (uiState.destination.isEmpty()) {
                                            Text(text = "Search destination (e.g. Marina Beach)", color = TextLight, fontSize = 13.sp)
                                        }
                                        inner()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                if (uiState.destination.isNotEmpty()) {
                                    Text(
                                        text = "✕",
                                        color = TextMuted,
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
                                    .background(PastelCardTeal)
                                    .border(1.dp, TealLight, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "🕒", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = uiState.dateTime, color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Button(
                                onClick = {
                                    val dest = if (uiState.destination.isBlank()) "Mahabalipuram" else uiState.destination
                                    val start = if (uiState.origin.isBlank()) "Chennai Central" else uiState.origin
                                    onPlanJourney(start, dest)
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(PrimaryGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "Search Routes", color = SurfaceWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "➔", color = SurfaceWhite, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Transportation Mode Shortcuts
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "MULTI-MODAL MODES IN TAMIL NADU",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    val transportShortcuts = listOf(
                        "🚇 Metro (CMRL)" to "Chennai Metro",
                        "🚌 Bus (MTC/TNSTC)" to "MTC Bus",
                        "🚆 Rail (SR)" to "Southern Railway",
                        "🚲 Cycling" to "Smart Cycle Track",
                        "🚶 Walking" to "Heritage Corridor",
                        "🚕 Taxi / Auto" to "Prepaid Auto"
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(transportShortcuts) { (label, modeDesc) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PastelCardBlue)
                                    .border(1.dp, SkyBlueSoft, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = SkyBlueDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Section: Tamil Nadu Tourist Places Discovery
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TAMIL NADU TOURIST PLACES",
                                color = SkyBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Discover Famous Attractions",
                                color = TextNavyDark,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Search tourist places filter bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔍", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            singleLine = true,
                            textStyle = TextStyle(color = TextNavyDark, fontSize = 12.sp),
                            cursorBrush = SolidColor(SkyBluePrimary),
                            decorationBox = { inner ->
                                if (uiState.searchQuery.isEmpty()) {
                                    Text(text = "Search by city, temple, beach, or hill station...", color = TextLight, fontSize = 12.sp)
                                }
                                inner()
                            },
                            modifier = Modifier.weight(1f)
                        )
                        if (uiState.searchQuery.isNotEmpty()) {
                            Text(
                                text = "✕",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { viewModel.onSearchQueryChange("") }
                            )
                        }
                    }

                    // Category Filter Pills
                    val categories = listOf("All", "Temples", "Beaches", "Historical Places", "Nature", "Museums")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSel = uiState.selectedCategory.equals(cat, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) SkyBluePrimary else SurfaceWhite)
                                    .border(
                                        1.dp,
                                        if (isSel) SkyBluePrimary else BorderLight,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { viewModel.selectCategory(cat, context) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) SurfaceWhite else TextNavyBody,
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
                                .background(PastelCardTeal)
                                .border(1.dp, TealSoft, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "✓ ${uiState.successMessage}",
                                color = TealDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Tourist Places Vertical Cards List
                    if (uiState.isLoadingPlaces) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SkyBluePrimary, modifier = Modifier.size(26.dp))
                        }
                    } else if (displayedPlaces.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .background(SurfaceWhite, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No attractions match your query. Try a different city or category.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            displayedPlaces.forEach { place ->
                                TouristPlaceExploreCard(
                                    place = place,
                                    onViewDetails = { onOpenPlaceDetail(place) },
                                    onToggleSave = { viewModel.toggleSavePlace(context, place) },
                                    onPlanHere = {
                                        viewModel.setQuickDestination(place.name)
                                        val start = if (uiState.origin.isBlank()) "Chennai Central" else uiState.origin
                                        onPlanJourney(start, place.name)
                                    }
                                )
                            }
                        }
                    }
                }

                // Recent Commute Shortcut
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "RECENT COMMUTE SHORTCUT",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .clickable {
                                onPlanJourney("Chennai Central", uiState.recentDestination)
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
                                    .background(PastelCardTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🕒", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = uiState.recentDestination,
                                    color = TextNavyDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.recentLocationInfo,
                                    color = TextNavyBody,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Text(text = "Re-route ➔", color = SkyBluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight)
                    .padding(vertical = 10.dp),
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
                            color = if (isActive) SkyBluePrimary else TextMuted,
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
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceWhite)
            .border(
                1.dp,
                if (place.isRecommended) SkyBlueSoft else BorderLight,
                RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Recommendation Tag
            if (place.isRecommended || !place.aiReason.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PastelCardTeal)
                        .border(1.dp, TealLight, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = place.aiReason ?: "AI Recommendation",
                            color = TealDark,
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
                // Image Thumbnail
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PastelCardBlue)
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
                            color = TextNavyDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(PastelCardPeach)
                                .clickable { onToggleSave() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (place.isSaved) "❤️" else "🤍", fontSize = 12.sp)
                        }
                    }

                    Text(
                        text = "📍 ${place.city} · ${place.category}",
                        color = TextNavyBody,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "★ ${place.rating}", color = WarmOrangeDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "🎟️ ${place.entryFee}", color = TextMuted, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "🧭 ${place.distanceKm} km", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }

            Text(
                text = place.description,
                color = TextNavyBody,
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
                        .height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PastelCardBlue),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "View Details", color = SkyBlueDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onPlanHere,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "Plan Journey", color = SurfaceWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
