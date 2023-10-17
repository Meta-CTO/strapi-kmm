package com.swensonhe.strapikmm.backgrounddownloader

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.tonyodev.fetch2.R

class BackgroundDownloaderService : Service() {
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        // Init notification manager
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create the notification channel
        val channelId = createNotificationChannel(notificationManager)

        // Create and start the on going notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setSilent(true)
        startForeground(NOTIFICATION_ID, notification.build())

        // Return default handling
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager): String {
        // Prepare the channel identifiers
        val channelId = getString(R.string.fetch_notification_default_channel_id)
        val channelName = getString(R.string.fetch_notification_default_channel_name)

        // Check android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Get the notification channel if possible
            var channel = notificationManager.getNotificationChannel(channelId)

            // Check if does not exist to create it
            if (channel == null) {
                channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
        }

        // Return the channel id
        return channelId
    }

    override fun onDestroy() {
        notificationManager.cancel(NOTIFICATION_ID)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        private const val NOTIFICATION_ID = 6789272
    }
}
