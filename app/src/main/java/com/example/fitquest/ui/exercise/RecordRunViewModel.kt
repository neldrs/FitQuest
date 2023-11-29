package com.example.fitquest.ui.exercise

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.time.Duration
import java.time.Instant
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*


class RecordRunViewModel() : ViewModel() {

    private var startInstant: Instant? = null
    private var endInstant: Instant? = null
    var duration: Duration? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null

    private val _elapsedTime = MutableLiveData<String>()
    val elapsedTime: LiveData<String> get() = _elapsedTime

    private var timerJob: Job? = null
    private var _totalDistance = MutableLiveData<Float>()
    val totalDistance: LiveData<Float> get() = _totalDistance


    fun initializeLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location ->
                calculateDistance(location)
            }
        }
    }

    private fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    private fun calculateDistance(newLocation: Location) {
        lastLocation?.let {
            val distance = it.distanceTo(newLocation)
            _totalDistance.value = (_totalDistance.value ?: 0f) + distance
        } ?: run {
            lastLocation = newLocation
        }
    }


    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(10000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(5000L)
            .build()

        try {
            fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }


    fun startRun() {
        startInstant = Instant.now()
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val now = Instant.now()
                startInstant?.let { start ->
                    val duration = Duration.between(start, now)
                    _elapsedTime.value = formatDuration(duration)
                }
                delay(1000)
            }
        }
    }

    fun endRun() {
        endInstant = Instant.now()
        timerJob?.cancel()
        startInstant?.let { start ->
            duration = Duration.between(start, endInstant)
            // Possibly calculate distance here
        }
    }

}