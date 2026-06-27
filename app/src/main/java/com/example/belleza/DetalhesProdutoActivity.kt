package com.example.belleza

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.belleza.model.Produto

class DetalhesProdutoActivity : AppCompatActivity() {

    private lateinit var btnVoltar: ImageView
    private lateinit var btnMenos: TextView
    private lateinit var btnMais: TextView
    private lateinit var txtQuantidade: TextView
    private lateinit var btnAdicionarCarrinho: View
    
    private lateinit var imgProduto: ImageView
    private lateinit var txtNome: TextView
    private lateinit var txtPreco: TextView
    private lateinit var txtDescricao: TextView

    private var quantidade = 1
    private var produto: Produto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_produto)

        // Recuperar o produto enviado pela Intent
        produto = intent.getSerializableExtra("produto") as? Produto

        iniciarComponentes()
        configurarCliques()
        preencherDados()
    }

    private fun iniciarComponentes() {
        btnVoltar = findViewById(R.id.btnVoltar)
        btnMenos = findViewById(R.id.btnMenos)
        btnMais = findViewById(R.id.btnMais)
        txtQuantidade = findViewById(R.id.txtQuantidade)
        btnAdicionarCarrinho = findViewById(R.id.btnAdicionarCarrinho)
        
        imgProduto = findViewById(R.id.imgProdutoDetalhe)
        txtNome = findViewById(R.id.txtNomeProduto)
        txtPreco = findViewById(R.id.txtPrecoProduto)
        txtDescricao = findViewById(R.id.txtDescricaoProduto)
        
        txtQuantidade.text = quantidade.toString()
    }

    private fun preencherDados() {
        produto?.let {
            txtNome.text = it.titulo
            txtPreco.text = "R$ ${String.format("%.2f", it.preco)}"
            txtDescricao.text = it.descricao
            
            Glide.with(this)
                .load(it.urlImagem)
                .placeholder(R.drawable.ic_launcher_background)
                .into(imgProduto)
        }
    }

    private fun configurarCliques() {
        btnVoltar.setOnClickListener {
            finish()
        }

        btnMais.setOnClickListener {
            quantidade++
            txtQuantidade.text = quantidade.toString()
        }

        btnMenos.setOnClickListener {
            if (quantidade > 1) {
                quantidade--
                txtQuantidade.text = quantidade.toString()
            }
        }

        btnAdicionarCarrinho.setOnClickListener {
            Toast.makeText(this, "${produto?.titulo} adicionado ao carrinho ($quantidade)", Toast.LENGTH_SHORT).show()
            // Aqui futuramente chamaremos o ViewModel para salvar no banco/carrinho
        }
    }
}
