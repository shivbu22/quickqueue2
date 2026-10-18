package com.example.quickqueue

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {

    private lateinit var queueRef: DatabaseReference
    private lateinit var txtQueueInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnScanQR = findViewById<Button>(R.id.btnScanQR)
        txtQueueInfo = findViewById(R.id.txtQueueInfo)

        btnScanQR.setOnClickListener {
            startQRScanner()
        }

        // Initialize Firebase Database
        queueRef = FirebaseDatabase.getInstance().getReference("queues")

        // Listen to real-time queue updates
        listenToQueueUpdates()
    }

    private fun startQRScanner() {
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setPrompt("Scan a QR code")
        intentIntegrator.setOrientationLocked(false)
        intentIntegrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                joinQueue(result.contents)
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun joinQueue(queueId: String) {
        // Add user to queue in Firebase
        val userQueueRef = queueRef.child(queueId).push()
        userQueueRef.setValue("user123") // Replace with actual user ID

        Toast.makeText(this, "Joined queue!", Toast.LENGTH_SHORT).show()
    }

    private fun listenToQueueUpdates() {
        queueRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Display queue information
                txtQueueInfo.text = "Queue Position: ${snapshot.childrenCount}"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load data.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
