package com.simats.multimodaltransportationrecommendation.ui.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

object NotificationHelper {
    const val CHANNEL_JOURNEY = "movesmart_journey_channel"
    const val CHANNEL_UPDATES = "movesmart_updates_channel"
    const val CHANNEL_STOPS = "movesmart_stops_channel"

    private const val PREFS_NAME = "movesmart_notification_settings"
    private const val KEY_NOTIFY_EACH_STOP = "key_notify_each_stop"
    private const val KEY_NOTIFY_DESTINATION = "key_notify_destination"
    private const val KEY_SOUND_VIBRATE = "key_sound_vibrate"
    private const val KEY_STATUS_BAR = "key_status_bar"

    // ----------------------------------------------------
    // PREFERENCES MANAGEMENT
    // ----------------------------------------------------

    fun isStopArrivalNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_NOTIFY_EACH_STOP, true)
    }

    fun setStopArrivalNotificationEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_NOTIFY_EACH_STOP, enabled).apply()
    }

    fun isDestinationNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_NOTIFY_DESTINATION, true)
    }

    fun setDestinationNotificationEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_NOTIFY_DESTINATION, enabled).apply()
    }

    fun isSoundVibrateEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SOUND_VIBRATE, true)
    }

    fun setSoundVibrateEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SOUND_VIBRATE, enabled).apply()
    }

    fun isStatusBarNotificationEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_STATUS_BAR, true)
    }

    fun setStatusBarNotificationEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_STATUS_BAR, enabled).apply()
    }

    // ----------------------------------------------------
    // NOTIFICATION CHANNELS
    // ----------------------------------------------------

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Dedicated Channel for Transit Stop & Destination Arrivals
            val stopsChannel = NotificationChannel(
                CHANNEL_STOPS,
                "MoveSmart Transit & Stop Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority alerts when traveler reaches en-route stations and destination"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
            }

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

            notificationManager.createNotificationChannel(stopsChannel)
            notificationManager.createNotificationChannel(journeyChannel)
            notificationManager.createNotificationChannel(updatesChannel)
        }
    }

    // ----------------------------------------------------
    // STOP REACHED NOTIFICATION (EACH STOP)
    // ----------------------------------------------------

    fun notifyStopReached(
        context: Context,
        personName: String,
        stopName: String,
        nextStopName: String? = null,
        arrivalTime: String = "",
        distanceKm: Double = 0.0
    ) {
        if (!isStopArrivalNotificationEnabled(context)) return

        createNotificationChannels(context)

        val timePart = if (arrivalTime.isNotBlank()) "at $arrivalTime" else ""
        val distPart = if (distanceKm > 0) "(${distanceKm.toInt()} km mark)" else ""
        val nextPart = if (!nextStopName.isNullOrBlank()) "Next stop: $nextStopName." else ""

        val title = "🚉 $personName reached $stopName"
        val message = "$personName has reached $stopName $timePart $distPart. $nextPart".trim()

        showNotification(
            context = context,
            title = title,
            message = message,
            channelId = CHANNEL_STOPS,
            notificationId = (stopName.hashCode() % 9000).let { if (it < 0) -it else it } + 1000
        )
    }

    // ----------------------------------------------------
    // FINAL DESTINATION STOPPING NOTIFICATION
    // ----------------------------------------------------

    fun notifyDestinationReached(
        context: Context,
        personName: String,
        destinationName: String,
        arrivalTime: String = "",
        totalKm: Double = 0.0
    ) {
        if (!isDestinationNotificationEnabled(context)) return

        createNotificationChannels(context)

        val timePart = if (arrivalTime.isNotBlank()) "at $arrivalTime" else ""
        val distPart = if (totalKm > 0) "· Completed ${totalKm.toInt()} km journey" else ""

        val title = "🎉 $personName reached $destinationName (Final Stop)!"
        val message = "Journey Complete! $personName arrived safely at the final stopping destination of $destinationName $timePart $distPart.".trim()

        showNotification(
            context = context,
            title = title,
            message = message,
            channelId = CHANNEL_STOPS,
            notificationId = (destinationName.hashCode() % 9000).let { if (it < 0) -it else it } + 5000
        )
    }

    // ----------------------------------------------------
    // BASE NOTIFICATION DISPATCHER
    // ----------------------------------------------------

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String = CHANNEL_JOURNEY,
        notificationId: Int = (System.currentTimeMillis() % 10000).toInt()
    ) {
        if (!isStatusBarNotificationEnabled(context)) return

        createNotificationChannels(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return // Runtime permission not yet granted
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val soundVibrate = isSoundVibrateEnabled(context)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setAutoCancel(true)

        if (soundVibrate) {
            builder.setSound(soundUri)
            builder.setVibrate(longArrayOf(0, 300, 150, 300))
            builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        } else {
            builder.setSilent(true)
        }

        notificationManager.notify(notificationId, builder.build())
    }
}
