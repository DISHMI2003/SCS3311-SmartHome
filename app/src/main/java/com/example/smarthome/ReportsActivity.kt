package com.example.smarthome

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReportsActivity : AppCompatActivity() {

    private lateinit var btnReportsBack: TextView
    private lateinit var tvTodayEnergy: TextView
    private lateinit var tvMostUsedDevice: TextView
    private lateinit var tvSafetyActionCount: TextView
    private lateinit var deviceUsageContainer: LinearLayout

    private val usageItems = mutableListOf<DeviceUsage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_reports)

        initializeViews()
        loadTemporaryReportData()
        displayUsageItems()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnReportsBack = findViewById(R.id.btnReportsBack)
        tvTodayEnergy = findViewById(R.id.tvTodayEnergy)
        tvMostUsedDevice = findViewById(R.id.tvMostUsedDevice)
        tvSafetyActionCount =
            findViewById(R.id.tvSafetyActionCount)

        deviceUsageContainer =
            findViewById(R.id.deviceUsageContainer)
    }

    private fun setupClickListeners() {
        btnReportsBack.setOnClickListener {
            finish()
        }
    }

    private fun loadTemporaryReportData() {
        tvTodayEnergy.text = "3.8 kWh"
        tvMostUsedDevice.text = "Bedroom Fan"
        tvSafetyActionCount.text = "2"

        usageItems.clear()

        usageItems.addAll(
            listOf(
                DeviceUsage(
                    name = "Kitchen Light",
                    room = "Kitchen",
                    icon = "L",
                    status = "ACTIVE",
                    labelOne = "ON TIME",
                    valueOne = "4 hours",
                    labelTwo = "ENERGY",
                    valueTwo = "0.08 kWh"
                ),
                DeviceUsage(
                    name = "Bedroom Fan",
                    room = "Bedroom",
                    icon = "F",
                    status = "MOST USED",
                    labelOne = "ON TIME",
                    valueOne = "6 hours",
                    labelTwo = "ENERGY",
                    valueTwo = "0.42 kWh"
                ),
                DeviceUsage(
                    name = "Kitchen Iron",
                    room = "Kitchen",
                    icon = "I",
                    status = "SAFETY",
                    labelOne = "ON TIME",
                    valueOne = "25 minutes",
                    labelTwo = "AUTO OFF",
                    valueTwo = "2 times"
                ),
                DeviceUsage(
                    name = "Garage Camera",
                    room = "Garage",
                    icon = "C",
                    status = "CONNECTED",
                    labelOne = "CONNECTED TIME",
                    valueOne = "10 hours",
                    labelTwo = "SNAPSHOTS",
                    valueTwo = "12"
                )
            )
        )
    }

    private fun displayUsageItems() {
        deviceUsageContainer.removeAllViews()

        usageItems.forEach { usage ->
            addUsageItem(usage)
        }
    }

    private fun addUsageItem(usage: DeviceUsage) {
        val itemView = LayoutInflater.from(this).inflate(
            R.layout.item_device_usage,
            deviceUsageContainer,
            false
        )

        val tvUsageIcon =
            itemView.findViewById<TextView>(R.id.tvUsageIcon)

        val tvUsageDeviceName =
            itemView.findViewById<TextView>(
                R.id.tvUsageDeviceName
            )

        val tvUsageRoom =
            itemView.findViewById<TextView>(R.id.tvUsageRoom)

        val tvUsageStatus =
            itemView.findViewById<TextView>(R.id.tvUsageStatus)

        val tvUsageLabelOne =
            itemView.findViewById<TextView>(
                R.id.tvUsageLabelOne
            )

        val tvUsageValueOne =
            itemView.findViewById<TextView>(
                R.id.tvUsageValueOne
            )

        val tvUsageLabelTwo =
            itemView.findViewById<TextView>(
                R.id.tvUsageLabelTwo
            )

        val tvUsageValueTwo =
            itemView.findViewById<TextView>(
                R.id.tvUsageValueTwo
            )

        tvUsageIcon.text = usage.icon
        tvUsageDeviceName.text = usage.name
        tvUsageRoom.text = usage.room
        tvUsageStatus.text = usage.status
        tvUsageLabelOne.text = usage.labelOne
        tvUsageValueOne.text = usage.valueOne
        tvUsageLabelTwo.text = usage.labelTwo
        tvUsageValueTwo.text = usage.valueTwo

        setStatusColor(
            status = usage.status,
            statusView = tvUsageStatus
        )

        deviceUsageContainer.addView(itemView)
    }

    private fun setStatusColor(
        status: String,
        statusView: TextView
    ) {
        val color = when (status) {
            "ACTIVE" -> "#70E5A4"
            "MOST USED" -> "#6FE4F2"
            "SAFETY" -> "#FFD36F"
            "CONNECTED" -> "#C6BEFF"
            else -> "#A8B2C4"
        }

        statusView.setTextColor(
            Color.parseColor(color)
        )
    }
}