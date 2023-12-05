package com.example.fitquest.ui.exercise

// ScheduleAlarmActivity.kt
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import com.example.fitquest.R
import java.util.*

class ScheduleAlarmActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_alarm)

        val hourSpinner: Spinner = findViewById(R.id.hourSpinner)
        val minuteSpinner: Spinner = findViewById(R.id.minuteSpinner)
        val scheduleButton: Button = findViewById(R.id.scheduleButton)

        // Populate hour spinner with values from 0 to 23
        val hours = (0..23).map { it.toString() }
        val hourAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hours)
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        hourSpinner.adapter = hourAdapter

        // Populate minute spinner with values from 0 to 59
        val minutes = (0..59).map { it.toString().padStart(2, '0') }
        val minuteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, minutes)
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        minuteSpinner.adapter = minuteAdapter

        // Set up onClickListener for the schedule button
        scheduleButton.setOnClickListener {
            val selectedHour = hourSpinner.selectedItem.toString().toInt()
            val selectedMinute = minuteSpinner.selectedItem.toString().toInt()

            scheduleExerciseAlarm(selectedHour, selectedMinute)

            Toast.makeText(this, "Exercise alarm scheduled!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun scheduleExerciseAlarm(hour: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ExerciseAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}
