package com.example.smarthome.repository

import android.util.Log
import com.example.smarthome.firebase.FirebaseManager
import com.example.smarthome.model.Floor
import com.google.firebase.firestore.ListenerRegistration

class FirebaseRepository {

    private val db = FirebaseManager.db

    private var floorListener: ListenerRegistration? = null

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

                val floorList = mutableListOf<Floor>()

                for (document in snapshot.documents) {

                    val floor = Floor(

                        id = document.id,

                        name = document.getString("name") ?: ""

                    )

                    floorList.add(floor)

                }

                onUpdate(floorList)

            }

    }

    fun removeListeners() {

        floorListener?.remove()

    }

}