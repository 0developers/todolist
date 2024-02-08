package com.zerodev.todo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date


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

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        // apply colors to text view
        val applyColor = ApplyColorTextView()
        applyColor.applyColorToTextView(task.title , holder.textViewTitle)
        // check the checkbox if task is completed ...
        holder.checkBoxCompleted.isChecked = task.completed
        if (task.completed) {
            holder.textViewTitle.paintFlags =
                holder.textViewTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textViewFinished.visibility = View.VISIBLE
            val date = Date(task.completedDate)
            var sdf = SimpleDateFormat("MM-dd HH:mm")
            // if the task was done in the same day , just show the hour and minutes
            if (isToday(task.completedDate))
                sdf = SimpleDateFormat("HH:mm")

            val finisehdDate = sdf.format(date)
            holder.textViewFinished.text = "Task Finished in $finisehdDate"
        } else {
            holder.textViewTitle.paintFlags = holder.textViewTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.textViewFinished.visibility = View.GONE
        }
        // if checkbox clicked , save the status
        holder.checkBoxCompleted.setOnClickListener {
            task.completed = holder.checkBoxCompleted.isChecked
            task.completedDate = System.currentTimeMillis()
            refreshSharedPref()
            notifyItemChanged(position)
        }
        if (isDarkMode(context))
            holder.moreimg.setColorFilter(Color.WHITE , PorterDuff.Mode.MULTIPLY)
        else
            holder.moreimg.setColorFilter(Color.BLACK , PorterDuff.Mode.MULTIPLY)
        // more details
        //TODO : Add a edit dialog in the next update
        holder.moreimg.setOnClickListener {
                    val popupMenu = PopupMenu(context, holder.moreimg)
                    popupMenu.menuInflater.inflate(R.menu.menu_recycler, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.detail -> {
                                val date = Date(task.createdDate)
                                val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
                                val sdfNoTime = SimpleDateFormat("yyyy/MM/dd")
                                val createdDate = sdf.format(date)
                                val desc = task.description
                                val dateEnded = sdf.format(Date(task.completedDate))
                                val dateReminder = sdf.format(Date(task.reminderDate))
                                val dateDue = sdfNoTime.format(Date(task.dueDate))
                                val dateDueSet = task.dueDate != 0L
                                val message = """
   Task Description : $desc
   Task Created Date : $createdDate
   Task Finished Date : ${if (task.completed) dateEnded else "Not completed"}
   Reminder Time : ${if (task.reminderDate != 0L) dateReminder else "Not set"}
   Due Date : ${if (dateDueSet) dateDue else "Not set"}
""".trimIndent()

                                val detailDialog = AlertDialog.Builder(context)
                                    .setTitle(applyColor.parseAndApplyColor(task.title))
                                    .setMessage(message)
                                    .setPositiveButton("ok" , null)

                                val dialog: AlertDialog = detailDialog.create()
                                dialog.show()
                             true
                            }
                            R.id.remitem -> {

                                //del item
                                val alertDialog = AlertDialog.Builder(context)
                                alertDialog.setTitle("Delete item")
                                alertDialog.setMessage("Are you sure you want to delete this item?")
                                alertDialog.setPositiveButton("Yes") { _, _ ->
                                        tasks.removeAt(position)
                                        refreshSharedPref()
                                        notifyItemRemoved(position)
                                        notifyDataSetChanged()
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
    fun isToday(timestamp: Long): Boolean {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val finisehdDate = sdf.format(date)

        val currentTimestamp = System.currentTimeMillis()
        val dateToday = Date(currentTimestamp)
        val today = sdf.format(dateToday)

        return finisehdDate == today
    }
    fun isDarkMode(context: Context): Boolean {
        val darkModeFlag = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
    }



}
