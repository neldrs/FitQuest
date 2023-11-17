package com.example.fitquest.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsViewModel : ViewModel() {
    private lateinit var auth: FirebaseAuth

    private val _text = MutableLiveData<String>().apply {
        auth = Firebase.auth
        //value = "This is settings Fragment"
        value = auth.currentUser.toString()
    }
    val text: LiveData<String> = _text
}