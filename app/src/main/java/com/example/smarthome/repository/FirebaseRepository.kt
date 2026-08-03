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

    // REMOVE LISTENERS

    fun removeListeners() {

        floorListener?.remove()
        roomListener?.remove()
        deviceListener?.remove()
        dashboardListener?.remove()
    }
}