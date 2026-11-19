package com.simats.multimodaltransportationrecommendation.ui.stopover

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    destination: String = "Marina Beach",
    onBack: () -> Unit = {},
    onViewRouteMap: () -> Unit = {},
    onStartJourney: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Generate smart stopover plan based on the origin & destination
    val basePlan = remember(origin, destination) {
        AiStopoverEngine.generateStopoverPlan(origin, destination)
    }

    var planState by remember(basePlan) { mutableStateOf(basePlan) }
    var showDetourDialog by remember { mutableStateOf(false) }
    var showOffersDialog by remember { mutableStateOf(false) }
    var showInsightsDialog by remember { mutableStateOf(false) }
    var selectedStopDetail by remember { mutableStateOf<AiStopoverItem?>(null) }

    // Color definitions for app's signature Sky Blue & Teal theme
    val appPrimaryBlue = SkyBluePrimary // 0xFF0284C7
    val appTealSecondary = TealSecondary // 0xFF0D9488
    val appDarkBlue = SkyBlueDark // 0xFF0369A1
    val appLightTint = SkyBlueLight // 0xFFE0F2FE
    val appTealLight = TealLight // 0xFFCCFBF1
    val appCardBorder = BorderLight // 0xFFE2E8F0
    val appNavyDark = TextNavyDark // 0xFF0F172A
    val appSlateMuted = TextMutedSlate // 0xFF64748B

    val primaryHeroGradient = Brush.horizontalGradient(
        listOf(appPrimaryBlue, appTealSecondary)
    )

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
                // Back Button
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
                        text = "Discover amazing places on your route ✨",
                        color = appSlateMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Sparkle Action Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .shadow(2.dp, CircleShape)
                        .clip(CircleShape)
                        .background(appLightTint)
                        .border(1.dp, appPrimaryBlue.copy(alpha = 0.3f), CircleShape)
                        .clickable {
                            Toast.makeText(context, "AI Route Optimization is Active ✨", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✨", fontSize = 18.sp)
                }
            }

            // ==========================================
            // SCROLLABLE CONTENT
            // ==========================================
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ------------------------------------------
                // 2. ROUTE PILL: Origin -> Destination
                // ------------------------------------------
                item {
                    RouteIndicatorCard(
                        origin = planState.origin,
                        destination = planState.destination,
                        transitMode = planState.transitMode,
                        appNavyDark = appNavyDark,
                        appSlateMuted = appSlateMuted,
                        appPrimaryBlue = appPrimaryBlue,
                        appCardBorder = appCardBorder
                    )
                }

                // ------------------------------------------
                // 3. AI DETOUR SUGGESTIONS CARD
                // ------------------------------------------
                item {
                    AiDetourBanner(
                        detourCount = planState.detourOptions.size,
                        extraTimeText = "just 1 hour extra!",
                        botCardGradient = botCardGradient,
                        appPrimaryBlue = appPrimaryBlue,
                        appTealSecondary = appTealSecondary,
                        appNavyDark = appNavyDark,
                        appSlateMuted = appSlateMuted,
                        appCardBorder = appCardBorder,
                        onViewSuggestions = { showDetourDialog = true }
                    )
                }

                // ------------------------------------------
                // 4. "YOUR DAY PLAN" SECTION HEADER
                // ------------------------------------------
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

                        // Total Travel Time Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF1F5F9))
                                .border(1.dp, appCardBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🕒", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Total Travel Time: ${planState.totalTravelTimeText}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = appNavyDark
                                )
                            }
                        }
                    }
                }

                // ------------------------------------------
                // 5. TIMELINE STOPS CONNECTED LIST
                // ------------------------------------------
                itemsIndexed(planState.stops) { index, stopItem ->
                    val isFirst = index == 0
                    val isLast = index == planState.stops.lastIndex

                    TimelineStopRow(
                        stop = stopItem,
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

                // ------------------------------------------
                // 6. AI ROUTE OPTIMIZATION BANNER
                // ------------------------------------------
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

                // ------------------------------------------
                // 7. TRAVEL INSIGHTS SECTION
                // ------------------------------------------
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🧭", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Travel Insights",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appNavyDark
                                )
                            }

                            Text(
                                text = "More Info ➔",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = appPrimaryBlue,
                                modifier = Modifier
                                    .clickable { showInsightsDialog = true }
                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 4 Modern Insight Cards Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TravelMetricCard(
                                icon = "☀️",
                                title = planState.insights.temperature,
                                subtitle = planState.insights.weatherCondition,
                                modifier = Modifier.weight(1f),
                                appCardBorder = appCardBorder,
                                appNavyDark = appNavyDark,
                                appSlateMuted = appSlateMuted
                            )
                            TravelMetricCard(
                                icon = "👥",
                                title = planState.insights.crowdLevel.split(" ").firstOrNull() ?: "Moderate",
                                subtitle = "Crowd",
                                modifier = Modifier.weight(1f),
                                appCardBorder = appCardBorder,
                                appNavyDark = appNavyDark,
                                appSlateMuted = appSlateMuted
                            )
                            TravelMetricCard(
                                icon = "⭐",
                                title = planState.insights.overallRating.split("/").firstOrNull()?.trim() ?: "4.6",
                                subtitle = "Rating",
                                modifier = Modifier.weight(1f),
                                appCardBorder = appCardBorder,
                                appNavyDark = appNavyDark,
                                appSlateMuted = appSlateMuted
                            )
                            TravelMetricCard(
                                icon = "📅",
                                title = "Best Time",
                                subtitle = planState.insights.bestTimeVisit,
                                modifier = Modifier.weight(1f),
                                appCardBorder = appCardBorder,
                                appNavyDark = appNavyDark,
                                appSlateMuted = appSlateMuted
                            )
                        }
                    }
                }

                // ------------------------------------------
                // 8. SPECIAL OFFERS SECTION
                // ------------------------------------------
                item {
                    SpecialOffersBanner(
                        offersCount = planState.specialOffers.size,
                        appNavyDark = appNavyDark,
                        appSlateMuted = appSlateMuted,
                        appPrimaryBlue = appPrimaryBlue,
                        appCardBorder = appCardBorder,
                        onViewOffers = { showOffersDialog = true }
                    )
                }
            }
        }

        // ==========================================
        // 9. FIXED BOTTOM ACTION BAR
        // ==========================================
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(12.dp)
                .background(SurfaceWhite)
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Button 1: View Route Map (Outlined)
                OutlinedButton(
                    onClick = { onViewRouteMap() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(appPrimaryBlue, appTealSecondary))
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = appPrimaryBlue
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🗺️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "View Route Map",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = appPrimaryBlue
                        )
                    }
                }

                // Button 2: Start Journey (Filled Primary Gradient)
                Button(
                    onClick = { onStartJourney() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(primaryHeroGradient, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "🚀", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Start Journey",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // DIALOGS & MODAL SHEETS
        // ==========================================

        // DETOUR SUGGESTIONS MODAL
        if (showDetourDialog) {
            DetourSuggestionsDialog(
                detours = planState.detourOptions,
                appPrimaryBlue = appPrimaryBlue,
                appTealSecondary = appTealSecondary,
                appNavyDark = appNavyDark,
                appSlateMuted = appSlateMuted,
                appCardBorder = appCardBorder,
                onDismiss = { showDetourDialog = false },
                onApplyDetour = { detour ->
                    // Add detour stop into the plan stops right before destination
                    val currentStops = planState.stops.toMutableList()
                    val destStop = currentStops.removeLastOrNull()
                    detour.places.forEach { detourPlace ->
                        currentStops.add(detourPlace)
                    }
                    if (destStop != null) currentStops.add(destStop)

                    planState = planState.copy(
                        stops = currentStops,
                        totalTravelTimeText = "~ 6h 30m"
                    )
                    showDetourDialog = false
                    Toast.makeText(context, "Added '${detour.title}' to Day Plan!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // SPECIAL OFFERS MODAL
        if (showOffersDialog) {
            SpecialOffersDialog(
                offers = planState.specialOffers,
                appPrimaryBlue = appPrimaryBlue,
                appTealSecondary = appTealSecondary,
                appNavyDark = appNavyDark,
                appSlateMuted = appSlateMuted,
                appCardBorder = appCardBorder,
                onDismiss = { showOffersDialog = false },
                onCopyCode = { code ->
                    clipboardManager.setText(AnnotatedString(code))
                    Toast.makeText(context, "Coupon code '$code' copied!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // TRAVEL INSIGHTS MODAL
        if (showInsightsDialog) {
            TravelInsightsDialog(
                insights = planState.insights,
                origin = planState.origin,
                destination = planState.destination,
                appPrimaryBlue = appPrimaryBlue,
                appTealSecondary = appTealSecondary,
                appNavyDark = appNavyDark,
                appSlateMuted = appSlateMuted,
                appCardBorder = appCardBorder,
                onDismiss = { showInsightsDialog = false }
            )
        }

        // STOP DETAIL MODAL
        selectedStopDetail?.let { stop ->
            StopDetailDialog(
                stop = stop,
                appPrimaryBlue = appPrimaryBlue,
                appNavyDark = appNavyDark,
                appSlateMuted = appSlateMuted,
                appCardBorder = appCardBorder,
                onDismiss = { selectedStopDetail = null }
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
    transitMode: String,
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
    extraTimeText: String,
    botCardGradient: Brush,
    appPrimaryBlue: Color,
    appTealSecondary: Color,
    appNavyDark: Color,
    appSlateMuted: Color,
    appCardBorder: Color,
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
                        .size(56.dp)
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.5.dp, appPrimaryBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🤖", fontSize = 32.sp)
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
                        text = "Explore $detourCount amazing places on your way with $extraTimeText",
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
private fun TimelineStopRow(
    stop: AiStopoverItem,
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
            // Node Circle
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (isFirst || isLast) appPrimaryBlue else appTealSecondary)
                    .border(2.5.dp, SurfaceWhite, CircleShape)
            )

            // Connecting Vertical Line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.5.dp)
                        .height(98.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    if (isFirst) appPrimaryBlue else appTealSecondary,
                                    appPrimaryBlue.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Content Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
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
                    // Badge (Start, Stop 1 • 1h, Destination)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (stop.type) {
                                    "start" -> appLightTint
                                    "destination" -> TealLight
                                    else -> Color(0xFFF1F5F9)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = stop.badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (stop.type) {
                                "start" -> appPrimaryBlue
                                "destination" -> appTealSecondary
                                else -> appNavyDark
                            }
                        )
                    }

                    // Distance Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📍", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = stop.distanceBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = appSlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Content Row: Thumbnail + Titles + Schedule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Thumbnail
                    AsyncImage(
                        model = stop.imageUrl,
                        contentDescription = stop.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE2E8F0))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stop.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = appNavyDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = stop.shortDescription,
                            fontSize = 11.sp,
                            color = appSlateMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Time Window / Departure
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🕒", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stop.timeWindowText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = appPrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TravelMetricCard(
    icon: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    appCardBorder: Color,
    appNavyDark: Color,
    appSlateMuted: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, appCardBorder, RoundedCornerShape(14.dp))
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = appNavyDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = appSlateMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SpecialOffersBanner(
    offersCount: Int,
    appNavyDark: Color,
    appSlateMuted: Color,
    appPrimaryBlue: Color,
    appCardBorder: Color,
    onViewOffers: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, appCardBorder, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gift Illustration
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF7ED)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎁", fontSize = 26.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Special Offers",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = appNavyDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Get exclusive deals on hotels & activities!",
                        fontSize = 11.sp,
                        color = appSlateMuted,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Pill button: View Offers ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(appPrimaryBlue)
                        .clickable { onViewOffers() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "View Offers ➔",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
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
    appCardBorder: Color,
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
                        border = ButtonDefaults.outlinedButtonBorder.copy(
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
                                text = "💡 AI Note: ${detour.aiBenefit}",
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
private fun SpecialOffersDialog(
    offers: List<SpecialOfferItem>,
    appPrimaryBlue: Color,
    appTealSecondary: Color,
    appNavyDark: Color,
    appSlateMuted: Color,
    appCardBorder: Color,
    onDismiss: () -> Unit,
    onCopyCode: (String) -> Unit
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
                        Text(text = "🎁", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Special Offers & Deals",
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

                offers.forEach { offer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFF97316)))
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = offer.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appNavyDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = offer.discount,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFEA580C)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = offer.description,
                                fontSize = 11.sp,
                                color = appSlateMuted,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Code: ${offer.code}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appPrimaryBlue
                                )

                                Button(
                                    onClick = { onCopyCode(offer.code) },
                                    modifier = Modifier.height(32.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Text(
                                        text = "Copy Code",
                                        fontSize = 10.sp,
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
}

@Composable
private fun TravelInsightsDialog(
    insights: TravelInsightsData,
    origin: String,
    destination: String,
    appPrimaryBlue: Color,
    appTealSecondary: Color,
    appNavyDark: Color,
    appSlateMuted: Color,
    appCardBorder: Color,
    onDismiss: () -> Unit
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
                        Text(text = "🧭", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Route Travel Insights",
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

                Text(
                    text = "Corridor: $origin ➔ $destination",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = appPrimaryBlue
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = insights.summaryText,
                    fontSize = 12.sp,
                    color = appNavyDark,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "🕒 Peak Traffic Hours",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = appNavyDark
                        )
                        Text(
                            text = insights.peakHours,
                            fontSize = 11.sp,
                            color = appSlateMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(TealLight.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "💡 Local Travel Tip",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = appTealSecondary
                        )
                        Text(
                            text = insights.localAdvice,
                            fontSize = 11.sp,
                            color = appNavyDark,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appPrimaryBlue)
                ) {
                    Text(text = "Close", color = Color.White, fontWeight = FontWeight.Bold)
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
    appCardBorder: Color,
    onDismiss: () -> Unit
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
                        .height(150.dp)
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

                    Spacer(modifier = Modifier.height(8.dp))

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
                            color = appSlateMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = appPrimaryBlue)
                    ) {
                        Text(text = "Done", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
