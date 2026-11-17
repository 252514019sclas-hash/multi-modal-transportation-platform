package com.simats.multimodaltransportationrecommendation.ui.completed

import android.widget.Toast
import androidx.compose.animation.core.*
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import com.simats.multimodaltransportationrecommendation.data.model.trips.TripItem
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun JourneyCompletedScreen(
    viewModel: JourneyCompletedViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {},
    onViewJourneyDetails: (TripItem) -> Unit = {},
    onViewTicket: (TripItem) -> Unit = {},
    onExploreNearbyPlaces: (TouristPlace) -> Unit = {},
    onBackToMyTrips: () -> Unit = {},
    onGoToExplore: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(uiState.statusMessage) {
        uiState.statusMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearStatusMessage()
        }
    }

    // Success pulsing celebration animation
    val infiniteTransition = rememberInfiniteTransition(label = "celebrationPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

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
            // Main Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Screen App Bar
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
                            Text(text = "←", color = TextNavyDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "TAMIL NADU SMART TRANSIT",
                                color = SkyBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Journey Completed",
                                color = TextNavyDark,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Eco-friendly badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PastelCardTeal)
                            .border(1.dp, TealLight, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "🌱 Eco-Ride", color = TealDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // 1. SUCCESS ANIMATION & ARRIVAL BADGE
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PastelCardTeal)
                        .border(2.dp, TealSecondary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎉", fontSize = 36.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Arrived at ${uiState.destination.split(",").firstOrNull() ?: uiState.destination}!",
                        color = TextNavyDark,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Completed on ${uiState.completionDateTime}",
                        color = TextMutedSlate,
                        fontSize = 12.sp
                    )
                }

                // 2. ACTUAL RECORDED JOURNEY METRICS CARD
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
                        // Origin and Destination
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(SkyBluePrimary, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "From: ${uiState.origin}", color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(WarmOrangeAccent, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "To: ${uiState.destination}", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        HorizontalDivider(color = BorderLight)

                        // Mode, Duration, Distance & Fare
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "${uiState.durationMinutes} min", color = TextNavyDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Duration", color = TextMutedSlate, fontSize = 10.sp)
                            }
                            Column {
                                Text(text = "${uiState.distanceKm} km", color = TextNavyDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Distance", color = TextMutedSlate, fontSize = 10.sp)
                            }
                            Column {
                                Text(text = uiState.fareText, color = TealDark, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Fare Paid", color = TextMutedSlate, fontSize = 10.sp)
                            }
                            Column {
                                Text(text = "${uiState.co2SavedKg} kg", color = EmeraldGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = "CO₂ Saved", color = TextMutedSlate, fontSize = 10.sp)
                            }
                        }

                        // Modes used tag
                        Text(
                            text = "Transit Mode Used: ${uiState.mode}",
                            color = SkyBlueDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // 3. COMPLETED ROUTE MAP CANVAS
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
                            text = "🗺️ Completed Route Corridor",
                            color = TextNavyDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PastelCardBlue)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height

                                // Path
                                drawLine(
                                    color = EmeraldGreen,
                                    start = Offset(w * 0.12f, h * 0.75f),
                                    end = Offset(w * 0.88f, h * 0.25f),
                                    strokeWidth = 4.dp.toPx()
                                )

                                // Node 1
                                drawCircle(SkyBluePrimary, 7.dp.toPx(), Offset(w * 0.12f, h * 0.75f))
                                drawCircle(Color.White, 3.dp.toPx(), Offset(w * 0.12f, h * 0.75f))

                                // Node 2
                                drawCircle(SkyBlueDark, 6.dp.toPx(), Offset(w * 0.40f, h * 0.55f))
                                drawCircle(Color.White, 2.5.dp.toPx(), Offset(w * 0.40f, h * 0.55f))

                                // Node 3
                                drawCircle(SkyBlueDark, 6.dp.toPx(), Offset(w * 0.65f, h * 0.38f))
                                drawCircle(Color.White, 2.5.dp.toPx(), Offset(w * 0.65f, h * 0.38f))

                                // Arrived Checkmark Node
                                drawCircle(EmeraldGreen, 9.dp.toPx(), Offset(w * 0.88f, h * 0.25f))
                                drawCircle(Color.White, 4.dp.toPx(), Offset(w * 0.88f, h * 0.25f))
                            }

                            Box(modifier = Modifier.align(Alignment.BottomStart).padding(6.dp)) {
                                Text(text = "Origin: ${uiState.origin.take(16)}", color = TextNavyDark, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                                Text(text = "✓ Arrived: ${uiState.destination.take(16)}", color = EmeraldGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Completed stops list
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Completed Stops (${uiState.completedStops.size}):", color = TextNavyDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            uiState.completedStops.forEachIndexed { i, stopName ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "✓", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = stopName, color = TextNavyBody, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // 4. FEATURE 1 & 2: AI JOURNEY SUMMARY & TRAVEL INSIGHTS
                uiState.aiSummary?.let { summary ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PastelCardTeal)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "✨ AI Journey Summary", color = TealDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = summary.onTimeScore, color = EmeraldGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = summary.narrative,
                                color = TextNavyDark,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Badge: ${summary.efficiencyBadge}", color = TealDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = summary.ecoImpactHighlight, color = EmeraldGreen, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // 5. FEATURE 3: AI NEXT DESTINATION RECOMMENDATIONS
                if (uiState.nextDestinations.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🏛️ AI Next Destination Recommendations", color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Nearby Spots", color = TextMutedSlate, fontSize = 10.sp)
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(uiState.nextDestinations) { nextPlace ->
                                Card(
                                    modifier = Modifier
                                        .width(230.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                                ) {
                                    Column {
                                        AsyncImage(
                                            model = nextPlace.imageUrl,
                                            contentDescription = nextPlace.name,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(105.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = nextPlace.name,
                                                color = TextNavyDark,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "★ ${nextPlace.rating} · ${nextPlace.distanceText}",
                                                color = WarmOrangeAccent,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = nextPlace.shortDescription,
                                                color = TextNavyBody,
                                                fontSize = 10.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                lineHeight = 13.sp
                                            )
                                            Text(
                                                text = "Transit: ${nextPlace.availableTransport}",
                                                color = SkyBlueDark,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )

                                            Button(
                                                onClick = {
                                                    val tp = TouristPlace(
                                                        placeId = nextPlace.placeId,
                                                        name = nextPlace.name,
                                                        city = "Chennai",
                                                        category = nextPlace.category,
                                                        description = nextPlace.shortDescription,
                                                        imageUrl = nextPlace.imageUrl,
                                                        locationAddress = "Chennai, Tamil Nadu",
                                                        rating = nextPlace.rating,
                                                        distanceKm = 1.5,
                                                        entryFee = "Free",
                                                        durationVisit = "1-2 hrs"
                                                    )
                                                    onExploreNearbyPlaces(tp)
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(32.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text(text = "View Details →", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. FEATURE 4: AI ALTERNATIVE ROUTE COMPARISON
                if (uiState.alternativeRoutes.isNotEmpty()) {
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
                                text = "⚖️ AI Alternative Route Comparison",
                                color = TextNavyDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "How your ${uiState.mode} journey compared against other transport options:",
                                color = TextMutedSlate,
                                fontSize = 11.sp
                            )

                            uiState.alternativeRoutes.forEach { alt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(PastelCardBlue)
                                        .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = alt.mode, color = TextNavyDark, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                            Text(text = "${alt.durationDiffText} · ${alt.transferDiffText}", color = TextNavyBody, fontSize = 10.sp)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(text = alt.fareDiffText, color = if (alt.fareDiffText.contains("higher")) Color(0xFFDC2626) else EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(text = alt.co2Comparison, color = TextMutedSlate, fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 7. FEATURE 5: AI TRAVEL FEEDBACK ASSISTANT
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Rate your transit experience",
                            color = TextNavyDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Stars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..5) {
                                val isFilled = i <= uiState.rating
                                Text(
                                    text = if (isFilled) "★" else "☆",
                                    color = if (isFilled) WarmOrangeAccent else TextMutedSlate,
                                    fontSize = 26.sp,
                                    modifier = Modifier.clickable { viewModel.setRating(i) }
                                )
                            }
                        }

                        // AI Suggested feedback chips
                        Text(
                            text = "AI Suggested Feedback Tags:",
                            color = TextMutedSlate,
                            fontSize = 10.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(uiState.suggestedFeedbackTags) { tag ->
                                    val isSel = uiState.selectedFeedbackTags.contains(tag)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSel) PastelCardTeal else BorderSubtle)
                                            .border(1.dp, if (isSel) TealSecondary else Color.Transparent, RoundedCornerShape(12.dp))
                                            .clickable { viewModel.toggleFeedbackTag(tag) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = tag, fontSize = 10.sp, color = if (isSel) TealDark else TextNavyDark, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                    }
                                }
                            }
                        }

                        // Submit Feedback button
                        if (!uiState.feedbackSubmitted) {
                            Button(
                                onClick = { viewModel.submitFeedback() },
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "Submit Feedback ✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Text(text = "✓ Feedback Recorded. Thank you!", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 8. FEATURE 6: AI FUTURE TRIP SUGGESTIONS
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🚀 AI Future Trip Suggestions",
                        color = TextNavyDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    uiState.futureTripIdeas.forEach { idea ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = idea.destination, color = TextNavyDark, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PastelCardPeach)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = idea.interestTag, color = WarmOrangeAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(text = "Corridor: ${idea.corridor} · ${idea.travelDuration}", color = TextNavyBody, fontSize = 10.5.sp)
                                Text(text = idea.highlights, color = TextMutedSlate, fontSize = 10.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Est. Budget: ${idea.estimatedBudget}", color = TealDark, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                                    TextButton(
                                        onClick = { viewModel.addFutureTripToMyTrips(context, idea) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "+ Add to My Trips", color = SkyBlueDark, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // REQUIRED 6 ACTION BUTTONS IN STACKED BOTTOM BAR
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Row 1: View Journey Details & View Digital Ticket
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onViewJourneyDetails(viewModel.asTripItem()) },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PastelCardBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "📋 Journey Details", color = SkyBlueDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onViewTicket(viewModel.asTripItem()) },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "🎫 Digital Ticket", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Row 2: Save Journey & Explore Nearby Places
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.saveJourneyToBackend(context) },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (uiState.isSavedToTrips) PastelCardTeal else SkyBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (uiState.isSavedToTrips) "✓ Saved" else "💾 Save Journey",
                            color = if (uiState.isSavedToTrips) TealDark else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val firstNext = uiState.nextDestinations.firstOrNull()
                            val tp = TouristPlace(
                                placeId = firstNext?.placeId ?: 4,
                                name = firstNext?.name ?: "Marina Beach & Promenade",
                                city = "Chennai",
                                category = firstNext?.category ?: "Beaches",
                                description = firstNext?.shortDescription ?: "World's second longest urban beach.",
                                imageUrl = firstNext?.imageUrl,
                                locationAddress = "Chennai, Tamil Nadu",
                                rating = firstNext?.rating ?: 4.7,
                                distanceKm = 1.2,
                                entryFee = "Free",
                                durationVisit = "2-3 hrs"
                            )
                            onExploreNearbyPlaces(tp)
                        },
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarmOrangeAccent),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "🏛️ Explore Nearby", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Row 3: Back to My Trips & Go to Explore
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackToMyTrips,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SkyBluePrimary)
                    ) {
                        Text(text = "Back to My Trips", color = SkyBluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onGoToExplore,
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TextNavyDark),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Go to Explore ➔", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
