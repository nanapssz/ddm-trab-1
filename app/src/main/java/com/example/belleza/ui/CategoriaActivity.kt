package com.example.belleza.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.belleza.R
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.databinding.ActivityCategoriaBinding
import com.example.belleza.model.Produto
import com.example.belleza.repository.LojaRepository
import com.example.belleza.viewmodel.LojaViewModel
import com.example.belleza.viewmodel.LojaViewModelFactory
import com.example.belleza.adapter.ProdutoCategoriaAdapter

class CategoriaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriaBinding
    private lateinit var viewModel: LojaViewModel
    private lateinit var adapter: ProdutoCategoriaAdapter

    private var categoriaAtual: String = CATEGORIA_TUDO
    private var produtosAtuais: List<Produto> = emptyList()
    private var ordenarMenorPreco = true

    companion object {
        const val EXTRA_CATEGORIA = "categoria"

        const val CATEGORIA_TUDO = "tudo"
        const val CATEGORIA_SKINCARE = "skincare"
        const val CATEGORIA_MAQUIAGEM = "maquiagem"
        const val CATEGORIA_CABELOS = "cabelos"
        const val CATEGORIA_PERFUME = "perfume"

        fun abrir(activity: AppCompatActivity, categoria: String) {
            val intent = Intent(activity, CategoriaActivity::class.java)
            intent.putExtra(EXTRA_CATEGORIA, categoria)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iniciarViewModel()
        configurarRecyclerView()
        configurarCliques()
        observarDados()

        val categoriaRecebida = intent.getStringExtra(EXTRA_CATEGORIA) ?: CATEGORIA_TUDO
        carregarCategoria(categoriaRecebida)
    }

    private fun iniciarViewModel() {
        val banco = BancoDeDadosApp.obterBancoDeDados(this)
        val repositorio = LojaRepository(banco.favoritoDao(), applicationContext)
        val factory = LojaViewModelFactory(repositorio)

        viewModel = ViewModelProvider(this, factory)[LojaViewModel::class.java]
    }

    private fun configurarRecyclerView() {
        adapter = ProdutoCategoriaAdapter(
            onProdutoClick = { produto ->
                val intent = Intent(this, DetalhesProdutoActivity::class.java)
                intent.putExtra("produto", produto)
                startActivity(intent)
            },
            onFavoritoClick = { produto ->
                viewModel.favoritarProduto(produto)
                Toast.makeText(this, "${produto.titulo} salvo nos favoritos", Toast.LENGTH_SHORT).show()
            },
            onAdicionarCarrinhoClick = { produto ->
                viewModel.adicionarAoCarrinho(produto)
                Toast.makeText(this, "${produto.titulo} adicionado ao carrinho", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerProdutosCategoria.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerProdutosCategoria.adapter = adapter
    }

    private fun configurarCliques() {
        binding.btnVoltarCategoria.setOnClickListener {
            finish()
        }

        binding.chipTudo.setOnClickListener {
            carregarCategoria(CATEGORIA_TUDO)
        }

        binding.chipSkincare.setOnClickListener {
            carregarCategoria(CATEGORIA_SKINCARE)
        }

        binding.chipMaquiagem.setOnClickListener {
            carregarCategoria(CATEGORIA_MAQUIAGEM)
        }

        binding.chipCabelos.setOnClickListener {
            carregarCategoria(CATEGORIA_CABELOS)
        }

        binding.chipPerfume.setOnClickListener {
            carregarCategoria(CATEGORIA_PERFUME)
        }

        binding.txtOrdenar.setOnClickListener {
            ordenarProdutosPorPreco()
        }

        binding.btnFiltroCategoria.setOnClickListener {
            Toast.makeText(this, "Filtro em desenvolvimento", Toast.LENGTH_SHORT).show()
        }

        binding.bottomNavigationCategoria.selectedItemId = R.id.nav_home

        binding.bottomNavigationCategoria.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_ABA, MainActivity.ABA_HOME)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_carrinho -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_ABA, MainActivity.ABA_CARRINHO)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_conta -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_ABA, MainActivity.ABA_CONTA)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> false
            }
        }

    }

    private fun observarDados() {
        viewModel.produtos.observe(this) { listaProdutos ->
            produtosAtuais = listaProdutos

            binding.txtQtdProdutos.text = when (listaProdutos.size) {
                0 -> "Nenhum produto encontrado"
                1 -> "1 produto encontrado"
                else -> "${listaProdutos.size} produtos encontrados"
            }

            adapter.atualizarLista(listaProdutos)
        }

        viewModel.estaCarregando.observe(this) { carregando ->
            if (carregando) {
                binding.txtQtdProdutos.text = "Carregando produtos..."
            }
        }
    }

    private fun carregarCategoria(categoria: String) {
        categoriaAtual = normalizarCategoria(categoria)

        binding.txtTituloCategoria.text = obterTituloCategoria(categoriaAtual)
        atualizarChipSelecionado(categoriaAtual)

        if (categoriaAtual == CATEGORIA_TUDO) {
            viewModel.carregarProdutos()
        } else {
            viewModel.carregarProdutosPorCategoria(categoriaAtual)
        }
    }

    private fun ordenarProdutosPorPreco() {
        if (produtosAtuais.isEmpty()) {
            Toast.makeText(this, "Nenhum produto para ordenar", Toast.LENGTH_SHORT).show()
            return
        }

        val listaOrdenada = if (ordenarMenorPreco) {
            produtosAtuais.sortedBy { it.preco }
        } else {
            produtosAtuais.sortedByDescending { it.preco }
        }

        ordenarMenorPreco = !ordenarMenorPreco
        produtosAtuais = listaOrdenada

        binding.txtOrdenar.text = if (ordenarMenorPreco) {
            "Ordenar  ▾"
        } else {
            "Ordenar  ▴"
        }

        adapter.atualizarLista(listaOrdenada)
    }

    private fun atualizarChipSelecionado(categoriaSelecionada: String) {
        val chips = listOf(
            binding.chipTudo,
            binding.chipSkincare,
            binding.chipMaquiagem,
            binding.chipCabelos,
            binding.chipPerfume
        )

        chips.forEach { chip ->
            deixarChipNormal(chip)
        }

        when (categoriaSelecionada) {
            CATEGORIA_TUDO -> deixarChipSelecionado(binding.chipTudo)
            CATEGORIA_SKINCARE -> deixarChipSelecionado(binding.chipSkincare)
            CATEGORIA_MAQUIAGEM -> deixarChipSelecionado(binding.chipMaquiagem)
            CATEGORIA_CABELOS -> deixarChipSelecionado(binding.chipCabelos)
            CATEGORIA_PERFUME -> deixarChipSelecionado(binding.chipPerfume)
        }
    }

    private fun deixarChipSelecionado(chip: TextView) {
        chip.setBackgroundResource(R.drawable.fundo_categoria_selecionada)
        chip.setTextColor(Color.WHITE)
    }

    private fun deixarChipNormal(chip: TextView) {
        chip.setBackgroundResource(R.drawable.fundo_categoria_nao_selecionada)
        chip.setTextColor(ContextCompat.getColor(this, R.color.terracota_marrom))
    }

    private fun obterTituloCategoria(categoria: String): String {
        return when (categoria) {
            CATEGORIA_SKINCARE -> "Skincare"
            CATEGORIA_MAQUIAGEM -> "Maquiagem"
            CATEGORIA_CABELOS -> "Cabelos"
            CATEGORIA_PERFUME -> "Perfumes"
            else -> "Produtos"
        }
    }

    private fun normalizarCategoria(categoria: String): String {
        return when (categoria.lowercase().trim()) {
            "make", "maquiagens" -> CATEGORIA_MAQUIAGEM
            "perfumes" -> CATEGORIA_PERFUME
            "skincare" -> CATEGORIA_SKINCARE
            "maquiagem" -> CATEGORIA_MAQUIAGEM
            "cabelos" -> CATEGORIA_CABELOS
            "perfume" -> CATEGORIA_PERFUME
            else -> CATEGORIA_TUDO
        }
    }

    private fun abrirTelaSeExistir(nomeClasse: String) {
        try {
            val classe = Class.forName("com.example.belleza.ui.$nomeClasse")
            startActivity(Intent(this, classe))
        } catch (e: ClassNotFoundException) {
            Toast.makeText(this, "Tela ainda não criada", Toast.LENGTH_SHORT).show()
        }
    }

}
