package com.simats.multimodaltransportationrecommendation.ui.ticket

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem

@Composable
fun YourTicketScreen(
    viewModel: YourTicketViewModel = viewModel(),
    bookedJourney: BookedJourneyData? = null,
    trip: TripItem? = null,
    onBack: () -> Unit = {},
    onStartJourney: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(bookedJourney, trip) {
        if (bookedJourney != null) {
            viewModel.setTicketFromBooking(bookedJourney)
        } else if (trip != null) {
            viewModel.setTicketFromTrip(trip)
        }
    }

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
            // Top Bar, Confirmation Header & Ticket Pass Card
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
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
                                text = "15 / MOVESMART AI",
                                color = Color(0xFF7A8B9E),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Your ticket",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Download Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF1E293B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📥", fontSize = 14.sp)
                    }
                }

                // Confirmation Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFF112017), CircleShape)
                            .border(1.dp, Color(0xFF1E4D2B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✓", color = Color(0xFFC6FF00), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "You're all set!",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.confirmationText,
                            color = Color(0xFF8A99AD),
                            fontSize = 11.sp
                        )
                    }
                }

                // Main Ticket Pass Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Card Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF112017))
                                    .border(1.dp, Color(0xFF1E4D2B), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "🚊  ${uiState.transportLine}",
                                    color = Color(0xFFC6FF00),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = uiState.passengerType,
                                color = Color(0xFF8A99AD),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Origin -> Destination & Time
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "${uiState.origin} → ${uiState.destination}",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${uiState.dateTime} · ${uiState.durationText}",
                                color = Color(0xFF8A99AD),
                                fontSize = 11.sp
                            )
                        }

                        // QR Code Visual Box
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val blockSize = size.width / 15f
                                drawRect(color = Color.Black, topLeft = Offset(0f, 0f), size = Size(blockSize * 4, blockSize * 4))
                                drawRect(color = Color.White, topLeft = Offset(blockSize, blockSize), size = Size(blockSize * 2, blockSize * 2))
                                drawRect(color = Color.Black, topLeft = Offset(size.width - blockSize * 4, 0f), size = Size(blockSize * 4, blockSize * 4))
                                drawRect(color = Color.White, topLeft = Offset(size.width - blockSize * 3, blockSize), size = Size(blockSize * 2, blockSize * 2))
                                drawRect(color = Color.Black, topLeft = Offset(0f, size.height - blockSize * 4), size = Size(blockSize * 4, blockSize * 4))
                                drawRect(color = Color.White, topLeft = Offset(blockSize, size.height - blockSize * 3), size = Size(blockSize * 2, blockSize * 2))

                                for (i in 4..10) {
                                    for (j in 4..10) {
                                        if ((i + j) % 2 == 0) {
                                            drawRect(color = Color.Black, topLeft = Offset(i * blockSize, j * blockSize), size = Size(blockSize, blockSize))
                                        }
                                    }
                                }
                            }
                        }

                        // Ticket Code
                        Text(
                            text = uiState.ticketCode,
                            color = Color(0xFFC6FF00),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = uiState.ticketValidity,
                            color = Color(0xFF8A99AD),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Bottom CTA Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Text(
                    text = uiState.ticketNotice,
                    color = Color(0xFF64748B),
                    fontSize = 10.sp
                )

                Button(
                    onClick = { onStartJourney() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC6FF00))
                ) {
                    Text(
                        text = "Start journey now  →",
                        color = Color(0xFF070B14),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onBack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text(
                        text = "View in My Trips",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
