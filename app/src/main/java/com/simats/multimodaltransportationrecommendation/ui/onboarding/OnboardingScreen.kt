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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit = {},
    onSignIn: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
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
                    text = "02 / DISCOVER TAMIL NADU",
                    color = SkyBluePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Smart Tourism & Seamless Transit",
                    color = TextNavyDark,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Map Graphic Card (Pastel Blue & White)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight, RoundedCornerShape(16.dp))
                    .clickable { onGetStarted() }
                    .padding(12.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid
                    val gridColor = BorderLight
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

                    // Route Lines
                    val tealPath = Path().apply {
                        moveTo(width * 0.1f, height * 0.7f)
                        lineTo(width * 0.4f, height * 0.7f)
                        cubicTo(width * 0.5f, height * 0.7f, width * 0.5f, height * 0.3f, width * 0.6f, height * 0.3f)
                        lineTo(width * 0.9f, height * 0.3f)
                    }
                    drawPath(
                        path = tealPath,
                        color = TealSecondary.copy(alpha = 0.6f),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    val skyPath = Path().apply {
                        moveTo(width * 0.25f, height * 0.8f)
                        lineTo(width * 0.25f, height * 0.5f)
                        lineTo(width * 0.6f, height * 0.5f)
                        lineTo(width * 0.88f, height * 0.22f)
                    }
                    drawPath(
                        path = skyPath,
                        color = SkyBluePrimary,
                        style = Stroke(width = 3.5.dp.toPx())
                    )
                }

                // Node Badges
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PastelCardTeal)
                        .border(1.dp, TealLight, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "🏛️ Mahabalipuram", color = TealDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PastelCardPeach)
                        .border(1.dp, WarmOrangeLight, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "🛕 Thanjavur Big Temple", color = WarmOrangeDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = (-4).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PastelCardBlue)
                        .border(1.dp, SkyBlueSoft, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "🌊 Kanyakumari Memorial", color = SkyBlueDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Powered By Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PastelCardTeal)
                    .border(1.dp, TealSoft, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌴", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TAMIL NADU TOURISM & COMMUTE ENGINE",
                        color = TealDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Main Headline & Subtitle
            Column {
                Text(
                    text = "Your journey. Your heritage.\nYour best route.",
                    color = TextNavyDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 30.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Discover popular temples, serene beaches, hill stations, and waterfalls connected by Metro, MTC, TNSTC, and Southern Railway.",
                    color = TextNavyBody,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            // Feature Cards
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Card 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PastelCardBlue)
                        .border(1.dp, SkyBlueSoft, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🗺️", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Tamil Nadu Tourist Discovery",
                                color = TextNavyDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Meenakshi Amman, Marina Beach, Ooty, Kodaikanal.",
                                color = TextNavyBody,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Text(text = "✓", color = SkyBluePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                // Card 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PastelCardTeal)
                        .border(1.dp, TealSoft, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🚇", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Unified Multi-Modal Transit",
                                color = TextNavyDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Chennai Metro, MTC buses, TNSTC, and Railways.",
                                color = TextNavyBody,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Text(text = "✓", color = TealSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Bottom CTA
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(SkyBluePrimary)
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(BorderLight)
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(BorderLight)
                    )
                }

                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
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
                            text = "Get started  ➔",
                            color = SurfaceWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.clickable { onSignIn() },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Sign in",
                        color = SkyBluePrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
