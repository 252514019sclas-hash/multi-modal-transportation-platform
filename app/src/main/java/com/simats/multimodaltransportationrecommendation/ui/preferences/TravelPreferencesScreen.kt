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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

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
            .background(Color(0xFF070B14))
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
                            .background(Color(0xFF111C2E))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "05 / MOVESMART AI",
                            color = Color(0xFF7A8B9E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Your travel preferences",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Banner Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF101D17))
                        .border(1.dp, Color(0xFF1E3F2A), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "☘️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PERSONALIZE YOUR MULTI-MODAL COMMUTE",
                            color = Color(0xFFC6FF00),
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
                            .background(Color(0x22C6FF00))
                            .border(1.dp, Color(0xFFC6FF00), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "✓ ${uiState.successMessage}",
                            color = Color(0xFFC6FF00),
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
                            .background(Color(0x22EF4444))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "⚠️ ${uiState.errorMessage}",
                            color = Color(0xFFFF8B8B),
                            fontSize = 12.sp
                        )
                    }
                }

                // SECTION 1: PREFERRED TRANSPORTATION MODES (All 7 modes)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "PREFERRED TRANSPORT MODES",
                        color = Color(0xFF8A99AD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Choose which modes the MoveSmart AI routing engine should include:",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )

                    val allModes = listOf(
                        Triple("Walking", "🚶", "0g CO₂ · Free & healthy"),
                        Triple("Cycling", "🚲", "0g CO₂ · Urban bike lanes"),
                        Triple("Bus", "🚌", "68g CO₂ · City transit network"),
                        Triple("Metro", "🚇", "35g CO₂ · High-speed rail"),
                        Triple("Train", "🚆", "40g CO₂ · Regional rail"),
                        Triple("Taxi", "🚕", "120g CO₂ · Door-to-door cab"),
                        Triple("Car", "🚗", "170g CO₂ · Private driving")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allModes.forEach { (modeName, icon, sub) ->
                            val isSelected = uiState.selectedModes.contains(modeName)
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) Color(0xFF13201C) else Color(0xFF0F172A),
                                animationSpec = tween(250),
                                label = "modeBg"
                            )
                            val borderColor by animateColorAsState(
                                targetValue = if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                animationSpec = tween(250),
                                label = "modeBorder"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgColor)
                                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.toggleMode(modeName) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = icon, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = modeName,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = sub,
                                            color = Color(0xFF8A99AD),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Text(text = "✓", color = Color(0xFF070B14), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // SECTION 2: TRAVEL PURPOSE (Daily Commute, Tourism, Business, Adventure)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "TRAVEL PURPOSE",
                        color = Color(0xFF8A99AD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "What is the primary focus of your travels?",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )

                    val purposes = listOf(
                        Triple("Daily Commute", "🚆", "Work, school & regular routine transit"),
                        Triple("Tourism", "📸", "Sightseeing, culture & famous attractions"),
                        Triple("Business", "💼", "Fast direct routes for meetings & transit"),
                        Triple("Adventure", "🧗", "Active exploration, trails & nature parks")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        purposes.forEach { (purpose, icon, desc) ->
                            val isSelected = uiState.travelPurpose.equals(purpose, ignoreCase = true)
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) Color(0xFF13201C) else Color(0xFF0F172A),
                                animationSpec = tween(250),
                                label = "purposeBg"
                            )
                            val borderColor by animateColorAsState(
                                targetValue = if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                animationSpec = tween(250),
                                label = "purposeBorder"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgColor)
                                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setTravelPurpose(purpose) }
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
                                            text = purpose,
                                            color = if (isSelected) Color(0xFFC6FF00) else Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = desc,
                                            color = Color(0xFF8A99AD),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFFC6FF00) else Color(0xFF1E293B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Text(text = "✓", color = Color(0xFF070B14), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                            .background(Color(0xFF0F172A))
                            .border(
                                1.dp,
                                if (uiState.touristRecommendations) Color(0xFF1E4D2B) else Color(0xFF1E293B),
                                RoundedCornerShape(14.dp)
                            )
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
                                        color = if (uiState.touristRecommendations) Color(0xFFC6FF00) else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Personalize tourist destinations on Explore screen using your travel preferences",
                                        color = Color(0xFF8A99AD),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Switch(
                                checked = uiState.touristRecommendations,
                                onCheckedChange = { viewModel.toggleTouristRecommendations() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF070B14),
                                    checkedTrackColor = Color(0xFFC6FF00),
                                    uncheckedThumbColor = Color(0xFF8A99AD),
                                    uncheckedTrackColor = Color(0xFF1E293B)
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
                                    color = Color(0xFF8A99AD),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Select what types of attractions you want AI to recommend (Multi-select):",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )

                                val destTypes = listOf(
                                    Triple("Historical Places", "🏛️", "Forts, heritage monuments & landmarks"),
                                    Triple("Temples", "🛕", "Spiritual shrines & cultural heritage"),
                                    Triple("Beaches", "🏖️", "Coastlines, marinas & seaside walks"),
                                    Triple("Parks", "🌳", "City parks, botanical gardens & plazas"),
                                    Triple("Museums", "🎨", "Art galleries, science & history museums"),
                                    Triple("Nature and Adventure", "⛰️", "Wildlife sanctuaries & hiking trails")
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    destTypes.forEach { (type, icon, desc) ->
                                        val isSel = uiState.selectedDestinationTypes.any { it.equals(type, ignoreCase = true) }
                                        val bgTypeColor by animateColorAsState(
                                            targetValue = if (isSel) Color(0xFF13201C) else Color(0xFF0F172A),
                                            animationSpec = tween(200),
                                            label = "destTypeBg"
                                        )
                                        val borderTypeColor by animateColorAsState(
                                            targetValue = if (isSel) Color(0xFFC6FF00) else Color(0xFF1E293B),
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
                                                        color = if (isSel) Color(0xFFC6FF00) else Color.White,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = desc,
                                                        color = Color(0xFF8A99AD),
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSel) Color(0xFFC6FF00) else Color(0xFF1E293B)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isSel) {
                                                    Text(text = "✓", color = Color(0xFF070B14), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                    color = Color(0xFF8A99AD),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "How far are you willing to travel for tourist recommendations?",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )

                                val distanceOpts = listOf(
                                    Triple("Nearby", "📍", "< 3 km · Walk / Bike"),
                                    Triple("Within City", "🏙️", "3–10 km · Metro / Bus"),
                                    Triple("Long Distance", "🛣️", "> 10 km · Rail / Cab")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    distanceOpts.forEach { (opt, icon, distSub) ->
                                        val isSel = uiState.distancePreference.equals(opt, ignoreCase = true)
                                        val bgDistColor by animateColorAsState(
                                            targetValue = if (isSel) Color(0xFF13201C) else Color(0xFF0F172A),
                                            animationSpec = tween(200),
                                            label = "distBg"
                                        )
                                        val borderDistColor by animateColorAsState(
                                            targetValue = if (isSel) Color(0xFFC6FF00) else Color(0xFF1E293B),
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
                                                    color = if (isSel) Color(0xFFC6FF00) else Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = distSub,
                                                    color = Color(0xFF8A99AD),
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

                // SECTION 4: LOW-CARBON TRAVEL PREFERENCE TOGGLE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF112017))
                        .border(1.dp, Color(0xFF1E4D2B), RoundedCornerShape(14.dp))
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
                            Text(text = "🌿", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Prioritize Low-Carbon Travel",
                                    color = Color(0xFFC6FF00),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Always recommend the greenest route when travel times are comparable",
                                    color = Color(0xFF8A99AD),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Switch(
                            checked = uiState.lowCarbon,
                            onCheckedChange = { viewModel.toggleLowCarbon() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF070B14),
                                checkedTrackColor = Color(0xFFC6FF00),
                                uncheckedThumbColor = Color(0xFF8A99AD),
                                uncheckedTrackColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }

                // SECTION 5: PREFERRED TRAVEL BUDGET & MAX DURATION
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "PREFERRED TRAVEL BUDGET",
                        color = Color(0xFF8A99AD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    val budgetOptions = listOf("Economy", "Moderate", "Premium")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        budgetOptions.forEach { opt ->
                            val isSel = uiState.selectedBudget.equals(opt, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) Color(0xFF13201C) else Color(0xFF0F172A))
                                    .border(
                                        1.dp,
                                        if (isSel) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.selectBudget(opt) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = when(opt) {
                                            "Economy" -> "🪙"
                                            "Moderate" -> "💳"
                                            else -> "💎"
                                        },
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = opt,
                                        color = if (isSel) Color(0xFFC6FF00) else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "MAXIMUM JOURNEY DURATION",
                        color = Color(0xFF8A99AD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    val durations = listOf("Fastest", "Under 30 min", "Under 45 min", "Flexible")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        durations.forEach { dur ->
                            val isSel = uiState.selectedDuration.equals(dur, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) Color(0xFF13201C) else Color(0xFF0F172A))
                                    .border(
                                        1.dp,
                                        if (isSel) Color(0xFFC6FF00) else Color(0xFF1E293B),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.selectDuration(dur) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dur,
                                    color = if (isSel) Color(0xFFC6FF00) else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Save Preferences Button (Pinned at Bottom)
            Button(
                onClick = { viewModel.savePreferences(context, onSavePreferences) },
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC6FF00),
                    disabledContainerColor = Color(0x66C6FF00)
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF070B14),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save Preferences  ✓",
                        color = Color(0xFF070B14),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF070B14)
@Composable
fun TravelPreferencesScreenPreview() {
    TravelPreferencesScreen()
}
