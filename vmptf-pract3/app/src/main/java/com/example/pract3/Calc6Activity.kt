package com.example.pract3

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Calc6Activity : AppCompatActivity() {

    private val FILE_NAME = "calculator_history.txt"
    private var currentResult = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calc6)

        // 1. Ініціалізація екранів
        val etExpression = findViewById<EditText>(R.id.etExpression)
        val tvLiveResult = findViewById<TextView>(R.id.tvLiveResult)

        // 2. Ініціалізація кнопок операцій та дій
        val bPlus = findViewById<Button>(R.id.bPlus)
        val bMinus = findViewById<Button>(R.id.bMinus)
        val bMultiply = findViewById<Button>(R.id.bMultiply)
        val bDivide = findViewById<Button>(R.id.bDivide)
        val bPower = findViewById<Button>(R.id.bPower)
        val bMod = findViewById<Button>(R.id.bMod)
        val bClear = findViewById<Button>(R.id.bClear)
        val bSaveToFile = findViewById<Button>(R.id.bSaveToFile)
        val bShowHistory = findViewById<Button>(R.id.bShowHistory)

        // 3. Ініціалізація цифрових кнопок
        val digitButtons = listOf(
            R.id.b0 to "0", R.id.b1 to "1", R.id.b2 to "2", R.id.b3 to "3",
            R.id.b4 to "4", R.id.b5 to "5", R.id.b6 to "6", R.id.b7 to "7",
            R.id.b8 to "8", R.id.b9 to "9", R.id.bDot to "."
        )

        // 🔒 ЗАХИСТ ВКАЗІВНИКА ТА КЛАВІАТУРИ
        etExpression.showSoftInputOnFocus = false // Вимикаємо системну клавіатуру
        etExpression.setOnClickListener {
            etExpression.setSelection(etExpression.text.length) // Завжди кидаємо курсор в кінець
        }

        // 📝 ЛОГІКА ДЛЯ ЦИФР ТА КРАПКИ
        digitButtons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener {
                etExpression.append(value)
                etExpression.setSelection(etExpression.text.length)
            }
        }

        // 🛠️ ФУНКЦІЯ РОЗУМНОГО ДОДАВАННЯ ЗНАКУ (ІЗ ЗАМІНОЮ СТАРОГО)
        fun appendOperator(op: String) {
            val text = etExpression.text.toString()
            if (text.isEmpty()) return // На початку ряду знак ставити не можна

            val lastChar = text.last()
            val operators = listOf('+', '-', '*', '/', '^', '%')

            if (lastChar in operators) {
                // Якщо знак уже є — стираємо його і пишемо новий
                val newText = text.dropLast(1) + op
                etExpression.setText(newText)
            } else {
                etExpression.append(op)
            }
            etExpression.setSelection(etExpression.text.length)
        }

        // Прив'язуємо знаки до розумної функції
        bPlus.setOnClickListener { appendOperator("+") }
        bMinus.setOnClickListener { appendOperator("-") }
        bMultiply.setOnClickListener { appendOperator("*") }
        bDivide.setOnClickListener { appendOperator("/") }
        bPower.setOnClickListener { appendOperator("^") }
        bMod.setOnClickListener { appendOperator("%") }

        // 🔴 КНОПКА ОЧИЩЕННЯ (C)
        bClear.setOnClickListener {
            etExpression.setText("")
            tvLiveResult.text = "= 0"
            currentResult = "0"
        }

        // 🧠 АВТОМАТИЧНИЙ ПІДРАХУНОК З ПРІОРИТЕТАМИ НА ЛЬОТУ
        etExpression.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val expression = s.toString()
                if (expression.isEmpty()) {
                    tvLiveResult.text = "= 0"
                    currentResult = "0"
                    return
                }

                try {
                    // Розбиваємо рядок на числа та знаки
                    val numbers = expression.split(Regex("[+\\-*/^%]")).map { it.toDouble() }.toMutableList()
                    val operators = expression.filter { it in listOf('+', '-', '*', '/', '^', '%') }.map { it }.toMutableList()

                    // Етап 1: Виконуємо операції високого пріоритету (^, *, /, %)
                    var i = 0
                    while (i < operators.size) {
                        val op = operators[i]
                        if (op == '^' || op == '*' || op == '/' || op == '%') {
                            val num1 = numbers[i]
                            val num2 = numbers[i + 1]
                            var localRes = 0.0

                            when (op) {
                                '^' -> localRes = Math.pow(num1, num2)
                                '*' -> localRes = num1 * num2
                                '%' -> localRes = num1 % num2
                                '/' -> {
                                    if (num2 == 0.0) {
                                        tvLiveResult.text = "= Ділення на 0"
                                        return
                                    }
                                    localRes = num1 / num2
                                }
                            }
                            numbers[i] = localRes
                            numbers.removeAt(i + 1)
                            operators.removeAt(i)
                        } else {
                            i++
                        }
                    }

                    // Етап 2: Виконуємо операції нижчого пріоритетного рівня (+, -)
                    var res = numbers[0]
                    for (j in operators.indices) {
                        val nextNumber = numbers[j + 1]
                        when (operators[j]) {
                            '+' -> res += nextNumber
                            '-' -> res -= nextNumber
                        }
                    }

                    // Оновлюємо результат без зайвих нулів, якщо число ціле
                    currentResult = if (res % 1 == 0.0) res.toInt().toString() else res.toString()
                    tvLiveResult.text = "= $currentResult"

                } catch (e: Exception) {
                    // Ігноруємо помилки під час введення (наприклад, коли вираз закінчується знаком)
                }
            }
        })

        // 💾 КНОПКА ЗБЕРЕЖЕННЯ ПОСЛІДОВНОСТІ У ФАЙЛ
        bSaveToFile.setOnClickListener {
            val expression = etExpression.text.toString()
            if (expression.isEmpty() || expression.last() in listOf('+', '-', '*', '/', '^', '%')) {
                Toast.makeText(this, "Завершіть вираз перед збереженням", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val logEntry = "$expression = $currentResult"

            try {
                openFileOutput(FILE_NAME, MODE_APPEND).use { output ->
                    output.write(("$logEntry\n").toByteArray())
                }
                Toast.makeText(this, "Послідовність збережена!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Помилка запису файлу", Toast.LENGTH_SHORT).show()
            }
        }

        // 📂 КНОПКА ПЕРЕГЛЯДУ ІСТОРІЇ З ФАЙЛУ
        bShowHistory.setOnClickListener {
            try {
                val fileExists = fileList().contains(FILE_NAME)
                if (!fileExists) {
                    Toast.makeText(this, "Історія порожня", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val historyText = openFileInput(FILE_NAME).bufferedReader().use { it.readText() }
                Toast.makeText(this, historyText.ifEmpty { "Історія порожня" }, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Помилка читання файлу", Toast.LENGTH_SHORT).show()
            }
        }
    }
}