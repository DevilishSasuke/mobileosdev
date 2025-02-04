package com.example.rps

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val maxScoreInput: EditText = findViewById<EditText>(R.id.maxScoreInput)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val maxScoreStr = maxScoreInput.text.toString().trim()
            var maxValue: Int = maxScoreStr.toIntOrNull() ?: 1

            if (maxValue < 1)
                maxValue = 1

            editor.putInt("maxScore", maxValue)
            editor.apply()
            finish()
        }
    }
}