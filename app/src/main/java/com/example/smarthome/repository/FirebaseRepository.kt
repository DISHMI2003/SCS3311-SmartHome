package com.example.smarthome.repository

import com.example.smarthome.firebase.FirebaseManager
import com.example.smarthome.model.Device
import com.example.smarthome.model.Floor
import com.google.firebase.firestore.ListenerRegistration

class FirebaseRepository {

    private val db = FirebaseManager.db

    private var floorListener: ListenerRegistration? = null
    private var roomListener: ListenerRegistration? = null
    private var deviceListener: ListenerRegistration? = null

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

                if (snapshot == null) return@addSnapshotListener

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

                if (snapshot == null) return@addSnapshotListener

                val rooms = mutableListOf<String>()

                for (document in snapshot.documents) {

                    rooms.add(document.id)

                }

                onUpdate(rooms)

            }

    }

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

                if (snapshot == null) return@addSnapshotListener

                val devices = mutableListOf<Device>()

                for (document in snapshot.documents) {

                    devices.add(

                        Device(

                            id = document.id,

                            name = document.getString("name") ?: "",

                            room = roomId,

                            status = document.getString("status") ?: "OFF",

                            type = document.getString("type") ?: "",

                            maxOnDuration = document.getLong("maxOnDuration") ?: 0,

                            autoOff = document.getBoolean("autoOff") ?: false

                        )

                    )

                }

                onUpdate(devices)

            }

    }

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

    fun removeListeners() {

        floorListener?.remove()
        roomListener?.remove()
        deviceListener?.remove()

    }

}