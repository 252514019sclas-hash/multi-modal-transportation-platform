package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun GroupLocationSettingsScreen(
    viewModel: GroupTravelViewModel = viewModel(),
    groupId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with Back Arrow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        Text(text = "←", color = TextNavyDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "LOCATION PRIVACY & CONTROLS",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Live Location Settings",
                            color = TextNavyDark,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Success / Error Feedback
                if (!uiState.successMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PastelCardTeal)
                            .border(1.dp, TealSoft, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = "✓ ${uiState.successMessage}", color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Current Status Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isSharingActive) PastelCardTeal else PastelCardPeach
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isSharingActive) EmeraldGreen else WarmOrangeAccent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.isSharingActive) "🟢 Live Location Sharing is ACTIVE" else "🔒 Live Location Sharing is OFF",
                                color = if (uiState.isSharingActive) TealDark else WarmOrangeDark,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (uiState.isSharingActive)
                                "Your real-time GPS coordinates are visible on the group map to approved members traveling to ${uiState.destinationMapPoint?.name ?: "your destination"}."
                            else
                                "You are currently appearing as 'Location Sharing Off'. No coordinates or movements are shared with other group members.",
                            color = TextNavyBody,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        // Main Toggle Button
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.isSharingActive) WarmOrangeAccent else SkyBluePrimary
                            )
                        ) {
                            Text(
                                text = if (uiState.isSharingActive) "Pause / Stop Location Sharing" else "Start Sharing My Location",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Privacy Guarantees
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "PRIVACY & SECURITY GUARANTEES",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        listOf(
                            "🛡️ 100% Opt-In Only" to "Location sharing is always voluntary. You must explicitly start sharing for your position to be transmitted.",
                            "👥 Restricted to Approved Members" to "Only approved members of this specific trip can see your marker. Non-members cannot access your coordinates.",
                            "⏱️ 4-Hour Automatic Expiration" to "Your location records automatically expire after 4 hours or upon trip completion to prevent lingering data.",
                            "🗑️ Instant Coordinate Erasure" to "Pressing 'Stop Sharing' immediately removes all active coordinates from the database.",
                            "🔋 Battery-Efficient Polling" to "Position updates are throttled to conserve battery while traveling across Tamil Nadu highways."
                        ).forEach { (title, desc) ->
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(text = title, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = desc, color = TextNavyBody, fontSize = 10.sp, lineHeight = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

