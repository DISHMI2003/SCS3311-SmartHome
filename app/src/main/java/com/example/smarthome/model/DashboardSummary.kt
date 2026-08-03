package com.example.smarthome.model

data class DashboardSummary(

    val name: String = "",

    val devicesOn: Int = 0,

    val onlineDevices: Int = 0,

    val alerts: Int = 0,

    val energyUsage: Double = 0.0

)