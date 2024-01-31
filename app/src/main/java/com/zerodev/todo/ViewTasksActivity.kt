package com.zerodev.todo

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.zerodev.todo.databinding.ActivityViewTasksBinding
import kotlinx.coroutines.handleCoroutineException
import kotlin.system.exitProcess

class ViewTasksActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityViewTasksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val crashLog = getSharedPreferences("crashLog", Context.MODE_PRIVATE)
            val editor = crashLog?.edit()
            editor?.putString("Log", throwable.toString())
            editor?.putBoolean("crashed", true)
            editor?.apply()
            Thread.sleep(3000)
            exitProcess(10)

        }
        val crashLog = getSharedPreferences("crashLog", Context.MODE_PRIVATE)
        if (crashLog.contains("crashed")) {
            if (crashLog.getBoolean("crashed", false)) {
                val report = ReportBug()
                if (crashLog.getString("Log", "") != null) {
                    report.showAppCrashedDialog(crashLog.getString("Log", "")!!, this) { state ->
                        /* you can check which button of dialog is clicked like this in here
                        if (state = report.OK_CLICKED)
                        if (state = report.CANCEL_CLICKED)
                        for now there's only two states , ok clicked or cancel clicked
                         */
                    }
                }
            }
        }
        setSupportActionBar(binding.appBarViewTasks.toolbar)
        // ask user to disable battery Optimization
        // make sure they haven't disabled it first
        val pm: PowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            // check if they dont want this message to show again
            val settings = getSharedPreferences("settings", Context.MODE_PRIVATE)
            if (!settings.getBoolean("battOptimizeMsgIgnore", false)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Disable Battery Optimization")
                builder.setMessage("Please disable battery optimization for this app to show Reminder Notifications")
                builder.setPositiveButton("Disable") { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                builder.setNegativeButton("Cancel", null)
                builder.setNeutralButton("Don't show it again") { _, _ ->
                    val editor = settings?.edit()
                    editor?.putBoolean("battOptimizeMsgIgnore", true)
                    editor?.apply()
                }
                builder.show()


            }
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_view_tasks)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_settings, R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.view_tasks, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_view_tasks)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}