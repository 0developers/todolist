package com.zerodev.todo.Data

import android.content.Context
import com.zerodev.todo.R

class NotifSounds (context : Context) {
    val soundArray = arrayOf(
        NotifSoundData("Default Notification Sound", "def"),
        NotifSoundData("Goofy auhh sound (Tada)", "android.resource://${context.packageName}/${R.raw.android_tada}"),
        NotifSoundData("Ohio Alarm Sound" , "android.resource://${context.packageName}/${R.raw.ohio_alarm}") ,
        NotifSoundData("This is your Reminder" , "android.resource://${context.packageName}/${R.raw.reminder_finish_task}") ,
        NotifSoundData("Reminder beep" , "android.resource://${context.packageName}/${R.raw.reminders}")
    )
}