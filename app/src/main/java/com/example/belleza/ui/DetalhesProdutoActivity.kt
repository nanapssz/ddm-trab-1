package com.example.belleza.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.belleza.R
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.databinding.ActivityDetalhesProdutoBinding
import com.example.belleza.model.Produto
import com.example.belleza.repository.LojaRepository
import com.example.belleza.viewmodel.LojaViewModel
import com.example.belleza.viewmodel.LojaViewModelFactory
import java.util.Locale

class DetalhesProdutoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalhesProdutoBinding
    private lateinit var viewModel: LojaViewModel

    private var quantidade = 1
    private var produtoAtual: Produto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetalhesProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iniciarViewModel()

        val produto = intent.getSerializableExtra("produto") as? Produto

        if (produto == null) {
            Toast.makeText(this, "Erro ao carregar produto", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        produtoAtual = produto
        preencherDados(produto)
        configurarCliques()
        observarDados()
    }

    private fun iniciarViewModel() {
        val banco = BancoDeDadosApp.obterBancoDeDados(this)
        val repositorio = LojaRepository(banco.favoritoDao(), applicationContext)
        val factory = LojaViewModelFactory(repositorio)

        viewModel = ViewModelProvider(this, factory)[LojaViewModel::class.java]
    }

    private fun preencherDados(produto: Produto) {
        binding.txtNomeProduto.text = produto.titulo
        binding.txtPrecoProduto.text = String.format(Locale("pt", "BR"), "R$ %.2f", produto.preco)
        binding.txtDescricaoProduto.text = produto.descricao

        Glide.with(this)
            .load(produto.urlImagem)
            .placeholder(R.drawable.fundo_card)
            .error(R.drawable.fundo_card)
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
            val produto = produtoAtual

            if (produto == null) {
                Toast.makeText(this, "Produto inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.adicionarAoCarrinho(produto, quantidade)
        }

        binding.btnCompartilhar.setOnClickListener {
            Toast.makeText(this, "Link compartilhado!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observarDados() {
        viewModel.statusOperacao.observe(this) { sucesso ->
            if (sucesso) {
                Toast.makeText(
                    this,
                    "$quantidade item(ns) adicionado(s) ao carrinho",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Não foi possível adicionar ao carrinho",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun atualizarQuantidade() {
        binding.txtQuantidade.text = quantidade.toString()
    }
}