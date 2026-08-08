package com.example.smarthome

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome.model.Device
import com.example.smarthome.repository.FirebaseRepository

class ReportsActivity : AppCompatActivity() {

    private lateinit var tvTotalEnergy: TextView
    private lateinit var llDeviceList: LinearLayout
    private lateinit var repository: FirebaseRepository

    private var groundFloorDevices: List<Device> = emptyList()
    private var firstFloorDevices: List<Device> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_reports)

        tvTotalEnergy = findViewById(R.id.tvTotalEnergy)
        llDeviceList = findViewById(R.id.llDeviceList)
        repository = FirebaseRepository()

        findViewById<TextView>(R.id.btnBackReports).setOnClickListener {
            finish()
        }

        loadReportData()
    }

    private fun loadReportData() {
        repository.listenToAllFloorDevices(
            floorId = "groundFloor",
            onUpdate = { devices ->
                groundFloorDevices = devices
                updateReportsUI()
            },
            onError = { }
        )

        repository.listenToAllFloorDevices(
            floorId = "firstFloor",
            onUpdate = { devices ->
                firstFloorDevices = devices
                updateReportsUI()
            },
            onError = { }
        )
    }

    private fun updateReportsUI() {
        val allDevices = groundFloorDevices + firstFloorDevices
        val activeCount = allDevices.count { repository.isDeviceActive(it.status) }
        val totalKwh = activeCount * 12.4

        tvTotalEnergy.text = "${String.format(java.util.Locale.US, "%.1f", totalKwh)} kWh"

        llDeviceList.removeAllViews()

        if (allDevices.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "No devices found."
                setTextColor(android.graphics.Color.parseColor("#B0BACB"))
                textSize = 16f
            }
            llDeviceList.addView(emptyText)
            return
        }

        allDevices.forEach { device ->
            val isCamera = device.type.equals("Camera", ignoreCase = true)
            val active = repository.isDeviceActive(device.status)
            
            // Calculate simulated energy usage for this specific device
            val simulatedUsage = if (active) "12.4 kWh" else "0.0 kWh"

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, (12 * resources.displayMetrics.density).toInt())
                }
                setBackgroundResource(R.drawable.dashboard_alert_card_background) // Reusing a card background
                setPadding(
                    (16 * resources.displayMetrics.density).toInt(),
                    (16 * resources.displayMetrics.density).toInt(),
                    (16 * resources.displayMetrics.density).toInt(),
                    (16 * resources.displayMetrics.density).toInt()
                )
            }

            val deviceName = TextView(this).apply {
                val dn = device.name.ifBlank { device.id }
                text = "$dn (${device.room})"
                setTextColor(android.graphics.Color.parseColor("#E3E8F7"))
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val statusAndPower = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (8 * resources.displayMetrics.density).toInt()
                }
            }

            val status = TextView(this).apply {
                text = "Status: ${device.status}"
                setTextColor(if (active) android.graphics.Color.parseColor("#70EAA4") else android.graphics.Color.parseColor("#A9B4C7"))
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val power = TextView(this).apply {
                text = simulatedUsage
                setTextColor(android.graphics.Color.parseColor("#71E4F2"))
                textSize = 14f
                gravity = android.view.Gravity.END
            }

            statusAndPower.addView(status)
            statusAndPower.addView(power)

            card.addView(deviceName)
            card.addView(statusAndPower)

            llDeviceList.addView(card)
        }
    }

    override fun onDestroy() {
        repository.removeListeners()
        super.onDestroy()
    }
}
