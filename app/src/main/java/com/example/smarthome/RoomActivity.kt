package com.example.smarthome

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class RoomActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ROOM_ID = "roomId"
        const val EXTRA_ROOM_NAME = "roomName"
    }

    private lateinit var btnRoomBack: TextView
    private lateinit var tvRoomName: TextView
    private lateinit var tvRoomSubtitle: TextView
    private lateinit var tvRoomDeviceCount: TextView
    private lateinit var tvRoomActiveCount: TextView
    private lateinit var tvRoomEnergy: TextView
    private lateinit var tvTurnOffAll: TextView
    private lateinit var tvNoDevices: TextView
    private lateinit var deviceContainer: LinearLayout

    
    private var roomId: String = ""
    private var roomName: String = ""

    private val devices = mutableListOf<RoomDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_room)

        readRoomInformation()
        initializeViews()
        setupHeader()
        loadTemporaryDevices()
        displayDevices()
        setupClickListeners()


    }

    private fun readRoomInformation() {
        roomId = intent.getStringExtra(EXTRA_ROOM_ID).orEmpty()
        roomName = intent.getStringExtra(EXTRA_ROOM_NAME)
            ?: "Unknown Room"
    }

    private fun initializeViews() {
        btnRoomBack = findViewById(R.id.btnRoomBack)
        tvRoomName = findViewById(R.id.tvRoomName)
        tvRoomSubtitle = findViewById(R.id.tvRoomSubtitle)
        tvRoomDeviceCount = findViewById(R.id.tvRoomDeviceCount)
        tvRoomActiveCount = findViewById(R.id.tvRoomActiveCount)
        tvRoomEnergy = findViewById(R.id.tvRoomEnergy)
        tvTurnOffAll = findViewById(R.id.tvTurnOffAll)
        tvNoDevices = findViewById(R.id.tvNoDevices)
        deviceContainer = findViewById(R.id.deviceContainer)
    }

    private fun setupHeader() {
        tvRoomName.text = roomName
        tvRoomSubtitle.text = "Manage $roomName devices"
    }

    private fun setupClickListeners() {
        btnRoomBack.setOnClickListener {
            finish()
        }

        tvTurnOffAll.setOnClickListener {
            turnOffAllDevices()
        }
    }

    private fun loadTemporaryDevices() {
        devices.clear()

        when (roomId) {
            "living_room" -> {
                devices.addAll(
                    listOf(
                        RoomDevice(
                            id = "living_room_main_light",
                            name = "Main Light",
                            type = "Light",
                            status = "ON",
                            powerWatts = 18,
                            primaryAction = "Schedule",
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "living_room_wall_light",
                            name = "Wall Light",
                            type = "Light",
                            status = "OFF",
                            powerWatts = 12,
                            primaryAction = "Schedule",
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "living_room_fan",
                            name = "Ceiling Fan",
                            type = "Fan",
                            status = "ON",
                            powerWatts = 75,
                            secondaryAction = "Speed"
                        ),
                        RoomDevice(
                            id = "living_room_tv_outlet",
                            name = "TV Outlet",
                            type = "Outlet",
                            status = "OFF",
                            powerWatts = 120,
                            secondaryAction = "Usage"
                        ),
                        RoomDevice(
                            id = "living_room_switch_board",
                            name = "3-Switch Board",
                            type = "Switch Board",
                            status = "ON",
                            primaryAction = "Open Switches",
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "living_room_camera",
                            name = "Security Camera",
                            type = "Camera",
                            status = "ON",
                            powerWatts = 8,
                            primaryAction = "View Camera",
                            secondaryAction = "Refresh"
                        )
                    )
                )
            }

            "kitchen" -> {
                devices.addAll(
                    listOf(
                        RoomDevice(
                            id = "kitchen_main_light",
                            name = "Kitchen Light",
                            type = "Light",
                            status = "ON",
                            powerWatts = 20,
                            primaryAction = "Schedule",
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "kitchen_refrigerator_outlet",
                            name = "Refrigerator Outlet",
                            type = "Outlet",
                            status = "ON",
                            powerWatts = 150,
                            secondaryAction = "Usage"
                        ),
                        RoomDevice(
                            id = "kitchen_cooker_outlet",
                            name = "Cooker Outlet",
                            type = "Outlet",
                            status = "OFF",
                            powerWatts = 1200,
                            secondaryAction = "Usage"
                        ),
                        RoomDevice(
                            id = "kitchen_iron",
                            name = "Iron Safety Outlet",
                            type = "Iron",
                            status = "OFF",
                            powerWatts = 1000,
                            primaryAction = "Set Duration",
                            secondaryAction = "Safety"
                        ),
                        RoomDevice(
                            id = "kitchen_exhaust_fan",
                            name = "Exhaust Fan",
                            type = "Fan",
                            status = "ON",
                            powerWatts = 60,
                            secondaryAction = "Speed"
                        )
                    )
                )
            }

            "garage" -> {
                devices.addAll(
                    listOf(
                        RoomDevice(
                            id = "garage_main_light",
                            name = "Garage Light",
                            type = "Light",
                            status = "ON",
                            powerWatts = 25,
                            primaryAction = "Schedule",
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "garage_charging_outlet",
                            name = "Vehicle Charging Outlet",
                            type = "Outlet",
                            status = "OFF",
                            powerWatts = 2200,
                            secondaryAction = "Usage"
                        ),
                        RoomDevice(
                            id = "garage_door_switch",
                            name = "Garage Door Switch",
                            type = "Door Switch",
                            status = "OFF",
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "garage_camera",
                            name = "Garage Camera",
                            type = "Camera",
                            status = "ON",
                            powerWatts = 8,
                            primaryAction = "View Camera",
                            secondaryAction = "Refresh"
                        )
                    )
                )
            }

            "master_bedroom" -> {
                devices.addAll(
                    listOf(
                        RoomDevice(
                            id = "master_bedroom_main_light",
                            name = "Main Light",
                            type = "Light",
                            status = "ON",
                            powerWatts = 18,
                            primaryAction = "Schedule",
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "master_bedroom_bedside_light",
                            name = "Bedside Light",
                            type = "Light",
                            status = "OFF",
                            powerWatts = 10,
                            primaryAction = "Schedule",
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "master_bedroom_fan",
                            name = "Ceiling Fan",
                            type = "Fan",
                            status = "ON",
                            powerWatts = 75,
                            secondaryAction = "Speed"
                        ),
                        RoomDevice(
                            id = "master_bedroom_ac_outlet",
                            name = "Air Conditioner Outlet",
                            type = "Outlet",
                            status = "OFF",
                            powerWatts = 1400,
                            secondaryAction = "Usage"
                        ),
                        RoomDevice(
                            id = "master_bedroom_switch_board",
                            name = "3-Switch Board",
                            type = "Switch Board",
                            status = "OFF",
                            primaryAction = "Open Switches",
                            secondaryAction = "Details"
                        )
                    )
                )
            }

            "bedroom" -> {
                devices.addAll(
                    listOf(
                        RoomDevice(
                            id = "bedroom_main_light",
                            name = "Main Light",
                            type = "Light",
                            status = "ON",
                            powerWatts = 18,
                            primaryAction = "Schedule",
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "bedroom_night_light",
                            name = "Night Light",
                            type = "Light",
                            status = "OFF",
                            powerWatts = 8,
                            primaryAction = "Schedule",
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "bedroom_fan",
                            name = "Ceiling Fan",
                            type = "Fan",
                            status = "ON",
                            powerWatts = 70,
                            secondaryAction = "Speed"
                        ),
                        RoomDevice(
                            id = "bedroom_switch_board",
                            name = "2-Switch Board",
                            type = "Switch Board",
                            status = "OFF",
                            primaryAction = "Open Switches",
                            secondaryAction = "Details"
                        )
                    )
                )
            }

            "bathroom" -> {
                devices.addAll(
                    listOf(
                        RoomDevice(
                            id = "bathroom_main_light",
                            name = "Bathroom Light",
                            type = "Light",
                            status = "ON",
                            powerWatts = 15,
                            secondaryAction = "Details"
                        ),
                        RoomDevice(
                            id = "bathroom_water_heater",
                            name = "Water Heater",
                            type = "Outlet",
                            status = "OFF",
                            powerWatts = 1800,
                            secondaryAction = "Usage"
                        ),
                        RoomDevice(
                            id = "bathroom_exhaust_fan",
                            name = "Exhaust Fan",
                            type = "Fan",
                            status = "OFF",
                            powerWatts = 45,
                            secondaryAction = "Speed"
                        )
                    )
                )
            }
        }
    }

    private fun displayDevices() {
        deviceContainer.removeAllViews()

        if (devices.isEmpty()) {
            tvNoDevices.visibility = View.VISIBLE
        } else {
            tvNoDevices.visibility = View.GONE

            devices.forEach { device ->
                addDeviceCard(device)
            }
        }

        updateSummary()
    }

    private fun addDeviceCard(device: RoomDevice) {
        val card = LayoutInflater.from(this)
            .inflate(
                R.layout.item_room_device,
                deviceContainer,
                false
            )

        val tvDeviceIcon =
            card.findViewById<TextView>(R.id.tvDeviceIcon)

        val tvDeviceName =
            card.findViewById<TextView>(R.id.tvDeviceName)

        val tvDeviceType =
            card.findViewById<TextView>(R.id.tvDeviceType)

        val switchDevice =
            card.findViewById<SwitchCompat>(R.id.switchDevice)

        val tvDeviceStatus =
            card.findViewById<TextView>(R.id.tvDeviceStatus)

        val tvDevicePower =
            card.findViewById<TextView>(R.id.tvDevicePower)

        val tvLastUpdated =
            card.findViewById<TextView>(R.id.tvLastUpdated)

        val actionContainer =
            card.findViewById<LinearLayout>(
                R.id.deviceActionContainer
            )

        val btnPrimaryAction =
            card.findViewById<Button>(
                R.id.btnDevicePrimaryAction
            )

        val btnSecondaryAction =
            card.findViewById<Button>(
                R.id.btnDeviceSecondaryAction
            )

        tvDeviceIcon.text = getDeviceIcon(device.type)
        tvDeviceName.text = device.name
        tvDeviceType.text = device.type
        tvDevicePower.text = "${device.powerWatts} W"
        tvLastUpdated.text = "Updated now"

        switchDevice.isChecked = device.status == "ON"

        updateDeviceStatusView(
            device = device,
            statusText = tvDeviceStatus
        )

        switchDevice.setOnCheckedChangeListener {
                _,
                isChecked ->

            device.status = if (isChecked) "ON" else "OFF"

            updateDeviceStatusView(
                device = device,
                statusText = tvDeviceStatus
            )

            updateSummary()

            Toast.makeText(
                this,
                "${device.name} turned ${device.status}",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (
            device.primaryAction != null ||
            device.secondaryAction != null
        ) {
            actionContainer.visibility = View.VISIBLE
        }

        if (device.primaryAction != null) {
            btnPrimaryAction.visibility = View.VISIBLE
            btnPrimaryAction.text = device.primaryAction

            btnPrimaryAction.setOnClickListener {
                handleDeviceAction(
                    device = device,
                    action = device.primaryAction
                )
            }
        } else {
            btnPrimaryAction.visibility = View.GONE
        }

        if (device.secondaryAction != null) {
            btnSecondaryAction.visibility = View.VISIBLE
            btnSecondaryAction.text = device.secondaryAction

            btnSecondaryAction.setOnClickListener {
                handleDeviceAction(
                    device = device,
                    action = device.secondaryAction
                )
            }
        } else {
            btnSecondaryAction.visibility = View.GONE
        }

        deviceContainer.addView(card)
    }

    private fun updateDeviceStatusView(
        device: RoomDevice,
        statusText: TextView
    ) {
        if (device.status == "ON") {
            statusText.text = "ON"
            statusText.setTextColor(
                Color.parseColor("#70EAA4")
            )
            statusText.setBackgroundResource(
                R.drawable.device_status_on_background
            )
        } else {
            statusText.text = "OFF"
            statusText.setTextColor(
                Color.parseColor("#A9B4C7")
            )
            statusText.setBackgroundResource(
                R.drawable.device_status_off_background
            )
        }
    }

    private fun updateSummary() {
        val activeDevices = devices.count {
            it.status == "ON"
        }

        val activePower = devices
            .filter { it.status == "ON" }
            .sumOf { it.powerWatts }

        tvRoomDeviceCount.text = devices.size.toString()
        tvRoomActiveCount.text = activeDevices.toString()
        tvRoomEnergy.text = "$activePower W"
    }

    private fun turnOffAllDevices() {
        if (devices.none { it.status == "ON" }) {
            Toast.makeText(
                this,
                "All devices are already OFF",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        devices.forEach {
            it.status = "OFF"
        }

        displayDevices()

        Toast.makeText(
            this,
            "All $roomName devices turned OFF",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleDeviceAction(
        device: RoomDevice,
        action: String
    ) {
        Toast.makeText(
            this,
            "$action: ${device.name}",
            Toast.LENGTH_SHORT
        ).show()

        /*
        Later replace this section with navigation:

        Schedule:
        startActivity(Intent(this, ScheduleActivity::class.java))

        Camera:
        startActivity(Intent(this, CameraActivity::class.java))

        Switch board:
        startActivity(Intent(this, SwitchBoardActivity::class.java))
        */
    }

    private fun getDeviceIcon(type: String): String {
        return when (type) {
            "Light" -> "L"
            "Fan" -> "F"
            "Outlet" -> "P"
            "Iron" -> "I"
            "Camera" -> "C"
            "Switch Board" -> "S"
            "Door Switch" -> "D"
            else -> "?"
        }
    }
}