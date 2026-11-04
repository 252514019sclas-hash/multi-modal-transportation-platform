package com.simats.multimodaltransportationrecommendation.ui.reroute

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun SmarterWayScreen(
    viewModel: SmarterWayViewModel = SmarterWayViewModel(),
    onBack: () -> Unit = {},
    onAcceptRoute: () -> Unit = {},
    onKeepRoute: () -> Unit = {}
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
            // Top Bar, Delay Alert & Map
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp)
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
                            text = "Smart Reroute Suggestion",
                            color = TextNavyDark,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Delay Alert Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PastelCardPeach)
                        .border(1.dp, WarmOrangeAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "⚠️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = uiState.delayTitle,
                                color = WarmOrangeAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.delayDetails,
                                color = TextNavyDark,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Map Card with Delay Marker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PastelCardBlue)
                        .border(1.dp, BorderLight, RoundedCornerShape(18.dp))
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

                        val routePath = Path().apply {
                            moveTo(width * 0.15f, height * 0.75f)
                            lineTo(width * 0.55f, height * 0.5f)
                            lineTo(width * 0.88f, height * 0.22f)
                        }
                        drawPath(routePath, SkyBluePrimary, style = Stroke(width = 4.dp.toPx()))
                    }

                    // Warning Marker over map
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = (-25.dp), y = 10.dp)
                            .size(28.dp)
                            .background(WarmOrangeAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚠️", fontSize = 13.sp)
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

            // Current Route vs AI Alternative
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(text = uiState.currentRouteStatus, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = uiState.currentRouteMeta, color = TextMutedSlate, fontSize = 11.sp)
                    }
                    Text(text = uiState.currentRouteTime, color = WarmOrangeAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // AI Alternative Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⚡", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI RECOMMENDED FASTER ROUTE",
                                    color = SkyBluePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PastelCardTeal)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = uiState.timeSaved,
                                    color = TealSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = uiState.aiAlternativeTitle,
                                color = TextNavyDark,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.aiAlternativeTime,
                                color = SkyBluePrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = uiState.alternativeDetails,
                            color = TextMutedSlate,
                            fontSize = 12.sp
                        )

                        Text(
                            text = uiState.alternativePerks,
                            color = TealSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    text = uiState.scenarioNote,
                    color = TextMutedSlate,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
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
                        .clickable { onAcceptRoute() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Accept Faster Route",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "🔀", fontSize = 13.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(23.dp))
                        .clickable { onKeepRoute() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Keep Current Route",
                        color = TextNavyDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
