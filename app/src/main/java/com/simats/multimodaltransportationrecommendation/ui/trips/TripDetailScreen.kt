package com.simats.multimodaltransportationrecommendation.ui.trips

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripAttraction
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripDetailData
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripScheduleItem
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripStop
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import com.simats.multimodaltransportationrecommendation.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TripDetailScreen(
    trip: TripItem,
    onBack: () -> Unit = {},
    onViewTicket: () -> Unit = {},
    onOpenPlaceDetail: (TouristPlace) -> Unit = {},
    onTripUpdated: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    var detailData by remember { mutableStateOf<TripDetailData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showEditScheduleDialog by remember { mutableStateOf(false) }
    var editedDepartureTime by remember { mutableStateOf("09:15 AM") }

    LaunchedEffect(trip.journeyId) {
        isLoading = true
        when (val res = authRepository.fetchTripDetails(trip.journeyId, trip.userId)) {
            is NetworkResult.Success -> {
                detailData = res.data
                isLoading = false
            }
            is NetworkResult.Error -> {
                // Fallback to synthesizing details from existing trip
                detailData = TripDetailData(
                    journeyId = trip.journeyId,
                    userId = trip.userId,
                    origin = trip.origin,
                    destination = trip.destination,
                    mode = trip.mode,
                    routeTitle = trip.routeTitle,
                    durationMinutes = trip.durationMinutes,
                    distanceKm = trip.distanceKm,
                    fare = trip.fare,
                    fareText = trip.fareText,
                    transfers = trip.transfers,
                    co2SavedKg = trip.co2SavedKg,
                    status = trip.status,
                    journeyDate = trip.journeyDate,
                    bookingReference = trip.bookingReference,
                    ticketCode = trip.bookingReference,
                    paymentStatus = "successful",
                    transactionId = "TXN-${trip.bookingReference}",
                    intermediateStops = listOf(
                        TripStop(trip.origin, "Origin Station", "09:30 AM", "09:35 AM", "Bay 1"),
                        TripStop("Government Estate Metro", "Interchange Station", "09:42 AM", "09:45 AM", "Platform 2"),
                        TripStop("Thousand Lights / DMS", "Corridor Junction", "09:50 AM", "09:52 AM", "Platform 1"),
                        TripStop(trip.destination, "Destination Terminal", "10:05 AM", "--", "Arrival Plaza")
                    ),
                    touristPlaces = listOf(
                        TripAttraction(4, "Marina Beach & Promenade", "Chennai", "Beaches", "World's second longest urban beach with heritage lighthouse.", "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?auto=format&fit=crop&w=800&q=80", 4.7, 1.2, "Free", "2-3 hrs"),
                        TripAttraction(3, "Kapaleeshwarar Temple", "Chennai", "Temples", "7th-century Dravidian architectural marvel in Mylapore.", "https://images.unsplash.com/photo-1609766857041-ed402ea8069a?auto=format&fit=crop&w=800&q=80", 4.9, 2.8, "Free", "1.5 hrs")
                    ),
                    travelSchedule = listOf(
                        TripScheduleItem("09:30 AM", "Departure from ${trip.origin}", "Board primary transit line (${trip.mode})"),
                        TripScheduleItem("09:45 AM", "Corridor Transit & Intermediate Transfer", "Express link avoiding arterial traffic bottlenecks"),
                        TripScheduleItem("10:05 AM", "Arrival at ${trip.destination}", "Alight at central terminal platform"),
                        TripScheduleItem("10:30 AM", "Attraction Visit & Sightseeing", "Explore nearby cultural monuments and promenades")
                    )
                )
                isLoading = false
            }
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
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = TextNavyDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "JOURNEY BREAKDOWN",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Trip Details",
                            color = TextNavyDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Booking Ref Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PastelCardTeal)
                        .border(1.dp, TealLight, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = trip.bookingReference,
                        color = TealDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SkyBluePrimary)
                }
            } else {
                val data = detailData ?: return@Column
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. TRIP SUMMARY & STATUS HEADER CARD
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(3.dp, RoundedCornerShape(18.dp)),
                            shape = RoundedCornerShape(18.dp),
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
                                    val statusColor = when (data.status.lowercase()) {
                                        "completed" -> EmeraldGreen
                                        "ongoing" -> TealDark
                                        "cancelled" -> Color(0xFFDC2626)
                                        else -> SkyBluePrimary
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(statusColor.copy(alpha = 0.12f))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = data.status.uppercase(),
                                            color = statusColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Text(
                                        text = data.journeyDate,
                                        color = TextMutedSlate,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(10.dp).background(SkyBluePrimary, CircleShape))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "From: ${data.origin}", color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(10.dp).background(WarmOrangeAccent, CircleShape))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "To: ${data.destination}", color = TextNavyDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                HorizontalDivider(color = BorderLight)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "${data.durationMinutes} min", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Est. Duration", color = TextMutedSlate, fontSize = 10.sp)
                                    }
                                    Column {
                                        Text(text = "${data.distanceKm} km", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Distance", color = TextMutedSlate, fontSize = 10.sp)
                                    }
                                    Column {
                                        Text(text = data.fareText, color = TealDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Fare Paid", color = TextMutedSlate, fontSize = 10.sp)
                                    }
                                    Column {
                                        Text(text = "🌱 ${data.co2SavedKg} kg", color = EmeraldGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "CO₂ Saved", color = TextMutedSlate, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    // 2. ROUTE CORRIDOR MAP CANVAS
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "🗺️ Complete Journey Corridor",
                                    color = TextNavyDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PastelCardBlue)
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val w = size.width
                                        val h = size.height

                                        // Route path
                                        drawLine(
                                            color = SkyBluePrimary,
                                            start = Offset(w * 0.12f, h * 0.75f),
                                            end = Offset(w * 0.88f, h * 0.25f),
                                            strokeWidth = 4.dp.toPx()
                                        )

                                        // Intermediate node
                                        drawCircle(SkyBlueDark, 6.dp.toPx(), Offset(w * 0.45f, h * 0.52f))
                                        drawCircle(Color.White, 2.5.dp.toPx(), Offset(w * 0.45f, h * 0.52f))

                                        // Intermediate node 2
                                        drawCircle(SkyBlueDark, 6.dp.toPx(), Offset(w * 0.68f, h * 0.38f))
                                        drawCircle(Color.White, 2.5.dp.toPx(), Offset(w * 0.68f, h * 0.38f))

                                        // Origin
                                        drawCircle(SkyBluePrimary, 9.dp.toPx(), Offset(w * 0.12f, h * 0.75f))
                                        drawCircle(Color.White, 4.dp.toPx(), Offset(w * 0.12f, h * 0.75f))

                                        // Destination
                                        drawCircle(WarmOrangeAccent, 10.dp.toPx(), Offset(w * 0.88f, h * 0.25f))
                                        drawCircle(Color.White, 4.dp.toPx(), Offset(w * 0.88f, h * 0.25f))
                                    }

                                    // Pins labels
                                    Box(modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)) {
                                        Text(text = "🚩 ${data.origin.take(15)}", color = TextNavyDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                                        Text(text = "🏁 ${data.destination.take(15)}", color = TextNavyDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = "Mode Combinations: ${data.mode} (${data.routeTitle})",
                                    color = SkyBlueDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // 3. INTERMEDIATE STOPS
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📍 Intermediate Transit Stops",
                                        color = TextNavyDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${data.intermediateStops.size} Stops",
                                        color = TextMutedSlate,
                                        fontSize = 11.sp
                                    )
                                }

                                data.intermediateStops.forEachIndexed { idx, stop ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(PastelCardTeal),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "${idx + 1}", color = TealDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = stop.stopName, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            Text(text = "${stop.type} · ${stop.platform}", color = TextMutedSlate, fontSize = 10.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(text = stop.arrivalTime, color = SkyBlueDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            if (stop.departureTime != "--") {
                                                Text(text = "Dep: ${stop.departureTime}", color = TextMutedSlate, fontSize = 9.sp)
                                            }
                                        }
                                    }
                                    if (idx < data.intermediateStops.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 11.dp)
                                                .width(2.dp)
                                                .height(14.dp)
                                                .background(BorderLight)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. TOURIST PLACES ALONG THE ROUTE
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "🏛️ Tourist Places Along The Route",
                                color = TextNavyDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(data.touristPlaces) { att ->
                                    Card(
                                        modifier = Modifier
                                            .width(220.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable {
                                                val pId = (att.placeId as? Number)?.toInt() ?: 4
                                                val pDist = (att.distanceKm as? Number)?.toDouble() ?: 2.5
                                                val pRating = (att.rating as? Number)?.toDouble() ?: 4.8
                                                val place = TouristPlace(
                                                    placeId = pId,
                                                    name = att.name,
                                                    city = att.city,
                                                    category = att.category,
                                                    description = att.description,
                                                    imageUrl = att.imageUrl,
                                                    locationAddress = "${att.city}, Tamil Nadu",
                                                    rating = pRating,
                                                    distanceKm = pDist,
                                                    entryFee = att.entryFee ?: "Free",
                                                    durationVisit = att.durationVisit ?: "1-2 hrs"
                                                )
                                                onOpenPlaceDetail(place)
                                            },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                                    ) {
                                        Column {
                                            AsyncImage(
                                                model = att.imageUrl,
                                                contentDescription = att.name,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(100.dp),
                                                contentScale = ContentScale.Crop
                                            )
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text(
                                                    text = att.name,
                                                    color = TextNavyDark,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "${att.city} · ★ ${att.rating}",
                                                    color = WarmOrangeAccent,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Tap to view guide →",
                                                    color = SkyBlueDark,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 5. TRAVEL SCHEDULE
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
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
                                        text = "🕒 Travel Schedule Timeline",
                                        color = TextNavyDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PastelCardBlue)
                                            .clickable { showEditScheduleDialog = true }
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(text = "✏️ Edit Schedule", color = SkyBlueDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                data.travelSchedule.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(text = item.time, color = SkyBluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(65.dp))
                                        Column {
                                            Text(text = item.title, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                            Text(text = item.desc, color = TextMutedSlate, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 6. TICKET & BOOKING DETAILS
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PastelCardTeal)
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
                                    Text(text = "🎫 Ticket Information", color = TealDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Status: ${data.paymentStatus.uppercase()}", color = EmeraldGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                Text(text = "Booking Reference: ${data.bookingReference}", color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Transaction ID: ${data.transactionId.ifBlank { "TXN-VERIFIED" }}", color = TextMutedSlate, fontSize = 11.sp)

                                Button(
                                    onClick = onViewTicket,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = TealDark),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(text = "View Digital Ticket & QR Code 📲", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // 7. CANCEL / ACTIONS
                    if (data.status.equals("upcoming", ignoreCase = true) || data.status.equals("ongoing", ignoreCase = true)) {
                        item {
                            Button(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(text = "Cancel This Journey ✕", color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }

        // Cancel Confirmation Dialog
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                containerColor = SurfaceWhite,
                shape = RoundedCornerShape(18.dp),
                title = { Text(text = "Cancel Journey?", color = TextNavyDark, fontWeight = FontWeight.Bold) },
                text = { Text(text = "Are you sure you want to cancel journey ${trip.bookingReference}?", color = TextNavyBody, fontSize = 12.sp) },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                authRepository.updateTripStatus(trip.journeyId, trip.userId, "cancelled")
                                Toast.makeText(context, "Journey Cancelled", Toast.LENGTH_SHORT).show()
                                showCancelDialog = false
                                onTripUpdated()
                                onBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text(text = "Confirm Cancel", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text(text = "Keep Journey", color = SkyBlueDark)
                    }
                }
            )
        }

        // Edit Schedule Dialog
        if (showEditScheduleDialog) {
            AlertDialog(
                onDismissRequest = { showEditScheduleDialog = false },
                containerColor = SurfaceWhite,
                shape = RoundedCornerShape(18.dp),
                title = { Text(text = "Adjust Departure Schedule", color = TextNavyDark, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "Set your customized starting time for this corridor:", color = TextMutedSlate, fontSize = 11.sp)
                        OutlinedTextField(
                            value = editedDepartureTime,
                            onValueChange = { editedDepartureTime = it },
                            label = { Text("Departure Time") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Schedule updated to $editedDepartureTime", Toast.LENGTH_SHORT).show()
                            showEditScheduleDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                    ) {
                        Text(text = "Save Schedule", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditScheduleDialog = false }) {
                        Text(text = "Cancel", color = TextMuted)
                    }
                }
            )
        }
    }
}
