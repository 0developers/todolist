package com.zerodev.todo.ui.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zerodev.todo.Notifications.NotificationReceiver
import com.zerodev.todo.Task
import com.zerodev.todo.TaskAdapter

class HomeViewModel : ViewModel() {

    private var tasks = mutableListOf<Task>()
    private var isTaskListEmpty = true
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun getTasksToRecycler(context: Context, recycler: RecyclerView) {
        val tasksSharedPref = context.getSharedPreferences("task", Context.MODE_PRIVATE)
        val gson = Gson()
        if (tasksSharedPref.getString("tasks", "") != "") {
            val json = tasksSharedPref.getString("tasks", "")
            tasks = gson.fromJson(json, object : TypeToken<MutableList<Task>>() {}.type)
        }
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = TaskAdapter(tasks, context)


    }

    fun isTaskListEmpty(): Boolean {
        return isTaskListEmpty
    }

    fun addTask(
        context: Context,
        recycler: RecyclerView,
        taskTitle: String,
        taskDesc: String,
        taskDueDate: Long,
        taskRepeat: Int,
        taskReminderDate: Long
    ): Boolean {
        val createdDate = System.currentTimeMillis()
        val newTask = Task(
            taskTitle,
            taskDesc,
            createdDate,
            taskDueDate,
            taskRepeat,
            taskReminderDate,
            false,
            0
        )
        Toast.makeText(context, "Reminder => $taskReminderDate", Toast.LENGTH_SHORT).show()
        if (taskReminderDate != 0L) {
            scheduleNotification(context, taskReminderDate, 1, taskTitle)
        }
        val gson = Gson()
        tasks.add(newTask)
        val adapter = TaskAdapter(tasks, context)
        recycler.adapter = adapter
        val tasksSharedPref = context.getSharedPreferences("task", Context.MODE_PRIVATE)
        val editor = tasksSharedPref?.edit()
        val json = gson.toJson(tasks)
        if (editor != null) {
            editor.putString("tasks", json)
            editor.apply()
            return true
        } else {
            return false
        }
    }

    fun scheduleNotification(
        context: Context,
        timestamp: Long,
        notificationId: Int,
        taskTitle: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).putExtra(
            "notificationId",
            notificationId
        ).putExtra("taskTitle", taskTitle)

        val pendingIntentFlags =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, pendingIntentFlags)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.w("AlarmScheduler", "App cannot schedule exact alarms")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent)
    }

}





