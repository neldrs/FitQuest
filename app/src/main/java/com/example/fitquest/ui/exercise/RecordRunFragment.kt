package com.example.fitquest.ui.exercise

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.fitquest.R
import java.time.Duration


class RecordRunFragment : Fragment() {
    private lateinit var viewModel: RecordRunViewModel
    private lateinit var tvRunStatus: TextView
    private lateinit var btnStartRun: Button
    private lateinit var btnEndRun: Button
    private lateinit var tvRunTime: TextView
    private lateinit var tvRunDistance: TextView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.startLocationUpdates()
        } else {
            Toast.makeText(context, "Location permission denied. The feature will not be available.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RecordRunViewModel::class.java)
        viewModel.initializeLocationClient(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_record_run, container, false)
        tvRunStatus = view.findViewById(R.id.tvRunStatus)
        btnStartRun = view.findViewById(R.id.btnStartRun)
        btnEndRun = view.findViewById(R.id.btnEndRun)
        tvRunTime = view.findViewById(R.id.tvRunTime)
        tvRunDistance = view.findViewById(R.id.tvRunDistance)

        val backButton: Button = view.findViewById(R.id.bBack)
        backButton.setOnClickListener {
            replaceFragment(ExerciseFragment())
        }
        viewModel.elapsedTime.observe(viewLifecycleOwner) { time ->
            tvRunTime.text = "$time"
        }

        btnStartRun.setOnClickListener {
            viewModel.startRun()
            checkPermissionsAndStartLocationUpdates()
            viewModel.startLocationUpdates()
            tvRunStatus.text = "Run Status: In Progress"
        }

        btnEndRun.setOnClickListener {
            viewModel.stopLocationUpdates()
            viewModel.endRun()
            tvRunStatus.text = "Run Status: Finished"
        }
        viewModel.totalDistance.observe(viewLifecycleOwner) { distance ->
            distance?.let {
                val distanceInMiles = it * 0.000621371
                tvRunDistance.text = String.format("%.2f miles", distanceInMiles)
            }
        }


        return view
    }
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    private fun checkPermissionsAndStartLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.startLocationUpdates()
                } else {
                    Toast.makeText(requireContext(), "Location permission is required for tracking the run.", Toast.LENGTH_SHORT).show();
                }
                return
            }
        }
    }

    private fun formatDuration(duration: Duration): String {
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        val seconds = duration.seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment)
        fragmentTransaction.commit()
    }

}