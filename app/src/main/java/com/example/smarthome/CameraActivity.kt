package com.example.smarthome

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome.repository.FirebaseRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FLOOR_ID = "floorId"
        const val EXTRA_ROOM_ID = "roomId"
        const val EXTRA_ROOM_NAME = "roomName"
        const val EXTRA_CAMERA_ID = "cameraId"
        const val EXTRA_CAMERA_NAME = "cameraName"
    }

    private lateinit var btnCameraBack: TextView
    private lateinit var tvCameraName: TextView
    private lateinit var tvCameraRoom: TextView
    private lateinit var tvCameraStatus: TextView
    private lateinit var tvLastUpdated: TextView
    private lateinit var tvOfflineMessage: TextView
    private lateinit var ivCameraSnapshot: ImageView
    private lateinit var offlineOverlay: View
    private lateinit var btnRefreshSnapshot: Button
    private lateinit var btnFullScreen: Button

    private lateinit var repository: FirebaseRepository

    private var floorId = ""
    private var roomId = ""
    private var roomName = ""
    private var cameraId = ""
    private var cameraName = ""

    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_camera)

        readIntentData()
        initializeViews()

        repository = FirebaseRepository()

        displayCameraInformation()
        setupClickListeners()
        setupBackButton()
        listenToCamera()
    }

    private fun readIntentData() {
        floorId = intent
            .getStringExtra(EXTRA_FLOOR_ID)
            .orEmpty()

        roomId = intent
            .getStringExtra(EXTRA_ROOM_ID)
            .orEmpty()

        roomName = intent
            .getStringExtra(EXTRA_ROOM_NAME)
            ?: "Unknown Room"

        cameraId = intent
            .getStringExtra(EXTRA_CAMERA_ID)
            .orEmpty()

        cameraName = intent
            .getStringExtra(EXTRA_CAMERA_NAME)
            ?: "Security Camera"
    }

    private fun initializeViews() {
        btnCameraBack =
            findViewById(R.id.btnCameraBack)

        tvCameraName =
            findViewById(R.id.tvCameraName)

        tvCameraRoom =
            findViewById(R.id.tvCameraRoom)

        tvCameraStatus =
            findViewById(R.id.tvCameraStatus)

        tvLastUpdated =
            findViewById(R.id.tvLastUpdated)

        tvOfflineMessage =
            findViewById(R.id.tvOfflineMessage)

        ivCameraSnapshot =
            findViewById(R.id.ivCameraSnapshot)

        offlineOverlay =
            findViewById(R.id.offlineOverlay)

        btnRefreshSnapshot =
            findViewById(R.id.btnRefreshSnapshot)

        btnFullScreen =
            findViewById(R.id.btnFullScreen)
    }

    private fun displayCameraInformation() {
        tvCameraName.text = cameraName
        tvCameraRoom.text = roomName

        val imageResource = when (
            cameraId.trim().lowercase(Locale.ROOT)
        ) {
            "garagecamera",
            "garage_camera" -> {
                R.drawable.garage_camera_mock
            }

            "securitycamera",
            "living_room_camera" -> {
                R.drawable.living_room_camera_mock
            }

            else -> {
                R.drawable.living_room_camera_mock
            }
        }

        ivCameraSnapshot.setImageResource(imageResource)
        updateLastUpdatedTime()
    }

    private fun listenToCamera() {
        if (
            floorId.isBlank() ||
            roomId.isBlank() ||
            cameraId.isBlank()
        ) {
            showDisconnectedState()

            Toast.makeText(
                this,
                "Missing floor, room or camera information",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        repository.listenToDevice(
            floorId = floorId,
            roomId = roomId,
            deviceId = cameraId,

            onUpdate = { device ->
                if (device == null) {
                    showDisconnectedState()
                    return@listenToDevice
                }

                if (device.name.isNotBlank()) {
                    cameraName = device.name
                    tvCameraName.text = device.name
                }

                val status = device.status
                    .trim()
                    .uppercase(Locale.ROOT)

                updateCameraStatus(status)
            },

            onError = { error ->
                showErrorState()

                Toast.makeText(
                    this,
                    "Camera error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun updateCameraStatus(status: String) {
        when (status) {
            "ONLINE",
            "CONNECTED",
            "ON" -> {
                showOnlineState()
            }

            "ERROR" -> {
                showErrorState()
            }

            else -> {
                showDisconnectedState()
            }
        }

        updateLastUpdatedTime()
    }

    private fun showOnlineState() {
        tvCameraStatus.text = "● ONLINE"

        tvCameraStatus.setTextColor(
            getColor(R.color.camera_status_online)
        )

        offlineOverlay.visibility = View.GONE
        ivCameraSnapshot.alpha = 1f

        btnRefreshSnapshot.isEnabled = true
        btnFullScreen.isEnabled = true

        btnRefreshSnapshot.alpha = 1f
        btnFullScreen.alpha = 1f
    }

    private fun showDisconnectedState() {
        tvCameraStatus.text = "● DISCONNECTED"

        tvCameraStatus.setTextColor(
            getColor(R.color.camera_status_disconnected)
        )

        tvOfflineMessage.text =
            "Camera is disconnected"

        offlineOverlay.visibility = View.VISIBLE
        ivCameraSnapshot.alpha = 0.35f

        btnRefreshSnapshot.isEnabled = false
        btnFullScreen.isEnabled = false

        btnRefreshSnapshot.alpha = 0.5f
        btnFullScreen.alpha = 0.5f
    }

    private fun showErrorState() {
        tvCameraStatus.text = "● ERROR"

        tvCameraStatus.setTextColor(
            getColor(R.color.camera_status_error)
        )

        tvOfflineMessage.text =
            "Camera connection error"

        offlineOverlay.visibility = View.VISIBLE
        ivCameraSnapshot.alpha = 0.35f

        btnRefreshSnapshot.isEnabled = false
        btnFullScreen.isEnabled = false

        btnRefreshSnapshot.alpha = 0.5f
        btnFullScreen.alpha = 0.5f
    }

    private fun setupClickListeners() {
        btnCameraBack.setOnClickListener {
            if (isFullScreen) {
                exitFullScreen()
            } else {
                finish()
            }
        }

        btnRefreshSnapshot.setOnClickListener {
            refreshSnapshot()
        }

        btnFullScreen.setOnClickListener {
            if (isFullScreen) {
                exitFullScreen()
            } else {
                enterFullScreen()
            }
        }

        ivCameraSnapshot.setOnClickListener {
            if (isFullScreen) {
                exitFullScreen()
            }
        }
    }

    private fun refreshSnapshot() {
        ivCameraSnapshot.alpha = 0.25f

        ivCameraSnapshot.animate()
            .alpha(1f)
            .setDuration(400)
            .start()

        updateLastUpdatedTime()

        Toast.makeText(
            this,
            "$cameraName snapshot refreshed",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateLastUpdatedTime() {
        val currentTime = SimpleDateFormat(
            "hh:mm:ss a",
            Locale.getDefault()
        ).format(Date())

        tvLastUpdated.text =
            "Last updated: $currentTime"
    }

    private fun enterFullScreen() {
        isFullScreen = true

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        btnFullScreen.text = "Exit Full Screen"

        Toast.makeText(
            this,
            "Tap the image or press Back to exit full screen",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun exitFullScreen() {
        isFullScreen = false

        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_VISIBLE

        btnFullScreen.text = "Full Screen"
    }

    private fun setupBackButton() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isFullScreen) {
                        exitFullScreen()
                    } else {
                        finish()
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        repository.removeListeners()
        super.onDestroy()
    }
}
