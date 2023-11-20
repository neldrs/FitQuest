package com.example.fitquest.ui.nutrition

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button  // Ensure that this import statement is present
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fitquest.databinding.FragmentNutritionBinding
import androidx.lifecycle.ViewModelProvider
import android.widget.ProgressBar


class NutritionFragment : Fragment() {

    private var _binding: FragmentNutritionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        return root
    }

    private fun updateResult(progressBar: ProgressBar) {
        val editTextNumber1: EditText = binding.editTextNumber1
        val editTextNumber2: EditText = binding.editTextNumber2

        val number1 = editTextNumber1.text.toString().toFloatOrNull() ?: 0f
        val number2 = editTextNumber2.text.toString().toFloatOrNull() ?: 0f

        val result = if (number1 != 0f) {
            number2 / (number1 / 2)
        } else {
            0f
        }

        // Update the ProgressBar's progress based on the ratio
        val ratio = (result).coerceIn(0f, 1f) // Adjust as needed
        val progress = (ratio * 100).toInt()
        progressBar.progress = progress
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
