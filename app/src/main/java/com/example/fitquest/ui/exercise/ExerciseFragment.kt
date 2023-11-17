package com.example.fitquest.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitquest.databinding.FragmentExerciseBinding
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.fitquest.R


class ExerciseFragment : Fragment(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private lateinit var tvStepCount: TextView
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupStepCounterSensor()
        } else {
            Toast.makeText(context, "Step counter permission denied. The feature will not be available.", Toast.LENGTH_LONG).show()        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)
        tvStepCount = view.findViewById(R.id.tvStepCount)

        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Toast.makeText(context, "Step counter sensor not available on this device.", Toast.LENGTH_LONG).show()
        } else {
            checkPermissions()
        }

        return view
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is granted, set up the sensor
                setupStepCounterSensor()
            }
            else -> {
                // Request permission
                requestPermissionLauncher.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    private fun setupStepCounterSensor() {
        // Get the step counter sensor from the sensor manager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Check if the sensor is available
        if (stepSensor != null) {
            // Register the sensor listener
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            // Handle the case where a step counter sensor is not available
            Toast.makeText(context, "Step counter sensor not available.", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onResume() {
        super.onResume()
        stepSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0].toInt()
            tvStepCount.text = "Steps: $steps"
        }
    }
}