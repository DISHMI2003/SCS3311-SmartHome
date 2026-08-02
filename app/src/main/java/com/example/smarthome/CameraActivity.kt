package com.example.smarthome

import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CAMERA_ID = "cameraId"
        const val EXTRA_CAMERA_NAME = "cameraName"
        const val EXTRA_ROOM_NAME = "roomName"
    }

    private lateinit var btnCameraBack: TextView
    private lateinit var tvCameraTitle: TextView
    private lateinit var tvCameraRoom: TextView
    private lateinit var tvCameraName: TextView
    private lateinit var tvCameraId: TextView
    private lateinit var tvLastUpdated: TextView
    private lateinit var tvSnapshotTimeOverlay: TextView
    private lateinit var tvSnapshotNumber: TextView
    private lateinit var ivCameraSnapshot: ImageView
    private lateinit var btnRefreshSnapshot: AppCompatButton
    private lateinit var btnFullScreen: AppCompatButton

    private var cameraId: String = ""
    private var cameraName: String = ""
    private var roomName: String = ""

    private var snapshotNumber = 1
    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_camera)

        readCameraInformation()
        initializeViews()
        displayCameraInformation()
        setupClickListeners()
    }

    private fun readCameraInformation() {
        cameraId = intent.getStringExtra(EXTRA_CAMERA_ID).orEmpty()

        cameraName = intent.getStringExtra(EXTRA_CAMERA_NAME)
            ?: "Security Camera"

        roomName = intent.getStringExtra(EXTRA_ROOM_NAME)
            ?: "Unknown Room"
    }

    private fun initializeViews() {
        btnCameraBack = findViewById(R.id.btnCameraBack)
        tvCameraTitle = findViewById(R.id.tvCameraTitle)
        tvCameraRoom = findViewById(R.id.tvCameraRoom)
        tvCameraName = findViewById(R.id.tvCameraName)
        tvCameraId = findViewById(R.id.tvCameraId)
        tvLastUpdated = findViewById(R.id.tvLastUpdated)

        tvSnapshotTimeOverlay =
            findViewById(R.id.tvSnapshotTimeOverlay)

        tvSnapshotNumber =
            findViewById(R.id.tvSnapshotNumber)

        ivCameraSnapshot =
            findViewById(R.id.ivCameraSnapshot)

        btnRefreshSnapshot =
            findViewById(R.id.btnRefreshSnapshot)

        btnFullScreen =
            findViewById(R.id.btnFullScreen)
    }

    private fun displayCameraInformation() {
        tvCameraTitle.text = cameraName
        tvCameraRoom.text = roomName
        tvCameraName.text = cameraName

        tvCameraId.text = if (cameraId.isBlank()) {
            "Unknown camera ID"
        } else {
            cameraId
        }

        setCorrectMockImage()
        updateSnapshotTime()
        updateSnapshotNumber()
    }

    private fun setCorrectMockImage() {
        val imageResource = when (cameraId) {
            "garage_camera" -> {
                R.drawable.garage_camera_mock
            }

            "living_room_camera" -> {
                R.drawable.living_room_camera_mock
            }

            else -> {
                R.drawable.living_room_camera_mock
            }
        }

        ivCameraSnapshot.setImageResource(imageResource)
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
        snapshotNumber += 1

        ivCameraSnapshot.alpha = 0.4f

        ivCameraSnapshot.animate()
            .alpha(1f)
            .setDuration(350)
            .start()

        updateSnapshotTime()
        updateSnapshotNumber()

        Toast.makeText(
            this,
            "$cameraName snapshot refreshed",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateSnapshotTime() {
        val currentTime = SimpleDateFormat(
            "hh:mm:ss a",
            Locale.getDefault()
        ).format(Date())

        tvSnapshotTimeOverlay.text = currentTime
        tvLastUpdated.text = "Just now"
    }

    private fun updateSnapshotNumber() {
        tvSnapshotNumber.text =
            "Snapshot #$snapshotNumber"
    }

    private fun enterFullScreen() {
        isFullScreen = true

        val rootView = findViewById<View>(
            android.R.id.content
        )

        btnFullScreen.text = "Exit Full Screen"

        if (android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.R
        ) {
            window.insetsController?.let { controller ->
                controller.hide(
                    WindowInsets.Type.statusBars() or
                            WindowInsets.Type.navigationBars()
                )

                controller.systemBarsBehavior =
                    WindowInsetsController
                        .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            rootView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        Toast.makeText(
            this,
            "Tap the camera image to exit full screen",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun exitFullScreen() {
        isFullScreen = false

        btnFullScreen.text = "Full Screen"

        if (android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.R
        ) {
            window.insetsController?.show(
                WindowInsets.Type.statusBars() or
                        WindowInsets.Type.navigationBars()
            )
        } else {
            @Suppress("DEPRECATION")
            findViewById<View>(
                android.R.id.content
            ).systemUiVisibility =
                View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    override fun onBackPressed() {
        if (isFullScreen) {
            exitFullScreen()
        } else {
            super.onBackPressed()
        }
    }
}