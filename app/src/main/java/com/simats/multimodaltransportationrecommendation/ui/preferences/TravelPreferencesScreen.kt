package com.simats.multimodaltransportationrecommendation.ui.preferences

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun TravelPreferencesScreen(
    viewModel: TravelPreferencesViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSavePreferences: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadPreferences(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PastelCardBlue)
                            .border(1.dp, SkyBlueSoft, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = SkyBluePrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "TAMIL NADU TRAVEL PREFERENCES",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Customize Your Travel",
                            color = TextNavyDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Banner Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PastelCardTeal)
                        .border(1.dp, TealLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌴", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PERSONALIZE MULTI-MODAL COMMUTE & TOURISM",
                            color = TealDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Status Message Banners
                if (!uiState.successMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PastelCardTeal)
                            .border(1.dp, TealSoft, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "✓ ${uiState.successMessage}",
                            color = TealDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (!uiState.errorMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFEF2F2))
                            .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "⚠️ ${uiState.errorMessage}",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp
                        )
                    }
                }

                // SECTION 1: PREFERRED TRANSPORTATION MODES (All 7 modes)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "PREFERRED TRANSPORT MODES",
                        color = SkyBluePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Choose which modes the MoveSmart AI routing engine should include:",
                        color = TextNavyBody,
                        fontSize = 12.sp
                    )

                    val allModes = listOf(
                        Triple("Walking", "🚶", "Free & healthy · Scenic promenade walks"),
                        Triple("Cycling", "🚲", "Smart city cycle tracks & lake corridors"),
                        Triple("Bus", "🚌", "MTC & TNSTC city and express bus networks"),
                        Triple("Metro", "🚇", "Chennai Metro Rail (CMRL) rapid transit"),
                        Triple("Train", "🚆", "Southern Railway suburban & express lines"),
                        Triple("Taxi", "🚕", "Prepaid auto-rickshaw & metered cab"),
                        Triple("Car", "🚗", "Private driving via state expressways")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allModes.forEach { (modeName, icon, sub) ->
                            val isSelected = uiState.selectedModes.contains(modeName)
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) PastelCardBlue else SurfaceWhite,
                                animationSpec = tween(250),
                                label = "modeBg"
                            )
                            val borderColor by animateColorAsState(
                                targetValue = if (isSelected) SkyBluePrimary else BorderLight,
                                animationSpec = tween(250),
                                label = "modeBorder"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(bgColor)
                                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                                    .clickable { viewModel.toggleMode(modeName) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = icon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = modeName,
                                            color = TextNavyDark,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = sub,
                                            color = TextNavyBody,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) SkyBluePrimary else BorderLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Text(text = "✓", color = SurfaceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // SECTION 2: TRAVEL PURPOSE
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "TRAVEL PURPOSE",
                        color = SkyBluePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "What is the primary focus of your travels?",
                        color = TextNavyBody,
                        fontSize = 12.sp
                    )

                    val purposes = listOf(
                        Triple("Daily Commute", "🚆", "Work, college & routine city transit"),
                        Triple("Tourism", "📸", "Temples, beaches, heritage & sightseeing"),
                        Triple("Business", "💼", "Fast direct transit for business meetings"),
                        Triple("Adventure", "🧗", "Hill stations, waterfalls & nature trails")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        purposes.forEach { (purpose, icon, desc) ->
                            val isSelected = uiState.travelPurpose.equals(purpose, ignoreCase = true)
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) PastelCardTeal else SurfaceWhite,
                                animationSpec = tween(250),
                                label = "purposeBg"
                            )
                            val borderColor by animateColorAsState(
                                targetValue = if (isSelected) TealSecondary else BorderLight,
                                animationSpec = tween(250),
                                label = "purposeBorder"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(bgColor)
                                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                                    .clickable { viewModel.setTravelPurpose(purpose) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = icon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = purpose,
                                            color = if (isSelected) TealDark else TextNavyDark,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = desc,
                                            color = TextNavyBody,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) TealSecondary else BorderLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Text(text = "✓", color = SurfaceWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // SECTION 3: AI TOURIST RECOMMENDATIONS & DESTINATION PREFERENCES
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Tourist Recommendations Toggle Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(PastelCardTeal)
                            .border(1.dp, TealLight, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "🧭", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "AI Tourist Place Recommendations",
                                        color = TealDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Personalize tourist destinations on Explore screen using your travel preferences",
                                        color = TextNavyBody,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Switch(
                                checked = uiState.touristRecommendations,
                                onCheckedChange = { viewModel.toggleTouristRecommendations() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = SurfaceWhite,
                                    checkedTrackColor = TealSecondary,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = BorderLight
                                )
                            )
                        }
                    }

                    // Animated Sub-sections: Preferred Destination Types & Travel Distance
                    AnimatedVisibility(
                        visible = uiState.touristRecommendations,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Sub-section A: Preferred Destination Type (Multi-select)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "PREFERRED DESTINATION TYPES",
                                    color = SkyBluePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Select what types of attractions you want AI to recommend (Multi-select):",
                                    color = TextNavyBody,
                                    fontSize = 12.sp
                                )

                                val destTypes = listOf(
                                    Triple("Historical Places", "🏛️", "Forts, heritage monuments & UNESCO landmarks"),
                                    Triple("Temples", "🛕", "Dravidian architecture & cultural shrines"),
                                    Triple("Beaches", "🏖️", "Marina, coastal promenades & sea views"),
                                    Triple("Parks", "🌳", "Botanical gardens & scenic green parks"),
                                    Triple("Museums", "🎨", "Art galleries & Tamil heritage museums"),
                                    Triple("Nature and Adventure", "⛰️", "Hill stations, waterfalls & boat rides")
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    destTypes.forEach { (type, icon, desc) ->
                                        val isSel = uiState.selectedDestinationTypes.any { it.equals(type, ignoreCase = true) }
                                        val bgTypeColor by animateColorAsState(
                                            targetValue = if (isSel) PastelCardPeach else SurfaceWhite,
                                            animationSpec = tween(200),
                                            label = "destTypeBg"
                                        )
                                        val borderTypeColor by animateColorAsState(
                                            targetValue = if (isSel) WarmOrangeAccent else BorderLight,
                                            animationSpec = tween(200),
                                            label = "destTypeBorder"
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(bgTypeColor)
                                                .border(1.dp, borderTypeColor, RoundedCornerShape(12.dp))
                                                .clickable { viewModel.toggleDestinationType(type) }
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(text = icon, fontSize = 18.sp)
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        text = type,
                                                        color = if (isSel) WarmOrangeDark else TextNavyDark,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = desc,
                                                        color = TextNavyBody,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSel) WarmOrangeAccent else BorderLight),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isSel) {
                                                    Text(text = "✓", color = SurfaceWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Sub-section B: Travel Distance Preference (Nearby, Within City, Long Distance)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "TRAVEL DISTANCE PREFERENCE",
                                    color = SkyBluePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "How far are you willing to travel for tourist recommendations?",
                                    color = TextNavyBody,
                                    fontSize = 12.sp
                                )

                                val distanceOpts = listOf(
                                    Triple("Nearby", "📍", "< 3 km · Walk / Bike"),
                                    Triple("Within City", "🏙️", "3–10 km · Metro / Bus"),
                                    Triple("Long Distance", "🛣️", "> 10 km · Rail / Bus")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    distanceOpts.forEach { (opt, icon, distSub) ->
                                        val isSel = uiState.distancePreference.equals(opt, ignoreCase = true)
                                        val bgDistColor by animateColorAsState(
                                            targetValue = if (isSel) PastelCardBlue else SurfaceWhite,
                                            animationSpec = tween(200),
                                            label = "distBg"
                                        )
                                        val borderDistColor by animateColorAsState(
                                            targetValue = if (isSel) SkyBluePrimary else BorderLight,
                                            animationSpec = tween(200),
                                            label = "distBorder"
                                        )

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(bgDistColor)
                                                .border(1.dp, borderDistColor, RoundedCornerShape(12.dp))
                                                .clickable { viewModel.setDistancePreference(opt) }
                                                .padding(vertical = 12.dp, horizontal = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(text = icon, fontSize = 16.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = opt,
                                                    color = if (isSel) SkyBluePrimary else TextNavyDark,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = distSub,
                                                    color = TextNavyBody,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Save Preferences Button
            Button(
                onClick = { viewModel.savePreferences(context, onSavePreferences) },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryGradient),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = SurfaceWhite,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Preferences  ✓",
                            color = SurfaceWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
