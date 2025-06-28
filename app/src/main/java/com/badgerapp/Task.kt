package com.badger

data class Task(
    val id: Int,            //used to reference the created task
    var name: String,       //name of the task chosen by user
    var dayOfWeek: Int,     //Calendar.SUNDAY=1 … Calendar.SATURDAY=7; TODO - change logic so MONDAY will be 1
    var hour: Int,          //to track when to ask
    var minute: Int,        //to track when to ask
    var nextAskEpoch: Long  //to track when to ask again
)
