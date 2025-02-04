package com.example.rps

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // creating object for data storage between activities
        val sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        // creating editor to put data in it (otherwise can only read)
        val editor = sharedPreferences.edit()

        // get input field and save button
        val maxScoreInput: EditText = findViewById<EditText>(R.id.maxScoreInput)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            // get trimmed string from input field
            val maxScoreStr = maxScoreInput.text.toString().trim()
            // getting valid value
            var maxValue: Int = maxScoreStr.toIntOrNull() ?: 1

            // cant be less than 1
            if (maxValue < 1)
                maxValue = 1

            // saving value in storage
            editor.putInt("maxScore", maxValue)
            editor.apply()
            // closing settings activity
            finish()
        }
    }
}