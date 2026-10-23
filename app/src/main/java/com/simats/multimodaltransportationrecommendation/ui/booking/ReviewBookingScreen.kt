package com.simats.multimodaltransportationrecommendation.ui.booking

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
fun ReviewBookingScreen(
    viewModel: ReviewBookingViewModel = ReviewBookingViewModel(),
    onBack: () -> Unit = {},
    onConfirmAndPay: () -> Unit = {}
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
            // Top Bar & Step Tabs
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = uiState.dateText, color = Color(0xFF8A99AD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = uiState.passengerText, color = Color(0xFF8A99AD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(text = "🚶", fontSize = 11.sp)
                            Text(text = "›", color = Color(0xFF8A99AD), fontSize = 10.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1A3824))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = "🚊  N Judah", color = Color(0xFFC6FF00), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "›", color = Color(0xFF8A99AD), fontSize = 10.sp)
                            Text(text = "🚶", fontSize = 11.sp)
                        }

                        Text(
                            text = "${uiState.timeRange} - ${uiState.duration} - ${uiState.transfers}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = uiState.stepDetails,
                            color = Color(0xFF8A99AD),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Fare Breakdown & Payment Method
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Fare breakdown
                Text(text = "Fare breakdown", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)

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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = uiState.ticketItem, color = Color(0xFF8A99AD), fontSize = 12.sp)
                            Text(text = uiState.ticketPrice, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Booking fee", color = Color(0xFF8A99AD), fontSize = 12.sp)
                            Text(text = uiState.bookingFee, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFF1E293B))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Total payable", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = uiState.totalPayable, color = Color(0xFFC6FF00), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Payment method
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Payment method", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Edit", color = Color(0xFFC6FF00), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                        .padding(12.dp),
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
                            Text(text = "💳", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = uiState.cardName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = uiState.cardHolder, color = Color(0xFF8A99AD), fontSize = 10.sp)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFFC6FF00), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✓", color = Color(0xFF070B14), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "ℹ️", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = uiState.termsNote,
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                }
            }

            // Bottom CTA Button & Secure checkout info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            ) {
                Text(
                    text = "Secure checkout · No hidden fees",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { onConfirmAndPay() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC6FF00)
                    )
                ) {
                    Text(
                        text = "Confirm & pay · $3.00  →",
                        color = Color(0xFF070B14),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun ReviewBookingScreenPreview() {
    ReviewBookingScreen()
}
