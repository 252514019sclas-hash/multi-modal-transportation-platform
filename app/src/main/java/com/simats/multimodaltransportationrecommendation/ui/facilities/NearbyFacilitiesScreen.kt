package com.simats.multimodaltransportationrecommendation.ui.facilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.facilities.TravelFacility
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun NearbyFacilitiesScreen(
    origin: String = "Chennai",
    destination: String = "Madurai",
    initialCity: String? = null,
    initialCategory: String? = null,
    viewModel: NearbyFacilitiesViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOpenStopover: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(origin, destination, initialCity, initialCategory) {
        viewModel.initParams(
            origin = origin,
            destination = destination,
            initialCity = initialCity ?: destination,
            initialCategory = initialCategory
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
            // 1. TOP APP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(2.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "←",
                        color = TextNavyDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "Verified Tourist Spot Services",
                    color = TextNavyDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SkyBlueLight)
                        .border(1.dp, SkyBluePrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .clickable { onOpenStopover() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "✨ Stopovers",
                        color = SkyBluePrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. ROUTE & ACTIVE HUB BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryGradient)
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${uiState.origin.replaceFirstChar { it.uppercase() }} ➔ ${uiState.destination.replaceFirstChar { it.uppercase() }}",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val spotSub = if (!uiState.selectedLandmark.isNullOrBlank()) {
                                "Focusing: ${uiState.selectedLandmark} (${uiState.totalFacilities} facilities)"
                            } else {
                                "Viewing facilities near: ${uiState.selectedCity} (${uiState.totalFacilities} verified)"
                            }
                            Text(
                                text = spotSub,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "REAL-TIME GPS",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Quick City Hub Selector Chips
                    if (uiState.availableCities.size > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            uiState.availableCities.forEach { city ->
                                val isSelected = uiState.selectedCity.equals(city, ignoreCase = true)
                                val label = if (city.equals(uiState.destination, ignoreCase = true)) {
                                    "📍 $city (Destination)"
                                } else if (city.equals(uiState.origin, ignoreCase = true)) {
                                    "📍 $city (Origin)"
                                } else {
                                    "📍 $city"
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) SurfaceWhite else Color.White.copy(alpha = 0.18f))
                                        .clickable { viewModel.selectCity(city) }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) SkyBlueDark else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Quick Action: AI Smart Stopovers & Day Plan Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.20f))
                            .clickable { onOpenStopover() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "✨", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI Smart Stopovers & Day Plan",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Explore ➔",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. TOURIST SPOT / ATTRACTION SELECTOR CHIPS
            if (uiState.availableLandmarks.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "🏛️ Select Tourist Spot in ${uiState.selectedCity}:",
                        color = TextNavyDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isAllSelected = uiState.selectedLandmark == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAllSelected) SkyBluePrimary else SurfaceWhite)
                                .border(1.dp, if (isAllSelected) SkyBluePrimary else BorderLight, RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectLandmark(null) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "✨ All Attractions",
                                color = if (isAllSelected) Color.White else TextNavyDark,
                                fontSize = 11.sp,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        uiState.availableLandmarks.forEach { landmark ->
                            val isSel = uiState.selectedLandmark.equals(landmark, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) TealSecondary else SurfaceWhite)
                                    .border(1.dp, if (isSel) TealSecondary else BorderLight, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.selectLandmark(landmark) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "🏛️ $landmark",
                                    color = if (isSel) Color.White else TextNavyDark,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // 4. SEARCH INPUT BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "Search by tourist place e.g. Rockfort, Srirangam, Marina, Meenakshi...",
                            color = TextMutedSlate,
                            fontSize = 11.sp
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite,
                        focusedBorderColor = SkyBluePrimary,
                        unfocusedBorderColor = BorderLight
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = TextNavyDark)
                )
            }

            // 5. CATEGORY FILTER TABS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val defaultCats = listOf(
                    Triple("all", "All Facilities", "📍"),
                    Triple("Petrol Stations", "Petrol", "⛽"),
                    Triple("EV Charging Stations", "EV Fast Charging", "⚡"),
                    Triple("Parking Areas", "Parking", "🅿️"),
                    Triple("Restaurants and Restrooms", "Food & Restrooms", "🍽️"),
                    Triple("Hospitals and ATMs", "Hospitals & ATMs", "🏥")
                )

                defaultCats.forEach { (catId, label, icon) ->
                    val isSelected = uiState.selectedCategory == catId
                    val count = uiState.categories.find { it.id == catId }?.count ?: 0
                    val displayCount = if (count > 0) " ($count)" else ""

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) SkyBluePrimary else SurfaceWhite)
                            .border(1.dp, if (isSelected) SkyBluePrimary else BorderLight, RoundedCornerShape(14.dp))
                            .clickable { viewModel.selectCategory(catId) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "$icon $label$displayCount",
                            color = if (isSelected) Color.White else TextNavyDark,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                        )
                    }
                }
            }

            // 6. FACILITIES LIST CONTENT
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = SkyBluePrimary, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Locating facilities at ${uiState.selectedLandmark ?: uiState.selectedCity}...",
                            color = TextMutedSlate,
                            fontSize = 12.sp
                        )
                    }
                }
            } else if (uiState.facilities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(text = "🔍", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No facilities match your search filter",
                            color = TextNavyDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing the search query or selecting 'All Facilities'.",
                            color = TextMutedSlate,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 6.dp, bottom = 32.dp)
                ) {
                    items(uiState.facilities, key = { it.facilityId }) { facility ->
                        FacilityCardItem(
                            facility = facility
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FacilityCardItem(
    facility: TravelFacility
) {
    val categoryBadgeColor = when (facility.category) {
        "Petrol Stations" -> Color(0xFFD97706) // Amber / Fuel
        "EV Charging Stations" -> Color(0xFF059669) // Emerald / EV
        "Parking Areas" -> Color(0xFF4F46E5) // Indigo / Parking
        "Restaurants and Restrooms" -> Color(0xFFEA580C) // Sunset Orange / Dining
        "Hospitals and ATMs" -> Color(0xFFDC2626) // Crimson / Emergency
        else -> SkyBlueDark
    }

    val categoryIcon = when (facility.category) {
        "Petrol Stations" -> "⛽"
        "EV Charging Stations" -> "⚡"
        "Parking Areas" -> "🅿️"
        "Restaurants and Restrooms" -> "🍽️"
        "Hospitals and ATMs" -> "🏥"
        else -> "📍"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Category Badge & Distance Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(categoryBadgeColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$categoryIcon ${facility.category}",
                        color = categoryBadgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PastelCardTeal)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "📍 ${facility.distanceKm} km away",
                            color = TealDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "⭐ ${facility.rating}",
                        color = TextNavyDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Facility Name
            Text(
                text = facility.name,
                color = TextNavyDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Particular Tourist Spot Badge
            if (!facility.nearbyLandmark.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PastelCardBlue)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🏛️ At Tourist Spot: ${facility.nearbyLandmark}",
                        color = SkyBlueDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Real Landmark & Address
            Row(verticalAlignment = Alignment.Top) {
                Text(text = "🗺️ ", fontSize = 11.sp)
                Text(
                    text = facility.address,
                    color = TextMutedSlate,
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Operating Hours / Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                val statusText = if (facility.is24Hours) "🟢 Open 24/7" else "🕒 ${facility.operatingHours}"
                Text(
                    text = statusText,
                    color = if (facility.is24Hours) Color(0xFF15803D) else TextNavyDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Amenities Tags
            if (!facility.amenities.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundSoftCream)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "✨ ${facility.amenities}",
                        color = TextNavyDark,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

// Helpers for Intents
fun launchGoogleMaps(context: Context, lat: Double, lng: Double, label: String) {
    try {
        val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(label)})")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            // Fallback to browser
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng"))
            context.startActivity(webIntent)
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to launch map navigation", Toast.LENGTH_SHORT).show()
    }
}
