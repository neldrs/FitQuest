package com.example.fitquest.ui.exercise

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import java.util.*

class ExerciseViewModel : ViewModel() {

    var initialStepCount = 0
    var isInitialStepCountSet = false

    private val _stepCount = MutableLiveData<Int>()
    val stepCount: LiveData<Int> = _stepCount

    private var lastUpdateDate = getCurrentDate()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance() // Firebase Authentication instance

    /*
    // LiveData for the selected recurring time
    private val _selectedTime = MutableLiveData<Pair<Int, Int>>() // Pair(hour, minute)
    val selectedTime: LiveData<Pair<Int, Int>> = _selectedTime

    private val _reminderStatus = MutableLiveData<Boolean>()
    val reminderStatus: LiveData<Boolean> = _reminderStatus
     */

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

    /*
    // Method to set the recurring time
    fun setAlarmTime(hour: Int, minute: Int) {
        _selectedTime.value = Pair(hour, minute)
    }

    // Method to save the recurring time to SharedPreferences
    fun saveAlarmTime(context: Context?) {
        val sharedPref = context?.getSharedPreferences("ExercisePrefs", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt("recurringHour", _selectedTime.value?.first ?: 0)
            putInt("recurringMinute", _selectedTime.value?.second ?: 0)
            apply()
        }
    }

    // Method to load the recurring time from SharedPreferences
    fun loadAlarmTime(context: Context?) {
        val sharedPref = context?.getSharedPreferences("ExercisePrefs", Context.MODE_PRIVATE) ?: return
        val hour = sharedPref.getInt("recurringHour", 0)
        val minute = sharedPref.getInt("recurringMinute", 0)
        _selectedTime.value = Pair(hour, minute)
    }

    // Method to set the reminder status
    fun setReminderStatus(status: Boolean) {
        _reminderStatus.value = status
    }

    // Method to save the reminder status to SharedPreferences
    fun saveReminderStatus(context: Context) {
        val sharedPref = context.getSharedPreferences("ExercisePrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("reminderStatus", _reminderStatus.value ?: false)
            apply()
        }
    }

    // Method to load the reminder status from SharedPreferences
    fun loadReminderStatus(context: Context) {
        val sharedPref = context.getSharedPreferences("ExercisePrefs", Context.MODE_PRIVATE)
        _reminderStatus.value = sharedPref.getBoolean("reminderStatus", false)
    }

    // Schedule the exercise notification
    fun scheduleExerciseNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent to trigger the ExerciseNotificationReceiver
        val intent = Intent(context, ExerciseNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Set the notification time based on the selected alarm time
        val selectedTime = _selectedTime.value
        if (selectedTime != null) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, _selectedTime.value?.first ?: 8)
                set(Calendar.MINUTE, _selectedTime.value?.second ?: 0)
                set(Calendar.SECOND, 0)
            }

        // Schedule the notification
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, // Repeat daily
            pendingIntent
        )

        Log.d("ExerciseViewModel", "Exercise notification scheduled")
        }
    }

     */
}