package com.simats.multimodaltransportationrecommendation.ui.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

object NotificationHelper {
    const val CHANNEL_JOURNEY = "movesmart_journey_channel"
    const val CHANNEL_UPDATES = "movesmart_updates_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val journeyChannel = NotificationChannel(
                CHANNEL_JOURNEY,
                "MoveSmart Journey Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time departure, delay and ticket reminders for MoveSmart journeys"
                enableVibration(true)
            }

            val updatesChannel = NotificationChannel(
                CHANNEL_UPDATES,
                "MoveSmart Travel Updates & Recommendations",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Carbon savings milestones and personalized tourist recommendations"
            }

            notificationManager.createNotificationChannel(journeyChannel)
            notificationManager.createNotificationChannel(updatesChannel)
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String = CHANNEL_JOURNEY,
        notificationId: Int = (System.currentTimeMillis() % 10000).toInt()
    ) {
        createNotificationChannels(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return // Runtime permission not yet granted
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(if (channelId == CHANNEL_JOURNEY) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}
