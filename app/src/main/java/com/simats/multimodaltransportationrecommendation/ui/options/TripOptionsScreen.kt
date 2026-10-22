package com.simats.multimodaltransportationrecommendation.ui.options

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TripOptionsScreen(
    viewModel: TripOptionsViewModel = TripOptionsViewModel(),
    onBack: () -> Unit = {},
    onShowRoutes: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar & Route Summary
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
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
                            text = "08 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Trip options & filters",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFFC6FF00), CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = uiState.origin, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFF38BDF8), CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = uiState.destination, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Depart at / Arrive by tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Depart at", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Arrive by", color = Color(0xFF8A99AD), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // Time & passenger chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📅  ${uiState.dateText}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⏰  ${uiState.timeText}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "👤  ${uiState.passengers}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Options & Filters Sections
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Preferred modes
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Preferred modes", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Select multiple", color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val modes = listOf("Walk" to "🚶", "Cycle" to "🚲", "Bus" to "🚌", "Metro" to "🚇", "Rideshare" to "🚗")
                        modes.forEach { (mode, icon) ->
                            val isSelected = uiState.selectedModes.contains(mode)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFF142018) else Color(0xFF0F172A))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.toggleMode(mode) },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = icon, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = mode,
                                        color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF8A99AD),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Maximum walking per leg
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Maximum walking per leg", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${uiState.walkingMinutes.toInt()} min", color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Slider(
                        value = uiState.walkingMinutes,
                        onValueChange = { viewModel.setWalkingMinutes(it) },
                        valueRange = 5f..30f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFC6FF00),
                            activeTrackColor = Color(0xFFC6FF00),
                            inactiveTrackColor = Color(0xFF1E293B)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "5 min", color = Color(0xFF8A99AD), fontSize = 10.sp)
                        Text(text = "30 min", color = Color(0xFF8A99AD), fontSize = 10.sp)
                    }
                }

                // Optimize my route
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(text = "Optimize my route", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val options = listOf("Fastest" to "⚡", "Cheapest" to "👛", "Low-carbon" to "🍃")
                        options.forEach { (opt, icon) ->
                            val isSelected = uiState.optimizeBy == opt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFF142018) else Color(0xFF0F172A))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.setOptimize(opt) },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = icon, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = opt,
                                        color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF8A99AD),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // Accessible routes toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .clickable { viewModel.toggleAccessible() }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Accessible routes", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Step-free access and low-floor vehicles", color = Color(0xFF8A99AD), fontSize = 10.sp)
                    }
                    Switch(
                        checked = uiState.accessibleRoutes,
                        onCheckedChange = { viewModel.toggleAccessible() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF070B14),
                            checkedTrackColor = Color(0xFFC6FF00),
                            uncheckedThumbColor = Color(0xFF8A99AD),
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
            }

            // Bottom CTA Button & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            ) {
                Text(
                    text = "Personalized to your low-carbon preference",
                    color = Color(0xFF8A99AD),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { onShowRoutes() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC6FF00)
                    )
                ) {
                    Text(
                        text = "Show routes  →",
                        color = Color(0xFF070B14),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun TripOptionsScreenPreview() {
    TripOptionsScreen()
}
