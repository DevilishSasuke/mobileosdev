package com.example.curex

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.HttpURLConnection
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val testText = findViewById<TextView>(R.id.testText)
        val url = "https://www.cbr.ru/scripts/XML_daily.asp"

        GlobalScope.launch(Dispatchers.IO) {
            val xmlFileData = downloadXmlFile(url)

            withContext(Dispatchers.Main) {
                testText.text = xmlFileData
            }
        }
    }

    private fun downloadXmlFile(url: String): String? {
        val connection = URL(url).openConnection() as HttpURLConnection

        val response = try {
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                reader.use { it.readText() }
            } else {
                null
            }
        } finally {
            connection.disconnect()
        }

        return response
    }


}