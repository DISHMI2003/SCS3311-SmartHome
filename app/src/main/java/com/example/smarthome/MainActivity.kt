package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvDevicesOn: TextView
    private lateinit var tvOnlineDevices: TextView
    private lateinit var tvAlerts: TextView
    private lateinit var tvEnergyUsage: TextView
    private lateinit var tvNotification: TextView
    private lateinit var tvNotificationCount: TextView

    private lateinit var cardGroundFloor: LinearLayout
    private lateinit var cardFirstFloor: LinearLayout
    private lateinit var cardAlertSummary: LinearLayout

    private lateinit var btnReports: Button
    private lateinit var btnLogout: Button

    private lateinit var navHome: LinearLayout
    private lateinit var navFloors: LinearLayout
    private lateinit var navSecurity: LinearLayout
    private lateinit var navSettings: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        initializeViews()
        loadTemporaryDashboardData()
        setupClickListeners()
    }

    private fun initializeViews() {
        tvGreeting = findViewById(R.id.tvGreeting)
        tvDevicesOn = findViewById(R.id.tvDevicesOn)
        tvOnlineDevices = findViewById(R.id.tvOnlineDevices)
        tvAlerts = findViewById(R.id.tvAlerts)
        tvEnergyUsage = findViewById(R.id.tvEnergyUsage)
        tvNotification = findViewById(R.id.tvNotification)
        tvNotificationCount = findViewById(R.id.tvNotificationCount)

        cardGroundFloor = findViewById(R.id.cardGroundFloor)
        cardFirstFloor = findViewById(R.id.cardFirstFloor)
        cardAlertSummary = findViewById(R.id.cardAlertSummary)

        btnReports = findViewById(R.id.btnReports)
        btnLogout = findViewById(R.id.btnLogout)

        navHome = findViewById(R.id.navHome)
        navFloors = findViewById(R.id.navFloors)
        navSecurity = findViewById(R.id.navSecurity)
        navSettings = findViewById(R.id.navSettings)
    }

    private fun loadTemporaryDashboardData() {
        tvGreeting.text = "Hello, Pawani"
        tvDevicesOn.text = "8"
        tvOnlineDevices.text = "22"
        tvAlerts.text = "1"
        tvEnergyUsage.text = "12.4"
        tvNotificationCount.text = "1"
    }

    private fun setupClickListeners() {
        cardGroundFloor.setOnClickListener {
            openGroundFloor()
        }

        cardFirstFloor.setOnClickListener {
            openFirstFloor()
        }

        cardAlertSummary.setOnClickListener {
            showMessage("Alerts screen will be created later")
        }

        tvNotification.setOnClickListener {
            showMessage("You have one active alert")
        }

        tvNotificationCount.setOnClickListener {
            showMessage("You have one active alert")
        }

        btnReports.setOnClickListener {
            showMessage("Reports screen will be created later")
        }

        btnLogout.setOnClickListener {
            logout()
        }

        navHome.setOnClickListener {
            showMessage("You are already on Home")
        }

        navFloors.setOnClickListener {
            showMessage("Select Ground Floor or First Floor")
        }

        navSecurity.setOnClickListener {
            showMessage("Security screen will be created later")
        }

        navSettings.setOnClickListener {
            showMessage("Settings screen will be created later")
        }
    }

    private fun openGroundFloor() {
        val intent = Intent(
            this,
            GroundFloorActivity::class.java
        )

        startActivity(intent)
    }
    private fun openFirstFloor() {
        val intent = Intent(
            this,
            FirstFloorActivity::class.java
        )

        startActivity(intent)
    }
    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}