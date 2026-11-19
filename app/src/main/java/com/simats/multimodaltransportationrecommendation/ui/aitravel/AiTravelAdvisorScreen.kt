package com.simats.multimodaltransportationrecommendation.ui.aitravel

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.simats.multimodaltransportationrecommendation.data.model.ai.AiDayPlan
import com.simats.multimodaltransportationrecommendation.data.model.ai.AiScheduleSlot
import com.simats.multimodaltransportationrecommendation.data.model.ai.RecommendedStay
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.ui.theme.*
import java.util.Locale

@Composable
fun AiTravelAdvisorScreen(
    initialOrigin: String = "Chennai",
    initialDestination: String = "Madurai",
    viewModel: AiTravelAdvisorViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {},
    onOpenPlaceDetail: (TouristPlace) -> Unit = {},
    onViewMyTrips: () -> Unit = {},
    onOpenNearbyFacilities: (origin: String, destination: String, targetCity: String?, category: String?) -> Unit = { _, _, _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(initialOrigin, initialDestination) {
        viewModel.initRoute(initialOrigin, initialDestination)
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
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. TOP BAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MOVESMART AI ADVISER",
                                color = SkyBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "Smart Multi-Day Trip Planner",
                                color = TextNavyDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Top Action Buttons: Facilities & My Trips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PastelCardBlue)
                                .clickable { onOpenNearbyFacilities(uiState.origin, uiState.destination, uiState.destination, null) }
                                .padding(horizontal = 9.dp, vertical = 6.dp)
                        ) {
                            Text(text = "Facilities ⛽⚡", color = SkyBlueDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PastelCardTeal)
                                .clickable { onViewMyTrips() }
                                .padding(horizontal = 9.dp, vertical = 6.dp)
                        ) {
                            Text(text = "My Trips ➔", color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Notice Banner if active
                uiState.userNotice?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PastelCardBlue)
                            .border(1.dp, SkyBluePrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg,
                                color = SkyBlueDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "✕",
                                color = TextMutedSlate,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { viewModel.dismissNotice() }
                            )
                        }
                    }
                }

                // 2. TRIP CONFIGURATION CARD
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Plan Your Route & Duration",
                            color = TextNavyDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Origin & Destination Inputs with Swap
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Starting From", fontSize = 10.sp, color = TextMutedSlate, fontWeight = FontWeight.SemiBold)
                                OutlinedTextField(
                                    value = uiState.origin,
                                    onValueChange = { viewModel.setOrigin(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextNavyDark),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SkyBluePrimary,
                                        unfocusedBorderColor = BorderLight
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .padding(top = 14.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PastelCardBlue)
                                    .clickable { viewModel.swapOriginDestination() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⇄", color = SkyBluePrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Final Destination", fontSize = 10.sp, color = TextMutedSlate, fontWeight = FontWeight.SemiBold)
                                OutlinedTextField(
                                    value = uiState.destination,
                                    onValueChange = { viewModel.setDestination(it) },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextNavyDark),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SkyBluePrimary,
                                        unfocusedBorderColor = BorderLight
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        // Duration Selector Chips
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Trip Duration (Days)", fontSize = 11.sp, color = TextMutedSlate, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(1, 2, 3, 4, 5, 6, 7).forEach { d ->
                                    val isSelected = uiState.selectedDays == d
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSelected) SkyBluePrimary else SurfaceWhite)
                                            .border(1.dp, if (isSelected) SkyBluePrimary else BorderLight, RoundedCornerShape(14.dp))
                                            .clickable { viewModel.setSelectedDays(d) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "$d ${if (d == 1) "Day" else "Days"}",
                                            color = if (isSelected) Color.White else TextNavyDark,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Travel Theme Chips
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Trip Style / Preferences", fontSize = 11.sp, color = TextMutedSlate, fontWeight = FontWeight.SemiBold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Balanced", "Heritage & Family", "Nature & Hills", "Spiritual & Temples", "Food & Culture").forEach { theme ->
                                    val isSelected = uiState.selectedTheme == theme
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isSelected) TealSecondary else SurfaceWhite)
                                            .border(1.dp, if (isSelected) TealSecondary else BorderLight, RoundedCornerShape(14.dp))
                                            .clickable { viewModel.setSelectedTheme(theme) }
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = theme,
                                            color = if (isSelected) Color.White else TextNavyDark,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // Stay Preference Chips (Advisory)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Night Stay Preference", fontSize = 11.sp, color = TextMutedSlate, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "(Advisory only · No booking needed)", fontSize = 10.sp, color = TealDark)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Budget", "Mid-Range", "Heritage & Luxury").forEach { tier ->
                                    val isSelected = uiState.stayPreference == tier
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) PastelCardTeal else SurfaceWhite)
                                            .border(1.dp, if (isSelected) TealSecondary else BorderLight, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.setStayPreference(tier) }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = tier,
                                            color = if (isSelected) TealDark else TextNavyDark,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // Re-Generate Button
                        Button(
                            onClick = { viewModel.generateItinerary() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "AI Planning Itinerary...", fontSize = 12.sp, color = Color.White)
                            } else {
                                Text(
                                    text = "✨ Generate Itinerary for ${uiState.selectedDays} Days",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 3. TRIP OVERVIEW BANNER (IF PLAN IS LOADED)
                uiState.tripPlan?.tripSummary?.let { summary ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp))
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
                                Column {
                                    Text(
                                        text = "${summary.origin} ➔ ${summary.destination}",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${summary.totalDays} Days · ~${summary.totalDistanceKm} km · ${summary.tripTheme}",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 11.sp
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(SurfaceWhite)
                                        .clickable { viewModel.saveItineraryToMyTrips(context) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (uiState.isSaving) "Saving..." else "💾 Save Plan",
                                        color = SkyBlueDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "✓ ${summary.totalAttractionsPlanned} En-route Attractions · ${summary.totalDays} Night Stays",
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "FREE ADVISORY",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. DAY TABS SELECTOR
                uiState.tripPlan?.dailyItinerary?.let { daysList ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Daily Tour Itinerary & Stays",
                            color = TextNavyDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            daysList.forEachIndexed { idx, day ->
                                val isSelected = uiState.activeDayIndex == idx
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) SkyBluePrimary else SurfaceWhite)
                                        .border(1.dp, if (isSelected) SkyBluePrimary else BorderLight, RoundedCornerShape(12.dp))
                                        .clickable { viewModel.setActiveDayIndex(idx) }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Day ${day.dayNumber}",
                                            color = if (isSelected) Color.White else TextNavyDark,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = day.hubCity,
                                            color = if (isSelected) Color.White.copy(alpha = 0.85f) else TextMutedSlate,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }

                        // ACTIVE DAY PLAN DISPLAY
                        val activeDay = daysList.getOrNull(uiState.activeDayIndex) ?: daysList.firstOrNull()
                        activeDay?.let { day ->
                            DayPlanCard(
                                day = day,
                                onOpenPlaceDetail = onOpenPlaceDetail
                            )
                        }

                        // 5. NEARBY TRAVEL FACILITIES (Petrol, EV, Parking, Restrooms, Hospitals, ATMs)
                        NearbyFacilitiesPromoCard(
                            destination = uiState.destination,
                            hubCity = activeDay?.hubCity ?: uiState.destination,
                            onExploreAll = {
                                onOpenNearbyFacilities(uiState.origin, uiState.destination, activeDay?.hubCity ?: uiState.destination, null)
                            },
                            onSelectCategory = { cat ->
                                onOpenNearbyFacilities(uiState.origin, uiState.destination, activeDay?.hubCity ?: uiState.destination, cat)
                            }
                        )

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DayPlanCard(
    day: AiDayPlan,
    onOpenPlaceDetail: (TouristPlace) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Day Header & Route Segment
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceWhite)
                .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Day ${day.dayNumber}",
                        color = SkyBlueDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PastelCardTeal)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = day.themeFocus, color = TealDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = day.dayTitle,
                    color = TextNavyDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "🚗 Route: ${day.travelSegment}",
                    color = TextMutedSlate,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // RECOMMENDED NIGHT STAY CARD
        RecommendedNightStayCard(stay = day.recommendedNightStay)

        // SCHEDULE SLOTS: MORNING, AFTERNOON, EVENING
        Text(
            text = "Planned Tourist Attractions Along Route",
            color = TextNavyDark,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        day.schedule.forEach { slot ->
            ScheduleSlotCard(
                slot = slot,
                onViewDetails = {
                    val p = TouristPlace(
                        placeId = (slot.placeName.hashCode() % 1000).let { if (it < 0) -it else it },
                        name = slot.placeName,
                        city = slot.city,
                        district = slot.district,
                        category = slot.category,
                        description = slot.description,
                        imageUrl = slot.imageUrl,
                        locationAddress = "${slot.city}, Tamil Nadu",
                        visitingHours = "9:00 AM - 6:00 PM",
                        entryFee = "Free",
                        rating = slot.rating,
                        distanceKm = 10.0,
                        durationVisit = slot.duration,
                        latitude = null,
                        longitude = null,
                        nearbyTransit = slot.transitAdvice
                    )
                    onOpenPlaceDetail(p)
                }
            )
        }

        // HOW TO ENJOY THIS DAY - ADVISORY BOX
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PastelCardTeal)
                .border(1.dp, TealSecondary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "💡 How to Enjoy Day ${day.dayNumber}",
                    color = TealDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.Top) {
                    Text(text = "🍲 ", fontSize = 14.sp)
                    Column {
                        Text(text = "Local Famous Delicacy:", color = TextNavyDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = day.culinaryDelicacy, color = TextNavyBody, fontSize = 11.sp)
                    }
                }

                Row(verticalAlignment = Alignment.Top) {
                    Text(text = "📸 ", fontSize = 14.sp)
                    Column {
                        Text(text = "Sightseeing & Photo Moment:", color = TextNavyDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(text = day.enjoymentAdvice, color = TextNavyBody, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendedNightStayCard(stay: RecommendedStay) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceWhite)
            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🏨", fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Recommended Night Stay (${stay.city})",
                        color = TextNavyDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PastelCardTeal)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "Advisory", color = TealDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Stay Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BorderSubtle)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(stay.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = stay.stayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = stay.stayName,
                        color = TextNavyDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${stay.stayType} · ${stay.locationAddress}",
                        color = TextMutedSlate,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐ ${stay.rating}", color = WarmOrangeDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stay.priceRange, color = TealSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    stay.amenities?.let {
                        Text(
                            text = "✨ $it",
                            color = TextNavyBody,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            stay.whyRecommended?.let { note ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PastelCardBlue.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Why this stay: $note",
                        color = SkyBlueDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleSlotCard(
    slot: AiScheduleSlot,
    onViewDetails: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceWhite)
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // Slot Period Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (slot.period) {
                        "Morning" -> "🌅"
                        "Afternoon" -> "☀️"
                        else -> "🌆"
                    }
                    Text(text = icon, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = slot.slot,
                        color = SkyBlueDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BorderSubtle)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = slot.category, color = TextNavyDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Place Image
                Box(
                    modifier = Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BorderSubtle)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(slot.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = slot.placeName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = slot.placeName,
                        color = TextNavyDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "📍 ${slot.district} · Visit: ${slot.duration}",
                        color = TextMutedSlate,
                        fontSize = 10.sp
                    )
                    Text(
                        text = slot.description,
                        color = TextNavyBody,
                        fontSize = 10.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚗 ${slot.transitAdvice}",
                    color = TealDark,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PastelCardBlue)
                        .clickable { onViewDetails() }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "Details →", color = SkyBlueDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NearbyFacilitiesPromoCard(
    destination: String,
    hubCity: String,
    onExploreAll: () -> Unit,
    onSelectCategory: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⛽ ⚡ 🅿️ 🍽️ 🏥 ", fontSize = 13.sp)
                        Text(
                            text = "Nearby Travel Facilities",
                            color = TextNavyDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Essential services around $hubCity & travel route",
                        color = TextMutedSlate,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PastelCardTeal)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = "VERIFIED GPS", color = TealDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Quick Category Clickable Badges
            val facilityChips = listOf(
                Pair("Petrol Stations", "⛽ Petrol"),
                Pair("EV Charging Stations", "⚡ EV Fast"),
                Pair("Parking Areas", "🅿️ Parking"),
                Pair("Restaurants and Restrooms", "🍽️ Food/Rest"),
                Pair("Hospitals and ATMs", "🏥 Hospital/ATM")
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                facilityChips.forEach { (catId, label) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundSoftCream)
                            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                            .clickable { onSelectCategory(catId) }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = label,
                            color = TextNavyDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Main Explore Button
            Button(
                onClick = { onExploreAll() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
            ) {
                Text(
                    text = "Explore Nearby Facilities in $hubCity ➔",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
