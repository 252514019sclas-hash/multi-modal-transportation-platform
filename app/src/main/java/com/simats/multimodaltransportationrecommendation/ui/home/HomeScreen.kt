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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.window.Dialog
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
    onPlanJourney: (String, String, String) -> Unit = { _, _, _ -> },
    onOpenPlaceDetail: (TouristPlace) -> Unit = {},
    onOpenGroupTravel: () -> Unit = {},
    onOpenAiTravelAdvisor: () -> Unit = {},
    onNavigateTab: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showWeatherDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

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

    var showAllPlaces by remember { mutableStateOf(false) }

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
                    Text(
                        text = "Tamil Nadu Explorer",
                        color = TextNavyDark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

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
                                .clickable { showWeatherDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val weatherIcon = when (uiState.weatherIconType) {
                                    "sunny" -> Icons.Default.WbSunny
                                    "night" -> Icons.Default.Nightlight
                                    "cloudy" -> Icons.Default.Cloud
                                    "rainy" -> Icons.Default.WaterDrop
                                    "breeze" -> Icons.Default.Air
                                    else -> Icons.Default.WbSunny
                                }
                                val iconTint = when (uiState.weatherIconType) {
                                    "sunny" -> WarmOrangeAccent
                                    "night" -> Color(0xFF6366F1)
                                    "cloudy" -> Color(0xFF64748B)
                                    "rainy" -> Color(0xFF0284C7)
                                    "breeze" -> Color(0xFF0D9488)
                                    else -> WarmOrangeAccent
                                }
                                Icon(
                                    imageVector = weatherIcon,
                                    contentDescription = "Weather",
                                    tint = iconTint,
                                    modifier = Modifier.size(13.dp)
                                )
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
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = TextNavyDark,
                                modifier = Modifier.size(18.dp)
                            )
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
                        text = "Vanakkam, ${uiState.userName}",
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
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = null,
                                tint = SurfaceWhite,
                                modifier = Modifier.size(16.dp)
                            )
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
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(16.dp)
                                )
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
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = SkyBluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
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
                                    .clickable { showTimePickerDialog = true }
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Select Departure Time",
                                        tint = TealDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = uiState.dateTime,
                                        color = TealDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val dest = if (uiState.destination.isBlank()) "Mahabalipuram" else uiState.destination
                                    val start = if (uiState.origin.isBlank()) "Chennai Central" else uiState.origin
                                    onPlanJourney(start, dest, uiState.dateTime)
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
                        Triple("Metro (CMRL)", Icons.Default.DirectionsSubway, "Chennai Metro"),
                        Triple("Bus (MTC/TNSTC)", Icons.Default.DirectionsBus, "MTC Bus"),
                        Triple("Rail (SR)", Icons.Default.DirectionsTransit, "Southern Railway"),
                        Triple("Cycling", Icons.AutoMirrored.Filled.DirectionsBike, "Smart Cycle Track"),
                        Triple("Walking", Icons.AutoMirrored.Filled.DirectionsWalk, "Heritage Corridor"),
                        Triple("Taxi / Auto", Icons.Default.DirectionsCar, "Prepaid Auto")
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(transportShortcuts) { (label, icon, _) ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PastelCardBlue)
                                    .border(1.dp, SkyBlueSoft, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = SkyBluePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
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

                // AI Travel Adviser & Multi-Day Tour Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PastelCardBlue)
                        .border(1.dp, SkyBluePrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .clickable { onOpenAiTravelAdvisor() }
                        .padding(14.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceWhite)
                                    .border(1.dp, SkyBluePrimary.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SkyBluePrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(EmeraldGreen)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "FREE ADVISER",
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI Multi-Day Trip & Stay Planner",
                                        color = TextNavyDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Plan 1-7 days with en-route spots & night stays (e.g. Chennai ➔ Madurai 5 Days)",
                                    color = TextNavyBody,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(SkyBluePrimary)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Plan", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = "➔", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }

                // Group Travel Feature Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PastelCardTeal)
                        .border(1.dp, TealLight, RoundedCornerShape(16.dp))
                        .clickable { onOpenGroupTravel() }
                        .padding(14.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceWhite)
                                    .border(1.dp, TealSoft, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = TealDark,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "NEW",
                                        color = WarmOrangeDark,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Group Travel & Live Map",
                                        color = TextNavyDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Coordinate family trips from different starting points",
                                    color = TextNavyBody,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(TealSecondary)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Open", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = "➔", color = Color.White, fontSize = 10.sp)
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
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
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
                        val placesToShow = if (showAllPlaces || displayedPlaces.size <= 8) displayedPlaces else displayedPlaces.take(8)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            placesToShow.forEach { place ->
                                TouristPlaceExploreCard(
                                    place = place,
                                    onViewDetails = { onOpenPlaceDetail(place) },
                                    onToggleSave = { viewModel.toggleSavePlace(context, place) },
                                    onPlanHere = {
                                        viewModel.setQuickDestination(place.name)
                                        val start = if (uiState.origin.isBlank()) "Chennai Central" else uiState.origin
                                        onPlanJourney(start, place.name, uiState.dateTime)
                                    }
                                )
                            }

                            if (displayedPlaces.size > 8) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(PastelCardBlue)
                                        .border(1.dp, SkyBlueSoft, RoundedCornerShape(10.dp))
                                        .clickable { showAllPlaces = !showAllPlaces }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (showAllPlaces) "Show less attractions ▲" else "Show all (${displayedPlaces.size}) attractions ➔",
                                        color = SkyBluePrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
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
                                onPlanJourney("Chennai Central", uiState.recentDestination, uiState.dateTime)
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
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = TealDark,
                                    modifier = Modifier.size(16.dp)
                                )
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
                    Triple("Explore", Icons.Default.Explore, true),
                    Triple("My trips", Icons.Default.ConfirmationNumber, false),
                    Triple("Saved", Icons.Default.Bookmark, false),
                    Triple("Profile", Icons.Default.Person, false)
                )
                tabs.forEach { (label, icon, isActive) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onNavigateTab(label)
                        }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isActive) SkyBluePrimary else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
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

        if (showWeatherDialog) {
            WeatherDetailsDialog(
                uiState = uiState,
                onDismiss = { showWeatherDialog = false },
                onRefresh = { viewModel.refreshWeather() }
            )
        }

        if (showTimePickerDialog) {
            DepartureTimeDialog(
                currentDateTime = uiState.dateTime,
                onDismiss = { showTimePickerDialog = false },
                onTimeSelected = { viewModel.updateDateTime(it) }
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
                                .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                                .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                                .build(),
                            contentDescription = place.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = SkyBluePrimary,
                                modifier = Modifier.size(28.dp)
                            )
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
                            Icon(
                                imageVector = if (place.isSaved) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (place.isSaved) Color(0xFFEF4444) else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = SkyBluePrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${place.city} · ${place.category}",
                            color = TextNavyBody,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "★ ${place.rating}", color = WarmOrangeDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = place.entryFee ?: "Free", color = TextMuted, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.NearMe, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "${place.distanceKm} km", color = TextMuted, fontSize = 10.sp)
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

@Composable
private fun WeatherDetailsDialog(
    uiState: HomeUiState,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceWhite)
                .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Top Row: Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Live Climatic Conditions",
                            color = TextNavyDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.weatherCity} · ${uiState.weatherZone}",
                            color = TextMutedSlate,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PastelCardBlue)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Main Weather Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PrimaryGradient)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = uiState.temperature.substringBefore(" ·"),
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.weatherCondition,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        val weatherIcon = when (uiState.weatherIconType) {
                            "sunny" -> Icons.Default.WbSunny
                            "night" -> Icons.Default.Nightlight
                            "cloudy" -> Icons.Default.Cloud
                            "rainy" -> Icons.Default.WaterDrop
                            "breeze" -> Icons.Default.Air
                            else -> Icons.Default.WbSunny
                        }
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = weatherIcon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Climate Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PastelCardTeal)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "Humidity", color = TextMutedSlate, fontSize = 10.sp)
                            Text(text = uiState.weatherHumidity, color = TealDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PastelCardBlue)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "Wind Speed", color = TextMutedSlate, fontSize = 10.sp)
                            Text(text = uiState.weatherWind, color = SkyBlueDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Traveler Climate Advisory
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(16.dp).offset(y = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.weatherAdvisory,
                            color = Color(0xFF92400E),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PastelCardTeal)
                            .clickable { onRefresh() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (uiState.isLoadingWeather) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = TealDark)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Updating...", color = TealDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = TealDark, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Refresh Live Weather", color = TealDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(0.7f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundSoftCream)
                            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Got It", color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DepartureTimeDialog(
    currentDateTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val liveNow = remember { HomeViewModel.getCurrentDynamicTime() }

    val quickOptions = listOf(
        "⚡ Leave Now (Live: ${liveNow.removePrefix("Today, ")})" to liveNow,
        "☀️ Today Morning (09:00 AM)" to "Today, 9:00 AM",
        "🌤️ Today Afternoon (02:00 PM)" to "Today, 2:00 PM",
        "🌙 Tonight (08:30 PM)" to "Today, 8:30 PM",
        "🌅 Tomorrow Early (06:30 AM)" to "Tomorrow, 6:30 AM"
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceWhite)
                .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Journey Departure Time",
                            color = TextNavyDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Currently set: $currentDateTime",
                            color = TealDark,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PastelCardBlue)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = "Quick Departure Options:",
                    color = TextNavyDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    quickOptions.forEach { (label, value) ->
                        val isSelected = currentDateTime == value
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PastelCardTeal else BackgroundSoftCream)
                                .border(1.dp, if (isSelected) TealLight else BorderLight, RoundedCornerShape(10.dp))
                                .clickable {
                                    onTimeSelected(value)
                                    onDismiss()
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) TealDark else TextNavyDark,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(TealDark, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }

                // Custom Time Picker Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryGradient)
                        .clickable {
                            val cal = java.util.Calendar.getInstance()
                            val curHour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                            val curMin = cal.get(java.util.Calendar.MINUTE)
                            android.app.TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val amPm = if (hourOfDay >= 12) "PM" else "AM"
                                    val h = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
                                    val m = String.format(java.util.Locale.US, "%02d", minute)
                                    onTimeSelected("Today, $h:$m $amPm")
                                    onDismiss()
                                },
                                curHour,
                                curMin,
                                false
                            ).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pick Custom Clock Time ➔",
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
