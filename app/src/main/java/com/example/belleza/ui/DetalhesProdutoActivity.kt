package com.example.belleza.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.belleza.R
import com.example.belleza.databinding.ActivityDetalhesProdutoBinding
import com.example.belleza.model.Produto
import java.util.Locale

class DetalhesProdutoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesProdutoBinding
    private var quantidade = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalhesProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val produto = intent.getSerializableExtra("produto") as? Produto

        produto?.let {
            preencherDados(it)
        } ?: run {
            Toast.makeText(this, "Erro ao carregar produto", Toast.LENGTH_SHORT).show()
            finish()
        }

        configurarCliques()
    }

    private fun preencherDados(produto: Produto) {
        binding.txtNomeProduto.text = produto.titulo

        binding.txtPrecoProduto.text = String.format(Locale("pt", "BR"), "R$ %.2f", produto.preco)

        binding.txtDescricaoProduto.text = produto.descricao

        Glide.with(this)
            .load(produto.urlImagem)
            .placeholder(R.drawable.fundo_card) // imagem enquanto carrega
            .error(R.drawable.fundo_card)       // imagem se der erro
            .into(binding.imgProdutoDetalhe)
    }

    private fun configurarCliques() {
        binding.btnVoltar.setOnClickListener {
            finish()
        }

        binding.btnMais.setOnClickListener {
            quantidade++
            atualizarQuantidade()
        }

        binding.btnMenos.setOnClickListener {
            if (quantidade > 1) {
                quantidade--
                atualizarQuantidade()
            }
        }

        binding.btnAdicionarCarrinho.setOnClickListener {
            Toast.makeText(this, "$quantidade item(ns) adicionado(s) ao carrinho", Toast.LENGTH_SHORT).show()
            // Aqui você chamaria o seu ViewModel para salvar no banco do carrinho
        }

        binding.btnCompartilhar.setOnClickListener {
            Toast.makeText(this, "Link compartilhado!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarQuantidade() {
        binding.txtQuantidade.text = quantidade.toString()
    }
}