package com.simats.multimodaltransportationrecommendation.ui.grouptravel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.simats.multimodaltransportationrecommendation.data.model.group.GroupChatMessage
import com.simats.multimodaltransportationrecommendation.ui.theme.*

@Composable
fun GroupChatScreen(
    viewModel: GroupTravelViewModel,
    groupId: Int
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(groupId) {
        viewModel.loadChat(context, groupId)
    }

    LaunchedEffect(uiState.chatMessages.size) {
        if (uiState.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundSoftCream)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Message List
        if (uiState.chatMessages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No messages yet. Send a message or coordinate arrival with family!",
                    color = TextMutedSlate,
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.chatMessages) { msg ->
                    ChatMessageBubble(msg = msg)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Meeting point quick alert button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(PastelCardTeal)
                    .border(1.dp, TealLight, CircleShape)
                    .clickable {
                        viewModel.sendChatMessage(context, groupId, "meeting_point")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📍", fontSize = 16.sp)
            }

            // Text input box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(SurfaceWhite)
                    .border(1.dp, BorderLight, RoundedCornerShape(22.dp))
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = uiState.chatInput,
                    onValueChange = { viewModel.onChatInputChange(it) },
                    singleLine = true,
                    textStyle = TextStyle(color = TextNavyDark, fontSize = 13.sp),
                    cursorBrush = SolidColor(SkyBluePrimary),
                    decorationBox = { inner ->
                        if (uiState.chatInput.isEmpty()) {
                            Text(text = "Type message or coordination...", color = TextLight, fontSize = 12.sp)
                        }
                        inner()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Send Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(PrimaryGradient)
                    .clickable {
                        viewModel.sendChatMessage(context, groupId, "text")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "➤", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(msg: GroupChatMessage) {
    if (msg.messageType == "system") {
        // System notification pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PastelCardTeal)
                    .border(1.dp, TealLight, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🔔 ${msg.messageContent}",
                    color = TealDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (msg.isSelf) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!msg.isSelf) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(TealSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = msg.senderInitials, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            Column(
                horizontalAlignment = if (msg.isSelf) Alignment.End else Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (!msg.isSelf) {
                    Text(
                        text = msg.senderName,
                        color = TextMutedSlate,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .widthIn(max = 260.dp)
                        .shadow(1.dp, RoundedCornerShape(14.dp))
                        .clip(
                            RoundedCornerShape(
                                topStart = 14.dp,
                                topEnd = 14.dp,
                                bottomStart = if (msg.isSelf) 14.dp else 2.dp,
                                bottomEnd = if (msg.isSelf) 2.dp else 14.dp
                            )
                        )
                        .background(if (msg.isSelf) SkyBluePrimary else SurfaceWhite)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (msg.messageType == "place_share") {
                            Text(text = "🏛️ PLACE SUGGESTION", color = if (msg.isSelf) TealLight else SkyBlueDark, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        } else if (msg.messageType == "meeting_point") {
                            Text(text = "📍 REGROUP ALERT", color = if (msg.isSelf) WarmOrangeLight else WarmOrangeDark, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = msg.messageContent,
                            color = if (msg.isSelf) Color.White else TextNavyDark,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Text(
                            text = msg.timeText,
                            color = if (msg.isSelf) Color.White.copy(alpha = 0.7f) else TextMutedSlate,
                            fontSize = 8.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}

