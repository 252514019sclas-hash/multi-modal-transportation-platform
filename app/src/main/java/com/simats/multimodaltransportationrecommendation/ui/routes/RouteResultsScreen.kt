package com.simats.multimodaltransportationrecommendation.ui.routes

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun RouteResultsScreen(
    origin: String = "Chennai Central",
    destination: String = "Marina Beach & Promenade",
    viewModel: RouteResultsViewModel = viewModel(),
    onBack: () -> Unit = {},
    onViewRecommended: (RouteOption) -> Unit = {},
    onCompareRoutes: (RouteOption, List<RouteOption>) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(origin, destination) {
        viewModel.searchRoutes(context, origin, destination)
    }

    LaunchedEffect(uiState.saveMessage) {
        uiState.saveMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearSaveMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header & Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PastelCardBlue)
                                .border(1.dp, SkyBlueSoft, CircleShape)
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "←", color = SkyBluePrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "TAMIL NADU TRANSIT ROUTES",
                                color = SkyBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "Available Transportation",
                                color = TextNavyDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Compare shortcut button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PastelCardTeal)
                            .border(1.dp, TealLight, RoundedCornerShape(12.dp))
                            .clickable {
                                uiState.selectedRoute?.let { sel ->
                                    onCompareRoutes(sel, uiState.allRoutes)
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚖️ Compare", color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Route summary box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(SkyBluePrimary, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.origin,
                                color = TextNavyDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(WarmOrangeAccent, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.destination,
                                color = TextNavyDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // AI Insight sentence
                Text(
                    text = uiState.aiInsight,
                    color = TextNavyBody,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = SkyBluePrimary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Finding Tamil Nadu multi-modal transit routes...", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                } else if (uiState.errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "⚠️ ${uiState.errorMessage}", color = Color(0xFFDC2626), fontSize = 12.sp)
                            Button(
                                onClick = { viewModel.searchRoutes(context, origin, destination) },
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "Retry Search", color = SurfaceWhite, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Recommended Route Section
                        uiState.recommendedRoute?.let { rec ->
                            item {
                                val isSelected = uiState.selectedRoute?.id == rec.id
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(PastelCardTeal)
                                        .border(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) TealSecondary else TealLight,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable { viewModel.selectRoute(rec) }
                                        .padding(14.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "⭐", fontSize = 11.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = rec.badge ?: "RECOMMENDED ROUTE",
                                                    color = TealDark,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.5.sp
                                                )
                                            }

                                            // Bookmark button
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clip(CircleShape)
                                                    .background(SurfaceWhite)
                                                    .clickable { viewModel.saveRoute(context, rec) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = "🔖", fontSize = 12.sp)
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            Column {
                                                Text(
                                                    text = rec.durationText,
                                                    color = TextNavyDark,
                                                    fontSize = 26.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = rec.title,
                                                    color = TealSecondary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = rec.fareText,
                                                    color = WarmOrangeDark,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${rec.distanceText} distance",
                                                    color = TextMuted,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }

                                        // Mode steps
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            val steps = if (rec.steps.isNotEmpty()) rec.steps else listOf(rec.mode)
                                            steps.forEachIndexed { idx, step ->
                                                val icon = getModeIcon(step)
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(SurfaceWhite)
                                                        .border(1.dp, TealLight, RoundedCornerShape(6.dp))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(text = "$icon $step", color = TealDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                                if (idx < steps.size - 1) {
                                                    Text(text = "›", color = TextMuted, fontSize = 11.sp)
                                                }
                                            }
                                        }

                                        Text(
                                            text = rec.routeDetails,
                                            color = TextNavyBody,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Other Options Title
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "All Transportation Modes",
                                    color = TextNavyDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${uiState.allRoutes.size} options",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Alternative routes list
                        items(uiState.alternativeRoutes) { route ->
                            val isSelected = uiState.selectedRoute?.id == route.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) PastelCardBlue else SurfaceWhite)
                                    .border(
                                        1.dp,
                                        if (isSelected) SkyBluePrimary else BorderLight,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { viewModel.selectRoute(route) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(PastelCardBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = getModeIcon(route.mode), fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = route.title,
                                            color = TextNavyDark,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${route.distanceText} · ${route.mode}",
                                            color = TextNavyBody,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = route.routeDetails,
                                            color = TextMuted,
                                            fontSize = 9.sp,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = route.durationText,
                                        color = TextNavyDark,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = route.fareText,
                                        color = if (route.fare == 0.0) TealSecondary else WarmOrangeDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "🔖 Save",
                                        color = SkyBluePrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.clickable { viewModel.saveRoute(context, route) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom CTA Button
            val selected = uiState.selectedRoute ?: uiState.recommendedRoute
            Button(
                onClick = {
                    selected?.let { onViewRecommended(it) }
                },
                enabled = selected != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selected != null) "Select ${selected.mode.replaceFirstChar { it.uppercase() }} Route  ➔" else "Select a Route  ➔",
                        color = SurfaceWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun getModeIcon(mode: String): String {
    val m = mode.lowercase()
    return when {
        m.contains("walk") -> "🚶"
        m.contains("cycle") || m.contains("bike") -> "🚲"
        m.contains("metro") || m.contains("cmrl") -> "🚇"
        m.contains("train") || m.contains("rail") || m.contains("emu") -> "🚆"
        m.contains("bus") || m.contains("mtc") || m.contains("tnstc") || m.contains("setc") -> "🚌"
        m.contains("taxi") || m.contains("auto") || m.contains("cab") -> "🚕"
        m.contains("car") || m.contains("drive") -> "🚗"
        else -> "🚀"
    }
}
