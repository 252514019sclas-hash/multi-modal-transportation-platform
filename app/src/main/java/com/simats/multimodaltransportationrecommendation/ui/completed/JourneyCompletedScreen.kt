package com.simats.multimodaltransportationrecommendation.ui.completed

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
fun JourneyCompletedScreen(
    viewModel: JourneyCompletedViewModel = JourneyCompletedViewModel(),
    onBack: () -> Unit = {},
    onBackToHome: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Screen Label
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
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
                            text = "Journey Completed",
                            color = TextNavyDark,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Location Pin Icon Badge
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(PastelCardTeal)
                        .border(1.5.dp, TealSecondary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📍", fontSize = 26.sp)
                }

                // Greeting & Subtitle
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.arrivalTitle,
                        color = TextNavyDark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.arrivalSubtitle,
                        color = TextMutedSlate,
                        fontSize = 13.sp
                    )
                }

                // Trip Summary Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(9.dp).background(SkyBluePrimary, CircleShape))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = uiState.origin, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(9.dp).background(WarmOrangeAccent, CircleShape))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = uiState.destination, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        HorizontalDivider(color = BorderLight, thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = uiState.travelTime, color = TextNavyDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Travel time", color = TextMutedSlate, fontSize = 11.sp)
                            }
                            Column {
                                Text(text = uiState.totalFare, color = SkyBluePrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Total fare", color = TextMutedSlate, fontSize = 11.sp)
                            }
                            Column {
                                Text(text = uiState.routeLine, color = TealSecondary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Transit line", color = TextMutedSlate, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Nearby Tourist Highlights Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PastelCardPeach)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(text = "🏛️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = uiState.touristTipTitle,
                                color = WarmOrangeAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.touristTipDetails,
                                color = TextNavyDark,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Rating Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How was your journey?",
                            color = TextNavyDark,
                            fontSize = 14.sp,
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
                                    color = if (isFilled) WarmOrangeAccent else TextMutedSlate,
                                    fontSize = 26.sp,
                                    modifier = Modifier.clickable { viewModel.setRating(i) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.ratingPrompt,
                            color = TextMutedSlate,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom CTA Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(PrimaryGradient)
                        .clickable { onBackToHome() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Explore More Places  ➔",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
