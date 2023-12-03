package com.example.fitquest.ui.exercise

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import java.time.Duration
import java.time.Instant


class RecordRunViewModel() : ViewModel() {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null

    private val _elapsedTime = MutableLiveData<String>()
    val elapsedTime: LiveData<String> get() = _elapsedTime

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

    fun startRun(context: Context) {
        val serviceIntent = Intent(context, RecordRunService::class.java)
        context.startService(serviceIntent)
    }

    fun endRun(context: Context) {
        val serviceIntent = Intent(context, RecordRunService::class.java)
        context.stopService(serviceIntent)
    }

}