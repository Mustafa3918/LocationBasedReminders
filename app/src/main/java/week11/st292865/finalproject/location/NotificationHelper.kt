package week11.st292865.finalproject.location

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import week11.st292865.finalproject.R

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "geofence_reminders"
        private const val CHANNEL_NAME = "Location Reminders"
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val existing = manager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications when you enter a saved location."
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showTriggered(title: String, body: String) {
        ensureChannel()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // ✅ MUST exist, otherwise no notification
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // ✅ heads-up on most devices
            .setAutoCancel(true)
            .build()

        // Use a unique ID so multiple geofences don't overwrite each other
        val id = System.currentTimeMillis().toInt()

        NotificationManagerCompat.from(context).notify(id, notification)
    }
}
