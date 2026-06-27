package com.example.belleza

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Liga este código à sua tela visual de login
        setContentView(R.layout.activity_login)

        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputSenha = findViewById<EditText>(R.id.inputSenha)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        btnEntrar.setOnClickListener {
            val emailDigitado = inputEmail.text.toString()
            val senhaDigitada = inputSenha.text.toString()

            if (emailDigitado == "camila@email.com" && senhaDigitada == "123456") {

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)

                finish()

            } else {
                Toast.makeText(this, "E-mail ou senha incorretos!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}