package com.simats.multimodaltransportationrecommendation.ui.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
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
                Text(
                    text = "01 / WELCOME TO MOVESMART",
                    color = Color(0xFF7A8B9E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Logo Box
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF111E16))
                        .border(1.5.dp, Color(0xFFC6FF00), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = Color(0xFFC6FF00),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "MoveSmart AI",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Smarter Routes. Better Journeys.",
                    color = Color(0xFF8A99AD),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Center Route Map Graphic
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Grid Dots and Route Paths
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Draw subtle grid dots
                    val dotColor = Color(0xFF1E293B)
                    val rows = 6
                    val cols = 8
                    val dx = width / (cols - 1)
                    val dy = height / (rows - 1)

                    for (r in 0 until rows) {
                        for (c in 0 until cols) {
                            drawCircle(
                                color = dotColor,
                                radius = 2.dp.toPx(),
                                center = Offset(c * dx, r * dy)
                            )
                        }
                    }

                    // Draw secondary blue path
                    val bluePath = Path().apply {
                        moveTo(width * 0.05f, height * 0.75f)
                        lineTo(width * 0.35f, height * 0.75f)
                        cubicTo(width * 0.45f, height * 0.75f, width * 0.45f, height * 0.45f, width * 0.55f, height * 0.45f)
                        lineTo(width * 0.85f, height * 0.45f)
                    }
                    drawPath(
                        path = bluePath,
                        color = Color(0xFF38BDF8).copy(alpha = 0.4f),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw primary lime green path
                    val greenPath = Path().apply {
                        moveTo(width * 0.1f, height * 0.65f)
                        lineTo(width * 0.25f, height * 0.65f)
                        cubicTo(width * 0.35f, height * 0.65f, width * 0.35f, height * 0.35f, width * 0.55f, height * 0.35f)
                        cubicTo(width * 0.65f, height * 0.35f, width * 0.65f, height * 0.48f, width * 0.88f, height * 0.48f)
                    }
                    drawPath(
                        path = greenPath,
                        color = Color(0xFFC6FF00),
                        style = Stroke(width = 3.5.dp.toPx())
                    )
                }

                // Node Icons / Badges overlaid on canvas
                // Bottom-left node (Walking)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 48.dp, y = (-50).dp)
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFFC6FF00), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚶", fontSize = 16.sp)
                }

                // Center node (Transit)
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 10.dp, y = 10.dp)
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚆", fontSize = 16.sp)
                }

                // Top-right node (Bicycle)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-48.dp), y = 36.dp)
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFFC6FF00), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚲", fontSize = 16.sp)
                }

                // Bottom Pill Badge: ONE CITY. EVERY WAY TO MOVE.
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF101D17))
                        .border(1.dp, Color(0xFF1E3F2A), RoundedCornerShape(20.dp))
                        .clickable { onGetStarted() }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color(0xFFC6FF00), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF070B14), CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ONE CITY. EVERY WAY TO MOVE.",
                            color = Color(0xFFC6FF00),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Footer Section (Loader & Subtitle)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                // Progress Bar Indicator
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF1E293B))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .fillMaxHeight()
                            .background(Color(0xFFC6FF00), RoundedCornerShape(2.dp))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Intelligent mobility, made for you",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
