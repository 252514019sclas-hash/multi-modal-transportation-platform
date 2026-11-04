package com.simats.multimodaltransportationrecommendation.ui.compare

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.routes.RouteOption
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun CompareRoutesScreen(
    viewModel: CompareRoutesViewModel = viewModel(),
    onBack: () -> Unit = {},
    onChooseRoute: (RouteOption?) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

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
            // Top Bar & Route Summary
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
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
                            text = "COMPARE TAMIL NADU ROUTES",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Compare Transit Options",
                            color = TextNavyDark,
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
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(SkyBluePrimary, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = uiState.origin, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(WarmOrangeAccent, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = uiState.destination, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Text(
                    text = "Compare duration, fare and comfort across Tamil Nadu modes:",
                    color = TextNavyBody,
                    fontSize = 12.sp
                )

                // Table Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "TIME", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "FARE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "WALK", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "TRANSIT", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "ON TIME", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Route Comparison Cards List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.routes.forEach { route ->
                    val isSelected = uiState.selectedRouteTitle == route.title
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) PastelCardBlue else SurfaceWhite)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) SkyBluePrimary else BorderLight,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { viewModel.selectRoute(route.title) }
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = route.title,
                                    color = TextNavyDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (route.isAiPick) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(PastelCardTeal)
                                                .border(1.dp, TealLight, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "AI PICK",
                                                color = TealDark,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(if (isSelected) SkyBluePrimary else BorderLight, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Text(text = "✓", color = SurfaceWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Metrics Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = route.time, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(text = route.fare, color = WarmOrangeDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = route.walk, color = TextNavyBody, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(text = route.co2, color = TealDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(text = route.onTime, color = SkyBlueDark, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // AI Insight Footer & Note
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💡", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.aiInsight,
                        color = TextNavyBody,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = uiState.note,
                    color = TextMuted,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }

            // Bottom CTA Button
            Button(
                onClick = { onChooseRoute(uiState.selectedRouteOption) },
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
                        text = "Choose ${uiState.selectedRouteTitle}  ➔",
                        color = SurfaceWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
