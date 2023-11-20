package com.example.fitquest.ui.nutrition

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitquest.R
import com.example.fitquest.databinding.FragmentNutritionBinding
import com.example.fitquest.ui.nutrition.RowEntry
import com.example.fitquest.ui.nutrition.RowEntryAdapter
import java.lang.Math.round
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.fitquest.ui.nutrition.NutritionViewModel


class NutritionFragment : Fragment() {

    private val nutritionViewModel: NutritionViewModel by activityViewModels()


    private var _binding: FragmentNutritionBinding? = null
    private val binding get() = _binding!!

    // Initialize RowEntryAdapter with an empty list
    private val rowEntryAdapter: RowEntryAdapter by lazy {
        RowEntryAdapter(mutableListOf())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe changes in the ViewModel
        nutritionViewModel.entriesLiveData.observe(viewLifecycleOwner, Observer { entries ->
            // Update your RecyclerView adapter with the new list of entries
            rowEntryAdapter.entries = entries.map { entry ->
                RowEntry(entry.textString, entry.textNumber)
            }.toMutableList()
            rowEntryAdapter.notifyDataSetChanged()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNutritionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val editTextNumber1: EditText = binding.editTextNumber1
        val editTextNumber2: EditText = binding.editTextNumber2
        val progressBar: ProgressBar = binding.progressBar
        val buttonAddEntry: Button = binding.button4

        // Set the adapter for the RecyclerView
        val recyclerView: RecyclerView = binding.CalorieList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = rowEntryAdapter

        editTextNumber2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for this example
            }

            override fun afterTextChanged(s: Editable?) {
                // Update result and progress when text is entered into editTextNumber2
                updateResult(progressBar)
            }
        })

        buttonAddEntry.setOnClickListener {
            // Create an AlertDialog builder
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Add Entry")

            // Inflate the popup layout
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.popup_layout, null)
            builder.setView(dialogView)

            // Set up the buttons and listeners
            builder.setPositiveButton("Add") { _, _ ->
                // Handle positive button click (Add Entry)
                val editTextString = dialogView.findViewById<EditText>(R.id.editTextString)
                val editTextNumber = dialogView.findViewById<EditText>(R.id.editTextNumber)

                // Get user input from the EditTexts
                val inputString = editTextString.text.toString()
                val inputNumber = editTextNumber.text.toString().toDoubleOrNull()

                // Check if both inputs are valid
                if (inputString.isNotBlank() && inputNumber != null) {
                    // Add a new entry to the ViewModel
                    val newEntry = RowEntry(inputString, inputNumber)
                    nutritionViewModel.addEntry(newEntry)
                } else {
                    // Show a toast or handle invalid input
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                // Handle negative button click (Cancel)
                dialog.dismiss()
            }

            // Show the AlertDialog
            builder.show()
        }

        return root
    }

    private fun updateResult(progressBar: ProgressBar) {
        val editTextNumber1: EditText = binding.editTextNumber1
        val editTextNumber2: EditText = binding.editTextNumber2

        val number1 = editTextNumber1.text.toString().toFloatOrNull() ?: 0f
        val number2 = editTextNumber2.text.toString().toFloatOrNull() ?: 0f

        val waterIntakeText: TextView = binding.textView4

        val result = if (number1 != 0f) {
            number2 / (number1 / 2)
        } else {
            0f
        }

        // Update the ProgressBar's progress based on the ratio
        val ratio = (result).coerceIn(0f, 1f) // Adjust as needed
        val progress = (ratio * 100).toInt()
        // Update the water intake text based on the entered value
        waterIntakeText.text = "Daily Water Intake Goal: ${(round(result * 100))}%"
        progressBar.progress = progress
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

