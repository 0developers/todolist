package com.zerodev.todo

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.NonDisposableHandle.parent

class TaskAdapter(private var tasks: MutableList<Task> , private val context: Context) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {


    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val checkBoxCompleted: CheckBox = itemView.findViewById(R.id.checkBoxCompleted)
        val deleteimg: ImageView = itemView.findViewById(R.id.deleteimg)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task , parent , false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.textViewTitle.text = task.title
        holder.checkBoxCompleted.isChecked = task.completed
        holder.checkBoxCompleted.setOnClickListener {
            task.completed = holder.checkBoxCompleted.isChecked
            refreshSharedPref()
            Toast.makeText(context , "item checked : " + task.completed , Toast.LENGTH_SHORT).show()
        }
        holder.deleteimg.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes") { _, _ ->
                    tasks.removeAt(position)
                    notifyItemRemoved(position)
                    refreshSharedPref()
                }
                .setNegativeButton("No", null)
            builder.create().show()

        }

    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    fun setTasks(newTasks: List<Task>) {
        tasks = newTasks.toMutableList()
        notifyDataSetChanged()
    }

    fun refreshSharedPref() {
        val tasksSharedPref = context.getSharedPreferences("task", Context.MODE_PRIVATE)
        val editor = tasksSharedPref.edit()
        val gson = Gson()
        val json = gson.toJson(tasks)
        editor.putString("tasks", json)
        editor.apply()

    }
}
