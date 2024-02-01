package com.zerodev.todo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.zerodev.todo.Data.NotifSounds
import java.util.UUID

class NotificationReceiver : BroadcastReceiver() {
    private val CHANNEL_ID = UUID.randomUUID().toString()
    override fun onReceive(context: Context, intent: Intent) {
        val settingsPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val notificationId = intent.getIntExtra("notificationId", 0)
        val taskTitle = intent.getStringExtra("taskTitle")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val deleteIntent = Intent(context, NotifDissmiss::class.java)
            deleteIntent.action = "notification_dismissed_action_$notificationId"
            deleteIntent.putExtra("notificationId", notificationId)
            val deletePendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            var notifImportance= NotificationManager.IMPORTANCE_NONE
            when (settingsPref.getInt("notifImportance" , 0)) {
            0 -> notifImportance = NotificationManager.IMPORTANCE_NONE
                1 -> notifImportance = NotificationManager.IMPORTANCE_MIN
                2 -> notifImportance = NotificationManager.IMPORTANCE_LOW
                3 -> notifImportance = NotificationManager.IMPORTANCE_DEFAULT
                4 -> notifImportance = NotificationManager.IMPORTANCE_HIGH
            }
            Log.d("notif" , "Notif importance is ==> $notifImportance")
            val notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                "todo",
                notifImportance
            )
            var notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            if (settingsPref.contains("notifSound") && settingsPref.getInt("notifSound" , 0) != 0) {
                // get the sound path
                val notifSound = NotifSounds(context)
                val soundPath = notifSound.soundArray[settingsPref.getInt("notifSound" , 0)].path
                notificationSoundUri = Uri.parse(soundPath)
                Log.d("notif" , "Custom sound path set ==> $soundPath ")
            }
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "You got a task todo"
            notificationChannel.setSound(notificationSoundUri , audioAttributes)
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
            val messageBody = "You got a task to do : $taskTitle"
            val contentIntent = Intent(context, MainActivity::class.java)
            val contentPendingIntent = PendingIntent.getActivity(
                context,
                0,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.baseline_home_24)
                .setContentTitle("Task Reminder")
                .setContentText(messageBody)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setDeleteIntent(deletePendingIntent)

            notificationManager.notify(notificationId, builder.build())
        }
    }
}
