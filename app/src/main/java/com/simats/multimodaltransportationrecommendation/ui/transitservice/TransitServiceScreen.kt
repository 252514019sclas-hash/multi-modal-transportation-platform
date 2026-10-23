package com.simats.multimodaltransportationrecommendation.ui.transitservice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun TransitServiceScreen(
    viewModel: TransitServiceViewModel = TransitServiceViewModel(),
    onBack: () -> Unit = {},
    onContinueToBooking: () -> Unit = {}
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
            // Top Bar & Header Card
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
                            text = "12 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Transit service",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Transit Line Header Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF112017))
                        .border(1.5.dp, Color(0xFF1E4D2B), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFC6FF00)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "N",
                                color = Color(0xFF070B14),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = uiState.lineName,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.direction,
                                color = Color(0xFF8A99AD),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "✅", fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = uiState.status,
                                    color = Color(0xFFC6FF00),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Departures Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Embarcadero departures", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Live times", color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.departures.forEach { (time, tag) ->
                        val isSelected = uiState.selectedDeparture == time
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF112017) else Color(0xFF0F172A))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.selectDeparture(time) }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = time,
                                    color = if (isSelected) Color(0xFFC6FF00) else Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = tag,
                                    color = Color(0xFF8A99AD),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            // Station Sequence & Fare Card
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Your station sequence", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = uiState.rideDuration, color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                // Timeline List
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    uiState.stations.forEachIndexed { index, (station, desc) ->
                        val isEndpoint = index == 0 || index == uiState.stations.size - 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isEndpoint) 8.dp else 5.dp)
                                        .background(if (isEndpoint) Color(0xFFC6FF00) else Color(0xFF334155), CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = station,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = desc,
                                    color = Color(0xFF8A99AD),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // Fare / Accessibility Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = uiState.fareType, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = uiState.fareAmount, color = Color(0xFFC6FF00), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = uiState.fareDetails,
                            color = Color(0xFF8A99AD),
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Bottom CTA Button
            Button(
                onClick = { onContinueToBooking() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC6FF00)
                )
            ) {
                Text(
                    text = "Continue to booking  →",
                    color = Color(0xFF070B14),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun TransitServiceScreenPreview() {
    TransitServiceScreen()
}
