package com.example.belleza.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.belleza.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputSenha = findViewById<EditText>(R.id.inputSenha)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        btnEntrar.setOnClickListener {
            val emailDigitado = inputEmail.text.toString()
            val senhaDigitada = inputSenha.text.toString()

            if (emailDigitado.isEmpty() || senhaDigitada.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginComFirebase(emailDigitado, senhaDigitada)
        }
    }

    private fun loginComFirebase(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val erro = task.exception?.message ?: "E-mail ou senha incorretos. Tente novamente."
                    Toast.makeText(this, "Erro: $erro", Toast.LENGTH_LONG).show()
                }
            }
    }
}
