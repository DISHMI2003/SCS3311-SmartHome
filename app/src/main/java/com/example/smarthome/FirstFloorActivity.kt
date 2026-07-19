package com.example.smarthome

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
    }

    private fun setupClickListeners() {
        btnBackFirstFloor.setOnClickListener {
            finish()
        }

        cardMasterBedroom.setOnClickListener {
            openRoom(
                roomId = "master_bedroom",
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
        /*
        After RoomActivity is created, replace this Toast with:

        val intent = Intent(this, RoomActivity::class.java)
        intent.putExtra("roomId", roomId)
        intent.putExtra("roomName", roomName)
        startActivity(intent)
        */

        Toast.makeText(
            this,
            "$roomName selected",
            Toast.LENGTH_SHORT
        ).show()
    }
}