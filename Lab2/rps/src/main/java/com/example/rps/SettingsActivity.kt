package com.example.rps

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)

        val maxScoreInput = findViewById<EditText>(R.id.maxScoreInput)
        val btnSave = findViewById<Button>(R.id.btnSave)
    }
}