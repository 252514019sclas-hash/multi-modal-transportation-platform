package com.simats.multimodaltransportationrecommendation.ui.onboarding

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
fun OnboardingScreen(
    onGetStarted: () -> Unit = {},
    onSignIn: () -> Unit = {}
) {
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
            // Top Header
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = "02 / MOVESMART AI",
                    color = Color(0xFF7A8B9E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A smarter way to go",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Map Graphic Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0B1323))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid lines
                    val gridColor = Color(0xFF162238)
                    val cols = 6
                    val rows = 5
                    val dx = width / cols
                    val dy = height / rows

                    for (i in 1 until cols) {
                        drawLine(
                            color = gridColor,
                            start = Offset(i * dx, 0f),
                            end = Offset(i * dx, height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    for (j in 1 until rows) {
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, j * dy),
                            end = Offset(width, j * dy),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Secondary blue route
                    val bluePath = Path().apply {
                        moveTo(width * 0.1f, height * 0.7f)
                        lineTo(width * 0.4f, height * 0.7f)
                        cubicTo(width * 0.5f, height * 0.7f, width * 0.5f, height * 0.3f, width * 0.6f, height * 0.3f)
                        lineTo(width * 0.9f, height * 0.3f)
                    }
                    drawPath(
                        path = bluePath,
                        color = Color(0xFF38BDF8).copy(alpha = 0.4f),
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Primary lime green route
                    val greenPath = Path().apply {
                        moveTo(width * 0.25f, height * 0.75f)
                        lineTo(width * 0.25f, height * 0.55f)
                        lineTo(width * 0.55f, height * 0.55f)
                        lineTo(width * 0.88f, height * 0.22f)
                    }
                    drawPath(
                        path = greenPath,
                        color = Color(0xFFC6FF00),
                        style = Stroke(width = 3.5.dp.toPx())
                    )
                }

                // Map Labels Overlay
                Text(
                    text = "RICHMOND",
                    color = Color(0xFF64748B),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopStart).padding(4.dp)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Ferry Building",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = 12.dp, y = (-20).dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF112218))
                        .border(1.dp, Color(0xFF1E4028), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Academy of Sciences",
                        color = Color(0xFFC6FF00),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = (-30.dp), y = 10.dp)
                        .size(20.dp)
                        .background(Color(0xFFC6FF00), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "N", color = Color(0xFF070B14), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Powered By Badge
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
                        text = "POWERED BY MOVE AI",
                        color = Color(0xFFC6FF00),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Main Headline & Subtitle
            Column {
                Text(
                    text = "Your city. Your pace.\nYour best route.",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Walk, ride or roll. AI connects the best ways to travel, all in one personal journey.",
                    color = Color(0xFF8A99AD),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            // Feature Cards (2 rows)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Card 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF162238)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔄", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Every mode, one plan",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Transit, bikes and rides that work together.",
                                color = Color(0xFF8A99AD),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFF162238), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✓", color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Card 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF162238)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🌿", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Better for you. Lighter on the planet.",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Balance time, cost and your carbon footprint.",
                                color = Color(0xFF8A99AD),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFF162238), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✓", color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Bottom CTA & Sign In Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onGetStarted() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC6FF00)
                    )
                ) {
                    Text(
                        text = "Get started  →",
                        color = Color(0xFF070B14),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.clickable { onSignIn() },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = Color(0xFF8A99AD),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Sign in",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen()
}
