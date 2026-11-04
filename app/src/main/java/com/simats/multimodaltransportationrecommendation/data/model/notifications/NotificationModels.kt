package com.simats.multimodaltransportationrecommendation.data.model.notifications

import com.google.gson.annotations.SerializedName

data class NotificationItem(
    @SerializedName("notification_id")
    val notificationId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("type")
    val type: String, // "journey_reminder", "travel_update", "tourist_recommendation", "travel_alert"
    @SerializedName("is_read")
    val isRead: Boolean = false,
    @SerializedName("action_data")
    val actionData: String? = null,
    @SerializedName("created_at")
    val createdAt: String
)

data class NotificationsResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("unread_count")
    val unreadCount: Int = 0,
    @SerializedName("notifications")
    val notifications: List<NotificationItem>? = null,
    @SerializedName("message")
    val message: String? = null
)

data class MarkReadRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("notification_id")
    val notificationId: Int? = null,
    @SerializedName("mark_all")
    val markAll: Boolean = false
)

