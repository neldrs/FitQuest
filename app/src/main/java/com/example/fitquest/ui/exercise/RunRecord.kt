package com.example.fitquest.ui.exercise

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RunRecord(
    val distance: Float = 0f,
    val time: String = "00:00:00",
    val date: String = getCurrentDate()
)
fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(Date())
}