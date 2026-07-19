package com.example.smarthome.firebase

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {

    val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}