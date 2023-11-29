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
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitquest.R
import androidx.navigation.fragment.findNavController


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

    private lateinit var viewModel: ExerciseViewModel
    private lateinit var stepHistoryRecyclerView: RecyclerView
    private lateinit var stepHistoryAdapter: RVStepsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)
        viewModel.loadFromPreferences(requireContext())
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)

        val runButton : ImageButton = view.findViewById(R.id.bRun)
        runButton.setOnClickListener {
            replaceFragment(RecordRunFragment())
        }


        val bWeightButton: ImageButton = view.findViewById(R.id.bWeight)
        bWeightButton.setOnClickListener(WeightButtonClickListener(view))

        tvStepCount = view.findViewById(R.id.tvStepCount)

        sensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        stepHistoryRecyclerView = view.findViewById(R.id.step_history_recycler_view)
        stepHistoryAdapter = RVStepsAdapter(emptyList())
        stepHistoryRecyclerView.adapter = stepHistoryAdapter
        stepHistoryRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.stepHistory.observe(viewLifecycleOwner, Observer { stepHistory ->
            stepHistoryAdapter.updateData(stepHistory)
        })

        viewModel.loadStepHistory()

        val btnSaveSteps = view.findViewById<Button>(R.id.bSaveSteps)
        btnSaveSteps.setOnClickListener {
            val currentDate = viewModel.getCurrentDate()
            val currentSteps = viewModel.stepCount.value ?: 0
            viewModel.saveDailyStepTotal(StepEntry(currentDate, currentSteps))
        }

        if (stepSensor == null) {
            Toast.makeText(context, "Step counter sensor not available on this device.", Toast.LENGTH_LONG).show()
        } else {
            checkPermissions()
        }

        viewModel.stepCount.observe(viewLifecycleOwner, Observer { steps ->
            tvStepCount.text = "Steps: $steps"
        })


        return view
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    inner class WeightButtonClickListener(private val rootView: View) : View.OnClickListener {
        override fun onClick(view: View?) {

            val weightTrainingFragment = WeightTrainingFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment_activity_main, weightTrainingFragment)
            transaction.addToBackStack(null) // Optional: Add to back stack
            transaction.commit()
            val navController = findNavController()
            navController.popBackStack(R.id.navigation_exercise, false)
            navController.navigate(R.id.navigation_weight_training)
        }
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
        // Consider not resetting isInitialStepCountSet here.
        stepSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveToPreferences(requireContext())
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsSinceReboot = event.values[0].toInt()
            viewModel.updateStepCount(totalStepsSinceReboot)
        }
    }
}