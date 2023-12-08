package com.example.fitquest.ui.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitquest.ui.nutrition.RowEntry

class WeightTrainingViewModel : ViewModel() {
    //private val _text = MutableLiveData<String>().apply {
        //value = "This is weight training Fragment"
    //}
    //val text: LiveData<String> = _text

    val exerciseEntry: MutableLiveData<MutableList<Exercise>> by lazy {
        MutableLiveData<MutableList<Exercise>>()
    }

    // Function to add an exercise to the list
    fun addExercise(exercise: Exercise) {
        val currentList = exerciseEntry.value.orEmpty().toMutableList()
        currentList.add(exercise)
        exerciseEntry.value = currentList
    }

    // function to remove exercise from the list
    fun removeExercise(position: Int) {
        val currentList = exerciseEntry.value ?: mutableListOf()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            exerciseEntry.value = currentList
        }
    }
}