package com.example.curex

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader
import java.net.URL
import java.net.HttpURLConnection
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class MainActivity : AppCompatActivity() {
    private val url = "https://www.cbr.ru/scripts/XML_daily.asp"
    private var currencies: Map<String, Double> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val dateText = findViewById<TextView>(R.id.dateText)
        val userInputValue = findViewById<EditText>(R.id.userInputValue)
        val exchangedValue = findViewById<EditText>(R.id.exchangedValue)
        val currOne = findViewById<Spinner>(R.id.currOne)
        val currTwo = findViewById<Spinner>(R.id.currTwo)
        val btnExc = findViewById<Button>(R.id.btnExc)

        btnExc.setOnClickListener {
            if (currencies.isNotEmpty()) {
                val firstCur = currOne.selectedItem.toString()
                val secondCur = currTwo.selectedItem.toString()
                val userValue = userInputValue.text.toString().toDouble()

                if (firstCur == secondCur) {
                    exchangedValue.setText(String.format("%.2f", userValue))
                }
                else {
                    val value = exchangeCurrencies(userValue,
                        currencies[firstCur]!!.toDouble(),
                        currencies[secondCur]!!.toDouble())
                    exchangedValue.setText(String.format("%.2f", value))
                }

            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            val xmlFileData = downloadXmlFile(url)

            withContext(Dispatchers.Main) {
                val parsedData = parseXmlData(xmlFileData, dateText)
                currencies = parsedData
                var currenciesCodes = currencies.keys.sorted()

                if (currenciesCodes.isNotEmpty()) {
                    val adapter = getSpinnerAdapter(currOne, currenciesCodes.toList())
                    currOne.adapter = adapter
                    currOne.setSelection(0)
                    currTwo.adapter = adapter
                    currTwo.setSelection(0)
                }

                btnExc.isFocusable = true
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
        }
        finally { connection.disconnect() }

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

    private fun exchangeCurrencies(value: Double, course1: Double, course2: Double): Double {
        val rel: Double = course1 / course2

        return rel * value
    }

    private fun getDoubleFromStr(str: String): Double {
        val newStr = str.replace(",", ".")
        return newStr.toDouble()
    }

    private fun getSpinnerAdapter(spinner: Spinner, items: List<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return adapter
    }
}