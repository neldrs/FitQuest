package com.example.fitquest.ui.exercise

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.fitquest.R

class WeightTrainingFragment : Fragment() {

    companion object {
        fun newInstance() = WeightTrainingFragment()
    }

    private lateinit var viewModel: WeightTrainingViewModel
    private lateinit var exerciseTypeEditText: EditText
    private lateinit var repsEditText: EditText
    private lateinit var setsEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var enterButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.fragment_weight_training, container, false)
        val view = inflater.inflate(R.layout.fragment_weight_training, container, false)

        exerciseTypeEditText = view.findViewById(R.id.exerciseType)
        repsEditText = view.findViewById(R.id.reps)
        setsEditText = view.findViewById(R.id.sets)
        weightEditText = view.findViewById(R.id.weight)
        enterButton = view.findViewById(R.id.bEnter)

        enterButton.setOnClickListener {
            // Retrieve input values from EditText fields
            val exerciseType = exerciseTypeEditText.text.toString()
            val reps = repsEditText.text.toString()
            val sets = setsEditText.text.toString()
            val weight = weightEditText.text.toString()

            // TODO: Process the entered data, perform actions as needed
            // For example, you might save this data or perform calculations

            // Clear EditText fields after processing
            exerciseTypeEditText.text.clear()
            repsEditText.text.clear()
            setsEditText.text.clear()
            weightEditText.text.clear()
        }

        return view

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeightTrainingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}