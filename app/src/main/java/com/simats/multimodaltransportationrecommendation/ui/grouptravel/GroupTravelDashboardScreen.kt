package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.group.MemberPreview
import com.simats.multimodaltransportationrecommendation.data.model.group.TravelGroup
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun GroupTravelDashboardScreen(
    viewModel: GroupTravelViewModel = viewModel(),
    onBack: () -> Unit,
    onCreateGroup: () -> Unit,
    onJoinGroup: () -> Unit,
    onOpenGroupDetails: (groupId: Int) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadGroups(context)
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Top Header with Back Arrow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                text = "MOVE SMART AI",
                                color = SkyBluePrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "Group Travel & Live Map",
                                color = TextNavyDark,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Refresh Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PastelCardBlue)
                            .border(1.dp, SkyBlueSoft, CircleShape)
                            .clickable { viewModel.loadGroups(context) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔄", fontSize = 14.sp)
                    }
                }

                // Hero Group Travel Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(HeroBannerGradient)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👨‍👩‍👧‍👦", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SMART FAMILY & FRIENDS TRAVEL",
                                color = SurfaceWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Travel Together from Different Places",
                            color = SurfaceWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Coordinate live locations on map, suggest tourist spots & vote for group destinations across Tamil Nadu.",
                            color = SurfaceWhite.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons Row: Create New Group & Join Existing Group
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onCreateGroup,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "＋", color = SurfaceWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Create Group", color = SurfaceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Button(
                        onClick = onJoinGroup,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PastelCardTeal),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TealLight),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔑", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Join with Code", color = TealDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status message
                if (!uiState.successMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PastelCardTeal)
                            .border(1.dp, TealSoft, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(text = "✓ ${uiState.successMessage}", color = TealDark, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SkyBluePrimary, modifier = Modifier.size(32.dp))
                    }
                } else if (uiState.myGroups.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                            .background(SurfaceWhite, RoundedCornerShape(16.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(text = "🌴", fontSize = 40.sp)
                            Text(
                                text = "No Active Travel Groups Yet",
                                color = TextNavyDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Plan a trip with family or friends to Ooty, Mahabalipuram, Kodaikanal or Salem! Create a group or join with an invitation code.",
                                color = TextMutedSlate,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 16.sp
                            )
                            Button(
                                onClick = onCreateGroup,
                                colors = ButtonDefaults.buttonColors(containerColor = SkyBluePrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "Create First Family Group", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Section: My Travel Groups
                        item {
                            Text(
                                text = "MY TRAVEL GROUPS (${uiState.myGroups.size})",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        items(uiState.myGroups) { group ->
                            GroupCardItem(
                                group = group,
                                onClick = { onOpenGroupDetails(group.groupId) }
                            )
                        }

                        // Section: Upcoming Trips
                        if (uiState.upcomingTrips.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "UPCOMING GROUP TRIPS (${uiState.upcomingTrips.size})",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            items(uiState.upcomingTrips) { group ->
                                GroupCardItem(
                                    group = group,
                                    onClick = { onOpenGroupDetails(group.groupId) }
                                )
                            }
                        }

                        // Section: Recently Completed
                        if (uiState.completedTrips.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "RECENTLY COMPLETED TRIPS (${uiState.completedTrips.size})",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            items(uiState.completedTrips) { group ->
                                GroupCardItem(
                                    group = group,
                                    onClick = { onOpenGroupDetails(group.groupId) }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupCardItem(
    group: TravelGroup,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(1.dp, BorderLight, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Code Pill + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PastelCardTeal)
                        .border(1.dp, TealLight, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "CODE: ${group.invitationCode}",
                        color = TealDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (group.status == "active") PastelCardBlue else PastelCardPeach)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (group.status == "active") "Active Trip" else "Planning",
                        color = if (group.status == "active") SkyBlueDark else WarmOrangeDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Group Name & Destination
            Column {
                Text(
                    text = group.groupName,
                    color = TextNavyDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎯 Common Destination: ", color = TextMutedSlate, fontSize = 11.sp)
                    Text(text = group.destination, color = TextNavyDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Date & Time + Creator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(BackgroundSoftCream)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📅", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${group.travelDate} · ${group.travelTime}", color = TextNavyDark, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Text(
                    text = if (group.isCreator) "Created by You" else "By ${group.creatorName}",
                    color = if (group.isCreator) SkyBluePrimary else TextMutedSlate,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Member Avatars Row & Navigation Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatars preview
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy((-6).dp)
                ) {
                    group.membersPreview.take(4).forEach { mem ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (mem.role == "creator") SkyBluePrimary else TealSecondary)
                                .border(1.5.dp, SurfaceWhite, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = mem.initials, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (group.memberCount > 4) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(PastelCardPeach)
                                .border(1.5.dp, SurfaceWhite, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+${group.memberCount - 4}", color = WarmOrangeDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${group.memberCount} members traveling",
                        color = TextMutedSlate,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Manage Trip", color = SkyBluePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "➔", color = SkyBluePrimary, fontSize = 11.sp)
                }
            }
        }
    }
}

