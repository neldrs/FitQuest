package com.example.fitquest.ui.nutrition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class NutritionViewModel(application: Application) : AndroidViewModel(application) {

    val entriesLiveData: MutableLiveData<MutableList<RowEntry>> by lazy {
        MutableLiveData<MutableList<RowEntry>>()
    }

    fun addEntry(entry: RowEntry) {
        val currentList = entriesLiveData.value ?: mutableListOf()
        currentList.add(entry)
        entriesLiveData.value = currentList
    }
}