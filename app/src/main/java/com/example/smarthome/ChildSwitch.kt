package com.example.smarthome

data class ChildSwitch(
    val id: String,
    val name: String,
    var status: String = "OFF",
    val powerWatts: Int = 0
)