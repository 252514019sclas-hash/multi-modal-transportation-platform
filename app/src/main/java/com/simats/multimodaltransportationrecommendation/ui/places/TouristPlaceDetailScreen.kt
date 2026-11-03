package com.simats.multimodaltransportationrecommendation.ui.places

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.places.TouristPlace
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun TouristPlaceDetailScreen(
    initialPlace: TouristPlace? = null,
    viewModel: TouristPlaceDetailViewModel = viewModel(),
    onBack: () -> Unit = {},
    onPlanJourneyToPlace: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val place = uiState.place
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(initialPlace) {
        if (initialPlace != null && uiState.place == null) {
            viewModel.setPlaceDirect(initialPlace)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFC6FF00))
            }
        } else if (place != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                    // Top Hero Image with Back and Save buttons
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    ) {
                        if (!place.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(place.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = place.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF0F172A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🏛️", fontSize = 64.sp)
                            }
                        }

                        // Gradient overlay at top for controls visibility
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x44000000))
                        )

                        // Top Controls Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xCC070B14))
                                    .clickable { onBack() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "←", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }

                            // Save / Unsave button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xCC070B14))
                                    .clickable { viewModel.toggleSavePlace(context) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color(0xFFC6FF00),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = if (uiState.isSaved) "❤️" else "🤍",
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }

                        // Category Pill at Bottom Left of Image
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xEE070B14))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "📍 ${place.city} · ${place.category}",
                                color = Color(0xFFC6FF00),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Content Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title & Rating
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = place.name,
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = place.locationAddress,
                                    color = Color(0xFF8A99AD),
                                    fontSize = 13.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF112017))
                                    .border(1.dp, Color(0xFF1E4D2B), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "★ ${place.rating}",
                                    color = Color(0xFFC6FF00),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Status message
                        if (!uiState.successMessage.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x22C6FF00))
                                    .border(1.dp, Color(0xFFC6FF00), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "✓ ${uiState.successMessage}",
                                    color = Color(0xFFC6FF00),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Quick Info Grid Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            InfoBadgeCard(
                                icon = "⏱️",
                                title = "Duration",
                                value = place.durationVisit ?: "2-3 hours",
                                modifier = Modifier.weight(1f)
                            )
                            InfoBadgeCard(
                                icon = "🎟️",
                                title = "Entry Fee",
                                value = place.entryFee ?: "Free",
                                modifier = Modifier.weight(1f)
                            )
                            InfoBadgeCard(
                                icon = "🧭",
                                title = "Distance",
                                value = "${place.distanceKm} km",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Visiting Hours Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF0F172A))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🕒", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Available Visiting Hours",
                                        color = Color(0xFF8A99AD),
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = place.visitingHours ?: "Open daily",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Description
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "ABOUT THIS DESTINATION",
                                color = Color(0xFF8A99AD),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = place.description,
                                color = Color(0xFFCBD5E1),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }

                        // Environmental / Transit Tip
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF112017))
                                .border(1.dp, Color(0xFF1E4D2B), RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🌿", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Taking public transit, walking, or cycling here avoids up to 1.5 kg of CO₂ compared to a car ride.",
                                    color = Color(0xFFC6FF00),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Bottom Action Bar: Plan Journey Here
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B111E))
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { onPlanJourneyToPlace(place.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC6FF00)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Plan Journey Here  →",
                                color = Color(0xFF070B14),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // Not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Place not found", color = Color.White, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { onBack() }) {
                        Text(text = "Go Back")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBadgeCard(
    icon: String,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = title, color = Color(0xFF8A99AD), fontSize = 10.sp)
            Text(text = value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

