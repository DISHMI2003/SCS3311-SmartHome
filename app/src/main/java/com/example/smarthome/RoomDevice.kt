package com.example.smarthome

data class RoomDevice(
    val id: String,
    val name: String,
    val type: String,
    var status: String = "OFF",
    val powerWatts: Int = 0,
    val primaryAction: String? = null,
    val secondaryAction: String? = null
)