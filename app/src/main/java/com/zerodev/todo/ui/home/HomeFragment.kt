package com.zerodev.todo.ui.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.zerodev.todo.R
import com.zerodev.todo.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.fab.setOnClickListener { view ->
            val dialogView = LayoutInflater.from(context).inflate(R.layout.new_item_dialog , null)

            val taskEditText = dialogView.findViewById<EditText>(R.id.taskEditText)
            val desc = dialogView.findViewById<EditText>(R.id.desc)
            val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
            val addButton = dialogView.findViewById<Button>(R.id.addButton)
            val pickDueDate = dialogView.findViewById<Button>(R.id.pickDueDate)
            // we remove this for now since it dosen't work
            pickDueDate.visibility = View.GONE
            val repeatSpinner = dialogView.findViewById<Spinner>(R.id.spinner)
            // we remove this for now since it dosen't work
            repeatSpinner.visibility = View.GONE
            val reminderCheckBox = dialogView.findViewById<CheckBox>(R.id.reminderCheckbox)
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            var pickedYear = 0
            var pickedMonth = 0
            var pickedDay = 0
            var pickedHour = 0
            var pickedMinute = 0
            var dueDateTimestamp = 0L
            var timestampReminder = 0L
            val dialog = context?.let { Dialog(it) }

            if (dialog != null) {
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(dialogView)
                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }
                pickDueDate.setOnClickListener {
                    val datePickerDialog = DatePickerDialog(
                        requireContext(),
                        { _, selectedYear, selectedMonth, selectedDay ->
                            val selectedCalendar = Calendar.getInstance()
                            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                            // Now you can use the timestamp
                            dueDateTimestamp = selectedCalendar.timeInMillis
                        },
                        year, month, day
                    )
                    datePickerDialog.show()
                }
                reminderCheckBox.setOnClickListener {
                    if (reminderCheckBox.isChecked) {
                        val datePickerDialog = DatePickerDialog(
                            requireContext(),
                            { _, selectedYear, selectedMonth, selectedDay ->
                                val selectedCalendar = Calendar.getInstance()
                                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
                                pickedYear = selectedYear
                                pickedMonth = selectedMonth + 1
                                pickedDay = selectedDay
                                Log.d("picked date" , "pickedDate : $pickedYear picked month : $pickedMonth picked day : $pickedDay")

                                // Dismiss the DatePickerDialog and show the TimePickerDialog here
                                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                val minute = calendar.get(Calendar.MINUTE)
                                val timePickerDialog = TimePickerDialog(
                                    requireContext(),
                                    { _, selectedHour, selectedMinute ->
                                        val selectedCalendar = Calendar.getInstance()
                                        selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                                        selectedCalendar.set(Calendar.MINUTE, selectedMinute)
                                        pickedHour = selectedHour
                                        pickedMinute = selectedMinute
                                        Log.d("setting hour" , "picked Hour : $pickedHour picked min : $selectedMinute" )
                                    },
                                    hour, minute, true
                                )
                                timePickerDialog.show()
                            },
                            year, month, day
                        )
                        datePickerDialog.show()

                    } else {
                         pickedYear = 0
                         pickedMonth = 0
                         pickedDay = 0
                         pickedHour = 0
                         pickedMinute = 0
                         dueDateTimestamp = 0L
                         timestampReminder = 0L
                    }
                }
                addButton.setOnClickListener {
                    //get reminder timestamp
                    if (reminderCheckBox.isChecked && pickedYear != 0 && pickedMonth != 0 && pickedDay != 0) {
                        val dateTime = "$pickedYear-$pickedMonth-$pickedDay $pickedHour:$pickedMinute"
                        Log.d("Date time" , "Time picked : $dateTime" )
                        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val date = simpleDateFormat.parse(dateTime)
                        timestampReminder = date?.time!!
                        Log.d("timestamp" , "time stamp reminder val $timestampReminder")
                    }

                    if (context?.let { it1 ->
                            homeViewModel.addTask(
                                it1,
                                binding.recyclerViewTasks,
                                taskEditText.text.toString(),
                                desc.text.toString(),
                                dueDateTimestamp,
                                repeatSpinner.selectedItemPosition,
                                timestampReminder
                            )
                        } == true) {
                        dialog.dismiss()
                        val snackbar = Snackbar.make(view, "Item Added", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    } else {
                        val snackbar = Snackbar.make(
                            view,
                            "couldn't add item , try again",
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.show()
                    }

                }
                val dialogHeight = context?.let { dpToPx(450, it) }
                val dialogWidth = context?.let { dpToPx(400, it) }
                if (dialogWidth != null && dialogHeight != null) {
                    dialog.window?.setLayout(dialogWidth, dialogHeight)
                }
                dialog.show()

            }

        }
        homeViewModel.getTasksToRecycler(requireContext(), binding.recyclerViewTasks)
        return binding.root
    }

    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}