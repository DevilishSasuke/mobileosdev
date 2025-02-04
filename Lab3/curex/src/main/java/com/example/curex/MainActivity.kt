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
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val url = "https://www.cbr.ru/scripts/XML_daily.asp"
        val testText = findViewById<TextView>(R.id.testText)
        val dateText = findViewById<TextView>(R.id.dateText)

        GlobalScope.launch(Dispatchers.IO) {
            val xmlFileData = downloadXmlFile(url)

            withContext(Dispatchers.Main) {
                val parsedData = parseXmlData(xmlFileData, dateText)
                var str = ""
                for ((key, value) in parsedData)
                    str += "$key: $value\n"
                testText.text = str
            }
        }
    }

    private fun downloadXmlFile(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection

        val response = try {
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                reader.use { it.readText() }
            } else {
                "null"
            }
        } finally {
            connection.disconnect()
        }

        return response
    }

    private fun parseXmlData(xml: String, dateText: TextView): Map<String, Double> {
        val parserFactory = XmlPullParserFactory.newInstance()
        val parser = parserFactory.newPullParser()
        parser.setInput(StringReader(xml))

        val currencies = mutableMapOf<String, Double>()
        var eventType = parser.eventType
        var currencyName = ""
        var currencyRate = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "CharCode" -> currencyName = parser.nextText()
                        "Value" -> currencyRate = parser.nextText()
                        "ValCurs" -> {
                            val date = parser.getAttributeValue(null, "Date")
                            dateText.text = "Дата сбора данных: $date"
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "Valute" && currencyName.isNotEmpty()
                        && currencyRate.isNotEmpty()
                    ) {
                        currencies[currencyName] = getDoubleFromStr(currencyRate)
                        currencyName = ""
                        currencyRate = ""
                    }
                }
            }

            eventType = parser.next()
        }

        currencies["RUB"] = 1.0
        return currencies
    }

    private fun getDoubleFromStr(str: String): Double {
        val newStr = str.replace(",", ".")
        return newStr.toDouble()
    }
}