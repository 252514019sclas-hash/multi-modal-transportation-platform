package com.simats.multimodaltransportationrecommendation.ui.ticket

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun YourTicketScreen(
    viewModel: YourTicketViewModel = viewModel(),
    bookedJourney: BookedJourneyData? = null,
    trip: TripItem? = null,
    onBack: () -> Unit = {},
    onStartJourney: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            .background(BackgroundSoftCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                text = "Digital Transit Pass",
                                color = TextNavyDark,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Download / Share Action
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📥", fontSize = 16.sp)
                    }
                }

                // Confirmation Badge Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PastelCardTeal)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(TealSecondary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✓", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ticket Confirmed & Active!",
                            color = TealSecondary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.confirmationText,
                            color = TextNavyDark,
                            fontSize = 12.sp
                        )
                    }
                }

                // Ticket Pass Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Card Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PastelCardBlue)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🚇  ${uiState.transportLine}",
                                    color = SkyBluePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = uiState.passengerType,
                                color = TextMutedSlate,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Origin -> Destination & Time
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${uiState.origin} → ${uiState.destination}",
                                color = TextNavyDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${uiState.dateTime} · ${uiState.durationText} journey",
                                color = TextMutedSlate,
                                fontSize = 12.sp
                            )
                        }

                        HorizontalDivider(color = BorderLight, thickness = 1.dp)

                        // QR Code Visual Box
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                                .border(1.5.dp, BorderLight, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val blockSize = size.width / 15f
                                drawRect(color = Color(0xFF0F172A), topLeft = Offset(0f, 0f), size = Size(blockSize * 4, blockSize * 4))
                                drawRect(color = Color.White, topLeft = Offset(blockSize, blockSize), size = Size(blockSize * 2, blockSize * 2))
                                drawRect(color = Color(0xFF0F172A), topLeft = Offset(size.width - blockSize * 4, 0f), size = Size(blockSize * 4, blockSize * 4))
                                drawRect(color = Color.White, topLeft = Offset(size.width - blockSize * 3, blockSize), size = Size(blockSize * 2, blockSize * 2))
                                drawRect(color = Color(0xFF0F172A), topLeft = Offset(0f, size.height - blockSize * 4), size = Size(blockSize * 4, blockSize * 4))
                                drawRect(color = Color.White, topLeft = Offset(blockSize, size.height - blockSize * 3), size = Size(blockSize * 2, blockSize * 2))

                                for (i in 4..10) {
                                    for (j in 4..10) {
                                        if ((i + j) % 2 == 0) {
                                            drawRect(color = Color(0xFF0F172A), topLeft = Offset(i * blockSize, j * blockSize), size = Size(blockSize, blockSize))
                                        }
                                    }
                                }
                            }
                        }

                        // Ticket Code
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.ticketCode,
                                color = SkyBluePrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.ticketValidity,
                                color = TextMutedSlate,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Bottom CTA Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = uiState.ticketNotice,
                    color = TextMutedSlate,
                    fontSize = 11.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(PrimaryGradient)
                        .clickable { onStartJourney() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start Journey Now  ➔",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(23.dp))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "View in My Trips",
                        color = TextNavyDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
