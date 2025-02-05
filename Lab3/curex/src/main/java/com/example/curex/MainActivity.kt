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

        val dateText = findViewById<TextView>(R.id.dateText) // data actuality text
        val userInputValue = findViewById<EditText>(R.id.userInputValue)
        val exchangedValue = findViewById<EditText>(R.id.exchangedValue)
        val currOne = findViewById<Spinner>(R.id.currOne) // spinner of first picked currency
        val currTwo = findViewById<Spinner>(R.id.currTwo) // spinner of second picked currency
        val btnExc = findViewById<Button>(R.id.btnExc)

        // calculating value only by clicking on the button
        btnExc.setOnClickListener {
            if (currencies.isNotEmpty()) {
                val firstCur = currOne.selectedItem.toString()
                val secondCur = currTwo.selectedItem.toString()
                val userValue = userInputValue.text.toString().toDouble()

                if (firstCur == secondCur) {
                    // no need to calcuate
                    exchangedValue.setText(String.format("%.2f", userValue))
                }
                else {
                    // getting calculated value
                    val value = exchangeCurrencies(userValue,
                        currencies[firstCur]!!.toDouble(),
                        currencies[secondCur]!!.toDouble())
                    exchangedValue.setText(String.format("%.2f", value))
                }

            }
        }

        // using coroutine launch to read xml in IO thread
        // because connection can't be opened in main thread
        GlobalScope.launch(Dispatchers.IO) {
            val xmlFileData = downloadXmlFile(url)

            // then switching back to main thread
            // because ui must be changed here
            withContext(Dispatchers.Main) {
                // getting handled currencies
                val parsedData = parseXmlData(xmlFileData, dateText)
                currencies = parsedData
                // getting list of currencies names
                var currenciesCodes = currencies.keys.sorted()

                // setting up spinner values
                if (currenciesCodes.isNotEmpty()) {
                    val adapter = getSpinnerAdapter(currOne, currenciesCodes.toList())
                    currOne.adapter = adapter
                    currOne.setSelection(0)
                    currTwo.adapter = adapter
                    currTwo.setSelection(0)
                }

                // is inactive until data load
                btnExc.isFocusable = true
            }
        }
    }

    private fun downloadXmlFile(url: String): String {
        /*
            downloading xml file from url parameter by sending get request
            return response of this request:
                if successful - xml in String format
                otherwise string "null"

            args:
                url: String - url of site with xml file

         */

        // creating new connection object
        val connection = URL(url).openConnection() as HttpURLConnection

        val response = try {
            // setting up request method
            connection.requestMethod = "GET"
            // connecting with configured settings
            connection.connect()

            // if successful
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                // creating stream to read data
                // buffer reader - to read by strings
                // input reader - to read chars from bytes
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                // reading all data using buffered reader
                reader.use { it.readText() }
            } else {
                // otherwise return useless string
                "null"
            }
        }
        finally { connection.disconnect() } // closing the connection

        return response
    }

    private fun parseXmlData(xml: String, dateText: TextView): Map<String, Double> {
        /*
            parsing data from xml in string format to currency map
            of the type: key: currency code (3 letters), value: double value of current course
            return created currency map

            args:
                xml: String - xml in string format
                dateText: TextView - element that needed to show data actuality
         */

        // creating new parser factory (to generate new parser)
        val parserFactory = XmlPullParserFactory.newInstance()
        // generating new parser object
        val parser = parserFactory.newPullParser()
        // setting input type
        // for us is string reader
        // because xml was written in string earlier
        parser.setInput(StringReader(xml))

        // creating a map to storage data
        val currencies = mutableMapOf<String, Double>()
        var eventType = parser.eventType // event type of current string in xml
        var currencyName = ""
        var currencyRate = ""

        // through the all file
        while (eventType != XmlPullParser.END_DOCUMENT) {
            // getting event type
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    // if useful tag for us
                    when (parser.name) {
                        // rememeber it
                        "CharCode" -> currencyName = parser.nextText()
                        "Value" -> currencyRate = parser.nextText()
                        "ValCurs" -> {
                            val date = parser.getAttributeValue(null, "Date")
                            dateText.text = "По курсу валют от \n$date"
                        }
                    }
                }
                // if end of current Valute tag
                XmlPullParser.END_TAG -> {
                    // saving all remembered data
                    if (parser.name == "Valute" && currencyName.isNotEmpty()
                        && currencyRate.isNotEmpty()
                    ) {
                        currencies[currencyName] = getDoubleFromStr(currencyRate)
                        currencyName = ""
                        currencyRate = ""
                    }
                }
            }

            // reading next line
            eventType = parser.next()
        }

        // adding ruble because it is not in the xml file
        currencies["RUB"] = 1.0
        return currencies
    }

    private fun exchangeCurrencies(value: Double, course1: Double, course2: Double): Double {
        /*
            returns exchanged value of the entered number from currency 1 to currency 2

            args:
                value: Double - number that was entered by user
                course1: Double - course of ther first picked currency from the list
                course2: Double - course of ther second picked currency from the list
         */

        // getting ratio of two courses relatively to ruble
        val ratio: Double = course1 / course2

        return ratio * value // mulpiplying it on entereg number
    }

    /* utility functions to simplify code */

    private fun getDoubleFromStr(str: String): Double {
        val newStr = str.replace(",", ".")
        return newStr.toDouble()
    }

    private fun getSpinnerAdapter(spinner: Spinner, items: List<String>): ArrayAdapter<String> {
        // creating new adapter with all currency codes
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        // setting up default drop down list
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return adapter
    }
}