package com.example.rps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // get buttons from the screen
        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnSettings = findViewById<Button>(R.id.btnSettings)

        // on click of any button
        // starting a new activity accordingly
        btnPlay.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

    }
}