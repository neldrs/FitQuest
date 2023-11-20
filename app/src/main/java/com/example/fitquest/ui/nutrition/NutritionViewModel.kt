package com.example.fitquest.ui.nutrition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class NutritionViewModel(application: Application) : AndroidViewModel(application) {

    private val _totalCaloriesLiveData = MutableLiveData<Double>()
    val totalCaloriesLiveData: LiveData<Double> get() = _totalCaloriesLiveData

    val entriesLiveData: MutableLiveData<MutableList<RowEntry>> by lazy {
        MutableLiveData<MutableList<RowEntry>>()
    }
    public fun updateTotalCalories() {
        val totalCalories = entriesLiveData.value?.sumByDouble { it.textNumber } ?: 0.0
        _totalCaloriesLiveData.value = totalCalories
    }

    fun addEntry(entry: RowEntry) {
        val currentList = entriesLiveData.value ?: mutableListOf()
        currentList.add(entry)
        entriesLiveData.value = currentList
    }

    fun removeEntry(position: Int) {
        val currentList = entriesLiveData.value ?: mutableListOf()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            entriesLiveData.value = currentList
        }
    }
}