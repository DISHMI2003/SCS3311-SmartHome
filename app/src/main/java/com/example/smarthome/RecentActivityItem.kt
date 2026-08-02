package com.example.smarthome

data class RecentActivityItem(
    val deviceName: String,
    val action: String,
    val time: String,
    val room: String,
    val icon: String,
    val statusType: String
)