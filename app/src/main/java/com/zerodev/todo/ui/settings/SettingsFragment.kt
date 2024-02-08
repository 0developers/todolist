package com.zerodev.todo.ui.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.anggrayudi.storage.SimpleStorageHelper
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.zerodev.todo.Data.NotifSounds
import com.zerodev.todo.databinding.FragmentSettingsBinding
import com.zerodev.todo.signupActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private var storageHelper: SimpleStorageHelper? = null
    private val binding get() = _binding!!
    private val notifImportance = arrayOf(
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
        val notifSounds = context?.let { NotifSounds(it) }
        val notifSoundArray = notifSounds?.soundArray
        val intent = Intent(this.requireContext(), signupActivity::class.java)
        startActivity(intent)
        if (settingsPref != null) {
            settingsViewModel.setSharedPref(settingsPref)
        }
        // set the sound title to the text view
        if (settingsPref != null) {
            binding.notifSoundtxt.text =
                " ${notifSoundArray?.get(settingsPref.getInt("notifSound", 0))?.title}"
        }
        // set the notif importance to textview
        if (settingsPref != null) {
            setNotifImportanceColors(settingsPref)
            binding.notifImportancetxt.text =
                " ${notifImportance[settingsPref.getInt("notifImportance", 0)]}"
        }
        // backup list click


        // notif sound click listener
        binding.notifSound.setOnClickListener {
            //set notification sound
            val soundTitles = notifSoundArray?.map { it.title }?.toTypedArray()
            val builder = AlertDialog.Builder(context)
                .setTitle("Choose a Sound")
                .setItems(soundTitles) { _, index ->
                    val selectedSound = notifSounds?.soundArray?.get(index)
                    // Handle the item click
                    settingsPref?.edit()?.putInt("notifSound", index)
                        ?.apply()
                    if (settingsPref != null) {
                        binding.notifSoundtxt.text =
                            " ${notifSoundArray?.get(settingsPref.getInt("notifSound", 0))?.title}"
                    }
                    val snackbar = view?.let { it1 ->
                        Snackbar.make(
                            it1,
                            "Selected ${selectedSound?.title} ",
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
                    .setSingleChoiceItems(
                        notifImportance,
                        settingsPref.getInt("notifImportance", 0)
                    ) { dialog, index ->
                        settingsPref.edit()?.putInt("notifImportance", index)
                            ?.apply()
                        val snackbar = view?.let { it1 ->
                            Snackbar.make(
                                it1,
                                "Selected ${notifImportance[index]} ",
                                Snackbar.LENGTH_SHORT
                            )
                        }
                        snackbar?.show()
                        binding.notifImportancetxt.text =
                            " ${notifImportance[settingsPref.getInt("notifImportance", 0)]}"
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

    fun setNotifImportanceColors(settingsPref: SharedPreferences) {
        when (settingsPref.getInt("notifImportance", 0)) {
            0 -> binding.notifImportancetxt.setTextColor(Color.WHITE)
            1, 2 -> binding.notifImportancetxt.setTextColor(Color.YELLOW)
            3 -> binding.notifImportancetxt.setTextColor(Color.GREEN)
            4 -> binding.notifImportancetxt.setTextColor(Color.RED)
        }
    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}