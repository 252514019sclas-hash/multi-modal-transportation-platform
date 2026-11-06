package com.simats.multimodaltransportationrecommendation.ui.destination

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.location.LocationSearchResult
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun DestinationScreen(
    initialOrigin: String = "Chennai Central",
    initialDestination: String = "Mahabalipuram",
    viewModel: DestinationViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSearchRoutes: (String, String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(initialOrigin, initialDestination) {
        viewModel.setInitialLocations(initialOrigin, initialDestination)
    }

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearStatusMessage()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.useCurrentLocation(context)
        } else {
            Toast.makeText(context, "Location permission is required for GPS detection", Toast.LENGTH_SHORT).show()
        }
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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = TextNavyDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "TAMIL NADU TRANSIT & TOURISM",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Find Multi-Modal Route",
                            color = TextNavyDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Origin & Destination Input Card with Swap Action
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Starting Point (Origin)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "STARTING FROM (ORIGIN)",
                                color = TextNavyBody,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PastelCardBlue)
                                    .border(1.dp, SkyBlueSoft, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📍", fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = uiState.origin,
                                    onValueChange = { viewModel.onOriginChange(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Medium),
                                    cursorBrush = SolidColor(SkyBluePrimary),
                                    decorationBox = { inner ->
                                        if (uiState.origin.isEmpty()) {
                                            Text(text = "e.g. Chennai Central, Madurai...", color = TextLight, fontSize = 13.sp)
                                        }
                                        inner()
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                if (uiState.origin.isNotEmpty()) {
                                    Text(
                                        text = "✕",
                                        color = TextMuted,
                                        fontSize = 13.sp,
                                        modifier = Modifier
                                            .clickable { viewModel.onOriginChange("") }
                                            .padding(horizontal = 4.dp)
                                    )
                                }

                                Text(
                                    text = "GPS",
                                    color = SkyBluePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SkyBlueLight)
                                        .clickable {
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
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
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
                                    .size(36.dp)
                                    .shadow(2.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(PastelCardTeal)
                                    .border(1.5.dp, TealSecondary, CircleShape)
                                    .clickable { viewModel.swapLocations() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⇅", color = TealDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Destination Input
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "DESTINATION (TAMIL NADU)",
                                color = TextNavyBody,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceWhite)
                                    .border(1.dp, SkyBluePrimary, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎯", fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = uiState.destination,
                                    onValueChange = { viewModel.onDestinationChange(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                                    cursorBrush = SolidColor(SkyBluePrimary),
                                    decorationBox = { inner ->
                                        if (uiState.destination.isEmpty()) {
                                            Text(text = "e.g. Mahabalipuram, Ooty...", color = TextLight, fontSize = 13.sp)
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
                                        modifier = Modifier
                                            .clickable { viewModel.onDestinationChange("") }
                                            .padding(horizontal = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Autocomplete Suggestions List
                if (uiState.isSearching) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = SkyBluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Searching Tamil Nadu transit stops...", color = TextMuted, fontSize = 11.sp)
                    }
                }

                if (uiState.suggestions.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "MATCHING LOCATIONS",
                                color = TextMutedSlate,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            uiState.suggestions.forEach { loc ->
                                LocationSuggestionItem(
                                    location = loc,
                                    onClick = { viewModel.selectSuggestion(loc) }
                                )
                            }
                        }
                    }
                }

                // Popular Tamil Nadu Destinations Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Popular Tamil Nadu Destinations",
                        color = TextNavyDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.popularDestinations) { dest ->
                            val isSelected = uiState.destination.equals(dest, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) SkyBluePrimary else SurfaceWhite)
                                    .border(
                                        1.dp,
                                        if (isSelected) SkyBluePrimary else BorderLight,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.selectPopularDestination(dest) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dest,
                                    color = if (isSelected) Color.White else TextNavyDark,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Recent Searches
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Recent Tamil Nadu Searches",
                        color = TextNavyDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    uiState.recentSearches.forEach { (name, district) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.onDestinationChange(name)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🕒", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = name, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "$district, Tamil Nadu", color = TextMutedSlate, fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom CTA: Search Routes Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
            ) {
                val canSearch = uiState.origin.isNotBlank() && uiState.destination.isNotBlank()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(if (canSearch) PrimaryGradient else SolidColor(Color(0xFFCBD5E1)))
                        .clickable(enabled = canSearch) {
                            onSearchRoutes(uiState.origin.trim(), uiState.destination.trim())
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Search Multi-Modal Routes  ➔",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationSuggestionItem(
    location: LocationSearchResult,
    onClick: () -> Unit
) {
    val icon = when (location.category.lowercase()) {
        "railway station" -> "🚆"
        "bus terminal", "bus stop" -> "🚌"
        "tourist attraction", "tourist destination" -> "🏛️"
        "hill station" -> "⛰️"
        else -> "📍"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.name,
                color = TextNavyDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = location.displayName,
                color = TextMutedSlate,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(PastelCardBlue)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = location.category,
                color = SkyBluePrimary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
