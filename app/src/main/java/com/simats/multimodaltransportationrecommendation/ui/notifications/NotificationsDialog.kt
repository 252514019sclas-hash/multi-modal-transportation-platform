package com.simats.multimodaltransportationrecommendation.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.simats.multimodaltransportationrecommendation.data.local.SessionManager
import com.simats.multimodaltransportationrecommendation.data.model.notifications.NotificationItem
import com.simats.multimodaltransportationrecommendation.data.repository.AuthRepository
import com.simats.multimodaltransportationrecommendation.data.repository.NetworkResult
import kotlinx.coroutines.launch

@Composable
fun NotificationsHistoryDialog(
    onDismiss: () -> Unit,
    authRepository: AuthRepository = AuthRepository()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userId = SessionManager.getInstance(context).getUserId() ?: 1

    var isLoading by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    var unreadCount by remember { mutableStateOf(0) }

    fun load() {
        isLoading = true
        scope.launch {
            when (val res = authRepository.fetchNotifications(userId)) {
                is NetworkResult.Success -> {
                    unreadCount = res.data.first
                    notifications = res.data.second
                    isLoading = false
                }
                is NetworkResult.Error -> {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        load()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔔", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notifications",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (unreadCount > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF13201C))
                                .border(1.dp, Color(0xFFC6FF00), RoundedCornerShape(10.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(text = "$unreadCount new", color = Color(0xFFC6FF00), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (notifications.isNotEmpty() && unreadCount > 0) {
                    Text(
                        text = "Mark read",
                        color = Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable {
                            scope.launch {
                                authRepository.markNotificationRead(userId, markAll = true)
                                load()
                            }
                        }
                    )
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 380.dp)
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFC6FF00), modifier = Modifier.size(32.dp))
                    }
                } else if (notifications.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "🔕", fontSize = 28.sp)
                            Text(text = "No notifications yet", color = Color(0xFF8A99AD), fontSize = 13.sp)
                            Text(text = "Your journey reminders and travel alerts will appear here.", color = Color(0xFF64748B), fontSize = 10.sp)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(notifications) { item ->
                            val icon = when (item.type) {
                                "journey_reminder" -> "🎫"
                                "tourist_recommendation" -> "🧭"
                                "travel_alert" -> "⚠️"
                                else -> "🌿"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (item.isRead) Color(0xFF070B14) else Color(0xFF13201C))
                                    .border(
                                        1.dp,
                                        if (item.isRead) Color(0xFF1E293B) else Color(0xFF1E4D2B),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        if (!item.isRead) {
                                            scope.launch {
                                                authRepository.markNotificationRead(userId, item.notificationId)
                                                load()
                                            }
                                        }
                                    }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF162238)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = icon, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item.title,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (!item.isRead) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .background(Color(0xFFC6FF00), CircleShape)
                                            )
                                        }
                                    }
                                    Text(
                                        text = item.message,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                    Text(
                                        text = item.createdAt,
                                        color = Color(0xFF64748B),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Close", color = Color.White, fontSize = 12.sp)
            }
        }
    )
}
