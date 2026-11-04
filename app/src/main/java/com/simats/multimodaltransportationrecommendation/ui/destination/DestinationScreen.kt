package com.simats.multimodaltransportationrecommendation.ui.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun DestinationScreen(
    viewModel: DestinationViewModel = DestinationViewModel(),
    onBack: () -> Unit = {},
    onUseDestination: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, CircleShape)
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "←", color = TextNavyDark, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "TAMIL NADU SMART TRANSIT",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Find Your Destination",
                            color = TextNavyDark,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Origin Field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "From (Origin)", color = TextMutedSlate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(2.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📍", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = uiState.origin, color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Destination Field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "To (Destination)", color = TextMutedSlate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(2.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceWhite)
                            .border(1.5.dp, SkyBluePrimary, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔍", fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = uiState.destinationQuery, color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Recent Searches
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Recent Searches", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    uiState.recentSearches.forEach { (name, address) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.updateDestination(name)
                                    onUseDestination()
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🕒", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = name, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = address, color = TextMutedSlate, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Saved Locations
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Popular Transit Hubs", color = TextNavyDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    uiState.savedPlaces.forEach { (label, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceWhite)
                                .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.updateDestination(label)
                                    onUseDestination()
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⭐", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = label, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = desc, color = TextMutedSlate, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Bottom CTA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(PrimaryGradient)
                    .clickable { onUseDestination() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Search Multi-Modal Routes  ➔",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
