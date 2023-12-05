package com.example.fitquest.ui.exercise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var runRecyclerView: RecyclerView
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

        checkPermissionsAndStartLocationUpdates()

        val backButton: Button = view.findViewById(R.id.bBack)
        backButton.setOnClickListener {
            replaceFragment(ExerciseFragment())
        }
        viewModel.elapsedTime.observe(viewLifecycleOwner) { time ->
            tvRunTime.text = "$time"
        }

        btnStartRun.setOnClickListener { view: View? ->
            context?.let { ctx ->
                viewModel.startRun(ctx)
                tvRunStatus.text = "Run Status: In Progress"
                tvRunTime.text = "00:00:00"
                tvRunDistance.text = "0.00 miles"
            }
        }

        btnEndRun.setOnClickListener { view: View? ->
            val runRecord = RunRecord(
                distance = viewModel.totalDistance.value ?: 0f,
                time = tvRunTime.text.toString(),
                date = getCurrentDate()
            )
            viewModel.saveRunRecordToFirestore(runRecord)
            viewModel.endRun(requireContext())
            tvRunStatus.text = "Run Status: Finished"
        }
        runRecyclerView = view.findViewById(R.id.recyclerViewRuns)
        runRecyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.runHistory.observe(viewLifecycleOwner) { runList ->
            if (runRecyclerView.adapter == null) {
                runRecyclerView.adapter = RVRunRecordAdapter(runList)
            } else {
                (runRecyclerView.adapter as RVRunRecordAdapter).updateData(runList)
            }
        }

        viewModel.loadRunHistory()

        return view
    }
    private fun checkPermissionsAndStartLocationUpdates() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (!hasFineLocationPermission) {
            if (shouldShowRequestPermissionRationale) {
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            viewModel.startLocationUpdates()
        }
    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment)
        fragmentTransaction.commit()
    }

    private val locationUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.hasExtra("distance")) {
                val distance = intent.getFloatExtra("distance", 0f)
                tvRunDistance.text = formatDistance(distance)
            }
            val time = intent.getStringExtra("time")
            tvRunTime.text = time
        }
    }

    private fun formatDistance(distanceInMeters: Float): String {
        val distanceInMiles = distanceInMeters * 0.000621371
        return String.format("%.2f miles", distanceInMiles)
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            locationUpdateReceiver, IntentFilter("LOCATION_UPDATE")
        )
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(locationUpdateReceiver)
        super.onStop()
    }

}