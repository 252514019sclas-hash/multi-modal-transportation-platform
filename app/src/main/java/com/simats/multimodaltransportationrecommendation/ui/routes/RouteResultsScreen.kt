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

@Composable
fun RouteResultsScreen(
    origin: String = "Ferry Building",
    destination: String = "California Academy of Sciences",
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
            .background(Color(0xFF070B14))
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
                                .background(Color(0xFF111C2E))
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "←", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "09 / MOVESMART AI",
                                color = Color(0xFF7A8B9E),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Your best ways to go",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Compare shortcut button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .clickable {
                                uiState.selectedRoute?.let { sel ->
                                    onCompareRoutes(sel, uiState.allRoutes)
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚖️ Compare", color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Route summary box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFFC6FF00), CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.origin,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF38BDF8), CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.destination,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AI Insight sentence
                Text(
                    text = uiState.aiInsight,
                    color = Color(0xFF8A99AD),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFFC6FF00), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Finding multi-modal low-carbon routes...", color = Color(0xFF7A8B9E), fontSize = 12.sp)
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
                            Text(text = "⚠️ ${uiState.errorMessage}", color = Color(0xFFFF5252), fontSize = 12.sp)
                            Button(
                                onClick = { viewModel.searchRoutes(context, origin, destination) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "Retry Search", color = Color.White, fontSize = 12.sp)
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
                                        .background(Color(0xFF112017))
                                        .border(
                                            if (isSelected) 2.dp else 1.5.dp,
                                            if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E4D2B),
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
                                                Text(text = "☘️", fontSize = 11.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = rec.badge ?: "AI RECOMMENDED",
                                                    color = Color(0xFFC6FF00),
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
                                                    .background(Color(0xFF1A3824))
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
                                                    color = Color.White,
                                                    fontSize = 28.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = rec.title,
                                                    color = Color(0xFFC6FF00),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = rec.fareText,
                                                    color = Color(0xFFC6FF00),
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${rec.co2SavedKg} kg CO₂ saved",
                                                    color = Color(0xFF38BDF8),
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
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFF1A3824))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(text = "$icon $step", color = Color(0xFFC6FF00), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                                if (idx < steps.size - 1) {
                                                    Text(text = "›", color = Color(0xFF8A99AD), fontSize = 11.sp)
                                                }
                                            }
                                        }

                                        Text(
                                            text = rec.routeDetails,
                                            color = Color(0xFF8A99AD),
                                            fontSize = 10.sp
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
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${uiState.allRoutes.size} options",
                                    color = Color(0xFF7A8B9E),
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
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF132034) else Color(0xFF0F172A))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B),
                                        RoundedCornerShape(12.dp)
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
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF162238)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = getModeIcon(route.mode), fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = route.title,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${route.distanceText} · ${route.co2EmittedKg} kg CO₂",
                                            color = Color(0xFF8A99AD),
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = route.routeDetails,
                                            color = Color(0xFF64748B),
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
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = route.fareText,
                                        color = if (route.fare == 0.0) Color(0xFFC6FF00) else Color(0xFF8A99AD),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    // Save icon for each
                                    Text(
                                        text = "🔖 Save",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 9.sp,
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC6FF00),
                    disabledContainerColor = Color(0xFF1E293B)
                )
            ) {
                Text(
                    text = if (selected != null) "Select ${selected.mode.replaceFirstChar { it.uppercase() }} Route  →" else "Select a Route  →",
                    color = Color(0xFF070B14),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun getModeIcon(mode: String): String {
    val m = mode.lowercase()
    return when {
        m.contains("walk") -> "🚶"
        m.contains("cycle") || m.contains("bike") || m.contains("wheel") -> "🚲"
        m.contains("metro") || m.contains("judah") || m.contains("subway") -> "🚊"
        m.contains("train") || m.contains("rail") -> "🚆"
        m.contains("bus") -> "🚌"
        m.contains("taxi") || m.contains("cab") || m.contains("ride") -> "🚕"
        m.contains("car") || m.contains("drive") -> "🚗"
        else -> "🚀"
    }
}
