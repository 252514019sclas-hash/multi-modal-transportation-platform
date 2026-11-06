package com.simats.multimodaltransportationrecommendation.ui.grouptravel

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.simats.multimodaltransportationrecommendation.data.model.group.GroupPlaceSuggestion
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun ExploreTogetherScreen(
    viewModel: GroupTravelViewModel,
    groupId: Int,
    destination: String
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val categories = listOf("All", "Temples", "Beaches", "Historical Places", "Nature", "Museums")

    LaunchedEffect(groupId) {
        viewModel.loadExplorePlaces(context, groupId, uiState.selectedExploreCategory)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Subheader
        Text(
            text = "DISCOVER TOGETHER IN ${destination.uppercase()}",
            color = SkyBluePrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSel = uiState.selectedExploreCategory.equals(cat, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSel) SkyBluePrimary else SurfaceWhite)
                        .border(1.dp, if (isSel) SkyBluePrimary else BorderLight, RoundedCornerShape(16.dp))
                        .clickable { viewModel.setExploreCategory(context, groupId, cat) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSel) SurfaceWhite else TextNavyBody,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // List of Places
        if (uiState.explorePlaces.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No attractions found for this filter in $destination.",
                    color = TextMutedSlate,
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.explorePlaces) { place ->
                    ExplorePlaceCard(
                        place = place,
                        onVote = { viewModel.togglePlaceVote(context, groupId, place.placeId) },
                        onAddToItinerary = {
                            viewModel.addItineraryStop(
                                context = context,
                                groupId = groupId,
                                placeId = place.placeId,
                                name = place.name,
                                address = place.locationAddress,
                                stopType = "attraction",
                                plannedTime = "11:00 AM",
                                notes = "Added via Explore Together"
                            )
                        },
                        onShareInChat = {
                            viewModel.sharePlaceInChat(context, groupId, place)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ExplorePlaceCard(
    place: GroupPlaceSuggestion,
    onVote: () -> Unit,
    onAddToItinerary: () -> Unit,
    onShareInChat: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .border(1.dp, if (place.isVoted) SkyBlueSoft else BorderLight, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PastelCardBlue)
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
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "🏛️", fontSize = 28.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = place.name,
                        color = TextNavyDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = "📍 ${place.city} · ${place.category}",
                        color = TextNavyBody,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "★ ${place.rating}", color = WarmOrangeDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "🎟️ ${place.entryFee ?: "Free"}", color = TextMutedSlate, fontSize = 10.sp)
                        if (place.distanceKm != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "🧭 ${place.distanceKm} km", color = TextMutedSlate, fontSize = 10.sp)
                        }
                    }
                }
            }

            Text(
                text = place.description,
                color = TextNavyBody,
                fontSize = 11.sp,
                maxLines = 2,
                lineHeight = 15.sp
            )

            // Visiting Hours (if available)
            place.visitingHours?.let { hours ->
                Text(
                    text = "🕒 Hours: $hours",
                    color = TextMutedSlate,
                    fontSize = 10.sp
                )
            }

            // Action Buttons Row: Vote (👍), Add to Itinerary, Share to Chat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vote Button
                Button(
                    onClick = onVote,
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (place.isVoted) PastelCardTeal else PastelCardBlue
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (place.isVoted) TealSecondary else SkyBlueSoft
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (place.isVoted) "👍 Voted" else "👍 Vote", color = if (place.isVoted) TealDark else SkyBlueDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        if (place.groupVotes > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "(${place.groupVotes})", color = if (place.isVoted) TealDark else SkyBlueDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Add to Itinerary Button
                Button(
                    onClick = onAddToItinerary,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(34.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (place.isInItinerary) PastelCardTeal else SkyBluePrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (place.isInItinerary) "✓ In Itinerary" else "＋ Add to Itinerary",
                        color = if (place.isInItinerary) TealDark else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share to Chat Button
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PastelCardTeal)
                        .border(1.dp, TealLight, CircleShape)
                        .clickable { onShareInChat() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💬", fontSize = 14.sp)
                }
            }
        }
    }
}
