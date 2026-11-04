package com.simats.multimodaltransportationrecommendation.ui.options

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun TripOptionsScreen(
    viewModel: TripOptionsViewModel = TripOptionsViewModel(),
    onBack: () -> Unit = {},
    onShowRoutes: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val transportModes = listOf(
        "Walk" to "🚶",
        "Cycle" to "🚲",
        "Bus" to "🚌",
        "Metro" to "🚇",
        "Train" to "🚆",
        "Taxi" to "🚕",
        "Car" to "🚗"
    )

    val optimizeOptions = listOf("Fastest", "Fewest Transfers", "Scenic Route")

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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
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
                            text = "TAMIL NADU SMART TRANSIT",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Trip Options & Filters",
                            color = TextNavyDark,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Route summary box
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(9.dp).background(SkyBluePrimary, CircleShape))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = uiState.origin, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Box(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .height(12.dp)
                                .width(1.5.dp)
                                .background(BorderLight)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(9.dp).background(WarmOrangeAccent, CircleShape))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = uiState.destination, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Modes Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Include Transportation Modes", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        transportModes.take(4).forEach { (mode, icon) ->
                            val isSelected = uiState.selectedModes.contains(mode)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PastelCardBlue else SurfaceWhite)
                                    .border(
                                        1.dp,
                                        if (isSelected) SkyBluePrimary else BorderLight,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.toggleMode(mode) },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = icon, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = mode,
                                        color = if (isSelected) SkyBluePrimary else TextMutedSlate,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        transportModes.drop(4).forEach { (mode, icon) ->
                            val isSelected = uiState.selectedModes.contains(mode)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PastelCardBlue else SurfaceWhite)
                                    .border(
                                        1.dp,
                                        if (isSelected) SkyBluePrimary else BorderLight,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.toggleMode(mode) },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = icon, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = mode,
                                        color = if (isSelected) SkyBluePrimary else TextMutedSlate,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Optimization Preference
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Route Priority", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        optimizeOptions.forEach { opt ->
                            val isSelected = uiState.optimizeBy == opt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) PastelCardTeal else SurfaceWhite)
                                    .border(
                                        1.dp,
                                        if (isSelected) TealSecondary else BorderLight,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setOptimize(opt) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opt,
                                    color = if (isSelected) TealSecondary else TextNavyDark,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Step-Free Accessibility Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Step-Free Accessibility", color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Ramps, elevators & flat boarding", color = TextMutedSlate, fontSize = 11.sp)
                    }
                    Switch(
                        checked = uiState.accessibleRoutes,
                        onCheckedChange = { viewModel.toggleAccessible() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TealSecondary,
                            uncheckedThumbColor = TextMutedSlate,
                            uncheckedTrackColor = BackgroundSoftCream
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(PrimaryGradient)
                    .clickable { onShowRoutes() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Show Recommendations  ➔",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
