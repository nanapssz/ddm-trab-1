package com.example.belleza

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.model.Produto
import com.example.belleza.repository.LojaRepository
import com.example.belleza.viewmodel.LojaViewModel
import com.example.belleza.viewmodel.LojaViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var catTudo: TextView
    private lateinit var catSkincare: TextView
    private lateinit var catMake: TextView
    private lateinit var catCabelos: TextView
    private lateinit var catPerfumes: TextView

    private lateinit var rvTudo: RecyclerView
    private lateinit var rvSkincare: RecyclerView
    private lateinit var rvMake: RecyclerView
    private lateinit var rvCabelos: RecyclerView
    private lateinit var rvPerfumes: RecyclerView
    private lateinit var rvMaisVendidos: RecyclerView
    private lateinit var rvNovidades: RecyclerView

    private lateinit var containerExtrasTudo: LinearLayout
    private lateinit var btnVerMaisCategorias: View

    private lateinit var viewModel: LojaViewModel

    private var categoriaSelecionada: String = "tudo"

    private lateinit var adapterTudo: ProdutoAdapter
    private lateinit var adapterSkincare: ProdutoAdapter
    private lateinit var adapterMake: ProdutoAdapter
    private lateinit var adapterCabelos: ProdutoAdapter
    private lateinit var adapterPerfumes: ProdutoAdapter
    private lateinit var adapterMaisVendidos: ProdutoAdapter
    private lateinit var adapterNovidades: ProdutoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        iniciarComponentes()
        configurarAdapters()
        configurarViewModel()
        configurarCliques()

        carregarDadosIniciais()
    }

    private fun iniciarComponentes() {
        catTudo = findViewById(R.id.catTudo)
        catSkincare = findViewById(R.id.catSkincare)
        catMake = findViewById(R.id.catMake)
        catCabelos = findViewById(R.id.catCabelos)
        catPerfumes = findViewById(R.id.catPerfumes)

        rvTudo = findViewById(R.id.rvVitrineTudo)
        rvSkincare = findViewById(R.id.rvVitrineSkincare)
        rvMake = findViewById(R.id.rvVitrineMake)
        rvCabelos = findViewById(R.id.rvVitrineCabelos)
        rvPerfumes = findViewById(R.id.rvVitrinePerfumes)
        rvMaisVendidos = findViewById(R.id.rvMaisVendidos)
        rvNovidades = findViewById(R.id.rvNovidades)

        containerExtrasTudo = findViewById(R.id.containerExtrasTudo)
        btnVerMaisCategorias = findViewById(R.id.btnVerMaisCategorias)
    }

    private fun configurarAdapters() {
        adapterTudo = ProdutoAdapter { abrirDetalhes(it) }
        adapterSkincare = ProdutoAdapter(isGrid = true) { abrirDetalhes(it) }
        adapterMake = ProdutoAdapter(isGrid = true) { abrirDetalhes(it) }
        adapterCabelos = ProdutoAdapter(isGrid = true) { abrirDetalhes(it) }
        adapterPerfumes = ProdutoAdapter(isGrid = true) { abrirDetalhes(it) }
        adapterMaisVendidos = ProdutoAdapter { abrirDetalhes(it) }
        adapterNovidades = ProdutoAdapter { abrirDetalhes(it) }

        rvTudo.adapter = adapterTudo
        rvSkincare.adapter = adapterSkincare
        rvMake.adapter = adapterMake
        rvCabelos.adapter = adapterCabelos
        rvPerfumes.adapter = adapterPerfumes
        rvMaisVendidos.adapter = adapterMaisVendidos
        rvNovidades.adapter = adapterNovidades
    }

    private fun configurarViewModel() {
        val banco = BancoDeDadosApp.obterBancoDeDados(this)
        val repositorio = LojaRepository(banco.favoritoDao())
        val factory = LojaViewModelFactory(repositorio)
        viewModel = ViewModelProvider(this, factory)[LojaViewModel::class.java]

        viewModel.produtos.observe(this) { produtos ->
            // Preenche cada vitrine com os produtos certos vindos do Firebase
            adapterTudo.atualizarLista(produtos)
            adapterSkincare.atualizarLista(produtos.filter { it.categoria.lowercase() == "skincare" })
            adapterMake.atualizarLista(produtos.filter { it.categoria.lowercase().contains("make") })
            adapterCabelos.atualizarLista(produtos.filter { it.categoria.lowercase() == "cabelos" })
            adapterPerfumes.atualizarLista(produtos.filter { it.categoria.lowercase().contains("perfume") })

            // Exemplos para vitrines extras
            adapterMaisVendidos.atualizarLista(produtos.shuffled().take(5))
            adapterNovidades.atualizarLista(produtos.reversed().take(5))
        }
    }

    private fun configurarCliques() {
        catTudo.setOnClickListener { selecionarCategoria(catTudo, "tudo") }
        catSkincare.setOnClickListener { selecionarCategoria(catSkincare, "skincare") }
        catMake.setOnClickListener { selecionarCategoria(catMake, "make") }
        catCabelos.setOnClickListener { selecionarCategoria(catCabelos, "cabelos") }
        catPerfumes.setOnClickListener { selecionarCategoria(catPerfumes, "perfumes") }

        btnVerMaisCategorias.setOnClickListener {
            CategoriaActivity.abrir(this, categoriaSelecionada)
        }
    }

    private fun carregarDadosIniciais() {
        viewModel.carregarProdutos()
    }

    private fun abrirDetalhes(produto: Produto) {
        val intent = Intent(this, DetalhesProdutoActivity::class.java)
        intent.putExtra("produto", produto)
        startActivity(intent)
    }

    private fun selecionarCategoria(categoriaClicada: TextView, categoriaString: String) {
        this.categoriaSelecionada = categoriaString
        
        val listaCategorias = listOf(catTudo, catSkincare, catMake, catCabelos, catPerfumes)
        for (categoria in listaCategorias) {
            categoria.setBackgroundColor(Color.TRANSPARENT)
            categoria.setTextColor(ContextCompat.getColor(this, R.color.terracota_marrom))
        }

        categoriaClicada.setBackgroundResource(R.drawable.fundo_categoria_ativa)
        categoriaClicada.setTextColor(Color.WHITE)

        rvTudo.visibility = View.GONE
        rvSkincare.visibility = View.GONE
        rvMake.visibility = View.GONE
        rvCabelos.visibility = View.GONE
        rvPerfumes.visibility = View.GONE
        containerExtrasTudo.visibility = View.GONE
        btnVerMaisCategorias.visibility = View.GONE

        when (categoriaClicada) {
            catTudo -> { 
                rvTudo.visibility = View.VISIBLE
                containerExtrasTudo.visibility = View.VISIBLE 
            }
            catSkincare -> {
                rvSkincare.visibility = View.VISIBLE
                btnVerMaisCategorias.visibility = View.VISIBLE
            }
            catMake -> {
                rvMake.visibility = View.VISIBLE
                btnVerMaisCategorias.visibility = View.VISIBLE
            }
            catCabelos -> {
                rvCabelos.visibility = View.VISIBLE
                btnVerMaisCategorias.visibility = View.VISIBLE
            }
            catPerfumes -> {
                rvPerfumes.visibility = View.VISIBLE
                btnVerMaisCategorias.visibility = View.VISIBLE
            }
        }
    }
}