package com.example.fitquest.ui.exercise

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitquest.R
import com.example.fitquest.databinding.FragmentExerciseBinding
import com.example.fitquest.databinding.FragmentWeightTrainingBinding
import com.example.fitquest.ui.nutrition.OnItemClickListener
import androidx.lifecycle.Observer
import com.example.fitquest.ui.nutrition.RowEntry


class WeightTrainingFragment : Fragment() {

    private val WeightTrainingViewModel: WeightTrainingViewModel by activityViewModels()

    //companion object {
        //fun newInstance() = WeightTrainingFragment()
    //}
    private var _binding: FragmentWeightTrainingBinding? = null
    private val binding get() = _binding!!

    //private lateinit var viewModel: WeightTrainingViewModel
    //private lateinit var exerciseTypeEditText: EditText
    //private lateinit var repsEditText: EditText
    //private lateinit var setsEditText: EditText
    //private lateinit var weightEditText: EditText
    //private lateinit var enterButton: Button

    val ExerciseAdapter = ExerciseAdapter(mutableListOf(), object : OnItemClickListener {
        override fun onDeleteClick(position: Int) {
            // Handle item deletion here
            WeightTrainingViewModel.removeExercise(position)
        }
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //return inflater.inflate(R.layout.fragment_weight_training, container, false)

        WeightTrainingViewModel.exerciseEntry.observe(viewLifecycleOwner, Observer { entries ->
            // Update your RecyclerView adapter with the new list of entries
            ExerciseAdapter.entries = entries.map { entry ->
                Exercise(entry.exerciseType, entry.sets, entry.reps, entry.weight)
            }.toMutableList()
            ExerciseAdapter.notifyDataSetChanged()

        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_weight_training, container, false)
        _binding = FragmentWeightTrainingBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val exerciseType: EditText = binding.exerciseType
        val reps: EditText = binding.reps
        val sets: EditText = binding.sets
        val weight: EditText = binding.weight
        val bEnter: Button = binding.bEnter
        val backButton: Button = binding.bBack

        backButton.setOnClickListener {
            replaceFragment(ExerciseFragment())
        }

        exerciseType.setOnFocusChangeListener(ClearTextOnFocus())
        reps.setOnFocusChangeListener(ClearTextOnFocus())
        sets.setOnFocusChangeListener(ClearTextOnFocus())
        weight.setOnFocusChangeListener(ClearTextOnFocus())

        val recyclerView: RecyclerView = binding.exerciseList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ExerciseAdapter

        bEnter.setOnClickListener {

            // TODO: Process the entered data, perform actions as needed
            // For example, you might save this data or perform calculations
            val builder = AlertDialog.Builder(requireContext())

            // Inflate the popup layout
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.weight_training_entry, null)
            builder.setView(dialogView)

            builder.setPositiveButton("Add") { _, _ ->
                val exerciseText = dialogView.findViewById<EditText>(R.id.exerciseText)
                val setText = dialogView.findViewById<EditText>(R.id.setText)
                val repText = dialogView.findViewById<EditText>(R.id.repText)
                val weightText = dialogView.findViewById<EditText>(R.id.weightText)

                val exerciseInput = exerciseText.text.toString()
                val repInput = repText.text.toString().toInt()
                val setInput = setText.text.toString().toInt()
                val weightInput = weightText.text.toString().toInt()


                if (exerciseInput.isNotBlank() && repInput != null &&
                    setInput != null && weightInput != null && weightInput != null) {
                    val newEntry = Exercise(exerciseInput, repInput, setInput, weightInput)
                    WeightTrainingViewModel.addExercise(newEntry)
                } else {

                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->

                dialog.dismiss()
            }

            builder.show()



            exerciseType.text.clear()
            reps.text.clear()
            sets.text.clear()
            weight.text.clear()
        }



        //val recyclerView: RecyclerView = binding.ExerciseList
        //recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //recyclerView.adapter = ExerciseAdapter()


        //exerciseTypeEditText = view.findViewById(R.id.exerciseType)
        //repsEditText = view.findViewById(R.id.reps)
        //setsEditText = view.findViewById(R.id.sets)
        //weightEditText = view.findViewById(R.id.weight)
        //enterButton = view.findViewById(R.id.bEnter)

        //val exerciseTypeEditText: EditText = binding.exerciseType
        //val repsEditText: EditText = binding.reps
        //val setsEditText: EditText = binding.sets

        /**
        val backButton: Button = view.findViewById(R.id.bBack)
        backButton.setOnClickListener {
        replaceFragment(ExerciseFragment())
        }

        exerciseTypeEditText.setOnFocusChangeListener(ClearTextOnFocus())
        repsEditText.setOnFocusChangeListener(ClearTextOnFocus())
        setsEditText.setOnFocusChangeListener(ClearTextOnFocus())
        weightEditText.setOnFocusChangeListener(ClearTextOnFocus())

        enterButton.setOnClickListener {

        val exerciseType = exerciseTypeEditText.text.toString()
        val reps = repsEditText.text.toString()
        val sets = setsEditText.text.toString()
        val weight = weightEditText.text.toString()

        // TODO: Process the entered data, perform actions as needed
        // For example, you might save this data or perform calculations
        val exercise = Exercise(exerciseType, reps, sets, weight)
        viewModel.addExercise(exercise)


        exerciseTypeEditText.text.clear()
        repsEditText.text.clear()
        setsEditText.text.clear()
        weightEditText.text.clear()
        }

        return view
         **/

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun onDeleteClick(position: Int) {
        // Handle item deletion here
        WeightTrainingViewModel.removeExercise(position)
    }

    inner class ClearTextOnFocus : View.OnFocusChangeListener {
        override fun onFocusChange(view: View?, hasFocus: Boolean) {
            if (hasFocus) {
                // Clear text when EditText gains focus
                (view as? EditText)?.text?.clear()
            }
        }
    }

/**

    inner class ClearTextOnFocus : View.OnFocusChangeListener {
        override fun onFocusChange(view: View?, hasFocus: Boolean) {
            if (hasFocus) {
                // Clear text when EditText gains focus
                (view as? EditText)?.text?.clear()
            }
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WeightTrainingViewModel::class.java)
        // TODO: Use the ViewModel
    }
**/
    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment)
        fragmentTransaction.commit()
    }

}