package com.zerodev.todo.ui.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.zerodev.todo.Data.NotifSounds
import com.zerodev.todo.R
import com.zerodev.todo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    val soundArray = arrayOf(
        NotifSounds("Default Notification Sound", "def"),
        NotifSounds("Goofy auhh sound (Tada)", "android.resource://${context?.packageName}/${R.raw.android_tada}"),
        NotifSounds("Ohio Alarm Sound" , "android.resource://${context?.packageName}/${R.raw.ohio_alarm}") ,
        NotifSounds("This is your Reminder" , "android.resource://${context?.packageName}/${R.raw.reminder_finish_task}") ,
        NotifSounds("Reminder beep" , "android.resource://${context?.packageName}/${R.raw.reminders}")
    )
    val notifImportance = arrayOf(
        "None",
        "Min",
        "Low",
        "Default",
        "High"
    )
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val settingsPref = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)
        if (settingsPref != null) {
            settingsViewModel.setSharedPref(settingsPref)
        }
        // set the sound title to the text view
        if (settingsPref != null) {
            binding.notifSoundtxt.text = " ${soundArray[settingsPref.getInt("notifSound" , 0)].title}"
        }
        // set the notif importance to textview
        if (settingsPref != null) {
            setNotifImportanceColors(settingsPref)
            binding.notifImportancetxt.text = " ${notifImportance[settingsPref.getInt("notifImportance" , 0)]}"
        }

        // notif sound click listener
        binding.notifSound.setOnClickListener {
            //set notification sound
            val soundTitles = soundArray.map { it.title }.toTypedArray()
            val builder = AlertDialog.Builder(context)
            .setTitle("Choose a Sound")
                .setItems(soundTitles) { _, index ->
                    val selectedSound = soundArray[index]
                    // Handle the item click
                    settingsPref?.edit()?.putInt("notifSound" , index)
                        ?.apply()
                    if (settingsPref != null) {
                        binding.notifSoundtxt.text = " ${soundArray[settingsPref.getInt("notifSound" , 0)].title}"
                    }
                    val snackbar = view?.let { it1 ->
                        Snackbar.make(it1,
                            "Selected ${selectedSound.title} ",
                            Snackbar.LENGTH_SHORT
                        )
                    }
                    snackbar?.show()
                }
                .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _ ->
                    // Handle cancel
                    dialogInterface.dismiss()
                }

            builder.create().show()
        }
        //  notifImportance on click
        binding.notifImportance.setOnClickListener {
            if (settingsPref != null) {
                AlertDialog.Builder(context)
                    .setTitle("Choose Notification Importance")
                    .setSingleChoiceItems(notifImportance, settingsPref.getInt("notifImportance" , 0)) { dialog, index ->
                        settingsPref?.edit()?.putInt("notifImportance" , index)
                            ?.apply()
                        val snackbar = view?.let { it1 ->
                            Snackbar.make(it1,
                                "Selected ${notifImportance[index]} ",
                                Snackbar.LENGTH_SHORT
                            )
                        }
                        snackbar?.show()
                        binding.notifImportancetxt.text = " ${notifImportance[settingsPref.getInt("notifImportance" , 0)]}"
                        setNotifImportanceColors(settingsPref)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }


        return root
    }
    fun setNotifImportanceColors(settingsPref : SharedPreferences) {
        when (settingsPref.getInt("notifImportance" , 0)) {
            0 -> binding.notifImportancetxt.setTextColor(Color.WHITE)
            1 , 2 -> binding.notifImportancetxt.setTextColor(Color.YELLOW)
            3 -> binding.notifImportancetxt.setTextColor(Color.GREEN)
            4 -> binding.notifImportancetxt.setTextColor(Color.RED)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}