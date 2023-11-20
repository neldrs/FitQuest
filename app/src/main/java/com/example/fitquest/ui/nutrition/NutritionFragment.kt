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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitquest.R
import com.example.fitquest.databinding.FragmentNutritionBinding
import com.example.fitquest.ui.nutrition.RowEntry
import com.example.fitquest.ui.nutrition.RowEntryAdapter
import java.lang.Math.round

class NutritionFragment : Fragment() {

    private var _binding: FragmentNutritionBinding? = null
    private val binding get() = _binding!!

    // Initialize an empty list of RowEntry
    private val rowEntries: MutableList<RowEntry> = mutableListOf()

    // Initialize RowEntryAdapter with an empty list
    private val rowEntryAdapter: RowEntryAdapter by lazy {
        RowEntryAdapter(rowEntries)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNutritionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        Log.d("RecyclerView", "Item count: ${rowEntryAdapter.itemCount}")

        val editTextNumber1: EditText = binding.editTextNumber1
        val editTextNumber2: EditText = binding.editTextNumber2
        val progressBar: ProgressBar = binding.progressBar
        val buttonAddEntry: Button = binding.button4

        // Set the adapter for the RecyclerView
        val recyclerView: RecyclerView = binding.CalorieList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val rowEntryAdapter = RowEntryAdapter(mutableListOf())  // Use mutableListOf()
        recyclerView.adapter = rowEntryAdapter
        Log.d("RecyclerView", "Item count: ${rowEntryAdapter.itemCount}")

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
            // Add a sample entry when the button is clicked
            val newEntry = RowEntry("Sample String", 42.0)
            rowEntryAdapter.entries.add(newEntry)
            rowEntryAdapter.notifyDataSetChanged()
        }
        Log.d("RecyclerView", "Item count: ${rowEntryAdapter.itemCount}")

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
        waterIntakeText.text = "Daily Water Intake Goal: "+(round(result*100)).toString()+"%"
        progressBar.progress = progress
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

