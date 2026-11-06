package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.multimodaltransportationrecommendation.data.model.group.GroupItineraryItem
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun GroupItineraryScreen(
    viewModel: GroupTravelViewModel,
    groupId: Int,
    destination: String,
    onViewTransitRoute: (origin: String, destination: String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showAddStopDialog by remember { mutableStateOf(false) }
    var newStopName by remember { mutableStateOf("") }
    var newStopTime by remember { mutableStateOf("10:00 AM") }
    var newStopType by remember { mutableStateOf("attraction") }
    var newStopNotes by remember { mutableStateOf("") }

    LaunchedEffect(groupId) {
        viewModel.loadItinerary(context, groupId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Actions Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SHARED GROUP ITINERARY",
                    color = SkyBluePrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${uiState.itinerary.size} Planned Stops & Attractions",
                    color = TextNavyDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { showAddStopDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(text = "＋ Add Stop", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (uiState.itinerary.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp)
                    .background(SurfaceWhite, RoundedCornerShape(14.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "🗺️", fontSize = 32.sp)
                    Text(text = "No stops added yet", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Add meeting points, temples, hill viewpoints or food stops for your group to coordinate smoothly.",
                        color = TextMutedSlate,
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = { showAddStopDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Add First Stop", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(uiState.itinerary) { index, item ->
                    ItineraryStopCard(
                        item = item,
                        index = index + 1,
                        onRemove = {
                            viewModel.removeItineraryStop(context, groupId, item.itineraryId)
                        },
                        onViewRoute = {
                            val userStart = uiState.activeGroup?.userMembership?.startingLocation ?: "Chennai Central"
                            onViewTransitRoute(userStart, item.placeName)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Add Stop Dialog
    if (showAddStopDialog) {
        AlertDialog(
            onDismissRequest = { showAddStopDialog = false },
            title = {
                Text(text = "Add Itinerary Stop", color = TextNavyDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Name
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = "STOP OR PLACE NAME", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        BasicTextField(
                            value = newStopName,
                            onValueChange = { newStopName = it },
                            singleLine = true,
                            textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp),
                            cursorBrush = SolidColor(SkyBluePrimary),
                            decorationBox = { inner ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BackgroundSoftCream)
                                        .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (newStopName.isEmpty()) Text(text = "e.g. Mettupalayam Junction", color = TextLight, fontSize = 12.sp)
                                    inner()
                                }
                            }
                        )
                    }

                    // Stop Type
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = "STOP TYPE", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("meeting_point" to "Meeting Point", "attraction" to "Attraction", "meal" to "Meal / Rest").forEach { (typeKey, label) ->
                                val isSel = newStopType == typeKey
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) PastelCardTeal else BackgroundSoftCream)
                                        .border(1.dp, if (isSel) TealSecondary else BorderLight, RoundedCornerShape(8.dp))
                                        .clickable { newStopType = typeKey }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = label, color = if (isSel) TealDark else TextNavyBody, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Planned Time
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = "PLANNED TIME", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        BasicTextField(
                            value = newStopTime,
                            onValueChange = { newStopTime = it },
                            singleLine = true,
                            textStyle = TextStyle(color = TextNavyDark, fontSize = 12.sp),
                            cursorBrush = SolidColor(SkyBluePrimary),
                            decorationBox = { inner ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BackgroundSoftCream)
                                        .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (newStopTime.isEmpty()) Text(text = "10:00 AM", color = TextLight, fontSize = 12.sp)
                                    inner()
                                }
                            }
                        )
                    }

                    // Notes
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = "NOTES / INSTRUCTIONS", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        BasicTextField(
                            value = newStopNotes,
                            onValueChange = { newStopNotes = it },
                            textStyle = TextStyle(color = TextNavyDark, fontSize = 12.sp),
                            cursorBrush = SolidColor(SkyBluePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BackgroundSoftCream)
                                .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            decorationBox = { inner ->
                                if (newStopNotes.isEmpty()) Text(text = "Instructions for family members...", color = TextLight, fontSize = 11.sp)
                                inner()
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newStopName.isNotBlank()) {
                            viewModel.addItineraryStop(
                                context = context,
                                groupId = groupId,
                                name = newStopName.trim(),
                                stopType = newStopType,
                                plannedTime = newStopTime.trim(),
                                notes = newStopNotes.trim().ifBlank { null }
                            )
                            newStopName = ""
                            newStopNotes = ""
                            showAddStopDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary)
                ) {
                    Text(text = "Add Stop", color = Color.White, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStopDialog = false }) {
                    Text(text = "Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun ItineraryStopCard(
    item: GroupItineraryItem,
    index: Int,
    onRemove: () -> Unit,
    onViewRoute: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp)),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(SkyBluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$index", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (item.stopType) {
                                    "meeting_point" -> PastelCardTeal
                                    "meal" -> PastelCardPeach
                                    else -> PastelCardBlue
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (item.stopType) {
                                "meeting_point" -> "📍 MEETING POINT"
                                "meal" -> "🍽️ FOOD / BREAK"
                                else -> "🏛️ ATTRACTION"
                            },
                            color = when (item.stopType) {
                                "meeting_point" -> TealDark
                                "meal" -> WarmOrangeDark
                                else -> SkyBlueDark
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "🕒 ${item.plannedTime ?: "--"}",
                    color = TextNavyDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = item.placeName,
                color = TextNavyDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            if (!item.locationAddress.isNullOrBlank()) {
                Text(text = "📍 ${item.locationAddress}", color = TextMutedSlate, fontSize = 10.sp)
            }

            if (!item.notes.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(BackgroundSoftCream)
                        .padding(6.dp)
                ) {
                    Text(text = "💡 ${item.notes}", color = TextNavyBody, fontSize = 10.sp)
                }
            }

            // Buttons: View Route & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Suggested by ${item.suggestedByName}",
                    color = TextMutedSlate,
                    fontSize = 9.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "View Route ➔",
                        color = SkyBluePrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onViewRoute() }
                    )

                    Text(
                        text = "Remove",
                        color = Color.Red.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onRemove() }
                    )
                }
            }
        }
    }
}
