package com.zerodev.todo

data class Task(val title: String,
                val description : String ,
                val createdDate : Long ,
                val dueDate : Long ,
                val repeat : Int ,
                val reminderDate : Long ,
                var completed: Boolean ,
                var completedDate : Long
    )

