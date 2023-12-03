package com.example.fitquest.ui.exercise

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

class RecordRunService : Service() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                val newDistance = calculateAndUpdateDistance(locationResult.locations.last())
                val elapsedTime = getElapsedTime()
                val intent = Intent("LOCATION_UPDATE")
                intent.putExtra("distance", newDistance)
                intent.putExtra("time", elapsedTime)
                LocalBroadcastManager.getInstance(this@RecordRunService).sendBroadcast(intent)
            }



        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, getNotification())
        startRun()
        startLocationUpdates()
        return START_STICKY
    }

    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(10000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(5000L)
            .build()

        try {
            fusedLocationClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRun()
        fusedLocationClient!!.removeLocationUpdates(locationCallback!!)
    }

    private var lastLocation: Location? = null
    private var totalDistance: Float = 0f

    private fun calculateAndUpdateDistance(newLocation: Location): Float {
        lastLocation?.let {
            val distance = it.distanceTo(newLocation)
            totalDistance += distance
        }
        lastLocation = newLocation
        return totalDistance
    }

    private var startTime: Instant? = null

    private var timerJob: Job? = null

    fun startRun() {
        resetRunData()
        startTime = Instant.now()
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val elapsedTime = getElapsedTime()
                val intent = Intent("LOCATION_UPDATE")
                intent.putExtra("time", elapsedTime)
                LocalBroadcastManager.getInstance(this@RecordRunService).sendBroadcast(intent)
                delay(1000)
            }
        }
    }

    fun stopRun() {
        timerJob?.cancel()
    }


    private fun getElapsedTime(): String {
        if (startTime == null) {
            return "00:00:00"
        }
        val elapsed = Duration.between(startTime, Instant.now())
        return formatDuration(elapsed)
    }


    private fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun getNotification(): Notification {
        val notificationChannelId = "YourChannelId"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Location Service"
            val descriptionText = "This channel is used by location service"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(notificationChannelId, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        return notificationBuilder
            .setContentTitle("Tracking Location")
            .setContentText("Your run is being tracked")
            .setSmallIcon(R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    fun resetRunData() {
        totalDistance = 0f
        lastLocation = null
        startTime = Instant.now()
    }


}