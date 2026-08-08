package com.example.smarthome.repository

import com.example.smarthome.firebase.FirebaseManager
import com.example.smarthome.model.DashboardSummary
import com.example.smarthome.model.Device
import com.example.smarthome.model.Floor
import com.google.firebase.firestore.ListenerRegistration

class FirebaseRepository {

    private val db = FirebaseManager.db

    private var floorListener: ListenerRegistration? = null
    private var roomListener: ListenerRegistration? = null
    private var deviceListener: ListenerRegistration? = null
    private var dashboardListener: ListenerRegistration? = null
    private var singleDeviceListener: ListenerRegistration? = null

    // FLOORS

    fun listenToFloors(
        onUpdate: (List<Floor>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        floorListener = db.collection("houses")
            .document("house1")
            .collection("floors")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val floors = mutableListOf<Floor>()

                for (document in snapshot.documents) {

                    floors.add(
                        Floor(
                            id = document.id,
                            name = document.getString("name") ?: ""
                        )
                    )
                }

                onUpdate(floors)
            }
    }

    // ROOMS

    fun listenToRooms(
        floorId: String,
        onUpdate: (List<String>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        roomListener = db.collection("houses")
            .document("house1")
            .collection("floors")
            .document(floorId)
            .collection("rooms")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val rooms = mutableListOf<String>()

                for (document in snapshot.documents) {
                    rooms.add(document.id)
                }

                onUpdate(rooms)
            }
    }

    // DEVICES

