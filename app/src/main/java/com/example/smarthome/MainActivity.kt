package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smarthome.repository.FirebaseRepository

class MainActivity : AppCompatActivity() {

    // Firebase
    private lateinit var repository: FirebaseRepository

    // UI
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

        enableEdgeToEdge()
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        initializeViews()
        loadTemporaryDashboardData()
        setupClickListeners()

        // -----------------------------
        // Firebase Realtime Listener
        // -----------------------------

        repository = FirebaseRepository()

        repository.listenToFloors(

            onUpdate = { floors ->

                Log.d("Realtime", "Firestore Updated")
                Log.d("Realtime", "Total Floors : ${floors.size}")

                floors.forEach {

                        floor ->

                    Log.d("Realtime", "Floor ID   : ${floor.id}")
                    Log.d("Realtime", "Floor Name : ${floor.name}")

                }

                // TODO
                // Later update dashboard values from Firestore

            },

            onError = {

                    exception ->

                Log.e(
                    "Realtime",
                    "Firestore Error",
                    exception
                )

            }

        )

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

        startActivity(

            Intent(
                this,
                GroundFloorActivity::class.java
            )

        )

    }

    private fun openFirstFloor() {

        startActivity(

            Intent(
                this,
                FirstFloorActivity::class.java
            )

        )

    }

    private fun logout() {

        val intent = Intent(
            this,
            LoginActivity::class.java
        )

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

    override fun onDestroy() {

        super.onDestroy()

        repository.removeListeners()

    }

}