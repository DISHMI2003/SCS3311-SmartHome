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
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        cardLivingRoom.setOnClickListener {
            openRoom(
                roomId = "living_room",
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
        /*
        After creating RoomActivity, replace the Toast with:

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