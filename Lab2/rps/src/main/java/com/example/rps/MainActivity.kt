package com.example.rps

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var userScore = 0;
    private var compScore = 0;
    private var maxScore = 3;
    private var choices = listOf("Камень", "Бумага", "Ножницы");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gameScore = findViewById<TextView>(R.id.gameScore)
        val maxScoreText = findViewById<TextView>(R.id.maxScore)
        val roundResult = findViewById<TextView>(R.id.roundResult)
        val btnRock = findViewById<Button>(R.id.btnRock)
        val btnPaper = findViewById<Button>(R.id.btnPaper)
        val btnScissors = findViewById<Button>(R.id.btnScissors)
        val btnBackToMenu = findViewById<Button>(R.id.btnBackToMenu)

        resetGame(gameScore, roundResult, maxScoreText)

        btnRock.setOnClickListener { playRound("Камень", gameScore, roundResult) }
        btnPaper.setOnClickListener { playRound("Бумага", gameScore, roundResult) }
        btnScissors.setOnClickListener { playRound("Ножницы", gameScore, roundResult) }

        btnBackToMenu.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }
    }

    private fun playRound(user: String, gameScore: TextView, roundResult: TextView) {
        val comp = choices[Random.nextInt(choices.size)]

        val result = checkWinner(user, comp)

        if (result > 0)
            userScore++
        if (result < 0)
            compScore++

        roundResult.text = getRoundResult(result)
        gameScore.text = getGameScoreStr(userScore, compScore)

        if (userScore == maxScore) {
            showWinnerToast("Пользователь")
            resetGame(gameScore, roundResult)
        }
        if (compScore == maxScore) {
            showWinnerToast("Приложение")
            resetGame(gameScore, roundResult)
        }
    }

    private fun checkWinner(user: String, comp: String): Int {
        /*
            1 - when user wins,
            0 - when draw,
            -1 - when computer wins
         */

        return when {
            user == comp -> 0
            user == "Камень" && comp == "Ножницы" -> 1
            user == "Ножницы" && comp == "Бумага" -> 1
            user == "Бумага" && comp == "Камень" -> 1
            else -> -1
        }
    }


    private fun getGameScoreStr(user: Int, comp: Int): String {
        return "Счёт: $user - $comp"
    }

    private fun getRoundResult(result: Int): String {
        return when {
            result == 1 -> "Результат раунда: \nПользователь победил"
            result == 0 -> "Результат раунда: \nНичья"
            result == -1 -> "Результат раунда: \nПриложение победило"
            else -> "Неверный результат раунда"
        }
    }

    private fun showWinnerToast(winner: String) {
        Toast.makeText(this, "Игра окончена. \nПобедитель: $winner", Toast.LENGTH_LONG).show()
    }

    private fun getMaxScoreStr(maxScore: Int): String {
        return "Игра идёт до: $maxScore"
    }

    private fun resetGame(gameScore: TextView,
                          roundResult: TextView,
                          maxScoreText: TextView? = null) {
        userScore = 0
        compScore = 0
        gameScore.text = getGameScoreStr(userScore, compScore)
        roundResult.text = ""
        maxScoreText?.text = getMaxScoreStr(maxScore)
    }
}

