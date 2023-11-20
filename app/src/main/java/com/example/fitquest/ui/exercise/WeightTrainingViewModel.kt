package com.example.fitquest.ui.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeightTrainingViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is weight training Fragment"
    }
    val text: LiveData<String> = _text
}