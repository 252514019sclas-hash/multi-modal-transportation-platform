package com.simats.multimodaltransportationrecommendation.ui.stopover

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.simats.multimodaltransportationrecommendation.data.model.stopover.*
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun AiSmartStopoverScreen(
    origin: String = "Chennai",
    destination: String = "Salem",
    onBack: () -> Unit = {},
    onViewRouteMap: () -> Unit = {},
    onStartJourney: () -> Unit = {}
) {
    val context = LocalContext.current

    // Generate comprehensive Tamil Nadu stopovers and attractions from origin to destination
    val basePlan = remember(origin, destination) {
        AiStopoverEngine.generateStopoverPlan(origin, destination)
    }

    var planState by remember(basePlan) { mutableStateOf(basePlan) }
    var showDetourDialog by remember { mutableStateOf(false) }
    var selectedStopDetail by remember { mutableStateOf<AiStopoverItem?>(null) }

    // Color definitions for app's signature Sky Blue & Teal theme
    val appPrimaryBlue = SkyBluePrimary // 0xFF0284C7
    val appTealSecondary = TealSecondary // 0xFF0D9488
    val appLightTint = SkyBlueLight // 0xFFE0F2FE
    val appTealLight = TealLight // 0xFFCCFBF1
    val appCardBorder = BorderLight // 0xFFE2E8F0
    val appNavyDark = TextNavyDark // 0xFF0F172A
    val appSlateMuted = TextMutedSlate // 0xFF64748B

    val botCardGradient = Brush.linearGradient(
        listOf(Color(0xFFE0F2FE), Color(0xFFF0FDFA), Color(0xFFFFFFFF))
    )

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
            // ==========================================
            // 1. TOP APP BAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Back Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(2.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.dp, appCardBorder, CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "‹",
                        color = appNavyDark,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(y = (-2).dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title & Subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Smart Stopover",
                        color = appNavyDark,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "All tourist spots from ${planState.origin} to ${planState.destination} ✨",
                        color = appSlateMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // AI Sparkle Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(2.dp, CircleShape)
                        .clip(CircleShape)
                        .background(appLightTint)
                        .border(1.dp, appPrimaryBlue.copy(alpha = 0.3f), CircleShape)
                        .clickable {
                            Toast.makeText(context, "${planState.stops.size} tourist spots curated on your route ✨", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✨", fontSize = 18.sp)
                }
            }

            // ==========================================
            // 2. SCROLLABLE DAY PLAN & TOURIST SPOTS
            // ==========================================
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Route Indicator Card: Origin -> Destination
                item {
                    RouteIndicatorCard(
                        origin = planState.origin,
                        destination = planState.destination,
                        appNavyDark = appNavyDark,
                        appSlateMuted = appSlateMuted,
                        appPrimaryBlue = appPrimaryBlue,
                        appCardBorder = appCardBorder
                    )
                }

                // AI Detour Suggestions Banner
                if (planState.detourOptions.isNotEmpty()) {
                    item {
                        AiDetourBanner(
                            detourCount = planState.detourOptions.size,
                            botCardGradient = botCardGradient,
                            appPrimaryBlue = appPrimaryBlue,
                            appTealSecondary = appTealSecondary,
                            appNavyDark = appNavyDark,
                            appSlateMuted = appSlateMuted,
                            onViewSuggestions = { showDetourDialog = true }
                        )
                    }
                }

                // AI Route Optimization Indicator Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(appTealLight.copy(alpha = 0.5f))
                            .border(1.dp, appTealSecondary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚡", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "AI Route Optimization",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appTealSecondary
                                )
                                Text(
                                    text = planState.routeOptimizationNote,
                                    fontSize = 11.sp,
                                    color = appNavyDark,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // "Your Day Plan" Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Your Day Plan",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = appNavyDark
                            )
                        }

                        // Spots Count & Travel Time Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF1F5F9))
                                .border(1.dp, appCardBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📍", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${planState.stops.size} Tourist Spots · ${planState.totalTravelTimeText}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = appNavyDark
                                )
                            }
                        }
                    }
                }

                // ALL TOURIST SPOTS CONNECTED TIMELINE
                itemsIndexed(planState.stops) { index, stopItem ->
                    val isFirst = index == 0
                    val isLast = index == planState.stops.lastIndex

                    TimelineTouristSpotRow(
                        stop = stopItem,
                        spotNumber = index + 1,
                        isFirst = isFirst,
                        isLast = isLast,
                        appPrimaryBlue = appPrimaryBlue,
                        appTealSecondary = appTealSecondary,
                        appNavyDark = appNavyDark,
                        appSlateMuted = appSlateMuted,
                        appCardBorder = appCardBorder,
                        appLightTint = appLightTint,
                        onClick = { selectedStopDetail = stopItem }
                    )
                }
            }
        }

        // ==========================================
        // 3. DIALOGS (DETOUR & SPOT DETAIL)
        // ==========================================

        // DETOUR SUGGESTIONS MODAL
        if (showDetourDialog) {
            DetourSuggestionsDialog(
                detours = planState.detourOptions,
                appPrimaryBlue = appPrimaryBlue,
                appTealSecondary = appTealSecondary,
                appNavyDark = appNavyDark,
                appSlateMuted = appSlateMuted,
                onDismiss = { showDetourDialog = false },
                onApplyDetour = { detour ->
                    val currentStops = planState.stops.toMutableList()
                    val destStop = currentStops.removeLastOrNull()
                    detour.places.forEach { detourPlace ->
                        currentStops.add(detourPlace)
                    }
                    if (destStop != null) currentStops.add(destStop)

                    planState = planState.copy(
                        stops = currentStops
                    )
                    showDetourDialog = false
                    Toast.makeText(context, "Added '${detour.title}' to Day Plan!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // STOP DETAIL MODAL
        selectedStopDetail?.let { stop ->
            StopDetailDialog(
                stop = stop,
                appPrimaryBlue = appPrimaryBlue,
                appNavyDark = appNavyDark,
                appSlateMuted = appSlateMuted,
                onDismiss = { selectedStopDetail = null },
                onNavigate = { lat, lng, name ->
                    launchGoogleMaps(context, lat, lng, name)
                }
            )
        }
    }
}

// ==========================================
// SUB-COMPONENTS
// ==========================================

@Composable
private fun RouteIndicatorCard(
    origin: String,
    destination: String,
    appNavyDark: Color,
    appSlateMuted: Color,
    appPrimaryBlue: Color,
    appCardBorder: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, appCardBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Origin
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📍", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = origin,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = appNavyDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "(Starting Point)",
                        fontSize = 10.sp,
                        color = appSlateMuted
                    )
                }
            }

            // Transit Icon in dashed divider
            Row(
                modifier = Modifier.padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "- - -", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(SkyBlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚌", fontSize = 13.sp)
                }
                Text(text = "- - -", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Destination
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = destination,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = appNavyDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "(Destination)",
                        fontSize = 10.sp,
                        color = appSlateMuted
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "📍", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun AiDetourBanner(
    detourCount: Int,
    botCardGradient: Brush,
    appPrimaryBlue: Color,
    appTealSecondary: Color,
    appNavyDark: Color,
    appSlateMuted: Color,
    onViewSuggestions: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(botCardGradient)
                .border(1.dp, appPrimaryBlue.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI Robot Avatar Illustration
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.5.dp, appPrimaryBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🤖", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Detour Pitch
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Take a short detour!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = appNavyDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Explore $detourCount scenic attractions on your way with minimal extra time!",
                        fontSize = 11.sp,
                        color = appSlateMuted,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Pill Button: View Suggestions ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(listOf(appPrimaryBlue, appTealSecondary))
                            )
                            .clickable { onViewSuggestions() }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "View Suggestions ➔",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineTouristSpotRow(
    stop: AiStopoverItem,
    spotNumber: Int,
    isFirst: Boolean,
    isLast: Boolean,
    appPrimaryBlue: Color,
    appTealSecondary: Color,
    appNavyDark: Color,
    appSlateMuted: Color,
    appCardBorder: Color,
    appLightTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.Top
    ) {
        // Vertical Timeline Track (Left Column)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(28.dp)
                .padding(top = 12.dp)
        ) {
            // Node Circle with spot number or indicator
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (isFirst || isLast) appPrimaryBlue else appTealSecondary)
                    .border(2.5.dp, SurfaceWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isFirst) "S" else if (isLast) "D" else "$spotNumber",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Connecting Vertical Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.5.dp)
                        .height(115.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    if (isFirst) appPrimaryBlue else appTealSecondary,
                                    appPrimaryBlue.copy(alpha = 0.35f)
                                )
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Content Card for Tourist Spot
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, appCardBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                // Header row: Badge + Distance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge (Start Hub, Spot #1 • 1h, Destination Attraction)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when {
                                    isFirst -> appLightTint
                                    isLast -> TealLight
                                    else -> Color(0xFFF1F5F9)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = stop.badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isFirst -> appPrimaryBlue
                                isLast -> appTealSecondary
                                else -> appNavyDark
                            }
                        )
                    }

                    // Distance Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📍", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = stop.distanceBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = appSlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Content Row: Thumbnail + Titles + Schedule + Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Real Thumbnail Image
                    AsyncImage(
                        model = stop.imageUrl,
                        contentDescription = stop.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE2E8F0))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stop.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = appNavyDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            // Rating
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⭐", fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${stop.rating}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // City & Category
                        Text(
                            text = "📍 ${stop.city} · ${stop.category}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = appTealSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        // Description snippet
                        Text(
                            text = stop.shortDescription,
                            fontSize = 11.sp,
                            color = appSlateMuted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        // Time Window / Visiting window
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🕒", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stop.timeWindowText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = appPrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// DIALOG IMPLEMENTATIONS
// ==========================================

@Composable
private fun DetourSuggestionsDialog(
    detours: List<AiDetourOption>,
    appPrimaryBlue: Color,
    appTealSecondary: Color,
    appNavyDark: Color,
    appSlateMuted: Color,
    onDismiss: () -> Unit,
    onApplyDetour: (AiDetourOption) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🤖", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Detour Suggestions",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = appNavyDark
                        )
                    }

                    Text(
                        text = "✕",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appSlateMuted,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                detours.forEach { detour ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                            brush = Brush.horizontalGradient(listOf(appPrimaryBlue.copy(alpha = 0.5f), appTealSecondary.copy(alpha = 0.5f)))
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = detour.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appNavyDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SkyBlueLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+${detour.extraTimeMinutes}m",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = appPrimaryBlue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = detour.subtitle,
                                fontSize = 11.sp,
                                color = appSlateMuted,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "💡 AI Benefit: ${detour.aiBenefit}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = appTealSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = { onApplyDetour(detour) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = appPrimaryBlue)
                            ) {
                                Text(
                                    text = "Add to Journey Itinerary ➔",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StopDetailDialog(
    stop: AiStopoverItem,
    appPrimaryBlue: Color,
    appNavyDark: Color,
    appSlateMuted: Color,
    onDismiss: () -> Unit,
    onNavigate: (Double, Double, String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Image
                AsyncImage(
                    model = stop.imageUrl,
                    contentDescription = stop.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stop.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = appNavyDark,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "⭐ ${stop.rating}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "📍 ${stop.city} · ${stop.category}",
                        fontSize = 11.sp,
                        color = appSlateMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stop.shortDescription,
                        fontSize = 12.sp,
                        color = appNavyDark,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Schedule: ${stop.timeWindowText}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = appPrimaryBlue
                        )
                        Text(
                            text = "Distance: ${stop.distanceBadgeText}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = appSlateMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onNavigate(stop.latitude, stop.longitude, stop.name) },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Navigate ➔", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { onDismiss() },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = appPrimaryBlue)
                        ) {
                            Text(text = "Done", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun launchGoogleMaps(context: Context, lat: Double, lng: Double, label: String) {
    try {
        val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(${Uri.encode(label)})")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng"))
            context.startActivity(webIntent)
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to launch map navigation", Toast.LENGTH_SHORT).show()
    }
}
