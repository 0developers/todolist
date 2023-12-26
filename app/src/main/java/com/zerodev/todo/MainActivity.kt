package com.zerodev.todo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zerodev.todo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var tasks = mutableListOf<Task>()
    private val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tasksSharedPref = getSharedPreferences("task", Context.MODE_PRIVATE)
        if (tasksSharedPref.contains("tasks")) {
            val json = tasksSharedPref.getString("tasks", "")
            tasks = gson.fromJson(json, object : TypeToken<MutableList<Task>>() {}.type)
        }
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTasks.adapter = TaskAdapter(tasks , this)

        binding.buttonAddTask.setOnClickListener {
            // Create a new task
            val newTask = Task(binding.editTextTask.text.toString(), false)
            tasks.add(newTask)
            val adapter = TaskAdapter(tasks , this)
            binding.recyclerViewTasks.adapter = adapter
            val editor = tasksSharedPref.edit()
            val json = gson.toJson(tasks)
            editor.putString("tasks" , json)
            editor.apply()
        }


    }
}