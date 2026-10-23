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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompareRoutesScreen(
    viewModel: CompareRoutesViewModel = CompareRoutesViewModel(),
    onBack: () -> Unit = {},
    onChooseRoute: () -> Unit = {}
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
            // Top Bar & Route Summary
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
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
                            text = "10 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Compare your routes",
                            color = Color.White,
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
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFFC6FF00), CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = uiState.origin, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(7.dp).background(Color(0xFF38BDF8), CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = uiState.destination, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Text(
                    text = "See the trade-offs. Choose what feels right.",
                    color = Color(0xFF8A99AD),
                    fontSize = 12.sp
                )

                // Table Headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "TIME", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "FARE", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "WALK", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "CO₂", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "ON TIME", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Route Comparison Cards List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.routes.forEach { route ->
                    val isSelected = uiState.selectedRouteTitle == route.title
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFF112017) else Color(0xFF0F172A))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) Color(0xFF1E4D2B) else Color(0xFF1E293B),
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
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (route.isAiPick) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFF1A3824))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "AI PICK",
                                                color = Color(0xFFC6FF00),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Text(text = "✓", color = Color(0xFF070B14), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Metrics Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = route.time, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(text = route.fare, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(text = route.walk, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(text = route.co2, color = if (route.co2 == "0.4 kg" || route.co2 == "0 kg") Color(0xFFC6FF00) else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(text = route.onTime, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // AI Insight Footer & Note
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌿", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.aiInsight,
                        color = Color(0xFF8A99AD),
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = uiState.note,
                    color = Color(0xFF475569),
                    fontSize = 9.sp,
                    lineHeight = 12.sp
                )
            }

            // Bottom CTA Button
            Button(
                onClick = { onChooseRoute() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC6FF00)
                )
            ) {
                Text(
                    text = "Choose N Judah · $3.00  →",
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
fun CompareRoutesScreenPreview() {
    CompareRoutesScreen()
}
