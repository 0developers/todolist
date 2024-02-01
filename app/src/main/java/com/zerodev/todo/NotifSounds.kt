package com.zerodev.todo

import android.content.Context
import com.zerodev.todo.Data.NotifSounds

class NotifSounds (context : Context) {
    val soundArray = arrayOf(
        NotifSounds("Default Notification Sound", "def"),
        NotifSounds("Goofy auhh sound (Tada)", "android.resource://${context.packageName}/${R.raw.android_tada}"),
        NotifSounds("Ohio Alarm Sound" , "android.resource://${context.packageName}/${R.raw.ohio_alarm}") ,
        NotifSounds("This is your Reminder" , "android.resource://${context.packageName}/${R.raw.reminder_finish_task}") ,
        NotifSounds("Reminder beep" , "android.resource://${context.packageName}/${R.raw.reminders}")
    )
}