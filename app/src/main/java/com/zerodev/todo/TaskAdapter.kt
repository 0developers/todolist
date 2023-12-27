package com.zerodev.todo

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class TaskAdapter(private var tasks: MutableList<Task> , private val context: Context) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {


    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val checkBoxCompleted: CheckBox = itemView.findViewById(R.id.checkBoxCompleted)
        val moreimg: ImageView = itemView.findViewById(R.id.moreimg)
        val textViewFinished : TextView = itemView.findViewById(R.id.textViewFinished)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task , parent , false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.textViewTitle.text = task.title
        holder.checkBoxCompleted.isChecked = task.completed
        if (task.completed) {
            holder.textViewTitle.paintFlags = holder.textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textViewFinished.visibility = View.VISIBLE
        } else {
            holder.textViewTitle.paintFlags = holder.textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.textViewFinished.visibility = View.GONE
        }
        holder.checkBoxCompleted.setOnClickListener {
            task.completed = holder.checkBoxCompleted.isChecked
            refreshSharedPref()
            notifyItemChanged(position)
        }
        holder.moreimg.setOnClickListener {
                    val popupMenu = PopupMenu(context, holder.moreimg)
                    popupMenu.menuInflater.inflate(R.menu.menu_recycler, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.detail -> {




                             true
                            }
                            R.id.remitem -> {

                                //del item
                                val alertDialog = AlertDialog.Builder(context)
                                alertDialog.setTitle("Delete item")
                                alertDialog.setMessage("Are you sure you want to delete this item?")
                                alertDialog.setPositiveButton("Yes") { _, _ ->
                                    refreshSharedPref()
                                    try {
                                        tasks.removeAt(position)
                                        notifyItemRemoved(position)
                                        notifyDataSetChanged()
                                    } catch (e: Exception) {
                                        val alertdialog = AlertDialog.Builder(context)
                                        alertdialog.setTitle("Error while deleting the item ")
                                        alertdialog.setMessage("Error :$e \nWould you like to report it so we can fix it ?")
                                            .setPositiveButton("Report the Error", null)
                                            .setNegativeButton("No", null)
                                        alertdialog.create().show()
                                    }
                                }
                                alertDialog.setNegativeButton("No", null)
                                alertDialog.show()

                                true
                            }
                            else -> false
                        }
                    }

                    popupMenu.show()
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
