package com.example.smarthome.model

data class Device(

    val id: String = "",

    val name: String = "",

    val type: String = "",

    val status: String = "",

    val room: String = "",

    val maxOnDuration: Long = 0,

    val autoOff: Boolean = false

)