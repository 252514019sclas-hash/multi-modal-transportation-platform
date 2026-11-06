package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.group.GroupMember
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun GroupDetailsScreen(
    viewModel: GroupTravelViewModel = viewModel(),
    groupId: Int,
    onBack: () -> Unit,
    onViewTransitRoute: (origin: String, destination: String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.loadGroupDetails(context, groupId)
    }

    val group = uiState.activeGroup
    val tabs = listOf("Overview", "Live Map", "Itinerary", "Explore Together", "Chat", "Members")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceWhite)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .shadow(2.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(SurfaceWhite)
                                    .border(1.dp, BorderLight, CircleShape)
                                    .clickable { onBack() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "←", color = TextNavyDark, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = group?.groupName ?: "Trip Details",
                                    color = TextNavyDark,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "🎯 ${group?.destination ?: "Tamil Nadu"} · ${group?.travelDate ?: ""}",
                                    color = TextNavyBody,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Invitation Code Pill (Click to Copy)
                        group?.let { g ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PastelCardTeal)
                                    .border(1.dp, TealLight, RoundedCornerShape(8.dp))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(g.invitationCode))
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📋", fontSize = 10.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = g.invitationCode,
                                        color = TealDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Navigation Tabs
                    ScrollableTabRow(
                        selectedTabIndex = uiState.selectedTab,
                        edgePadding = 0.dp,
                        containerColor = SurfaceWhite,
                        contentColor = SkyBluePrimary,
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = uiState.selectedTab == index
                            Tab(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedTab(index) },
                                text = {
                                    Text(
                                        text = title,
                                        color = if (isSelected) SkyBluePrimary else TextMutedSlate,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (group == null && uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SkyBluePrimary, modifier = Modifier.size(32.dp))
                    }
                } else {
                    when (uiState.selectedTab) {
                        0 -> OverviewTab(
                            group = group,
                            onPlanRoute = { start, dest -> onViewTransitRoute(start, dest) },
                            onOpenMap = { viewModel.setSelectedTab(1) },
                            onToggleSharing = { enable ->
                                viewModel.toggleLocationSharing(context, groupId, enable)
                            }
                        )
                        1 -> GroupLiveMapScreen(
                            viewModel = viewModel,
                            groupId = groupId,
                            onBack = { viewModel.setSelectedTab(0) },
                            onOpenSettings = { viewModel.setSelectedTab(5) }
                        )
                        2 -> GroupItineraryScreen(
                            viewModel = viewModel,
                            groupId = groupId,
                            destination = group?.destination ?: "Tamil Nadu",
                            onViewTransitRoute = onViewTransitRoute
                        )
                        3 -> ExploreTogetherScreen(
                            viewModel = viewModel,
                            groupId = groupId,
                            destination = group?.destination ?: "Tamil Nadu"
                        )
                        4 -> GroupChatScreen(
                            viewModel = viewModel,
                            groupId = groupId
                        )
                        5 -> MembersTab(
                            viewModel = viewModel,
                            group = group,
                            groupId = groupId
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(
    group: com.simats.multimodaltransportationrecommendation.data.model.group.GroupDetailsData?,
    onPlanRoute: (String, String) -> Unit,
    onOpenMap: () -> Unit,
    onToggleSharing: (Boolean) -> Unit
) {
    if (group == null) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Live Sharing Quick Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (group.userMembership?.isSharing == true) PastelCardTeal else PastelCardPeach
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (group.userMembership?.isSharing == true) "🟢 Location Sharing On" else "🔒 Location Sharing Off",
                            color = if (group.userMembership?.isSharing == true) TealDark else WarmOrangeDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (group.userMembership?.isSharing == true)
                                "${group.sharingCount} members live on map · Auto-expires"
                            else
                                "Enable to let family see your distance & transit arrival",
                            color = TextNavyBody,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = { onToggleSharing(group.userMembership?.isSharing != true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (group.userMembership?.isSharing == true) WarmOrangeAccent else SkyBluePrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (group.userMembership?.isSharing == true) "Pause" else "Share GPS",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Stats Matrix
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    "MEMBERS" to "${group.membersCount}",
                    "SHARING GPS" to "${group.sharingCount}",
                    "STOPS" to "${group.itineraryCount}"
                ).forEach { (label, value) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceWhite)
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = label, color = TextMutedSlate, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            Text(text = value, color = TextNavyDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Member Starting Locations List
        item {
            Text(
                text = "TRAVELING MEMBERS & STARTING HUBS",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(group.approvedMembers) { mem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (mem.role == "creator") SkyBluePrimary else TealSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = mem.initials, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = mem.fullName, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                if (mem.role == "creator") {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "👑 Creator", color = SkyBlueDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "Departure: ${mem.startingLocation}",
                                color = TextNavyBody,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (mem.isSharing) "🟢 Live GPS" else "🔒 Sharing Off",
                            color = if (mem.isSharing) EmeraldGreen else TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Transit ➔",
                            color = SkyBluePrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                onPlanRoute(mem.startingLocation, group.destination)
                            }
                        )
                    }
                }
            }
        }

        // Open Live Map CTA
        item {
            Button(
                onClick = onOpenMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
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
                    Text(text = "🗺️ Open Interactive Group Map ➔", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MembersTab(
    viewModel: GroupTravelViewModel,
    group: com.simats.multimodaltransportationrecommendation.data.model.group.GroupDetailsData?,
    groupId: Int
) {
    val context = LocalContext.current
    if (group == null) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Pending Join Requests (for Creator)
        if (group.isCreator && group.pendingMembers.isNotEmpty()) {
            item {
                Text(
                    text = "PENDING JOIN REQUESTS (${group.pendingMembers.size})",
                    color = WarmOrangeDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(group.pendingMembers) { pending ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PastelCardPeach)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = pending.fullName, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "From: ${pending.startingLocation}", color = TextNavyBody, fontSize = 10.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { viewModel.approveMember(context, groupId, pending.userId) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(text = "Approve", color = Color.White, fontSize = 10.sp)
                            }
                            Button(
                                onClick = { viewModel.removeMember(context, groupId, pending.userId) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(30.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(text = "Reject", color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "APPROVED MEMBERS (${group.approvedMembers.size})",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(group.approvedMembers) { mem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = mem.fullName, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            if (mem.role == "creator") {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "(Creator)", color = SkyBluePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(text = "Starting Point: ${mem.startingLocation}", color = TextNavyBody, fontSize = 10.sp)
                    }

                    if (group.isCreator && mem.role != "creator") {
                        Text(
                            text = "Remove",
                            color = Color.Red.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                viewModel.removeMember(context, groupId, mem.userId)
                            }
                        )
                    }
                }
            }
        }
    }
}

