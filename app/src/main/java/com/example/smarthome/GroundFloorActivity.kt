package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GroundFloorActivity : AppCompatActivity() {

    private lateinit var btnBack: TextView
    private lateinit var cardLivingRoom: LinearLayout
    private lateinit var cardKitchen: LinearLayout
    private lateinit var cardGarage: LinearLayout
    private lateinit var tvRoomCount: TextView
    private lateinit var tvDeviceCount: TextView
    private lateinit var tvActiveCount: TextView

    private lateinit var tvLivingRoomSummary: TextView
    private lateinit var llLivingRoomChips: LinearLayout

    private lateinit var tvKitchenSummary: TextView
    private lateinit var llKitchenChips: LinearLayout

    private lateinit var tvGarageSummary: TextView
    private lateinit var llGarageChips: LinearLayout

    private lateinit var repository: com.example.smarthome.repository.FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_ground_floor)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btnBack)
        cardLivingRoom = findViewById(R.id.cardLivingRoom)
        cardKitchen = findViewById(R.id.cardKitchen)
        cardGarage = findViewById(R.id.cardGarage)

        tvRoomCount = findViewById(R.id.tvRoomCount)
        tvDeviceCount = findViewById(R.id.tvDeviceCount)
        tvActiveCount = findViewById(R.id.tvActiveCount)

        tvLivingRoomSummary = findViewById(R.id.tvLivingRoomSummary)
        llLivingRoomChips = findViewById(R.id.llLivingRoomChips)

        tvKitchenSummary = findViewById(R.id.tvKitchenSummary)
        llKitchenChips = findViewById(R.id.llKitchenChips)

        tvGarageSummary = findViewById(R.id.tvGarageSummary)
        llGarageChips = findViewById(R.id.llGarageChips)
        
        repository = com.example.smarthome.repository.FirebaseRepository()
        
        listenToFloorData()
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        cardLivingRoom.setOnClickListener {
            openRoom(
                roomId = "livingRoom",
                roomName = "Living Room"
            )
        }

        cardKitchen.setOnClickListener {
            openRoom(
                roomId = "kitchen",
                roomName = "Kitchen"
            )
        }

        cardGarage.setOnClickListener {
            openRoom(
                roomId = "garage",
                roomName = "Garage"
            )
        }
    }

    private fun openRoom(
        roomId: String,
        roomName: String
    ) {


        val intent = Intent(this, RoomActivity::class.java)
        intent.putExtra("floorId", "groundFloor")
        intent.putExtra("roomId", roomId)
        intent.putExtra("roomName", roomName)
        startActivity(intent)


        Toast.makeText(
            this,
            "$roomName selected",
            Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun listenToFloorData() {
        repository.listenToAllFloorDevices(
            floorId = "groundFloor",
            onUpdate = { devices ->
                val rooms = devices.map { it.room }.distinct()
                val totalDevices = devices.size
                val activeDevices = devices.count { repository.isDeviceActive(it.status) }
                
                tvRoomCount.text = rooms.size.toString()
                tvDeviceCount.text = totalDevices.toString()
                tvActiveCount.text = activeDevices.toString()
                
                updateRoomSummary("livingRoom", devices, tvLivingRoomSummary, llLivingRoomChips)
                updateRoomSummary("kitchen", devices, tvKitchenSummary, llKitchenChips)
                updateRoomSummary("garage", devices, tvGarageSummary, llGarageChips)
            },
            onError = {
                // Ignore for now
            }
        )
    }
    
    private fun updateRoomSummary(
        roomId: String, 
        allDevices: List<com.example.smarthome.model.Device>, 
        tvSummary: TextView, 
        llChips: LinearLayout
    ) {
        val roomDevices = allDevices.filter { it.room.equals(roomId, ignoreCase = true) }
        val activeCount = roomDevices.count { repository.isDeviceActive(it.status) }
        
        tvSummary.text = "${roomDevices.size} devices • $activeCount currently ON"
        
        llChips.removeAllViews()
        roomDevices.take(3).forEach { device ->
            val chipName = device.name.ifBlank { device.id }
            val chip = TextView(this).apply {
                text = chipName
                setBackgroundResource(R.drawable.device_chip_background)
                setTextColor(android.graphics.Color.parseColor("#BCC6D8"))
                textSize = 11f
                gravity = android.view.Gravity.CENTER
                val paddingPx = (13 * resources.displayMetrics.density).toInt()
                setPadding(paddingPx, 0, paddingPx, 0)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply {
                    if (llChips.childCount > 0) {
                        marginStart = (9 * resources.displayMetrics.density).toInt()
                    }
                }
            }
            llChips.addView(chip)
        }
    }
    
    override fun onDestroy() {
        repository.removeListeners()
        super.onDestroy()
    }
}