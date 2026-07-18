package com.example.smarthome.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getFloors() {

        db.collection("houses")
            .document("house1")
            .collection("floors")
            .get()
            .addOnSuccessListener { documents ->

                Log.d("FirestoreTest", "Floors retrieved successfully")

                for (document in documents) {
                    Log.d("FirestoreTest", "Floor ID: ${document.id}")
                    Log.d("FirestoreTest", "Floor Name: ${document.getString("name")}")
                }

            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreTest", "Error reading floors", exception)
            }
    }
}