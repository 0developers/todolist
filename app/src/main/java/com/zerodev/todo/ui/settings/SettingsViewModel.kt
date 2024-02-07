package com.zerodev.todo.ui.settings

import android.content.SharedPreferences
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    private lateinit var settingsPref: SharedPreferences
    fun setSharedPref(sharedpf : SharedPreferences) {
         settingsPref = sharedpf
    }

    private val _text = MutableLiveData<String>().apply {
        /*
        var handler = android.os.Handler(Looper.getMainLooper())
        handler.postDelayed({

        }, 1000)

         */
    }

    val text: LiveData<String> = _text

}