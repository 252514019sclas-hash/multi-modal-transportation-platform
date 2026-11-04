package com.simats.multimodaltransportationrecommendation.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit = {}
) {
    // Smooth pulsing logo animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PastelCardBlue)
                        .border(1.dp, SkyBlueSoft, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "TAMIL NADU SMART TOURISM & TRANSIT",
                        color = SkyBluePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Logo Box with Sky Blue & Teal
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(PrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌴",
                        fontSize = 34.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "MoveSmart AI",
                    color = TextNavyDark,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Discover Enchanting Tamil Nadu · Intelligent Multi-Modal Transit",
                    color = TextNavyBody,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            // Center Interactive Map Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid points
                    val rows = 5
                    val cols = 7
                    val dx = width / (cols - 1)
                    val dy = height / (rows - 1)

                    for (r in 0 until rows) {
                        for (c in 0 until cols) {
                            drawCircle(
                                color = BorderLight,
                                radius = 2.dp.toPx(),
                                center = Offset(c * dx, r * dy)
                            )
                        }
                    }

                    // Teal Transit Path
                    val tealPath = Path().apply {
                        moveTo(width * 0.1f, height * 0.75f)
                        lineTo(width * 0.35f, height * 0.75f)
                        cubicTo(width * 0.45f, height * 0.75f, width * 0.45f, height * 0.45f, width * 0.55f, height * 0.45f)
                        lineTo(width * 0.88f, height * 0.45f)
                    }
                    drawPath(
                        path = tealPath,
                        color = TealSecondary.copy(alpha = 0.5f),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Sky Blue Rail Path
                    val skyPath = Path().apply {
                        moveTo(width * 0.15f, height * 0.65f)
                        lineTo(width * 0.3f, height * 0.65f)
                        cubicTo(width * 0.4f, height * 0.65f, width * 0.4f, height * 0.3f, width * 0.6f, height * 0.3f)
                        cubicTo(width * 0.7f, height * 0.3f, width * 0.7f, height * 0.5f, width * 0.9f, height * 0.5f)
                    }
                    drawPath(
                        path = skyPath,
                        color = SkyBluePrimary,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }

                // Node Badges
                // Marina Beach Node
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 24.dp, y = (-20).dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PastelCardBlue)
                        .border(1.dp, SkyBlueSoft, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(text = "🏖️ Marina Beach", color = SkyBlueDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // Chennai Metro Node
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-10).dp, y = (-10).dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PastelCardTeal)
                        .border(1.dp, TealSoft, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(text = "🚇 Chennai Metro", color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // Meenakshi Temple Node
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-20).dp, y = 20.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PastelCardPeach)
                        .border(1.dp, WarmOrangeLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(text = "🛕 Madurai Temple", color = WarmOrangeDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Bottom CTA Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
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
                            text = "Explore Tamil Nadu  ➔",
                            color = SurfaceWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Vanakkam! Smart mobility across temples, beaches & hills",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}
