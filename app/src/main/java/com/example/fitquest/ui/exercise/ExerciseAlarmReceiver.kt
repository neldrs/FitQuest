package com.example.fitquest.ui.exercise

// ExerciseAlarmReceiver.kt
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.Notification
import android.os.Build

class ExerciseAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            createNotificationChannel(context)
            showNotification(context)
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "exercise_channel"
            val channelName = "Exercise Reminder"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context) {
        val channelId = "exercise_channel"
        val notification = Notification.Builder(context, channelId)
            .setContentTitle("Exercise Reminder")
            .setContentText("Don't forget to work out today!")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(1, notification)
    }
}
