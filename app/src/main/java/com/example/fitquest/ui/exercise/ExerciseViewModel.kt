package com.example.fitquest.ui.exercise

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseViewModel : ViewModel() {

    var initialStepCount = 0
    var isInitialStepCountSet = false

    private val _stepCount = MutableLiveData<Int>()
    val stepCount: LiveData<Int> = _stepCount

    private var lastUpdateDate = getCurrentDate()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance() // Firebase Authentication instance


    fun updateStepCount(totalStepsSinceReboot: Int) {
        val currentDate = getCurrentDate()
        if (currentDate != lastUpdateDate) {
            // It's a new day, so first save the previous day's step count
            val previousDayStepCount = _stepCount.value ?: 0
            saveDailyStepTotal(StepEntry(lastUpdateDate, previousDayStepCount))

            // Now reset the step count for the new day
            initialStepCount = totalStepsSinceReboot
            _stepCount.value = 0
            lastUpdateDate = currentDate
        } else {
            // Same day, calculate the step count normally
            _stepCount.value = totalStepsSinceReboot - initialStepCount
        }
    }



    fun saveDailyStepTotal(stepEntry: StepEntry){
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userStepEntryDocument = db.collection("users").document(userId)
                .collection("step_entries").document(stepEntry.date)
            userStepEntryDocument.set(stepEntry)
                .addOnSuccessListener { Log.d("ExerciseViewModel", "Step data saved successfully") }
                .addOnFailureListener { e -> Log.e("ExerciseViewModel", "Error saving step data", e) }
        } else {
            Log.w("ExerciseViewModel", "Attempted to save step data without a signed-in user")
        }
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun saveToPreferences(context: Context) {
        val sharedPref = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt("lastStepCount", _stepCount.value ?: 0)
            putString("lastUpdateDate", lastUpdateDate)
            apply()
        }
    }

    fun loadFromPreferences(context: Context) {
        val sharedPref = context.getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE) ?: return
        lastUpdateDate = sharedPref.getString("lastUpdateDate", getCurrentDate()) ?: getCurrentDate()
        _stepCount.value = sharedPref.getInt("lastStepCount", 0)
    }

    private val _stepHistory = MutableLiveData<List<StepEntry>>()
    val stepHistory: LiveData<List<StepEntry>> = _stepHistory

    fun loadStepHistory() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId)
                .collection("step_entries")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("ExerciseViewModel", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    val stepHistoryList = snapshots?.toObjects(StepEntry::class.java)
                    _stepHistory.value = stepHistoryList ?: emptyList()
                }
        }
    }



}