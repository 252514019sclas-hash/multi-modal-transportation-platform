package com.simats.multimodaltransportationrecommendation.ui.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun ReviewBookingScreen(
    viewModel: ReviewBookingViewModel = viewModel(),
    onBack: () -> Unit = {},
    onConfirmAndPay: (RouteOption?) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
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
            // Top Bar & Step Tabs
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
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
                            text = "13 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Review your booking",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Steps Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "1. Review", color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = "2. Payment", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text(text = "3. Ticket", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                // Trip Summary Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = uiState.dateText, color = Color(0xFF8A99AD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = uiState.passengerText, color = Color(0xFF8A99AD), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(7.dp).background(Color(0xFFC6FF00), CircleShape))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = uiState.origin, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(7.dp).background(Color(0xFF38BDF8), CircleShape))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = uiState.destination, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = uiState.duration, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = uiState.transfers, color = Color(0xFF8A99AD), fontSize = 11.sp)
                        }

                        Text(
                            text = uiState.stepDetails,
                            color = Color(0xFF8A99AD),
                            fontSize = 11.sp
                        )
                    }
                }

                // Fare Breakdown Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "FARES & PRICING", color = Color(0xFF8A99AD), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = uiState.ticketItem, color = Color.White, fontSize = 12.sp)
                            Text(text = uiState.ticketPrice, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "MoveSmart Booking Fee", color = Color(0xFF8A99AD), fontSize = 12.sp)
                            Text(text = "FREE ($0.00)", color = Color(0xFFC6FF00), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        HorizontalDivider(color = Color(0xFF1E293B), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Total Payable", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = uiState.totalPayable, color = Color(0xFFC6FF00), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Bottom CTA Button & Terms Note
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = uiState.termsNote,
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )

                Button(
                    onClick = { onConfirmAndPay(uiState.routeOption) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF00))
                ) {
                    Text(
                        text = "Confirm & Proceed to Payment  →",
                        color = Color(0xFF070B14),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
