package com.example.fitquest.ui.nutrition

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androidplot.xy.XYPlot
import com.example.fitquest.R
import com.example.fitquest.ui.nutrition.CalorieChartHelp

class CalorieChartActivity : AppCompatActivity() {
    private lateinit var calorieChartHelper: CalorieChartHelp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie_chart)

        val barPlot: XYPlot = findViewById(R.id.barCalorie)

        val weekDays = listOf("M", "T", "W", "Th", "F", "Sat", "Sun")
        val consumedCals  = listOf(2000, 1800, 2200, 2500, 1900, 3000, 2100)

        calorieChartHelper = CalorieChartHelp(barPlot)
        calorieChartHelper.plotCals(weekDays, consumedCals)


    }
}