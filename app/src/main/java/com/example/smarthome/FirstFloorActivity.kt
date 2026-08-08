package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FirstFloorActivity : AppCompatActivity() {

    private lateinit var btnBackFirstFloor: TextView
    private lateinit var cardMasterBedroom: LinearLayout
    private lateinit var cardBedroom: LinearLayout
    private lateinit var cardBathroom: LinearLayout
    private lateinit var tvFirstFloorRoomCount: TextView
    private lateinit var tvFirstFloorDeviceCount: TextView
    private lateinit var tvFirstFloorActiveCount: TextView

    private lateinit var tvMasterBedroomSummary: TextView
    private lateinit var llMasterBedroomChips: LinearLayout

    private lateinit var tvBedroomSummary: TextView
    private lateinit var llBedroomChips: LinearLayout

    private lateinit var tvBathroomSummary: TextView
    private lateinit var llBathroomChips: LinearLayout

    private lateinit var repository: com.example.smarthome.repository.FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_first_floor)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnBackFirstFloor = findViewById(R.id.btnBackFirstFloor)
        cardMasterBedroom = findViewById(R.id.cardMasterBedroom)
        cardBedroom = findViewById(R.id.cardBedroom)
        cardBathroom = findViewById(R.id.cardBathroom)

        tvFirstFloorRoomCount = findViewById(R.id.tvFirstFloorRoomCount)
        tvFirstFloorDeviceCount = findViewById(R.id.tvFirstFloorDeviceCount)
        tvFirstFloorActiveCount = findViewById(R.id.tvFirstFloorActiveCount)

        tvMasterBedroomSummary = findViewById(R.id.tvMasterBedroomSummary)
        llMasterBedroomChips = findViewById(R.id.llMasterBedroomChips)

        tvBedroomSummary = findViewById(R.id.tvBedroomSummary)
        llBedroomChips = findViewById(R.id.llBedroomChips)

        tvBathroomSummary = findViewById(R.id.tvBathroomSummary)
        llBathroomChips = findViewById(R.id.llBathroomChips)
        
        repository = com.example.smarthome.repository.FirebaseRepository()
        
        listenToFloorData()
    }

    private fun setupClickListeners() {
        btnBackFirstFloor.setOnClickListener {
            finish()
        }

        cardMasterBedroom.setOnClickListener {
            openRoom(
                roomId = "masterBedroom",
                roomName = "Master Bedroom"
            )
        }

        cardBedroom.setOnClickListener {
            openRoom(
                roomId = "bedroom",
                roomName = "Bedroom"
            )
        }

        cardBathroom.setOnClickListener {
            openRoom(
                roomId = "bathroom",
                roomName = "Bathroom"
            )
        }
    }

    private fun openRoom(
        roomId: String,
        roomName: String
    ) {
        val intent = Intent(this, RoomActivity::class.java)

        intent.putExtra("floorId", "firstFloor")
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
            floorId = "firstFloor",
            onUpdate = { devices ->
                val rooms = devices.map { it.room }.distinct()
                val totalDevices = devices.size
                val activeDevices = devices.count { repository.isDeviceActive(it.status) }
                
                tvFirstFloorRoomCount.text = rooms.size.toString()
                tvFirstFloorDeviceCount.text = totalDevices.toString()
                tvFirstFloorActiveCount.text = activeDevices.toString()
                
                updateRoomSummary("masterBedroom", devices, tvMasterBedroomSummary, llMasterBedroomChips)
                updateRoomSummary("bedroom", devices, tvBedroomSummary, llBedroomChips)
                updateRoomSummary("bathroom", devices, tvBathroomSummary, llBathroomChips)
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