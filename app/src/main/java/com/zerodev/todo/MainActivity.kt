package com.zerodev.todo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.zerodev.todo.databinding.ActivityMainBinding
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                val settingsPref =
                    applicationContext?.getSharedPreferences("settings", Context.MODE_PRIVATE)
                if (settingsPref != null) {
                    if (!settingsPref.contains("notifSound") && !settingsPref.contains("notifImportance")) {
                        settingsPref.edit()
                            ?.putInt("notifSound", 0)
                            ?.putInt("notifImportance", 4)
                            ?.apply()
                    }
                }

                val intent = Intent()
                intent.setClass(applicationContext, ViewTasksActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()


    }
}