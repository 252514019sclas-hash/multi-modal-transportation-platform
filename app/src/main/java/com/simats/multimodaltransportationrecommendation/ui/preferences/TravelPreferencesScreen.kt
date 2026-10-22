package com.simats.multimodaltransportationrecommendation.ui.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun TravelPreferencesScreen(
    viewModel: TravelPreferencesViewModel = TravelPreferencesViewModel(),
    onBack: () -> Unit = {},
    onSavePreferences: () -> Unit = {}
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
            // Top Bar & Header
            Column {
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
                            text = "05 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Your travel preferences",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF101D17))
                        .border(1.dp, Color(0xFF1E3F2A), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "☘️", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PERSONALIZE YOUR JOURNEY",
                            color = Color(0xFFC6FF00),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Made for your kind of moving.",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tell AI what matters. You can change these anytime.",
                    color = Color(0xFF8A99AD),
                    fontSize = 13.sp
                )
            }

            // Preferences Content Section
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Section 1: How do you like to travel?
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "How do you like to travel?",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Select multiple",
                            color = Color(0xFFC6FF00),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Chips Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modesRow1 = listOf("Walk" to "🚶", "Cycle" to "🚲", "Bus" to "🚌", "Metro" to "🚇")
                        modesRow1.forEach { (mode, icon) ->
                            val isSelected = uiState.selectedModes.contains(mode)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF142018) else Color(0xFF0F172A))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.toggleMode(mode) },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = icon, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = mode,
                                        color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF8A99AD),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Chips Row 2 (Rideshare)
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val mode = "Rideshare"
                        val icon = "🚗"
                        val isSelected = uiState.selectedModes.contains(mode)
                        Box(
                            modifier = Modifier
                                .width(110.dp)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF142018) else Color(0xFF0F172A))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.toggleMode(mode) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = icon, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = mode,
                                    color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF8A99AD),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Section 2: What matters most?
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "What matters most?",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Choose a priority",
                            color = Color(0xFFC6FF00),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    val priorities = listOf(
                        Triple("Fastest", "Less time on the move", "⚡"),
                        Triple("Cheapest", "Keep your daily travel affordable", "👛"),
                        Triple("Low-carbon", "A lighter footprint, every journey", "🍃")
                    )

                    priorities.forEach { (title, subtitle, icon) ->
                        val isSelected = uiState.selectedPriority == title
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF142018) else Color(0xFF0F172A))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setPriority(title) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = icon, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = subtitle,
                                        color = Color(0xFF8A99AD),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Text(text = "✓", color = Color(0xFF070B14), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Step-free routes toggle card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .clickable { viewModel.toggleStepFree() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Step-free routes",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Prioritize accessible stations and vehicles.",
                                color = Color(0xFF8A99AD),
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = uiState.stepFree,
                            onCheckedChange = { viewModel.toggleStepFree() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF070B14),
                                checkedTrackColor = Color(0xFFC6FF00),
                                uncheckedThumbColor = Color(0xFF8A99AD),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }

            // Bottom Save Preferences Button
            Button(
                onClick = { onSavePreferences() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC6FF00)
                )
            ) {
                Text(
                    text = "Save preferences  →",
                    color = Color(0xFF070B14),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun TravelPreferencesScreenPreview() {
    TravelPreferencesScreen()
}
