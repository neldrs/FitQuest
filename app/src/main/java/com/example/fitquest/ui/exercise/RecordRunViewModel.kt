package com.example.fitquest.ui.exercise

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


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

    fun saveRunRecordToFirestore(runRecord: RunRecord) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val documentId = "${runRecord.date}_${System.currentTimeMillis()}"
            val userRunRecordDocument = db.collection("users").document(userId)
                .collection("runRecords").document(documentId)
            userRunRecordDocument.set(runRecord)
                .addOnSuccessListener { Log.d("RecordRunViewModel", "Run record saved successfully") }
                .addOnFailureListener { e -> Log.e("RecordRunViewModel", "Error saving run record", e) }
        } else {
            Log.w("RecordRunViewModel", "Attempted to save run record without a signed-in user")
        }
    }



    companion object {
    }

    fun loadRunHistory() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId)
                .collection("runRecords")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("RecordRunViewModel", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    val runHistoryList = snapshots?.toObjects(RunRecord::class.java)
                    _runHistory.value = runHistoryList ?: emptyList()
                }
        }
    }

    private val _runHistory = MutableLiveData<List<RunRecord>>()
    val runHistory: LiveData<List<RunRecord>> = _runHistory



}