    fun listenToDevices(
        floorId: String,
        roomId: String,
        onUpdate: (List<Device>) -> Unit,
        onError: (Exception) -> Unit
    ) {

        deviceListener = db.collection("houses")
            .document("house1")
            .collection("floors")
            .document(floorId)
            .collection("rooms")
            .document(roomId)
            .collection("devices")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                val devices = mutableListOf<Device>()

                for (document in snapshot.documents) {

                    val device = Device(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        room = roomId,
                        type = document.getString("type") ?: "",
                        status = document.getString("status") ?: "OFF",
                        maxOnDuration = document.getLong("maxOnDuration") ?: 0,
                        autoOff = document.getBoolean("autoOff") ?: false
                    )

                    devices.add(device)
                }

                onUpdate(devices)
            }
    }

    fun getDevice(
        floorId: String,
        roomId: String,
        deviceId: String,
        onSuccess: (Device) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("houses")
            .document("house1")
            .collection("floors")
            .document(floorId)
            .collection("rooms")
            .document(roomId)
            .collection("devices")
            .document(deviceId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val device = Device(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        room = roomId,
                        type = document.getString("type") ?: "",
                        status = document.getString("status") ?: "OFF",
                        maxOnDuration = document.getLong("maxOnDuration") ?: 0,
                        autoOff = document.getBoolean("autoOff") ?: false
                    )
                    onSuccess(device)
                } else {
                    onError(Exception("Device not found"))
                }
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    // ALL FLOOR DEVICES
    private val floorDevicesListeners = mutableMapOf<String, ListenerRegistration>()

    fun listenToAllFloorDevices(
        floorId: String,
        onUpdate: (List<Device>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        listenToRooms(floorId, onUpdate = { rooms ->
            if (rooms.isEmpty()) {
                onUpdate(emptyList())
                return@listenToRooms
            }

            val allDevices = mutableMapOf<String, List<Device>>()
            rooms.forEach { roomId ->
                val listenerKey = "$floorId-$roomId"
                if (!floorDevicesListeners.containsKey(listenerKey)) {
                    val listener = db.collection("houses")
                        .document("house1")
                        .collection("floors")
                        .document(floorId)
                        .collection("rooms")
                        .document(roomId)
                        .collection("devices")
                        .addSnapshotListener { snapshot, error ->
                            if (error != null) {
                                onError(error)
                                return@addSnapshotListener
                            }
                            val devices = mutableListOf<Device>()
                            snapshot?.documents?.forEach { document ->
                                devices.add(
                                    Device(
                                        id = document.id,
                                        name = document.getString("name") ?: "",
                                        room = roomId,
                                        type = document.getString("type") ?: "",
                                        status = document.getString("status") ?: "OFF",
                                        maxOnDuration = document.getLong("maxOnDuration") ?: 0,
                                        autoOff = document.getBoolean("autoOff") ?: false
                                    )
                                )
                            }
                            allDevices[roomId] = devices
                            onUpdate(allDevices.values.flatten())
                        }
                    floorDevicesListeners[listenerKey] = listener
                }
            }
        }, onError = onError)
    }
    
    fun isDeviceActive(status: String): Boolean {
        val upperStatus = status.trim().uppercase(java.util.Locale.ROOT)
        return upperStatus == "ON" || upperStatus == "ONLINE" || upperStatus == "CONNECTED"
    }

    // DASHBOARD

    fun listenToDashboard(
        onUpdate: (DashboardSummary) -> Unit,
        onError: (Exception) -> Unit
    ) {

        dashboardListener = db.collection("houses")
            .document("house1")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null || !snapshot.exists()) {
                    return@addSnapshotListener
                }

                val summary = DashboardSummary(
                    devicesOn = snapshot.getLong("devicesOn")?.toInt() ?: 0,
                    onlineDevices = snapshot.getLong("onlineDevices")?.toInt() ?: 0,
                    alerts = snapshot.getLong("alerts")?.toInt() ?: 0,
                    energyUsage = snapshot.getDouble("energyUsage") ?: 0.0
                )

                onUpdate(summary)
            }
    }

    // UPDATE DEVICE

    fun updateDeviceStatus(
        floorId: String,
        roomId: String,
        deviceId: String,
        status: String
    ) {

        db.collection("houses")
            .document("house1")
            .collection("floors")
            .document(floorId)
            .collection("rooms")
            .document(roomId)
            .collection("devices")
            .document(deviceId)
            .update("status", status)
    }

    fun updateDeviceProperties(
        floorId: String,
        roomId: String,
        deviceId: String,
        properties: Map<String, Any>
    ) {
        db.collection("houses")
            .document("house1")
            .collection("floors")
            .document(floorId)
            .collection("rooms")
            .document(roomId)
            .collection("devices")
            .document(deviceId)
            .update(properties)
    }

    // SWITCH BOARD

    private var switchBoardListener: ListenerRegistration? = null

    fun listenToSwitchBoard(
        floorId: String,
        roomId: String,
        deviceId: String,
        onUpdate: (Map<String, Boolean>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        switchBoardListener?.remove()

        switchBoardListener = db.collection("houses")
            .document("house1")
            .collection("floors")
            .document(floorId)
            .collection("rooms")
            .document(roomId)
            .collection("devices")
            .document(deviceId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null || !snapshot.exists()) {
                    onUpdate(emptyMap())
                    return@addSnapshotListener
                }

                val switches = mutableMapOf<String, Boolean>()
                switches["light1"] = snapshot.getBoolean("light1") ?: false
                switches["fan"] = snapshot.getBoolean("fan") ?: false
                switches["ac"] = snapshot.getBoolean("ac") ?: false

                onUpdate(switches)
            }
    }

    fun updateSwitchState(
        floorId: String,
        roomId: String,
        deviceId: String,
        switchName: String,
        state: Boolean
    ) {
        val updates = mapOf(
            switchName to state,
            "status" to if (state) "ON" else "OFF"
        )
        db.collection("houses")
            .document("house1")
            .collection("floors")
            .document(floorId)
            .collection("rooms")
            .document(roomId)
            .collection("devices")
            .document(deviceId)
            .update(updates)
    }

    fun listenToDevice(
        floorId: String,
        roomId: String,
        deviceId: String,
        onUpdate: (Device?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        singleDeviceListener?.remove()

        singleDeviceListener = db
            .collection("houses")
            .document("house1")
            .collection("floors")
            .document(floorId)
            .collection("rooms")
            .document(roomId)
            .collection("devices")
            .document(deviceId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot == null || !snapshot.exists()) {
                    onUpdate(null)
                    return@addSnapshotListener
                }

                val device = Device(
                    id = snapshot.id,
                    name = snapshot.getString("name").orEmpty(),
                    room = roomId,
                    type = snapshot.getString("type").orEmpty(),
                    status = snapshot.getString("status")
                        ?: "DISCONNECTED",
                    maxOnDuration =
                        snapshot.getLong("maxOnDuration") ?: 0L,
                    autoOff =
                        snapshot.getBoolean("autoOff") ?: false
                )

                onUpdate(device)
            }
    }

    // REMOVE LISTENERS

    fun removeListeners() {
        floorListener?.remove()
        roomListener?.remove()
        deviceListener?.remove()
        dashboardListener?.remove()
        switchBoardListener?.remove()
        singleDeviceListener?.remove()

        floorListener = null
        roomListener = null
        deviceListener = null
        dashboardListener = null
        switchBoardListener = null
        singleDeviceListener = null
        
        floorDevicesListeners.values.forEach { it.remove() }
        floorDevicesListeners.clear()
    }
}