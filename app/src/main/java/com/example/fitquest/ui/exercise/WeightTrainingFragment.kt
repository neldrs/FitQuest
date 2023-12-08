package com.example.fitquest.ui.exercise

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitquest.R
import com.example.fitquest.databinding.FragmentWeightTrainingBinding
import com.example.fitquest.ui.nutrition.OnItemClickListener
import androidx.lifecycle.Observer



class WeightTrainingFragment : Fragment() {

    private val WeightTrainingViewModel: WeightTrainingViewModel by activityViewModels()


    private var _binding: FragmentWeightTrainingBinding? = null
    private val binding get() = _binding!!


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


        //exerciseType.setOnFocusChangeListener(ClearTextOnFocus())
        //reps.setOnFocusChangeListener(ClearTextOnFocus())
        //sets.setOnFocusChangeListener(ClearTextOnFocus())
        //weight.setOnFocusChangeListener(ClearTextOnFocus())

        val recyclerView: RecyclerView = binding.exerciseList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ExerciseAdapter

        bEnter.setOnClickListener {

            // TODO: Process the entered data, perform actions as needed
            // For example, you might save this data or perform calculations

            val exerciseInput = exerciseType.text.toString()
            val repInput = reps.text.toString().toInt()
            val setInput = sets.text.toString().toInt()
            val weightInput = weight.text.toString().toInt()
            val newEntry = Exercise(exerciseInput, repInput, setInput, weightInput)
            WeightTrainingViewModel.addExercise(newEntry)


            exerciseType.text.clear()
            reps.text.clear()
            sets.text.clear()
            weight.text.clear()

        }

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


    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment_activity_main, fragment)
        fragmentTransaction.commit()
    }

}