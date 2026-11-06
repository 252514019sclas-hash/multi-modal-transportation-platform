package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.ui.theme.*
import java.util.Locale

@Composable
fun JoinGroupScreen(
    viewModel: GroupTravelViewModel = viewModel(),
    onBack: () -> Unit,
    onJoinedSuccess: () -> Unit
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
            tryDetectLocation(context) { detected ->
                viewModel.onJoinStartLocationChange(detected)
            }
        }
    }

    val popularDepartureHubs = listOf(
        "Chennai Central",
        "Madurai Mattuthavani",
        "Coimbatore Gandhipuram",
        "Salem Junction",
        "Trichy Central",
        "Tirunelveli"
    )

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
                // Top Bar
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
                            text = "SMART FAMILY TRIP",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Join Group Travel",
                            color = TextNavyDark,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Error or Status Banner
                if (!uiState.errorMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PastelCardPeach)
                            .border(1.dp, WarmOrangeAccent, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = "⚠️ ${uiState.errorMessage}", color = WarmOrangeDark, fontSize = 11.sp)
                    }
                }

                // Info card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PastelCardTeal)
                        .border(1.dp, TealLight, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔑", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Got an invitation code from a family member? Enter it below to link your journey and coordinate travel together.",
                            color = TealDark,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                // Card with Inputs
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
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. Invitation Code Field
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "GROUP INVITATION CODE", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            BasicTextField(
                                value = uiState.joinInviteCode,
                                onValueChange = { viewModel.onJoinInviteCodeChange(it) },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = TextNavyDark,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                cursorBrush = SolidColor(SkyBluePrimary),
                                decorationBox = { inner ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(BackgroundSoftCream)
                                            .border(1.dp, SkyBluePrimary, RoundedCornerShape(10.dp))
                                            .padding(horizontal = 14.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (uiState.joinInviteCode.isEmpty()) {
                                            Text(text = "e.g., OOTY-77 or ECR-99", color = TextLight, fontSize = 14.sp)
                                        }
                                        inner()
                                    }
                                }
                            )
                        }

                        // 2. Your Starting Point
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "YOUR STARTING LOCATION", color = TextNavyBody, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BackgroundSoftCream)
                                    .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "📍", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = uiState.joinStartLocation,
                                    onValueChange = { viewModel.onJoinStartLocationChange(it) },
                                    singleLine = true,
                                    textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp),
                                    cursorBrush = SolidColor(SkyBluePrimary),
                                    decorationBox = { inner ->
                                        if (uiState.joinStartLocation.isEmpty()) {
                                            Text(text = "Your departure city / transit hub", color = TextLight, fontSize = 13.sp)
                                        }
                                        inner()
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "GPS",
                                    color = SkyBluePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        if (!hasFine && !hasCoarse) {
                                            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                                        } else {
                                            tryDetectLocation(context) { detected ->
                                                viewModel.onJoinStartLocationChange(detected)
                                            }
                                        }
                                    }
                                )
                            }

                            // Departure Hub Chips
                            Text(
                                text = "Or select starting transit hub:",
                                color = TextMutedSlate,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                popularDepartureHubs.take(3).forEach { hub ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(PastelCardBlue)
                                            .clickable { viewModel.onJoinStartLocationChange(hub) }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = hub.split(" ")[0], color = SkyBlueDark, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }

                // Join Button
                Button(
                    onClick = {
                        viewModel.joinGroup(context) {
                            onJoinedSuccess()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryGradient),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                        } else {
                            Text(
                                text = "Join Travel Group ➔",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun tryDetectLocation(context: Context, onDetected: (String) -> Unit) {
    try {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (lm != null) {
            val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (loc != null) {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val list = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                    if (!list.isNullOrEmpty()) {
                        val addr = list[0]
                        val name = addr.locality ?: addr.subAdminArea ?: "Tamil Nadu"
                        onDetected("$name (GPS)")
                        return
                    }
                } catch (_: Exception) {}
                onDetected("Current Location (${String.format("%.3f", loc.latitude)}, ${String.format("%.3f", loc.longitude)})")
                return
            }
        }
    } catch (_: Exception) {}
    onDetected("Chennai Central (GPS)")
}

