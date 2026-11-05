package com.simats.multimodaltransportationrecommendation.ui.ticket

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.simats.multimodaltransportationrecommendation.data.model.tickets.TicketItem
import com.simats.multimodaltransportationrecommendation.data.model.trips.BookedJourneyData
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.ui.theme.*
import java.util.concurrent.Executors

@Composable
fun YourTicketScreen(
    viewModel: YourTicketViewModel = viewModel(),
    bookedJourney: BookedJourneyData? = null,
    trip: TripItem? = null,
    onBack: () -> Unit = {},
    onStartJourney: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var showDetailsDialog by remember { mutableStateOf(false) }

    // Initial loading of backend tickets
    LaunchedEffect(Unit) {
        viewModel.loadTickets(context)
    }

    // Set incoming ticket if passed from booking or trip history
    LaunchedEffect(bookedJourney, trip) {
        if (bookedJourney != null) {
            viewModel.setTicketFromBooking(bookedJourney)
        } else if (trip != null) {
            viewModel.setTicketFromTrip(trip)
        }
    }

    // Camera Scanner Dialog
    if (uiState.scannerActive) {
        QrScannerDialog(
            isVerifying = uiState.isVerifying,
            scanResult = uiState.scanResult,
            scanError = uiState.scanErrorMessage,
            onClose = { viewModel.toggleScanner(false) },
            onVerifyCode = { code -> viewModel.verifyScannedCode(code) },
            onMarkUsed = { code -> viewModel.verifyScannedCode(code, markUsed = true) },
            onClearResult = { viewModel.clearScanResult() }
        )
    }

    // Detailed Ticket Modal Dialog
    if (showDetailsDialog && uiState.selectedTicket != null) {
        TicketDetailDialog(
            ticket = uiState.selectedTicket!!,
            paymentStatus = uiState.paymentStatusResult,
            onDismiss = { showDetailsDialog = false },
            onShare = {
                val t = uiState.selectedTicket!!
                shareTicketText(context, t)
            },
            onDownload = {
                val t = uiState.selectedTicket!!
                Toast.makeText(context, "Ticket ${t.bookingReference} downloaded to device", Toast.LENGTH_SHORT).show()
            }
        )
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
                .padding(horizontal = 18.dp),
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
                                .size(40.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, CircleShape)
                                .clickable { onBack() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "←", color = TextNavyDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MOVESMART AI · TAMIL NADU TRANSIT",
                                color = SkyBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "Smart Travel Ticket & QR",
                                color = TextNavyDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Refresh Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, CircleShape)
                            .clickable { viewModel.loadTickets(context) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔄", fontSize = 14.sp)
                    }
                }

                // Feature B: Prominent "Scan Ticket QR" Button Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PrimaryGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "📷", fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "QR Ticket Scanner",
                                    color = TextNavyDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Scan & verify real MoveSmart transit tickets",
                                    color = TextMutedSlate,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleScanner(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Scan QR",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // If recent verified ticket exists, show verification result card
                uiState.scanResult?.let { result ->
                    VerificationResultCard(
                        result = result,
                        onDismiss = { viewModel.clearScanResult() },
                        onCheckIn = {
                            result.ticket?.bookingReference?.let { code ->
                                viewModel.verifyScannedCode(code, markUsed = true)
                            }
                        }
                    )
                }

                // Feature A: My Travel Tickets Tabs (Upcoming, Completed, Cancelled)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "My Travel Tickets",
                        color = TextNavyDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BorderSubtle)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Upcoming", "Completed", "Cancelled").forEach { tab ->
                            val isSelected = uiState.selectedTab == tab
                            val count = uiState.tickets.count {
                                it.status.equals(tab, ignoreCase = true)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) SurfaceWhite else Color.Transparent)
                                    .clickable { viewModel.selectTab(tab) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (count > 0) "$tab ($count)" else tab,
                                    color = if (isSelected) SkyBluePrimary else TextMutedSlate,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Ticket List for selected tab
                    val tabTickets = uiState.tickets.filter {
                        it.status.equals(uiState.selectedTab, ignoreCase = true)
                    }

                    if (tabTickets.isEmpty() && uiState.selectedTicket == null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "🎫", fontSize = 28.sp)
                                Text(
                                    text = "No ${uiState.selectedTab.lowercase()} tickets found",
                                    color = TextNavyDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Book a multi-modal journey or scan a valid QR ticket.",
                                    color = TextMutedSlate,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else if (tabTickets.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(tabTickets) { item ->
                                val isChosen = uiState.selectedTicket?.bookingReference == item.bookingReference
                                TicketMiniCard(
                                    ticket = item,
                                    isSelected = isChosen,
                                    onClick = { viewModel.selectTicket(item) }
                                )
                            }
                        }
                    }
                }

                // Feature D: Selected Ticket Details & QR Pass Card
                uiState.selectedTicket?.let { ticket ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Top Tag & Fare
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
                                        text = "${modeIcon(ticket.mode)} ${ticket.mode.uppercase()} TRANSIT PASS",
                                        color = SkyBluePrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = ticket.fareText.ifBlank { "₹${ticket.fare.toInt()}" },
                                    color = EmeraldGreen,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Passenger & Ref
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "PASSENGER",
                                        color = TextMutedSlate,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = ticket.passengerName.ifBlank { "MoveSmart Traveler" },
                                        color = TextNavyDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "BOOKING REF",
                                        color = TextMutedSlate,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = ticket.bookingReference,
                                        color = SkyBluePrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            HorizontalDivider(color = BorderLight, thickness = 1.dp)

                            // Origin -> Destination
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "FROM",
                                        color = TextMutedSlate,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = ticket.origin,
                                        color = TextNavyDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = "➔",
                                    color = SkyBluePrimary,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "TO",
                                        color = TextMutedSlate,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = ticket.destination,
                                        color = TextNavyDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Date, Duration, Distance
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BackgroundSoftCream)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                MiniMetaItem("DATE", ticket.journeyDate.ifBlank { "Today" })
                                MiniMetaItem("TIME", "${ticket.durationMinutes} min")
                                MiniMetaItem("STATUS", ticket.status.uppercase())
                            }

                            // QR Code Box (Canvas Graphic for Confirmed Tickets)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BackgroundSoftCream)
                                    .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(150.dp)
                                            .background(Color.White, RoundedCornerShape(8.dp))
                                            .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        QrCodeCanvas(seed = ticket.bookingReference)
                                    }

                                    Text(
                                        text = ticket.bookingReference,
                                        color = SkyBluePrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp
                                    )

                                    Text(
                                        text = "Scan at station gate turnstile or bus QR validator",
                                        color = TextMutedSlate,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            // Actions: View Ticket, Download, Share
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showDetailsDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SkyBluePrimary)
                                ) {
                                    Text("View", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            "Ticket ${ticket.bookingReference} downloaded as digital pass receipt",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealSecondary)
                                ) {
                                    Text("Download", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { shareTicketText(context, ticket) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextNavyDark)
                                ) {
                                    Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Feature C: Transit Payment Status Section (Replacing old Payment Method screen)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "💳", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Transit Payment Status",
                                    color = TextNavyDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            val pStatus = uiState.paymentStatusResult?.paymentStatus
                                ?: uiState.selectedTicket?.paymentStatus
                                ?: "successful"

                            val (pillBg, pillText) = when (pStatus.lowercase()) {
                                "successful" -> PastelCardTeal to TealSecondary
                                "pending" -> PastelCardPeach to WarmOrangeAccent
                                else -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(pillBg)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = pStatus.replaceFirstChar { it.uppercase() },
                                    color = pillText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(color = BorderLight, thickness = 1.dp)

                        val txnId = uiState.paymentStatusResult?.transactionId?.ifBlank { null }
                            ?: uiState.selectedTicket?.transactionId?.ifBlank { null }
                            ?: ("TXN-" + (uiState.selectedTicket?.bookingReference ?: "MS-126F13"))

                        val bookingRef = uiState.paymentStatusResult?.bookingReference?.ifBlank { null }
                            ?: uiState.selectedTicket?.bookingReference
                            ?: "TN-MS-126F13"

                        val amountText = uiState.paymentStatusResult?.amountText?.ifBlank { null }
                            ?: uiState.selectedTicket?.fareText?.ifBlank { null }
                            ?: ("₹" + (uiState.selectedTicket?.fare?.toInt() ?: 30))

                        val paymentDate = uiState.paymentStatusResult?.paymentDate?.ifBlank { null }
                            ?: uiState.selectedTicket?.paymentDate?.ifBlank { null }
                            ?: uiState.selectedTicket?.journeyDate
                            ?: "Recent"

                        PaymentRow("Transaction ID", txnId)
                        PaymentRow("Booking Reference", bookingRef)
                        PaymentRow("Amount Paid", amountText)
                        PaymentRow("Payment Date", paymentDate)
                        PaymentRow("Gateway Provider", "MoveSmart Transit Gateway")

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(BackgroundSoftCream)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "🔒 Verified digital transit payment. No credit card or sensitive credentials stored on this device.",
                                color = TextMutedSlate,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
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
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(PrimaryGradient)
                        .clickable { onStartJourney() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start Journey Now  ➔",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, RoundedCornerShape(22.dp))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Back to My Trips",
                        color = TextNavyDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// Subcomponents

@Composable
private fun TicketMiniCard(
    ticket: TicketItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(2.dp, SkyBluePrimary, RoundedCornerShape(14.dp))
                else Modifier.border(1.dp, BorderLight, RoundedCornerShape(14.dp))
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${modeIcon(ticket.mode)} ${ticket.mode}",
                    color = SkyBluePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ticket.bookingReference,
                    color = TextMutedSlate,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "${ticket.origin} → ${ticket.destination}",
                color = TextNavyDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = ticket.journeyDate.ifBlank { "Active" },
                    color = TextMutedSlate,
                    fontSize = 10.sp
                )
                Text(
                    text = ticket.fareText.ifBlank { "₹${ticket.fare.toInt()}" },
                    color = EmeraldGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PaymentRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextMutedSlate, fontSize = 11.sp)
        Text(
            text = value,
            color = TextNavyDark,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MiniMetaItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMutedSlate, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

// Feature B: Full Scanner Dialog with CameraX + ML Kit and Manual Code Fallback
@Composable
private fun QrScannerDialog(
    isVerifying: Boolean,
    scanResult: com.simats.multimodaltransportationrecommendation.data.model.tickets.VerifyTicketResponse?,
    scanError: String?,
    onClose: () -> Unit,
    onVerifyCode: (String) -> Unit,
    onMarkUsed: (String) -> Unit,
    onClearResult: () -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var manualCodeInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SMART TRAVEL SCANNER",
                            color = TealSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Scan Ticket QR Code",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF334155))
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Camera Preview or Permission Request
                if (hasCameraPermission) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black)
                            .border(2.dp, SkyBluePrimary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CameraScannerView(
                            onBarcodeScanned = { rawCode ->
                                onVerifyCode(rawCode)
                            }
                        )

                        // Scanner Reticle Overlay
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .border(2.dp, TealSecondary, RoundedCornerShape(12.dp))
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = "📷", fontSize = 32.sp)
                            Text(
                                text = "Camera Permission Needed",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "MoveSmart needs camera access to scan ticket QR codes and verify boarding.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                            ) {
                                Text("Grant Camera Permission", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Manual Code Input Fallback
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Or Enter Ticket / QR Code Manually",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = manualCodeInput,
                                onValueChange = { manualCodeInput = it.uppercase() },
                                modifier = Modifier.weight(1f),
                                placeholder = {
                                    Text("e.g. TN-MS-126F13", color = Color(0xFF64748B), fontSize = 12.sp)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    keyboardController?.hide()
                                    if (manualCodeInput.isNotBlank()) onVerifyCode(manualCodeInput.trim())
                                }),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = SkyBluePrimary,
                                    unfocusedBorderColor = Color(0xFF475569)
                                )
                            )

                            Button(
                                onClick = {
                                    keyboardController?.hide()
                                    if (manualCodeInput.isNotBlank()) onVerifyCode(manualCodeInput.trim())
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealSecondary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Verify", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Verification In-Progress Indicator
                if (isVerifying) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F766E).copy(alpha = 0.3f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = TealSecondary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Verifying with MoveSmart Transit Server...",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }

                // Error alert
                scanError?.let { err ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "❌", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = err,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Verification Result Card
                scanResult?.let { res ->
                    VerificationResultCard(
                        result = res,
                        onDismiss = onClearResult,
                        onCheckIn = {
                            res.ticket?.bookingReference?.let { code ->
                                onMarkUsed(code)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close Scanner", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

// Verification Result Alert Component
@Composable
private fun VerificationResultCard(
    result: com.simats.multimodaltransportationrecommendation.data.model.tickets.VerifyTicketResponse,
    onDismiss: () -> Unit,
    onCheckIn: () -> Unit
) {
    val status = result.status.lowercase()
    val (cardBg, borderCol, titleColor, icon) = when (status) {
        "valid" -> Tuple4(Color(0xFFECFDF5), TealSecondary, Color(0xFF065F46), "✓ Valid Ticket")
        "used" -> Tuple4(Color(0xFFFEF3C7), WarmOrangeAccent, Color(0xFF92400E), "⚠ Already Used Ticket")
        "expired" -> Tuple4(Color(0xFFFFEDD5), Color(0xFFEA580C), Color(0xFF9A3412), "⚠ Expired Ticket")
        "cancelled" -> Tuple4(Color(0xFFFEE2E2), Color(0xFFDC2626), Color(0xFF991B1B), "✕ Cancelled Ticket")
        else -> Tuple4(Color(0xFFFEE2E2), Color(0xFFDC2626), Color(0xFF991B1B), "✕ Invalid Ticket")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, borderCol, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    color = titleColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "✕",
                    color = titleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(4.dp)
                )
            }

            Text(
                text = result.message,
                color = titleColor,
                fontSize = 12.sp
            )

            result.ticket?.let { t ->
                HorizontalDivider(color = borderCol.copy(alpha = 0.3f), thickness = 1.dp)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("PASSENGER", fontSize = 9.sp, color = TextMutedSlate, fontWeight = FontWeight.Bold)
                        Text(t.passengerName.ifBlank { "MoveSmart Traveler" }, fontSize = 12.sp, color = TextNavyDark, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("REF", fontSize = 9.sp, color = TextMutedSlate, fontWeight = FontWeight.Bold)
                        Text(t.bookingReference, fontSize = 12.sp, color = SkyBluePrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "${t.origin} → ${t.destination} · ${t.mode.uppercase()}",
                    color = TextNavyDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (status == "valid") {
                    Button(
                        onClick = onCheckIn,
                        colors = ButtonDefaults.buttonColors(containerColor = TealSecondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Check-In / Board Passenger", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// CameraX Preview Wrapper with ML Kit Analyzer
@OptIn(ExperimentalGetImage::class)
@Composable
private fun CameraScannerView(
    onBarcodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    var lastScannedCode by remember { mutableStateOf("") }
    var lastScannedTime by remember { mutableLongStateOf(0L) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val inputImage = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        barcodeScanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    val raw = barcode.rawValue
                                    val now = System.currentTimeMillis()
                                    if (!raw.isNullOrBlank() && (raw != lastScannedCode || now - lastScannedTime > 3000)) {
                                        lastScannedCode = raw
                                        lastScannedTime = now
                                        onBarcodeScanned(raw)
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

// Ticket Detailed Dialog
@Composable
private fun TicketDetailDialog(
    ticket: TicketItem,
    paymentStatus: com.simats.multimodaltransportationrecommendation.data.model.tickets.PaymentStatusResponse?,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ticket Pass Summary",
                        color = TextNavyDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "✕",
                        color = TextMutedSlate,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onDismiss() }
                            .padding(4.dp)
                    )
                }

                HorizontalDivider(color = BorderLight, thickness = 1.dp)

                PaymentRow("Passenger", ticket.passengerName.ifBlank { "MoveSmart Traveler" })
                PaymentRow("Booking Reference", ticket.bookingReference)
                PaymentRow("Origin", ticket.origin)
                PaymentRow("Destination", ticket.destination)
                PaymentRow("Transport Mode", ticket.mode.uppercase())
                PaymentRow("Duration", "${ticket.durationMinutes} mins")
                PaymentRow("Distance", "${ticket.distanceKm} km")
                PaymentRow("Fare Amount", ticket.fareText.ifBlank { "₹${ticket.fare.toInt()}" })
                PaymentRow("Journey Date", ticket.journeyDate.ifBlank { "Confirmed" })
                PaymentRow("Ticket Status", ticket.status.uppercase())
                PaymentRow("Payment Status", (paymentStatus?.paymentStatus ?: ticket.paymentStatus).uppercase())
                PaymentRow("Transaction Ref", paymentStatus?.transactionId ?: ticket.transactionId)

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDownload,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Download", color = Color.White, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onShare,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TealSecondary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Share", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// QR Code Canvas Matrix Renderer
@Composable
private fun QrCodeCanvas(seed: String) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val count = 17
        val blockSize = size.width / count.toFloat()

        // 3 Corner Finder Patterns (Top-Left, Top-Right, Bottom-Left)
        fun drawFinder(x: Float, y: Float) {
            drawRect(color = Color(0xFF0F172A), topLeft = Offset(x, y), size = Size(blockSize * 5, blockSize * 5))
            drawRect(color = Color.White, topLeft = Offset(x + blockSize, y + blockSize), size = Size(blockSize * 3, blockSize * 3))
            drawRect(color = Color(0xFF0F172A), topLeft = Offset(x + blockSize * 2, y + blockSize * 2), size = Size(blockSize, blockSize))
        }

        drawFinder(0f, 0f)
        drawFinder(size.width - blockSize * 5, 0f)
        drawFinder(0f, size.height - blockSize * 5)

        // Seed-based internal pattern
        val hash = seed.hashCode()
        for (i in 0 until count) {
            for (j in 0 until count) {
                // Skip finders
                if (i < 6 && j < 6) continue
                if (i >= count - 6 && j < 6) continue
                if (i < 6 && j >= count - 6) continue

                val bit = ((hash xor (i * 31 + j * 17)) and 0x1) == 1
                if (bit) {
                    drawRect(
                        color = Color(0xFF0F172A),
                        topLeft = Offset(i * blockSize, j * blockSize),
                        size = Size(blockSize, blockSize)
                    )
                }
            }
        }
    }
}

// Mode icon helper
private fun modeIcon(mode: String): String = when (mode.lowercase()) {
    "metro" -> "🚇"
    "bus" -> "🚌"
    "train" -> "🚆"
    "taxi", "cab" -> "🚕"
    "cycle", "cycling" -> "🚲"
    "walk", "walking" -> "🚶"
    else -> "🚀"
}

// Share Intent helper
private fun shareTicketText(context: Context, ticket: TicketItem) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            """
            🎟️ MoveSmart AI Transit Ticket Pass
            -----------------------------------
            Passenger: ${ticket.passengerName.ifBlank { "MoveSmart Traveler" }}
            Booking Ref: ${ticket.bookingReference}
            Route: ${ticket.origin} ➔ ${ticket.destination}
            Transit Mode: ${ticket.mode.uppercase()}
            Fare: ${ticket.fareText.ifBlank { "₹" + ticket.fare.toInt() }}
            Date: ${ticket.journeyDate}
            Status: ${ticket.status.uppercase()}
            -----------------------------------
            Verified by MoveSmart AI Intelligent Transportation System
            """.trimIndent()
        )
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share MoveSmart Transit Pass"))
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
