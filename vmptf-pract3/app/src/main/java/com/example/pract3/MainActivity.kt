package com.example.pract3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editText1 = findViewById<EditText>(R.id.editText1)
        val editText2 = findViewById<EditText>(R.id.editText2)

        val btnPlus = findViewById<Button>(R.id.btnPlus)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val btnMultiply = findViewById<Button>(R.id.btnMultiply)
        val btnDivide = findViewById<Button>(R.id.btnDivide)

        val buttonCalculate = findViewById<Button>(R.id.buttonCalculate)

        var symbol : Char = '+'

        val btnGoToCalc6 = findViewById<Button>(R.id.btnGoToCalc6)

        btnGoToCalc6.setOnClickListener {
            val intent = Intent(this, Calc6Activity::class.java)
            startActivity(intent)
        }

        fun resetButtonsColor() {
            btnPlus.setBackgroundColor(resources.getColor(R.color.purple_500, theme))
            btnMinus.setBackgroundColor(resources.getColor(R.color.purple_500, theme))
            btnMultiply.setBackgroundColor(resources.getColor(R.color.purple_500, theme))
            btnDivide.setBackgroundColor(resources.getColor(R.color.purple_500, theme))
        }

        btnPlus.setOnClickListener {
            resetButtonsColor()
            btnPlus.setBackgroundColor(resources.getColor(R.color.purple_700, theme))
            symbol = '+'
        }

        btnMinus.setOnClickListener {
            resetButtonsColor()
            btnMinus.setBackgroundColor(resources.getColor(R.color.purple_700, theme))
            symbol = '-'
        }

        btnMultiply.setOnClickListener {
            resetButtonsColor()
            btnMultiply.setBackgroundColor(resources.getColor(R.color.purple_700, theme))
            symbol = '*'
        }

        btnDivide.setOnClickListener {
            resetButtonsColor()
            btnDivide.setBackgroundColor(resources.getColor(R.color.purple_700, theme))
            symbol = '/'
        }

        buttonCalculate.setOnClickListener {
            val text1 = editText1.text.toString()
            val text2 = editText2.text.toString()
            if (text1.isEmpty() || text2.isEmpty()){
                Toast.makeText(this, "Додайте значення", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val num1 = text1.toDoubleOrNull() ?: 0.0
            val num2 = text2.toDoubleOrNull() ?: 0.0

            val result: Double

            // 3. Вибір операції залежно від обраного символу
            when (symbol) {
                '+' -> result = num1 + num2
                '-' -> result = num1 - num2
                '*' -> result = num1 * num2
                '/' -> {
                    // Захист від ділення на нуль
                    if (num2 == 0.0) {
                        Toast.makeText(this, "Помилка: ділення на нуль!", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                    result = num1 / num2
                }
                else -> {
                    Toast.makeText(this, "Невідома операція", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            Toast.makeText(this, "Результат: $result", Toast.LENGTH_LONG).show()
        }
    }
}