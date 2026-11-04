package com.simats.multimodaltransportationrecommendation.ui.journey

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
fun OnYourWayScreen(
    viewModel: OnYourWayViewModel = OnYourWayViewModel(),
    onBack: () -> Unit = {},
    onViewTicket: () -> Unit = {},
    onSimulateDelay: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Smooth pulsing animation for live badge & map marker
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

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
            // Top Bar & Live Trip Header
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                text = "Live Journey Tracking",
                                color = TextNavyDark,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Options dots (Tap to simulate live alert)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, CircleShape)
                            .clickable { onSimulateDelay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "···", color = TextNavyDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Live Trip Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = uiState.currentTime,
                            color = TextNavyDark,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = uiState.remainingTimeText,
                            color = TealSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Live Pulsing Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(PastelCardTeal)
                            .border(1.dp, TealSecondary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(TealSecondary.copy(alpha = pulseAlpha), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE TRACKING",
                                color = TealSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                // Live Map Card with pulsing location marker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PastelCardBlue)
                        .border(1.dp, BorderLight, RoundedCornerShape(18.dp))
                        .clickable { onSimulateDelay() }
                        .padding(10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val gridColor = SkyBluePrimary.copy(alpha = 0.12f)
                        val cols = 6
                        val rows = 5

                        val dx = width / cols
                        val dy = height / rows

                        for (i in 1 until cols) {
                            drawLine(gridColor, Offset(i * dx, 0f), Offset(i * dx, height), 1.dp.toPx())
                        }
                        for (j in 1 until rows) {
                            drawLine(gridColor, Offset(0f, j * dy), Offset(width, j * dy), 1.dp.toPx())
                        }

                        // Transit Route Line
                        val routePath = Path().apply {
                            moveTo(width * 0.15f, height * 0.75f)
                            lineTo(width * 0.55f, height * 0.5f)
                            lineTo(width * 0.88f, height * 0.22f)
                        }
                        drawPath(routePath, SkyBluePrimary, style = Stroke(width = 4.dp.toPx()))

                        // Pulsing Current Location Marker at (0.55f * width, 0.5f * height)
                        val markerCenter = Offset(width * 0.55f, height * 0.5f)
                        drawCircle(
                            color = SkyBluePrimary.copy(alpha = 0.35f * pulseAlpha),
                            radius = 20.dp.toPx(),
                            center = markerCenter
                        )
                        drawCircle(
                            color = SkyBluePrimary,
                            radius = 7.dp.toPx(),
                            center = markerCenter
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.5.dp.toPx(),
                            center = markerCenter
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = "Chennai Central ➔ Marina", color = TextNavyDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Active Status Card & What's Ahead
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Active Status Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = uiState.activeStatusTitle,
                                color = TextNavyDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.nextStopInfo,
                                color = TextMutedSlate,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🔔", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = uiState.reminderText,
                                    color = TealSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(PastelCardTeal, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🚇", fontSize = 20.sp)
                        }
                    }
                }

                // What's ahead Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "What's Ahead", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Full route", color = SkyBluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Upcoming Steps
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.upcomingSteps.forEach { step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = step.time, color = TextMutedSlate, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
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
                                Text(text = step.subtitle, color = TextMutedSlate, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Bottom Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .shadow(2.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(24.dp))
                        .clickable { /* Get help */ },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Transit Help", color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🎧", fontSize = 13.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(PrimaryGradient)
                        .clickable { onViewTicket() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "View Digital Ticket", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🎫", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
