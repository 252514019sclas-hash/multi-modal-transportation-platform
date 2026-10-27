package com.simats.multimodaltransportationrecommendation.ui.completed

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
fun JourneyCompletedScreen(
    viewModel: JourneyCompletedViewModel = JourneyCompletedViewModel(),
    onBack: () -> Unit = {},
    onBackToHome: () -> Unit = {}
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header & Arrival Greeting
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Screen Label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
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
                            text = "18 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Journey completed",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Location Pin Icon Badge
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color(0xFF112017), CircleShape)
                        .border(1.5.dp, Color(0xFF1E4D2B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📍", fontSize = 24.sp)
                }

                // Greeting & Subtitle
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.arrivalTitle,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.arrivalSubtitle,
                        color = Color(0xFF8A99AD),
                        fontSize = 13.sp
                    )
                }

                // Trip Summary Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFFC6FF00), CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = uiState.origin, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF38BDF8), CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = uiState.destination, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = uiState.travelTime, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Total travel time", color = Color(0xFF8A99AD), fontSize = 10.sp)
                            }
                            Column {
                                Text(text = uiState.totalFare, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Total fare", color = Color(0xFF8A99AD), fontSize = 10.sp)
                            }
                            Column {
                                Text(text = uiState.routeLine, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = "0 transfers", color = Color(0xFF8A99AD), fontSize = 10.sp)
                            }
                        }
                    }
                }

                // Carbon Savings Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF112017))
                        .border(1.5.dp, Color(0xFF1E4D2B), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "🌿", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = uiState.co2Saved,
                                color = Color(0xFFC6FF00),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.co2Details,
                                color = Color(0xFF8A99AD),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Rating & Bottom CTA Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Rating Section
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "How was your journey?",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..5) {
                            val isFilled = i <= uiState.selectedRating
                            Text(
                                text = if (isFilled) "★" else "☆",
                                color = if (isFilled) Color(0xFFC6FF00) else Color(0xFF475569),
                                fontSize = 24.sp,
                                modifier = Modifier.clickable { viewModel.setRating(i) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.ratingPrompt,
                        color = Color(0xFF8A99AD),
                        fontSize = 11.sp
                    )
                }

                // Save Route Button
                Button(
                    onClick = { /* Save route */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(23.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F172A)
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Save this route",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🔖", fontSize = 12.sp)
                    }
                }

                // Back to Home Button
                Button(
                    onClick = { onBackToHome() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC6FF00)
                    )
                ) {
                    Text(
                        text = "Back to home  →",
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
fun JourneyCompletedScreenPreview() {
    JourneyCompletedScreen()
}
