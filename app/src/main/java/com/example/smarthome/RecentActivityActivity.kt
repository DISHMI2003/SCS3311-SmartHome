package com.example.smarthome

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RecentActivityActivity : AppCompatActivity() {

    private lateinit var btnRecentActivityBack: TextView
    private lateinit var tvActivityCount: TextView
    private lateinit var recentActivityListContainer: LinearLayout
    private lateinit var tvNoRecentActivities: TextView

    private val recentActivities =
        mutableListOf<RecentActivityItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_recent_activity)

        initializeViews()
        loadTemporaryActivities()
        displayActivities()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnRecentActivityBack =
            findViewById(R.id.btnRecentActivityBack)

        tvActivityCount =
            findViewById(R.id.tvActivityCount)

        recentActivityListContainer =
            findViewById(R.id.recentActivityListContainer)

        tvNoRecentActivities =
            findViewById(R.id.tvNoRecentActivities)
    }

    private fun setupClickListeners() {
        btnRecentActivityBack.setOnClickListener {
            finish()
        }
    }

    private fun loadTemporaryActivities() {
        recentActivities.clear()

        recentActivities.addAll(
            listOf(
                RecentActivityItem(
                    deviceName = "Kitchen Light",
                    action = "Turned ON",
                    time = "10:15 PM",
                    room = "Kitchen",
                    icon = "L",
                    statusType = "ON"
                ),
                RecentActivityItem(
                    deviceName = "Kitchen Iron",
                    action = "Automatically turned OFF",
                    time = "10:20 PM",
                    room = "Kitchen",
                    icon = "I",
                    statusType = "SAFETY"
                ),
                RecentActivityItem(
                    deviceName = "Garage Camera",
                    action = "Connected",
                    time = "10:35 PM",
                    room = "Garage",
                    icon = "C",
                    statusType = "CONNECTED"
                ),
                RecentActivityItem(
                    deviceName = "Bedroom Light",
                    action = "Turned OFF",
                    time = "11:05 PM",
                    room = "Bedroom",
                    icon = "L",
                    statusType = "OFF"
                )
            )
        )
    }

    private fun displayActivities() {
        recentActivityListContainer.removeAllViews()

        if (recentActivities.isEmpty()) {
            tvNoRecentActivities.visibility = View.VISIBLE
            tvActivityCount.text = "0 events"
            return
        }

        tvNoRecentActivities.visibility = View.GONE
        tvActivityCount.text =
            "${recentActivities.size} events"

        recentActivities.forEach { activity ->
            addActivityItem(activity)
        }
    }

    private fun addActivityItem(
        activity: RecentActivityItem
    ) {
        val itemView = LayoutInflater.from(this).inflate(
            R.layout.item_recent_activity,
            recentActivityListContainer,
            false
        )

        val tvIcon =
            itemView.findViewById<TextView>(
                R.id.tvRecentActivityIcon
            )

        val tvDeviceName =
            itemView.findViewById<TextView>(
                R.id.tvRecentActivityDeviceName
            )

        val tvAction =
            itemView.findViewById<TextView>(
                R.id.tvRecentActivityAction
            )

        val tvRoom =
            itemView.findViewById<TextView>(
                R.id.tvRecentActivityRoom
            )

        val tvTime =
            itemView.findViewById<TextView>(
                R.id.tvRecentActivityTime
            )

        tvIcon.text = activity.icon
        tvDeviceName.text = activity.deviceName
        tvAction.text = activity.action
        tvRoom.text = activity.room
        tvTime.text = activity.time

        tvAction.setTextColor(
            getStatusColor(activity.statusType)
        )

        itemView.setOnClickListener {
            Toast.makeText(
                this,
                "${activity.deviceName}: ${activity.action}",
                Toast.LENGTH_SHORT
            ).show()
        }

        recentActivityListContainer.addView(itemView)
    }

    private fun getStatusColor(
        statusType: String
    ): Int {
        val color = when (statusType) {
            "ON" -> "#70E5A4"
            "OFF" -> "#A8B2C4"
            "SAFETY" -> "#FFD36F"
            "CONNECTED" -> "#6FE4F2"
            "ERROR" -> "#FF9B9F"
            else -> "#A8B2C4"
        }

        return Color.parseColor(color)
    }
}