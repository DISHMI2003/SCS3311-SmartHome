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
import com.google.firebase.auth.FirebaseAuth
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

    private lateinit var tvProfileImage: TextView
    private lateinit var tvGroundActiveDevices: TextView
    private lateinit var tvFirstActiveDevices: TextView

    private lateinit var cardGroundFloor: LinearLayout
    private lateinit var cardFirstFloor: LinearLayout
    private lateinit var cardAlertSummary: LinearLayout
    private lateinit var cardEnergyUsage: LinearLayout

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
        loadDashboard()

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
        tvProfileImage = findViewById(R.id.tvProfileImage)
        tvGroundActiveDevices = findViewById(R.id.tvGroundActiveDevices)
        tvFirstActiveDevices = findViewById(R.id.tvFirstActiveDevices)

        cardGroundFloor = findViewById(R.id.cardGroundFloor)
        cardFirstFloor = findViewById(R.id.cardFirstFloor)
        cardAlertSummary = findViewById(R.id.cardAlertSummary)
        cardEnergyUsage = findViewById(R.id.cardEnergyUsage)

        btnReports = findViewById(R.id.btnReports)
        btnLogout = findViewById(R.id.btnLogout)

        navHome = findViewById(R.id.navHome)
        navFloors = findViewById(R.id.navFloors)
        navSecurity = findViewById(R.id.navSecurity)
        navSettings = findViewById(R.id.navSettings)

    }
    private fun loadTemporaryDashboardData() {
        // Basic load defaults
        tvDevicesOn.text = "0"
        tvOnlineDevices.text = "0"
        tvAlerts.text = "0"
        tvEnergyUsage.text = "0.0"
        tvNotificationCount.text = "0"
        
        // Profile Greeting
        val user = FirebaseAuth.getInstance().currentUser
        val displayName = user?.displayName ?: user?.email?.substringBefore("@") ?: "User"
        tvGreeting.text = "Hello, $displayName"
        if (displayName.isNotEmpty()) {
            tvProfileImage.text = displayName.first().uppercase()
        }

    }
    private var groundFloorDevices: List<com.example.smarthome.model.Device> = emptyList()
    private var firstFloorDevices: List<com.example.smarthome.model.Device> = emptyList()

    private fun updateGlobalDashboard() {
        val allDevices = groundFloorDevices + firstFloorDevices
        val devicesOn = allDevices.count { it.status == "ON" }
        val onlineDevices = allDevices.count { it.status == "ONLINE" || it.status == "CONNECTED" }
        val activeCount = allDevices.count { repository.isDeviceActive(it.status) }
        
        tvDevicesOn.text = devicesOn.toString()
        tvOnlineDevices.text = onlineDevices.toString()
        tvEnergyUsage.text = "${String.format(java.util.Locale.US, "%.1f", activeCount * 12.4)}"
        
        // Alerts can remain static for now since no alerts logic exists
        tvAlerts.text = if (activeCount > 5) "1" else "0"
    }

    private fun loadDashboard() {
        
        repository.listenToAllFloorDevices(
            floorId = "groundFloor",
            onUpdate = { devices ->
                groundFloorDevices = devices
                val activeCount = devices.count { repository.isDeviceActive(it.status) }
                tvGroundActiveDevices.text = "$activeCount active devices"
                updateGlobalDashboard()
            },
            onError = {
                tvGroundActiveDevices.text = "0 active devices"
            }
        )
        
        repository.listenToAllFloorDevices(
            floorId = "firstFloor",
            onUpdate = { devices ->
                firstFloorDevices = devices
                val activeCount = devices.count { repository.isDeviceActive(it.status) }
                tvFirstActiveDevices.text = "$activeCount active devices"
                updateGlobalDashboard()
            },
            onError = {
                tvFirstActiveDevices.text = "0 active devices"
            }
        )

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
        
        cardEnergyUsage.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        tvNotification.setOnClickListener {

            showMessage("You have one active alert")

        }

        tvNotificationCount.setOnClickListener {

            showMessage("You have one active alert")

        }

        btnReports.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
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

        FirebaseAuth.getInstance().signOut()

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