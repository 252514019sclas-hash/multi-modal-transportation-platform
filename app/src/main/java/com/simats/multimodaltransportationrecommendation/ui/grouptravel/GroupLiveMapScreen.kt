package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.simats.multimodaltransportationrecommendation.data.model.group.GroupMemberLocation
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun GroupLiveMapScreen(
    viewModel: GroupTravelViewModel,
    groupId: Int,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var selectedMember by remember { mutableStateOf<GroupMemberLocation?>(null) }
    var zoomScale by remember { mutableStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.toggleLocationSharing(context, groupId, true)
        }
    }

    LaunchedEffect(groupId) {
        viewModel.loadLiveLocations(context, groupId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
    ) {
        // Map Canvas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.6f, 3.5f)
                        panOffset += pan
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Background grid lines (resembling map coordinates)
                val gridColor = BorderLight.copy(alpha = 0.6f)
                val step = 50f * zoomScale
                var x = (panOffset.x % step)
                while (x < canvasWidth) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, canvasHeight), strokeWidth = 1f)
                    x += step
                }
                var y = (panOffset.y % step)
                while (y < canvasHeight) {
                    drawLine(gridColor, Offset(0f, y), Offset(canvasWidth, y), strokeWidth = 1f)
                    y += step
                }

                // Center point: Destination
                val destX = canvasWidth * 0.5f + panOffset.x
                val destY = canvasHeight * 0.42f + panOffset.y

                // Common Destination Marker (Gold/Orange Pin)
                drawCircle(
                    color = WarmOrangeAccent.copy(alpha = 0.25f),
                    radius = 28f * zoomScale,
                    center = Offset(destX, destY)
                )
                drawCircle(
                    color = WarmOrangeAccent,
                    radius = 16f * zoomScale,
                    center = Offset(destX, destY)
                )
                drawCircle(
                    color = SurfaceWhite,
                    radius = 7f * zoomScale,
                    center = Offset(destX, destY)
                )

                // Render Member Markers & Trajectories
                val membersWithLoc = uiState.liveLocations.filter { it.isSharing && it.latitude != null && it.longitude != null }

                membersWithLoc.forEachIndexed { idx, mem ->
                    // Calculate relative coordinate offset from destination
                    val destLat = uiState.destinationMapPoint?.latitude ?: 11.41
                    val destLng = uiState.destinationMapPoint?.longitude ?: 76.69

                    val dLat = (mem.latitude!! - destLat)
                    val dLng = (mem.longitude!! - destLng)

                    // Map latitude/longitude deltas to canvas pixel space
                    val memX = destX + (dLng * 140f * zoomScale).toFloat()
                    val memY = destY - (dLat * 140f * zoomScale).toFloat()

                    // Trajectory Line from Member to Destination (dashed)
                    drawLine(
                        color = if (mem.isSelf) SkyBluePrimary.copy(alpha = 0.7f) else TealSecondary.copy(alpha = 0.6f),
                        start = Offset(memX, memY),
                        end = Offset(destX, destY),
                        strokeWidth = 3f * zoomScale,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                    )

                    // Member Pulse ring
                    if (mem.isSelf) {
                        drawCircle(
                            color = SkyBluePrimary.copy(alpha = 0.3f),
                            radius = 26f * zoomScale,
                            center = Offset(memX, memY)
                        )
                    }

                    // Marker Base
                    drawCircle(
                        color = if (mem.isSelf) SkyBluePrimary else TealSecondary,
                        radius = 18f * zoomScale,
                        center = Offset(memX, memY)
                    )
                    drawCircle(
                        color = SurfaceWhite,
                        radius = 14f * zoomScale,
                        center = Offset(memX, memY)
                    )
                    drawCircle(
                        color = if (mem.isSelf) SkyBluePrimary else TealSecondary,
                        radius = 8f * zoomScale,
                        center = Offset(memX, memY)
                    )
                }

                // Meeting Points Markers (if any)
                uiState.meetingPoints.forEachIndexed { mIdx, mp ->
                    val mpX = destX - (45f + mIdx * 50f) * zoomScale
                    val mpY = destY + (35f + mIdx * 40f) * zoomScale

                    drawCircle(
                        color = EmeraldGreen.copy(alpha = 0.2f),
                        radius = 20f * zoomScale,
                        center = Offset(mpX, mpY)
                    )
                    drawCircle(
                        color = EmeraldGreen,
                        radius = 10f * zoomScale,
                        center = Offset(mpX, mpY)
                    )
                }
            }
        }

        // Top Controls: Back Arrow, Group Title, Recenter & Privacy Indicator
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = TextNavyDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = uiState.activeGroup?.groupName ?: "Group Live Map",
                            color = TextNavyDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Destination: ${uiState.destinationMapPoint?.name ?: "Tamil Nadu"}",
                            color = TextNavyBody,
                            fontSize = 11.sp
                        )
                    }
                }

                // Recenter & Zoom Reset Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(3.dp, CircleShape)
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(1.dp, BorderLight, CircleShape)
                        .clickable {
                            zoomScale = 1.0f
                            panOffset = Offset.Zero
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎯", fontSize = 16.sp)
                }
            }

            // Privacy Opt-In Live Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isSharingActive) PastelCardTeal else PastelCardPeach
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (uiState.isSharingActive) EmeraldGreen else WarmOrangeAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (uiState.isSharingActive) "🟢 Live Location Sharing Active" else "⏸️ Location Sharing Paused",
                                color = if (uiState.isSharingActive) TealDark else WarmOrangeDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (uiState.isSharingActive)
                                    "${uiState.activeSharingCount} members sharing voluntary GPS"
                                else
                                    "Your location is hidden from group members",
                                color = TextNavyBody,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Start / Stop Toggle Button
                    Button(
                        onClick = {
                            if (uiState.isSharingActive) {
                                viewModel.toggleLocationSharing(context, groupId, false)
                            } else {
                                val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                if (!hasFine) {
                                    locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                                } else {
                                    viewModel.toggleLocationSharing(context, groupId, true)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isSharingActive) WarmOrangeAccent else SkyBluePrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (uiState.isSharingActive) "Stop Sharing" else "Start Sharing",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Bottom Tray: Member Markers Carousel & Selected Member Info Card
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Member Carousel Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.liveLocations) { mem ->
                    val isSelected = selectedMember?.userId == mem.userId
                    Card(
                        modifier = Modifier
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .clickable { selectedMember = mem },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) PastelCardBlue else SurfaceWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) SkyBluePrimary else BorderLight
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (mem.isSharing) TealSecondary else TextMuted),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = mem.initials, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = if (mem.isSelf) "${mem.fullName} (You)" else mem.fullName,
                                    color = TextNavyDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (mem.isSharing)
                                        "${mem.distanceToDestinationKm ?: "?"} km away"
                                    else
                                        "Sharing Off",
                                    color = if (mem.isSharing) EmeraldGreen else TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Selected Member Detailed Card
            selectedMember?.let { mem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(if (mem.isSharing) SkyBluePrimary else TextMuted),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = mem.initials, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = mem.fullName + if (mem.isSelf) " (You)" else "",
                                        color = TextNavyDark,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Started from: ${mem.startingLocation}",
                                        color = TextMutedSlate,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Text(
                                text = "✕",
                                color = TextMuted,
                                fontSize = 14.sp,
                                modifier = Modifier.clickable { selectedMember = null }
                            )
                        }

                        if (mem.isSharing) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BackgroundSoftCream)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "CURRENT POSITION", color = TextMutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    Text(text = mem.locationName, color = TextNavyDark, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "TO DESTINATION", color = TextMutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "${mem.distanceToDestinationKm ?: "--"} km",
                                        color = TealSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🕒 Updated ${mem.lastUpdated ?: "recently"}",
                                    color = TextMutedSlate,
                                    fontSize = 10.sp
                                )
                                if (mem.distanceToUserKm != null && !mem.isSelf) {
                                    Text(
                                        text = "🧭 ${mem.distanceToUserKm} km from you",
                                        color = SkyBlueDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PastelCardPeach)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "🔒 Location Sharing Off: This member has not enabled live GPS tracking.",
                                    color = WarmOrangeDark,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

