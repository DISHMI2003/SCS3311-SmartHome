package com.example.smarthome

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smarthome.repository.FirebaseRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val repository = FirebaseRepository()

        repository.getFloors(

            onSuccess = { floors ->

                Log.d("Firestore", "Total Floors : ${floors.size}")

                floors.forEach { floor ->

                    Log.d("Firestore", "ID   : ${floor.id}")
                    Log.d("Firestore", "Name : ${floor.name}")

                }

            },

            onFailure = { exception ->

                Log.e(
                    "Firestore",
                    "Error retrieving floors",
                    exception
                )

            }

        )

    }

}