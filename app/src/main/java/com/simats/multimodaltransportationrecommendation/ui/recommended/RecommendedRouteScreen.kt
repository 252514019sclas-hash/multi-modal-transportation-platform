package com.simats.multimodaltransportationrecommendation.ui.recommended

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecommendedRouteScreen(
    viewModel: RecommendedRouteViewModel = RecommendedRouteViewModel(),
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {}
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
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar, Map & Overview Stats
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                text = "11 / MOVESMART AI",
                                color = Color(0xFF7A8B9E),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Recommended route",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bookmark Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔖", fontSize = 14.sp)
                    }
                }

                // Map Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0B1323))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val gridColor = Color(0xFF162238)
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

                        val greenPath = Path().apply {
                            moveTo(width * 0.2f, height * 0.7f)
                            lineTo(width * 0.45f, height * 0.5f)
                            lineTo(width * 0.85f, height * 0.2f)
                        }
                        drawPath(greenPath, Color(0xFFC6FF00), style = Stroke(width = 3.dp.toPx()))
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "Ferry Building", color = Color.White, fontSize = 9.sp)
                    }
                }

                // Overview Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = uiState.duration, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = uiState.timeRange, color = Color(0xFF8A99AD), fontSize = 10.sp)
                    }
                    Column {
                        Text(text = uiState.totalFare, color = Color(0xFFC6FF00), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Total fare", color = Color(0xFF8A99AD), fontSize = 10.sp)
                    }
                    Column {
                        Text(text = "${uiState.transfers}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Transit transfers", color = Color(0xFF8A99AD), fontSize = 10.sp)
                    }
                }

                // Badges Row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF112017))
                            .border(1.dp, Color(0xFF1E4D2B), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "☘️", fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "AI RECOMMENDED", color = Color(0xFFC6FF00), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Step-free available", color = Color(0xFF8A99AD), fontSize = 9.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Journey Step by Step Section
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Your journey, step by step", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = uiState.dateText, color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                // Steps Timeline List
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    uiState.steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = step.time, color = Color(0xFF8A99AD), fontSize = 10.sp, modifier = Modifier.width(55.dp))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFF0F172A), CircleShape)
                                    .border(1.dp, Color(0xFF1E293B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = step.icon, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = step.title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = step.description, color = Color(0xFF8A99AD), fontSize = 10.sp)
                            }
                        }
                    }
                }

                Text(
                    text = uiState.footerSummary,
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Bottom CTA Button
            Button(
                onClick = { onContinue() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC6FF00)
                )
            ) {
                Text(
                    text = "Continue to transit details  →",
                    color = Color(0xFF070B14),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun RecommendedRouteScreenPreview() {
    RecommendedRouteScreen()
}
