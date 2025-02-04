package com.example.rps

import android.content.Context
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
    // all available game choises
    private var choices = listOf("Камень", "Бумага", "Ножницы");

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // show activity layout
        setContentView(R.layout.activity_main)

        // get all data from layout
        val gameScore = findViewById<TextView>(R.id.gameScore)
        val maxScoreText = findViewById<TextView>(R.id.maxScore)
        // shows result of previous round (even from previous game)
        val roundResult = findViewById<TextView>(R.id.roundResult)
        val btnRock = findViewById<Button>(R.id.btnRock)
        val btnPaper = findViewById<Button>(R.id.btnPaper)
        val btnScissors = findViewById<Button>(R.id.btnScissors)
        val btnBackToMenu = findViewById<Button>(R.id.btnBackToMenu)

        // get user configured max score
        val sharedPreferences = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        // user chosen or default(3) if was not configured
        maxScore = sharedPreferences.getInt("maxScore", 3)

        // forcely reset all texts to correct
        resetGame(gameScore, roundResult, maxScoreText)

        // listen for player choises by buttons
        btnRock.setOnClickListener { playRound("Камень", gameScore, roundResult) }
        btnPaper.setOnClickListener { playRound("Бумага", gameScore, roundResult) }
        btnScissors.setOnClickListener { playRound("Ножницы", gameScore, roundResult) }

        // return player to menu
        btnBackToMenu.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }
    }

    private fun playRound(user: String, gameScore: TextView, roundResult: TextView) {
        /*
            imitates 1 round from real game versus computer

            args:
                user: String - user choice in text format
                gameScore: TextView - current game score text on screen
                roundResult: TextView - free space to show round results for player
         */

        // imitate computer choice with random pick
        val comp = choices[Random.nextInt(choices.size)]

        // get result in number value
        val result = checkWinner(user, comp)

        // changing scores
        if (result > 0)
            userScore++
        if (result < 0)
            compScore++

        // changing texts on screen
        roundResult.text = getRoundResult(result)
        gameScore.text = getGameScoreStr(userScore, compScore)

        // if someone win
        if (userScore == maxScore || compScore == maxScore) {
            // show toast notification with winner name
            val winner = if (userScore > compScore) "Пользователь" else "Приложение"
            showWinnerToast(winner)
            resetGame(gameScore)
        }
    }

    private fun checkWinner(user: String, comp: String): Int {
        /*
            1 - when user wins,
            0 - when draw,
            -1 - when computer wins

            args:
                user: String - user choice in text format
                comp: String - comp random choice in text format
         */

        return when {
            user == comp -> 0
            user == "Камень" && comp == "Ножницы" -> 1
            user == "Ножницы" && comp == "Бумага" -> 1
            user == "Бумага" && comp == "Камень" -> 1
            else -> -1
        }
    }

    /* utility functions to simplify code */

    private fun getGameScoreStr(user: Int, comp: Int): String {
        return "Текущий счёт: $user - $comp"
    }

    private fun getRoundResult(result: Int): String {
        return when (result) {
            1 -> "Результат прошлого раунда: \nПользователь победил"
            0 -> "Результат прошлого раунда: \nНичья"
            -1 -> "Результат прошлого раунда: \nПриложение победило"
            else -> "Неверный результат раунда"
        }
    }

    private fun showWinnerToast(winner: String) {
        Toast.makeText(this, "Игра окончена. " +
                "\nПобедитель: $winner", Toast.LENGTH_LONG).show()
    }

    private fun getMaxScoreStr(maxScore: Int): String {
        return "Игра идёт до $maxScore очков"
    }

    // set required parameters to default statement
    private fun resetGame(gameScore: TextView,
                          roundResult: TextView? = null,
                          maxScoreText: TextView? = null) {
        userScore = 0
        compScore = 0
        gameScore.text = getGameScoreStr(userScore, compScore)
        roundResult?.text = ""
        maxScoreText?.text = getMaxScoreStr(maxScore)
    }
}

