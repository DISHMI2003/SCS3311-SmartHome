package com.example.smarthome.repository

import com.example.smarthome.firebase.FirebaseManager
import com.example.smarthome.model.Floor

class FirebaseRepository {

    private val db = FirebaseManager.db

    fun getFloors(

        onSuccess: (List<Floor>) -> Unit,

        onFailure: (Exception) -> Unit

    ) {

        db.collection("houses")
            .document("house1")
            .collection("floors")
            .get()
            .addOnSuccessListener { documents ->

                val floorList = mutableListOf<Floor>()

                for (document in documents) {

                    val floor = Floor(

                        id = document.id,

                        name = document.getString("name") ?: ""

                    )

                    floorList.add(floor)

                }

                onSuccess(floorList)

            }

            .addOnFailureListener {

                onFailure(it)

            }

    }

}