package com.example.smarthome

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.smarthome.repository.FirebaseRepository

class SwitchBoardActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FLOOR_ID = "floorId"
        const val EXTRA_BOARD_ID = "boardId"
        const val EXTRA_BOARD_NAME = "boardName"
        const val EXTRA_ROOM_NAME = "roomName"
    }

    private lateinit var btnSwitchBoardBack: TextView
    private lateinit var tvSwitchBoardName: TextView
    private lateinit var tvSwitchBoardRoom: TextView
    private lateinit var tvTotalSwitchCount: TextView
    private lateinit var tvActiveSwitchCount: TextView
    private lateinit var tvBoardPower: TextView
    private lateinit var tvTurnOffAllSwitches: TextView
    private lateinit var switchItemContainer: LinearLayout

    private var floorId: String = ""
    private var boardId: String = ""
    private var boardName: String = ""
    private var roomName: String = ""

    private val childSwitches = mutableListOf<ChildSwitch>()
    private lateinit var repository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_switch_board)

        readIntentData()
        initializeViews()
        setupHeader()
        
        repository = FirebaseRepository()
        loadSwitchesFromFirebase()
        
        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.removeListeners()
    }

    private fun readIntentData() {
        floorId = intent.getStringExtra(EXTRA_FLOOR_ID).orEmpty()
        boardId = intent.getStringExtra(EXTRA_BOARD_ID).orEmpty()

        boardName = intent.getStringExtra(EXTRA_BOARD_NAME)
            ?: "Switch Board"

        roomName = intent.getStringExtra(EXTRA_ROOM_NAME)
            ?: "Unknown Room"
    }

    private fun initializeViews() {
        btnSwitchBoardBack =
            findViewById(R.id.btnSwitchBoardBack)

        tvSwitchBoardName =
            findViewById(R.id.tvSwitchBoardName)

        tvSwitchBoardRoom =
            findViewById(R.id.tvSwitchBoardRoom)

        tvTotalSwitchCount =
            findViewById(R.id.tvTotalSwitchCount)

        tvActiveSwitchCount =
            findViewById(R.id.tvActiveSwitchCount)

        tvBoardPower =
            findViewById(R.id.tvBoardPower)

        tvTurnOffAllSwitches =
            findViewById(R.id.tvTurnOffAllSwitches)

        switchItemContainer =
            findViewById(R.id.switchItemContainer)
    }

    private fun setupHeader() {
        tvSwitchBoardName.text = boardName
        tvSwitchBoardRoom.text = roomName
    }

    private fun setupClickListeners() {
        btnSwitchBoardBack.setOnClickListener {
            finish()
        }

        tvTurnOffAllSwitches.setOnClickListener {
            turnOffAllSwitches()
        }
    }

    private fun loadSwitchesFromFirebase() {
        if (floorId.isEmpty() || boardId.isEmpty()) return

        repository.listenToSwitchBoard(
            floorId = floorId,
            roomId = if (boardId.startsWith("switchPanel")) "masterBedroom" else "unknown", // Using a fallback logic, ideally should pass roomId in intent
            deviceId = boardId,
            onUpdate = { switchMap ->
                childSwitches.clear()
                
                if (switchMap.isNotEmpty()) {
                    childSwitches.add(
                        ChildSwitch(
                            id = "light1",
                            name = "💡 Light 1",
                            status = if (switchMap["light1"] == true) "ON" else "OFF"
                        )
                    )
                    childSwitches.add(
                        ChildSwitch(
                            id = "fan",
                            name = "🌀 Fan",
                            status = if (switchMap["fan"] == true) "ON" else "OFF"
                        )
                    )
                    childSwitches.add(
                        ChildSwitch(
                            id = "ac",
                            name = "❄️ AC",
                            status = if (switchMap["ac"] == true) "ON" else "OFF"
                        )
                    )
                }
                
                displayChildSwitches()
            },
            onError = { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun displayChildSwitches() {
        switchItemContainer.removeAllViews()

        childSwitches.forEachIndexed { index, childSwitch ->
            addChildSwitchCard(
                position = index,
                childSwitch = childSwitch
            )
        }

        updateSummary()
    }

    private fun addChildSwitchCard(
        position: Int,
        childSwitch: ChildSwitch
    ) {
        val itemView = LayoutInflater.from(this).inflate(
            R.layout.item_child_switch,
            switchItemContainer,
            false
        )

        val tvChildSwitchNumber =
            itemView.findViewById<TextView>(
                R.id.tvChildSwitchNumber
            )

        val tvChildSwitchName =
            itemView.findViewById<TextView>(
                R.id.tvChildSwitchName
            )

        val tvChildSwitchStatus =
            itemView.findViewById<TextView>(
                R.id.tvChildSwitchStatus
            )

        val tvChildSwitchPower =
            itemView.findViewById<TextView>(
                R.id.tvChildSwitchPower
            )

        val switchChild =
            itemView.findViewById<SwitchCompat>(
                R.id.switchChild
            )

        tvChildSwitchNumber.text =
            (position + 1).toString()

        tvChildSwitchName.text =
            childSwitch.name

        tvChildSwitchPower.text =
            "${childSwitch.powerWatts} W"

        switchChild.isChecked =
            childSwitch.status == "ON"

        updateChildSwitchStatus(
            childSwitch = childSwitch,
            statusView = tvChildSwitchStatus
        )

        switchChild.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isPressed) return@setOnCheckedChangeListener
            
            val newStatus = if (isChecked) "ON" else "OFF"
            
            repository.updateSwitchState(
                floorId = floorId,
                roomId = if (boardId.startsWith("switchPanel")) "masterBedroom" else "unknown",
                deviceId = boardId,
                switchName = childSwitch.id,
                state = isChecked
            )

            Toast.makeText(
                this,
                "${childSwitch.name} turning $newStatus",
                Toast.LENGTH_SHORT
            ).show()
        }

        switchItemContainer.addView(itemView)
    }

    private fun updateChildSwitchStatus(
        childSwitch: ChildSwitch,
        statusView: TextView
    ) {
        if (childSwitch.status == "ON") {
            statusView.text = "ON"

            statusView.setTextColor(
                Color.parseColor("#6FE5A3")
            )
        } else {
            statusView.text = "OFF"

            statusView.setTextColor(
                Color.parseColor("#9CA9BC")
            )
        }
    }

    private fun updateSummary() {
        val activeCount = childSwitches.count {
            it.status == "ON"
        }

        val activePower = childSwitches
            .filter { it.status == "ON" }
            .sumOf { it.powerWatts }

        tvTotalSwitchCount.text =
            childSwitches.size.toString()

        tvActiveSwitchCount.text =
            activeCount.toString()

        tvBoardPower.text =
            "$activePower W"
    }

    private fun turnOffAllSwitches() {
        if (childSwitches.none { it.status == "ON" }) {
            Toast.makeText(
                this,
                "All switches are already OFF",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        childSwitches.forEach { switch ->
            if (switch.status == "ON") {
                repository.updateSwitchState(
                    floorId = floorId,
                    roomId = if (boardId.startsWith("switchPanel")) "masterBedroom" else "unknown",
                    deviceId = boardId,
                    switchName = switch.id,
                    state = false
                )
            }
        }

        displayChildSwitches()

        Toast.makeText(
            this,
            "All switches turned OFF",
            Toast.LENGTH_SHORT
        ).show()
    }
}