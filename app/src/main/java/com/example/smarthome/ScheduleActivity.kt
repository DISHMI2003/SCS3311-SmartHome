package com.example.smarthome

import android.app.TimePickerDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import java.util.Calendar
import java.util.Locale

class ScheduleActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DEVICE_ID = "deviceId"
        const val EXTRA_DEVICE_NAME = "deviceName"
        const val EXTRA_DEVICE_TYPE = "deviceType"
        const val EXTRA_SCHEDULE_MODE = "scheduleMode"
        const val EXTRA_FLOOR_ID = "floorId"
        const val EXTRA_ROOM_ID = "roomId"

        const val MODE_LIGHT_SCHEDULE = "lightSchedule"
        const val MODE_IRON_DURATION = "ironDuration"
    }

    private lateinit var btnScheduleBack: TextView
    private lateinit var tvScheduleTitle: TextView
    private lateinit var tvScheduleSubtitle: TextView
    private lateinit var tvDeviceName: TextView
    private lateinit var tvDeviceType: TextView

    // Light schedule section
    private lateinit var lightScheduleSection: LinearLayout
    private lateinit var switchScheduleEnabled: SwitchCompat
    private lateinit var tvScheduleEnabledStatus: TextView
    private lateinit var btnSelectOnTime: LinearLayout
    private lateinit var btnSelectOffTime: LinearLayout
    private lateinit var tvOnTime: TextView
    private lateinit var tvOffTime: TextView
    private lateinit var btnSaveLightSchedule: AppCompatButton
    private lateinit var btnClearLightSchedule: AppCompatButton

    // Iron safety section
    private lateinit var ironDurationSection: LinearLayout
    private lateinit var switchIronSafetyEnabled: SwitchCompat
    private lateinit var tvIronSafetyStatus: TextView
    private lateinit var etMaximumMinutes: EditText
    private lateinit var btnSaveIronDuration: AppCompatButton
    private lateinit var btnStartTimerTest: AppCompatButton
    private lateinit var btnCancelTimer: AppCompatButton
    private lateinit var tvTimerCountdown: TextView
    private lateinit var timerStatusContainer: LinearLayout

    private var deviceId: String = ""
    private var deviceName: String = ""
    private var deviceType: String = ""
    private var scheduleMode: String = ""
    private var floorId: String = ""
    private var roomId: String = ""

    private var selectedOnHour = 18
    private var selectedOnMinute = 0
    private var selectedOffHour = 22
    private var selectedOffMinute = 0

    private var ironCountDownTimer: CountDownTimer? = null
    
    private val repository = com.example.smarthome.repository.FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        setContentView(R.layout.activity_schedule)

        readIntentData()
        initializeViews()
        setupHeader()
        setupCorrectMode()
        loadSavedSettings()
        setupClickListeners()
    }

    private fun readIntentData() {
        deviceId = intent.getStringExtra(EXTRA_DEVICE_ID).orEmpty()

        deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME)
            ?: "Unknown Device"

        deviceType = intent.getStringExtra(EXTRA_DEVICE_TYPE)
            ?: "Device"

        scheduleMode = intent.getStringExtra(EXTRA_SCHEDULE_MODE)
            ?: MODE_LIGHT_SCHEDULE
            
        floorId = intent.getStringExtra(EXTRA_FLOOR_ID).orEmpty()
        roomId = intent.getStringExtra(EXTRA_ROOM_ID).orEmpty()
    }

    private fun initializeViews() {
        btnScheduleBack = findViewById(R.id.btnScheduleBack)
        tvScheduleTitle = findViewById(R.id.tvScheduleTitle)
        tvScheduleSubtitle = findViewById(R.id.tvScheduleSubtitle)
        tvDeviceName = findViewById(R.id.tvScheduleDeviceName)
        tvDeviceType = findViewById(R.id.tvScheduleDeviceType)

        lightScheduleSection = findViewById(R.id.lightScheduleSection)
        switchScheduleEnabled = findViewById(R.id.switchScheduleEnabled)
        tvScheduleEnabledStatus =
            findViewById(R.id.tvScheduleEnabledStatus)

        btnSelectOnTime = findViewById(R.id.btnSelectOnTime)
        btnSelectOffTime = findViewById(R.id.btnSelectOffTime)
        tvOnTime = findViewById(R.id.tvOnTime)
        tvOffTime = findViewById(R.id.tvOffTime)
        btnSaveLightSchedule =
            findViewById(R.id.btnSaveLightSchedule)

        btnClearLightSchedule =
            findViewById(R.id.btnClearLightSchedule)

        ironDurationSection = findViewById(R.id.ironDurationSection)
        switchIronSafetyEnabled =
            findViewById(R.id.switchIronSafetyEnabled)

        tvIronSafetyStatus =
            findViewById(R.id.tvIronSafetyStatus)

        etMaximumMinutes = findViewById(R.id.etMaximumMinutes)

        btnSaveIronDuration =
            findViewById(R.id.btnSaveIronDuration)

        btnStartTimerTest =
            findViewById(R.id.btnStartTimerTest)

        btnCancelTimer =
            findViewById(R.id.btnCancelTimer)

        tvTimerCountdown =
            findViewById(R.id.tvTimerCountdown)

        timerStatusContainer =
            findViewById(R.id.timerStatusContainer)
    }

    private fun setupHeader() {
        tvDeviceName.text = deviceName
        tvDeviceType.text = deviceType

        if (scheduleMode == MODE_IRON_DURATION) {
            tvScheduleTitle.text = "Iron Safety"
            tvScheduleSubtitle.text = "Set maximum ON duration"
        } else {
            tvScheduleTitle.text = "Device Schedule"
            tvScheduleSubtitle.text = "Set automatic ON and OFF times"
        }
    }

    private fun setupCorrectMode() {
        if (scheduleMode == MODE_IRON_DURATION) {
            lightScheduleSection.visibility = View.GONE
            ironDurationSection.visibility = View.VISIBLE
        } else {
            lightScheduleSection.visibility = View.VISIBLE
            ironDurationSection.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        btnScheduleBack.setOnClickListener {
            finish()
        }

        switchScheduleEnabled.setOnCheckedChangeListener { _, enabled ->
            updateLightScheduleStatus(enabled)
            updateLightScheduleControls(enabled)
        }

        btnSelectOnTime.setOnClickListener {
            if (switchScheduleEnabled.isChecked) {
                showOnTimePicker()
            }
        }

        btnSelectOffTime.setOnClickListener {
            if (switchScheduleEnabled.isChecked) {
                showOffTimePicker()
            }
        }

        btnSaveLightSchedule.setOnClickListener {
            saveLightSchedule()
        }

        btnClearLightSchedule.setOnClickListener {
            clearLightSchedule()
        }

        switchIronSafetyEnabled.setOnCheckedChangeListener { _, enabled ->
            updateIronSafetyStatus(enabled)
            updateIronControls(enabled)

            if (!enabled) {
                cancelIronTimer(showMessage = false)
            }
        }

        btnSaveIronDuration.setOnClickListener {
            saveIronSafetyDuration()
        }

        btnStartTimerTest.setOnClickListener {
            startIronTimerTest()
        }

        btnCancelTimer.setOnClickListener {
            cancelIronTimer(showMessage = true)
        }
    }

    private fun loadSavedSettings() {
        if (scheduleMode == MODE_IRON_DURATION) {
            loadIronSettings()
        } else {
            loadLightSchedule()
        }
    }

    // ----------------------------------------------------------------
    // Light schedule
    // ----------------------------------------------------------------

    private fun loadLightSchedule() {
        if (floorId.isEmpty() || roomId.isEmpty()) return
        
        repository.getDevice(floorId, roomId, deviceId,
            onSuccess = { device ->
                // Simulate schedule data parsing from device document if it existed
                // Since `Device` model doesn't currently hold onTime/offTime, we load it manually via raw db call or mock it if not present.
                // For a proper implementation we would update `Device` model, but for now we query raw Document reference to get schedule fields.
                com.example.smarthome.firebase.FirebaseManager.db.collection("houses")
                    .document("house1")
                    .collection("floors")
                    .document(floorId)
                    .collection("rooms")
                    .document(roomId)
                    .collection("devices")
                    .document(deviceId)
                    .get()
                    .addOnSuccessListener { doc ->
                        val enabled = doc.getBoolean("scheduleEnabled") ?: false
                        
                        val onTimeStr = doc.getString("scheduleOnTime") ?: "18:00"
                        val offTimeStr = doc.getString("scheduleOffTime") ?: "22:00"
                        
                        selectedOnHour = onTimeStr.substringBefore(":").toIntOrNull() ?: 18
                        selectedOnMinute = onTimeStr.substringAfter(":").toIntOrNull() ?: 0
                        
                        selectedOffHour = offTimeStr.substringBefore(":").toIntOrNull() ?: 22
                        selectedOffMinute = offTimeStr.substringAfter(":").toIntOrNull() ?: 0

                        switchScheduleEnabled.isChecked = enabled

                        tvOnTime.text = formatTime(selectedOnHour, selectedOnMinute)
                        tvOffTime.text = formatTime(selectedOffHour, selectedOffMinute)

                        updateLightScheduleStatus(enabled)
                        updateLightScheduleControls(enabled)
                    }
            },
            onError = {
                Toast.makeText(this, "Failed to load device schedule", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showOnTimePicker() {
        val dialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedOnHour = hourOfDay
                selectedOnMinute = minute

                tvOnTime.text = formatTime(
                    hourOfDay,
                    minute
                )
            },
            selectedOnHour,
            selectedOnMinute,
            false
        )

        dialog.setTitle("Select automatic ON time")
        dialog.show()
    }

    private fun showOffTimePicker() {
        val dialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                selectedOffHour = hourOfDay
                selectedOffMinute = minute

                tvOffTime.text = formatTime(
                    hourOfDay,
                    minute
                )
            },
            selectedOffHour,
            selectedOffMinute,
            false
        )

        dialog.setTitle("Select automatic OFF time")
        dialog.show()
    }

    private fun saveLightSchedule() {
        val enabled = switchScheduleEnabled.isChecked

        if (enabled && timesAreEqual()) {
            Toast.makeText(
                this,
                "ON time and OFF time cannot be the same",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val onTimeStr = String.format(Locale.getDefault(), "%02d:%02d", selectedOnHour, selectedOnMinute)
        val offTimeStr = String.format(Locale.getDefault(), "%02d:%02d", selectedOffHour, selectedOffMinute)
        
        val properties = mapOf(
            "scheduleEnabled" to enabled,
            "scheduleOnTime" to onTimeStr,
            "scheduleOffTime" to offTimeStr
        )
        
        repository.updateDeviceProperties(floorId, roomId, deviceId, properties)

        val message = if (enabled) {
            "$deviceName schedule saved"
        } else {
            "$deviceName schedule saved as disabled"
        }

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun clearLightSchedule() {
        val properties = mapOf(
            "scheduleEnabled" to false,
            "scheduleOnTime" to "18:00",
            "scheduleOffTime" to "22:00"
        )
        repository.updateDeviceProperties(floorId, roomId, deviceId, properties)

        selectedOnHour = 18
        selectedOnMinute = 0
        selectedOffHour = 22
        selectedOffMinute = 0

        switchScheduleEnabled.isChecked = false

        tvOnTime.text = formatTime(
            selectedOnHour,
            selectedOnMinute
        )

        tvOffTime.text = formatTime(
            selectedOffHour,
            selectedOffMinute
        )

        Toast.makeText(
            this,
            "Schedule cleared",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun timesAreEqual(): Boolean {
        return selectedOnHour == selectedOffHour &&
                selectedOnMinute == selectedOffMinute
    }

    private fun updateLightScheduleStatus(enabled: Boolean) {
        if (enabled) {
            tvScheduleEnabledStatus.text = "Enabled"
            tvScheduleEnabledStatus.setTextColor(
                getColor(R.color.schedule_enabled)
            )
        } else {
            tvScheduleEnabledStatus.text = "Disabled"
            tvScheduleEnabledStatus.setTextColor(
                getColor(R.color.schedule_disabled)
            )
        }
    }

    private fun updateLightScheduleControls(enabled: Boolean) {
        btnSelectOnTime.isEnabled = enabled
        btnSelectOffTime.isEnabled = enabled

        btnSelectOnTime.alpha = if (enabled) 1f else 0.45f
        btnSelectOffTime.alpha = if (enabled) 1f else 0.45f
    }

    // ----------------------------------------------------------------
    // Iron safety
    // ----------------------------------------------------------------

    private fun loadIronSettings() {
        if (floorId.isEmpty() || roomId.isEmpty()) return
        
        com.example.smarthome.firebase.FirebaseManager.db.collection("houses")
            .document("house1")
            .collection("floors")
            .document(floorId)
            .collection("rooms")
            .document(roomId)
            .collection("devices")
            .document(deviceId)
            .get()
            .addOnSuccessListener { doc ->
                val enabled = doc.getBoolean("safetyEnabled") ?: true
                val maximumMinutes = doc.getLong("maxOnDuration")?.toInt() ?: 1

                switchIronSafetyEnabled.isChecked = enabled
                etMaximumMinutes.setText(maximumMinutes.toString())

                updateIronSafetyStatus(enabled)
                updateIronControls(enabled)
            }
    }

    private fun saveIronSafetyDuration() {
        hideKeyboard()

        val durationText =
            etMaximumMinutes.text.toString().trim()

        if (durationText.isEmpty()) {
            etMaximumMinutes.error =
                "Enter maximum ON duration"

            etMaximumMinutes.requestFocus()
            return
        }

        val minutes = durationText.toIntOrNull()

        if (minutes == null) {
            etMaximumMinutes.error =
                "Enter a valid number"

            etMaximumMinutes.requestFocus()
            return
        }

        if (minutes < 1 || minutes > 180) {
            etMaximumMinutes.error =
                "Duration must be between 1 and 180 minutes"

            etMaximumMinutes.requestFocus()
            return
        }

        val properties = mapOf(
            "safetyEnabled" to switchIronSafetyEnabled.isChecked,
            "maxOnDuration" to minutes
        )
        repository.updateDeviceProperties(floorId, roomId, deviceId, properties)

        Toast.makeText(
            this,
            "Iron safety duration saved: $minutes minute(s)",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun startIronTimerTest() {
        if (!switchIronSafetyEnabled.isChecked) {
            Toast.makeText(
                this,
                "Enable iron safety first",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val minutes =
            etMaximumMinutes.text.toString().trim().toIntOrNull()

        if (minutes == null || minutes < 1 || minutes > 180) {
            etMaximumMinutes.error =
                "Enter a duration from 1 to 180 minutes"

            etMaximumMinutes.requestFocus()
            return
        }

        saveIronSafetyDuration()
        cancelIronTimer(showMessage = false)

        val totalMilliseconds =
            minutes * 60L * 1000L

        timerStatusContainer.visibility = View.VISIBLE
        btnCancelTimer.visibility = View.VISIBLE
        btnStartTimerTest.isEnabled = false
        btnStartTimerTest.alpha = 0.5f

        ironCountDownTimer = object : CountDownTimer(
            totalMilliseconds,
            1000L
        ) {
            override fun onTick(millisecondsUntilFinished: Long) {
                val totalSeconds =
                    millisecondsUntilFinished / 1000L

                val remainingMinutes =
                    totalSeconds / 60L

                val remainingSeconds =
                    totalSeconds % 60L

                tvTimerCountdown.text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    remainingMinutes,
                    remainingSeconds
                )
            }

            override fun onFinish() {
                tvTimerCountdown.text = "00:00"
                btnCancelTimer.visibility = View.GONE
                btnStartTimerTest.isEnabled = true
                btnStartTimerTest.alpha = 1f

                Toast.makeText(
                    this@ScheduleActivity,
                    "Maximum duration reached. Iron turned OFF.",
                    Toast.LENGTH_LONG
                ).show()

                repository.updateDeviceStatus(floorId, roomId, deviceId, "OFF")
            }
        }.start()
    }

    private fun cancelIronTimer(showMessage: Boolean) {
        ironCountDownTimer?.cancel()
        ironCountDownTimer = null

        timerStatusContainer.visibility = View.GONE
        btnCancelTimer.visibility = View.GONE
        btnStartTimerTest.isEnabled = true
        btnStartTimerTest.alpha = 1f

        if (showMessage) {
            Toast.makeText(
                this,
                "Iron timer cancelled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateIronSafetyStatus(enabled: Boolean) {
        if (enabled) {
            tvIronSafetyStatus.text = "Safety enabled"
            tvIronSafetyStatus.setTextColor(
                getColor(R.color.schedule_enabled)
            )
        } else {
            tvIronSafetyStatus.text = "Safety disabled"
            tvIronSafetyStatus.setTextColor(
                getColor(R.color.schedule_disabled)
            )
        }
    }

    private fun updateIronControls(enabled: Boolean) {
        etMaximumMinutes.isEnabled = enabled
        btnStartTimerTest.isEnabled = enabled

        etMaximumMinutes.alpha = if (enabled) 1f else 0.45f
        btnStartTimerTest.alpha = if (enabled) 1f else 0.45f
    }

    // ----------------------------------------------------------------
    // Utility functions
    // ----------------------------------------------------------------

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()

        calendar.set(
            Calendar.HOUR_OF_DAY,
            hour
        )

        calendar.set(
            Calendar.MINUTE,
            minute
        )

        return String.format(
            Locale.getDefault(),
            "%1\$tI:%1\$tM %1\$Tp",
            calendar
        )
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE)
                    as InputMethodManager

        currentFocus?.let { view ->
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                0
            )
        }
    }

    override fun onDestroy() {
        ironCountDownTimer?.cancel()
        ironCountDownTimer = null

        super.onDestroy()
    }
}