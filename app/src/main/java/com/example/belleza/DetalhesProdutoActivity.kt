package com.example.belleza

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetalhesProdutoActivity : AppCompatActivity() {

    private lateinit var btnVoltar: ImageView
    private lateinit var btnMenos: TextView
    private lateinit var btnMais: TextView
    private lateinit var txtQuantidade: TextView

    private var quantidade = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_produto)

        btnVoltar = findViewById(R.id.btnVoltar)
        btnMenos = findViewById(R.id.btnMenos)
        btnMais = findViewById(R.id.btnMais)
        txtQuantidade = findViewById(R.id.txtQuantidade)

        btnVoltar.setOnClickListener {
            finish()
        }

        btnMais.setOnClickListener {
            quantidade++
            txtQuantidade.text = quantidade.toString()
        }

        btnMenos.setOnClickListener {
            if (quantidade > 0) {
                quantidade--
                txtQuantidade.text = quantidade.toString()
            }
        }
    }
}