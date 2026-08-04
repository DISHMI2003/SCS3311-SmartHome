package com.example.smarthome

import android.content.Intent
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
import com.example.smarthome.repository.FirebaseRepository
import com.example.smarthome.model.Device

class RoomActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FLOOR_ID = "floorId"
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

    private var floorId: String = ""
    private var roomId: String = ""
    private var roomName: String = ""

    private val devices = mutableListOf<RoomDevice>()
    private lateinit var repository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_room)

        readRoomInformation()
        initializeViews()
        setupHeader()
        
        repository = FirebaseRepository()
        loadDevicesFromFirebase()
        
        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.removeListeners()
    }

    private fun readRoomInformation() {
        floorId = intent.getStringExtra(EXTRA_FLOOR_ID).orEmpty()
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

    private fun loadDevicesFromFirebase() {
        if (floorId.isEmpty() || roomId.isEmpty()) {
            Toast.makeText(this, "Invalid room information", Toast.LENGTH_SHORT).show()
            return
        }

        repository.listenToDevices(
            floorId = floorId,
            roomId = roomId,
            onUpdate = { firebaseDevices ->
                devices.clear()
                firebaseDevices.forEach { device ->
                    // Map Firebase Device to RoomDevice for UI
                    devices.add(
                        RoomDevice(
                            id = device.id,
                            name = device.name.ifEmpty { device.id },
                            type = device.type.ifEmpty { "Unknown" },
                            status = device.status,
                            powerWatts = 0, // Fallback
                            primaryAction = null,
                            secondaryAction = null
                        )
                    )
                }
                displayDevices()
            },
            onError = { e ->
                Toast.makeText(this, "Error loading devices: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
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
        val card = LayoutInflater.from(this).inflate(
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

        val isCamera = device.type.equals("Camera", ignoreCase = true)
        
        switchDevice.isChecked = if (isCamera) {
            device.status == "ONLINE"
        } else {
            device.status == "ON"
        }

        updateDeviceStatusView(
            device = device,
            statusText = tvDeviceStatus,
            isCamera = isCamera
        )

        switchDevice.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isPressed) return@setOnCheckedChangeListener
            val newStatus = if (isChecked) {
                if (isCamera) "ONLINE" else "ON"
            } else {
                if (isCamera) "DISCONNECTED" else "OFF"
            }
            
            repository.updateDeviceStatus(
                floorId = floorId,
                roomId = roomId,
                deviceId = device.id,
                status = newStatus
            )
            
            Toast.makeText(
                this,
                "${device.name} turning $newStatus",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (
            device.primaryAction != null ||
            device.secondaryAction != null
        ) {
            actionContainer.visibility = View.VISIBLE
        } else {
            actionContainer.visibility = View.GONE
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

    private fun openLightSchedule(device: RoomDevice) {
        val intent = Intent(
            this,
            ScheduleActivity::class.java
        )

        intent.putExtra(
            ScheduleActivity.EXTRA_DEVICE_ID,
            device.id
        )

        intent.putExtra(
            ScheduleActivity.EXTRA_DEVICE_NAME,
            device.name
        )

        intent.putExtra(
            ScheduleActivity.EXTRA_DEVICE_TYPE,
            device.type
        )

        intent.putExtra(
            ScheduleActivity.EXTRA_SCHEDULE_MODE,
            ScheduleActivity.MODE_LIGHT_SCHEDULE
        )

        startActivity(intent)
    }

    private fun openIronSafetySchedule(device: RoomDevice) {
        val intent = Intent(
            this,
            ScheduleActivity::class.java
        )

        intent.putExtra(
            ScheduleActivity.EXTRA_DEVICE_ID,
            device.id
        )

        intent.putExtra(
            ScheduleActivity.EXTRA_DEVICE_NAME,
            device.name
        )

        intent.putExtra(
            ScheduleActivity.EXTRA_DEVICE_TYPE,
            device.type
        )

        intent.putExtra(
            ScheduleActivity.EXTRA_SCHEDULE_MODE,
            ScheduleActivity.MODE_IRON_DURATION
        )

        startActivity(intent)
    }

    private fun updateDeviceStatusView(
        device: RoomDevice,
        statusText: TextView,
        isCamera: Boolean = false
    ) {
        val isOn = if (isCamera) device.status == "ONLINE" else device.status == "ON"
        
        if (isOn) {
            statusText.text = if (isCamera) "ONLINE" else "ON"

            statusText.setTextColor(
                Color.parseColor("#70EAA4")
            )

            statusText.setBackgroundResource(
                R.drawable.device_status_on_background
            )
        } else {
            statusText.text = if (isCamera) "OFFLINE" else "OFF"

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

        devices.forEach { device ->
            if (device.status == "ON") {
                repository.updateDeviceStatus(
                    floorId = floorId,
                    roomId = roomId,
                    deviceId = device.id,
                    status = "OFF"
                )
            }
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
        when (action) {

            "Open Switches" -> {
                openSwitchBoard(device)
            }

            "Schedule" -> {
                Toast.makeText(
                    this,
                    "Schedule screen will be created later",
                    Toast.LENGTH_SHORT
                ).show()
            }

            "View Camera" -> {
                Toast.makeText(
                    this,
                    "Camera screen will be created later",
                    Toast.LENGTH_SHORT
                ).show()
            }

            "Set Duration" -> {
                Toast.makeText(
                    this,
                    "Iron safety timer screen will be created later",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                Toast.makeText(
                    this,
                    "$action: ${device.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openSwitchBoard(device: RoomDevice) {
        val intent = Intent(
            this,
            SwitchBoardActivity::class.java
        )

        intent.putExtra(
            SwitchBoardActivity.EXTRA_FLOOR_ID,
            floorId
        )

        intent.putExtra(
            SwitchBoardActivity.EXTRA_BOARD_ID,
            device.id
        )

        intent.putExtra(
            SwitchBoardActivity.EXTRA_BOARD_NAME,
            device.name
        )

        intent.putExtra(
            SwitchBoardActivity.EXTRA_ROOM_NAME,
            roomName
        )

        startActivity(intent)
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