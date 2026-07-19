package com.example.smarthome

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smarthome.repository.FirebaseRepository

class MainActivity : AppCompatActivity() {

    private lateinit var repository: FirebaseRepository

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

        repository = FirebaseRepository()

        repository.listenToFloors(

            onUpdate = { floors ->

                Log.d("Realtime", "Firestore Updated")

                Log.d("Realtime", "Total Floors : ${floors.size}")

                floors.forEach { floor ->

                    Log.d("Realtime", "ID   : ${floor.id}")

                    Log.d("Realtime", "Name : ${floor.name}")

                }

            },

            onError = { exception ->

                Log.e(
                    "Realtime",
                    "Firestore Error",
                    exception
                )

            }

        )

    }

    override fun onDestroy() {

        super.onDestroy()

        repository.removeListeners()

    }

}