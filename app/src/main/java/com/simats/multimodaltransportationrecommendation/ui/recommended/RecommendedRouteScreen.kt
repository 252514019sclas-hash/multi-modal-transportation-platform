package com.simats.multimodaltransportationrecommendation.ui.recommended

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun RecommendedRouteScreen(
    viewModel: RecommendedRouteViewModel = RecommendedRouteViewModel(),
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                text = "Recommended Route",
                                color = TextNavyDark,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bookmark
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔖", fontSize = 16.sp)
                    }
                }

                // Map Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PastelCardBlue)
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .padding(10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val gridColor = SkyBluePrimary.copy(alpha = 0.12f)
                        val cols = 6
                        val rows = 4
                        val dx = width / cols
                        val dy = height / rows

                        for (i in 1 until cols) {
                            drawLine(gridColor, Offset(i * dx, 0f), Offset(i * dx, height), 1.dp.toPx())
                        }
                        for (j in 1 until rows) {
                            drawLine(gridColor, Offset(0f, j * dy), Offset(width, j * dy), 1.dp.toPx())
                        }

                        val routePath = Path().apply {
                            moveTo(width * 0.15f, height * 0.75f)
                            lineTo(width * 0.5f, height * 0.5f)
                            lineTo(width * 0.88f, height * 0.22f)
                        }
                        drawPath(routePath, SkyBluePrimary, style = Stroke(width = 4.dp.toPx()))
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = "Chennai Central ➔ Marina Beach", color = TextNavyDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Overview Stats Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = uiState.duration, color = TextNavyDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(text = uiState.timeRange, color = TextMutedSlate, fontSize = 11.sp)
                        }
                        Column {
                            Text(text = uiState.totalFare, color = SkyBluePrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Total fare", color = TextMutedSlate, fontSize = 11.sp)
                        }
                        Column {
                            Text(text = "${uiState.transfers} transfers", color = TealSecondary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Transit line", color = TextMutedSlate, fontSize = 11.sp)
                        }
                    }
                }

                // Badges Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PastelCardTeal)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "⚡", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "AI RECOMMENDED", color = TealSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PastelCardBlue)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = "CMRL Rapid Metro", color = SkyBluePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Journey Step by Step Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Your Journey, Step by Step", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = uiState.dateText, color = SkyBluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Steps Timeline List
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            uiState.steps.forEach { step ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = step.time, color = TextMutedSlate, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(62.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(PastelCardBlue, CircleShape)
                                            .border(1.dp, SkyBluePrimary.copy(alpha = 0.3f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = step.icon, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = step.title, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = step.description, color = TextMutedSlate, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = uiState.footerSummary,
                        color = TextMutedSlate,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom CTA Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(PrimaryGradient)
                    .clickable { onContinue() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Continue to Transit Details  ➔",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
