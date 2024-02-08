package com.zerodev.todo.BugReport
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ReportBug {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://discordapp.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(DiscordApi::class.java)
    val OK_CLICKED  = 0
    val CANCEL_CLICKED = 1

    @SuppressLint("SuspiciousIndentation")
    fun reportBug(message : String, callback: (Boolean, String?) -> Unit) {
        val service = retrofit.create(DiscordApi::class.java)
        val webhookdata = WebhookData(message)
            service.sendMessage(
                "webhook ID",
                "Webhook token",
                webhookdata
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    callback(true, "Report Sent.")
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    callback(false, "Error : " + t.message)
                }
            })

    }
    fun showAppCrashedDialog(message: String , context: Context , callback: (Int) -> Unit) {
        val brand = Build.MANUFACTURER
        val model = Build.MODEL
        val androidVersion = Build.VERSION.RELEASE
        val versionName = getAPPVersion(context)
        val builder = AlertDialog.Builder(context)
        val log =
            "App Crash Log \nPhone Brand : $brand\nPhone Model : $model\nAndroid Version:$androidVersion\nApp Version :$versionName\n Error : \n$message"
        builder.setTitle("Error")
        builder.setMessage("An unexpected error occurred and caused the app to crash. \nWould you like to send us the error ? \nThis will help us understand and fix the problem.")
        builder.setCancelable(false)
        builder.setPositiveButton("Send Crash Log") { dialog, _ ->
            val report = ReportBug()
            report.reportBug(log) {success , response ->
                if (success) {
                    Toast.makeText(context, "Report sent successfully ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to send the report", Toast.LENGTH_SHORT).show()
                }
                val crashLog = context.getSharedPreferences("crashLog", Context.MODE_PRIVATE)
                val editor = crashLog?.edit()
                editor?.putBoolean("crashed", false)
                editor?.apply()
                callback(OK_CLICKED)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Don't send anything") {_ , _ ->
            val crashLog = context.getSharedPreferences("crashLog", Context.MODE_PRIVATE)
            val editor = crashLog?.edit()
            editor?.putBoolean("crashed", false)
            editor?.apply()
            callback(CANCEL_CLICKED)
        }
        builder.setNeutralButton("View Log" , null)
        val dialog = builder.create()
        dialog.setOnShowListener {
            val neutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            neutral.setOnClickListener {
                val showErr = AlertDialog.Builder(context)
                    .setTitle("Log")
                    .setMessage(log)
                    .setPositiveButton("ok" , null)
                    .setNeutralButton("Copy Error") {_ , _ ->
                        copyText(log , context)
                    }
                val showErrDialog = showErr.create()
                showErrDialog.show()
            }
        }
        dialog.show()
    }
    private fun getAPPVersion(context:Context) : String {
        val pkgManager = context.packageManager
        val pkgInfo = pkgManager.getPackageInfo(context.packageName, 0)
        return pkgInfo.versionName
    }
    private fun copyText(string : String , context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("todoApp Log", string)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context , "Copied to clipboard" , Toast.LENGTH_SHORT).show()
    }
}