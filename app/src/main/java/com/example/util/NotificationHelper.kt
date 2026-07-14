package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
    private const val CHANNEL_ID = "kisan_alerts"
    private const val CHANNEL_NAME = "KisanAlerts"
    private const val CHANNEL_DESC = "Notifications for orders and weather alerts"

    fun showNotification(context: Context, title: String, text: String) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = notificationManager.getNotificationChannel(CHANNEL_ID)
                if (channel == null) {
                    val newChannel = NotificationChannel(
                        CHANNEL_ID, 
                        CHANNEL_NAME, 
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {
                        description = CHANNEL_DESC
                    }
                    notificationManager.createNotificationChannel(newChannel)
                }
            }

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
