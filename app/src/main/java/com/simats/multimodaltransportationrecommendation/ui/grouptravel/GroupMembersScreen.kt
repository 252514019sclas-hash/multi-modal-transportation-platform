package com.simats.multimodaltransportationrecommendation.ui.grouptravel

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.multimodaltransportationrecommendation.data.model.group.GroupMember
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun GroupMembersScreen(
    viewModel: GroupTravelViewModel = viewModel(),
    groupId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.loadGroupDetails(context, groupId)
    }

    val group = uiState.activeGroup

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
                // Header with Back Arrow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 12.dp),
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
                            text = group?.groupName ?: "Group Members",
                            color = TextNavyDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Traveling to ${group?.destination ?: "Tamil Nadu"} · ${group?.membersCount ?: 0} Members",
                            color = TextNavyBody,
                            fontSize = 11.sp
                        )
                    }
                }

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

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Pending join requests (Creator only)
                    if (group?.isCreator == true && group.pendingMembers.isNotEmpty()) {
                        item {
                            Text(
                                text = "PENDING APPROVAL REQUESTS (${group.pendingMembers.size})",
                                color = WarmOrangeDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        items(group.pendingMembers) { pending ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(14.dp)),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = PastelCardPeach)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = pending.fullName, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Departure: ${pending.startingLocation}", color = TextNavyBody, fontSize = 10.sp)
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

                    // Approved members list
                    item {
                        Text(
                            text = "APPROVED GROUP MEMBERS (${group?.approvedMembers?.size ?: 0})",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    items(group?.approvedMembers ?: emptyList()) { mem ->
                        MemberItemCard(
                            member = mem,
                            isCreator = group?.isCreator == true,
                            onRemove = { viewModel.removeMember(context, groupId, mem.userId) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberItemCard(
    member: GroupMember,
    isCreator: Boolean,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp)),
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
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (member.role == "creator") SkyBluePrimary else TealSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = member.initials, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = member.fullName, color = TextNavyDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        if (member.role == "creator") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "👑 Creator", color = SkyBlueDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(text = "📍 Starting from: ${member.startingLocation}", color = TextNavyBody, fontSize = 10.sp)
                    Text(
                        text = if (member.isSharing) "🟢 Live Location Sharing Active" else "🔒 Location Sharing Off",
                        color = if (member.isSharing) EmeraldGreen else TextMutedSlate,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (isCreator && member.role != "creator") {
                Text(
                    text = "Remove",
                    color = Color.Red.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRemove() }
                )
            }
        }
    }
}

