package com.example.belleza.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.belleza.ProdutoAdapter
import com.example.belleza.R
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.databinding.ActivityHomeBinding
import com.example.belleza.model.Produto
import com.example.belleza.repository.LojaRepository
import com.example.belleza.viewmodel.LojaViewModel
import com.example.belleza.viewmodel.LojaViewModelFactory
import com.google.firebase.messaging.FirebaseMessaging

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: LojaViewModel
    private var categoriaSelecionada: String = "tudo"

    private val adapterTudo by lazy { ProdutoAdapter(onClick = { abrirDetalhes(it) }, onFavoritoClick = { favoritar(it) }) }
    private val adapterSkincare by lazy { ProdutoAdapter(isGrid = true, onClick = { abrirDetalhes(it) }, onFavoritoClick = { favoritar(it) }) }
    private val adapterMake by lazy { ProdutoAdapter(isGrid = true, onClick = { abrirDetalhes(it) }, onFavoritoClick = { favoritar(it) }) }
    private val adapterCabelos by lazy { ProdutoAdapter(isGrid = true, onClick = { abrirDetalhes(it) }, onFavoritoClick = { favoritar(it) }) }
    private val adapterPerfumes by lazy { ProdutoAdapter(isGrid = true, onClick = { abrirDetalhes(it) }, onFavoritoClick = { favoritar(it) }) }
    private val adapterMaisVendidos by lazy { ProdutoAdapter(onClick = { abrirDetalhes(it) }, onFavoritoClick = { favoritar(it) }) }
    private val adapterNovidades by lazy { ProdutoAdapter(onClick = { abrirDetalhes(it) }, onFavoritoClick = { favoritar(it) }) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarViewModel()
        configurarRecyclerViews()
        configurarCliques()

        binding.menuHomeCategoria?.setColorFilter(ContextCompat.getColor(this, R.color.terracota_marrom))

        viewModel.carregarProdutos()
        viewModel.carregarFavoritos()
        viewModel.carregarPerfil()

        obterTokenFirebase()
        solicitarPermissaoNotificacao()
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarPerfil()
    }

    private fun configurarViewModel() {
        val banco = BancoDeDadosApp.obterBancoDeDados(this)
        val repositorio = LojaRepository(banco.favoritoDao())
        val factory = LojaViewModelFactory(repositorio)
        viewModel = ViewModelProvider(this, factory)[LojaViewModel::class.java]

        viewModel.produtos.observe(this) { produtos ->
            adapterTudo.atualizarLista(produtos)
            adapterSkincare.atualizarLista(produtos.filter { it.categoria.equals("skincare", true) })
            adapterMake.atualizarLista(produtos.filter { it.categoria.contains("make", true) || it.categoria.contains("maquiagem", true) })
            adapterCabelos.atualizarLista(produtos.filter { it.categoria.equals("cabelos", true) })
            adapterPerfumes.atualizarLista(produtos.filter { it.categoria.contains("perfume", true) })

            adapterMaisVendidos.atualizarLista(produtos.shuffled().take(5))
            adapterNovidades.atualizarLista(produtos.reversed().take(5))
        }

        viewModel.favoritos.observe(this) { listaFavoritos ->
            val ids = listaFavoritos.map { it.idProduto }.toSet()
            adapterTudo.atualizarFavoritos(ids)
            adapterSkincare.atualizarFavoritos(ids)
            adapterMake.atualizarFavoritos(ids)
            adapterCabelos.atualizarFavoritos(ids)
            adapterPerfumes.atualizarFavoritos(ids)
            adapterMaisVendidos.atualizarFavoritos(ids)
            adapterNovidades.atualizarFavoritos(ids)
        }

        viewModel.perfilUsuario.observe(this) { usuario ->
            usuario?.let {
                if (!it.fotoUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(it.fotoUrl)
                        .placeholder(R.drawable.ic_user)
                        .circleCrop()
                        .into(binding.imgPerfilHome) // Certifique-se que este ID existe no activity_home.xml
                }
            }
        }
    }

    private fun obterTokenFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TESTE", "Falha ao obter o token", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM_TOKEN", "Seu token é: $token")
        }
    }

    private fun solicitarPermissaoNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "notificacao_principal"
            val channelName = "Avisos da Loja"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH

            val channel = android.app.NotificationChannel(channelId, channelName, importance).apply {
                description = "Canal para notificações de promoções"
            }

            val notificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), 101)
            }
        }
    }

    private fun configurarRecyclerViews() {
        binding.rvVitrineTudo.adapter = adapterTudo
        binding.rvVitrineSkincare.adapter = adapterSkincare
        binding.rvVitrineMake.adapter = adapterMake
        binding.rvVitrineCabelos.adapter = adapterCabelos
        binding.rvVitrinePerfumes.adapter = adapterPerfumes
        binding.rvMaisVendidos.adapter = adapterMaisVendidos
        binding.rvNovidades.adapter = adapterNovidades
    }

    private fun favoritar(produto: Produto) {
        viewModel.favoritarProduto(produto)
        Toast.makeText(this, "Favoritos atualizados!", Toast.LENGTH_SHORT).show()
    }

    private fun abrirDetalhes(produto: Produto) {
        val intent = Intent(this, DetalhesProdutoActivity::class.java)
        intent.putExtra("produto", produto)
        startActivity(intent)
    }

    private fun configurarCliques() {
        binding.catTudo.setOnClickListener { selecionarCategoria(binding.catTudo, "tudo") }
        binding.catSkincare.setOnClickListener { selecionarCategoria(binding.catSkincare, "skincare") }
        binding.catMake.setOnClickListener { selecionarCategoria(binding.catMake, "make") }
        binding.catCabelos.setOnClickListener { selecionarCategoria(binding.catCabelos, "cabelos") }
        binding.catPerfumes.setOnClickListener { selecionarCategoria(binding.catPerfumes, "perfumes") }

        binding.btnVerMaisCategorias.setOnClickListener {
            val intent = Intent(this, CategoriaActivity::class.java)
            intent.putExtra(CategoriaActivity.EXTRA_CATEGORIA, categoriaSelecionada)
            startActivity(intent)
        }

        binding.menuCarrinhoCategoria?.setOnClickListener {
            abrirTelaSeExistir("CarrinhoActivity")
        }

        binding.menuContaCategoria?.setOnClickListener {
            abrirTelaSeExistir("MinhaContaActivity")
        }

        binding.menuFavoritosCategoria?.setOnClickListener {
            Toast.makeText(this, "Abrindo seus favoritos...", Toast.LENGTH_SHORT).show()
        }

        binding.menuHomeCategoria?.setOnClickListener {
            binding.scrollHome?.smoothScrollTo(0, 0)
        }
    }

    private fun selecionarCategoria(textView: TextView, categoria: String) {
        this.categoriaSelecionada = categoria
        val categorias = listOf(binding.catTudo, binding.catSkincare, binding.catMake, binding.catCabelos, binding.catPerfumes)

        categorias.forEach {
            it.setBackgroundColor(Color.TRANSPARENT)
            it.setTextColor(ContextCompat.getColor(this, R.color.terracota_marrom))
        }

        textView.setBackgroundResource(R.drawable.fundo_categoria_ativa)
        textView.setTextColor(Color.WHITE)

        binding.rvVitrineTudo.visibility = if (categoria == "tudo") View.VISIBLE else View.GONE
        binding.containerExtrasTudo.visibility = if (categoria == "tudo") View.VISIBLE else View.GONE
        binding.rvVitrineSkincare.visibility = if (categoria == "skincare") View.VISIBLE else View.GONE
        binding.rvVitrineMake.visibility = if (categoria == "make") View.VISIBLE else View.GONE
        binding.rvVitrineCabelos.visibility = if (categoria == "cabelos") View.VISIBLE else View.GONE
        binding.rvVitrinePerfumes.visibility = if (categoria == "perfumes") View.VISIBLE else View.GONE

        binding.btnVerMaisCategorias.visibility = if (categoria == "tudo") View.GONE else View.VISIBLE
    }

    private fun abrirTelaSeExistir(nomeClasse: String) {
        try {
            val pacote = "com.example.belleza.ui"
            val classe = Class.forName("$pacote.$nomeClasse")
            startActivity(Intent(this, classe))
        } catch (e: ClassNotFoundException) {
            Toast.makeText(this, "A tela $nomeClasse ainda não foi criada", Toast.LENGTH_SHORT).show()
        }
    }
}