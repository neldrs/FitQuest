
package com.example.fitquest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.fitquest.MainActivity
import android.util.Log


class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "nutritionChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AlarmReceiver", "Alarm received") // Add this log statement
        // Call the function to show the notification
        context?.showNotification()
    }

    private fun Context.showNotification() {
        Log.d("AlarmReceiver", "Showing notification") // Add this log statement
        // Define your notification logic here

        // For example:
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Build the notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("fitquest")
            .setContentText("Drink More Water")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        // Add more configuration as needed

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

}
