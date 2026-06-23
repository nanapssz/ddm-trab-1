package com.example.belleza

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity() {

    // 1. Declarando os botões de categoria
    private lateinit var catTudo: TextView
    private lateinit var catSkincare: TextView
    private lateinit var catMake: TextView
    private lateinit var catCabelos: TextView
    private lateinit var catPerfumes: TextView

    // 2. Declarando as prateleiras (Vitrines)
    private lateinit var vitrineTudo: HorizontalScrollView
    private lateinit var vitrineSkincare: HorizontalScrollView
    private lateinit var vitrineMake: HorizontalScrollView
    private lateinit var vitrineCabelos: HorizontalScrollView
    private lateinit var vitrinePerfumes: HorizontalScrollView

    // A nossa "caixa" que guarda os Mais Vendidos e Novidades
    private lateinit var containerExtrasTudo: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Conectando os botões de categoria do XML pro Kotlin
        catTudo = findViewById(R.id.catTudo)
        catSkincare = findViewById(R.id.catSkincare)
        catMake = findViewById(R.id.catMake)
        catCabelos = findViewById(R.id.catCabelos)
        catPerfumes = findViewById(R.id.catPerfumes)

        // Conectando as prateleiras do XML pro Kotlin
        vitrineTudo = findViewById(R.id.vitrineTudo)
        vitrineSkincare = findViewById(R.id.vitrineSkincare)
        vitrineMake = findViewById(R.id.vitrineMake)
        vitrineCabelos = findViewById(R.id.vitrineCabelos)
        vitrinePerfumes = findViewById(R.id.vitrinePerfumes)

        // Conectando a caixa extra
        containerExtrasTudo = findViewById(R.id.containerExtrasTudo)

        // Avisando o que fazer quando a pessoa CLICAR em cada categoria
        catTudo.setOnClickListener { selecionarCategoria(catTudo) }
        catSkincare.setOnClickListener { selecionarCategoria(catSkincare) }
        catMake.setOnClickListener { selecionarCategoria(catMake) }
        catCabelos.setOnClickListener { selecionarCategoria(catCabelos) }
        catPerfumes.setOnClickListener { selecionarCategoria(catPerfumes) }
    }

    // A MÁGICA: Função que pinta o botão e mostra/esconde as prateleiras
    private fun selecionarCategoria(categoriaClicada: TextView) {

        // --- PARTE 1: Mudar a cor do botão ---
        val listaCategorias = listOf(catTudo, catSkincare, catMake, catCabelos, catPerfumes)
        for (categoria in listaCategorias) {
            categoria.setBackgroundColor(Color.TRANSPARENT)
            categoria.setTextColor(ContextCompat.getColor(this, R.color.terracota_marrom))
        }

        categoriaClicada.setBackgroundResource(R.drawable.fundo_categoria_ativa)
        categoriaClicada.setTextColor(Color.WHITE)

        // --- PARTE 2: Esconder TODAS as prateleiras por precaução ---
        vitrineTudo.visibility = View.GONE
        vitrineSkincare.visibility = View.GONE
        vitrineMake.visibility = View.GONE
        vitrineCabelos.visibility = View.GONE
        vitrinePerfumes.visibility = View.GONE

        // Esconde a caixa extra também!
        containerExtrasTudo.visibility = View.GONE

        // --- PARTE 3: Tirar a capa da invisibilidade SÓ da prateleira escolhida ---
        when (categoriaClicada) {
            catTudo -> {
                vitrineTudo.visibility = View.VISIBLE
                containerExtrasTudo.visibility = View.VISIBLE // Aparece de volta na aba Tudo
            }
            catSkincare -> vitrineSkincare.visibility = View.VISIBLE
            catMake -> vitrineMake.visibility = View.VISIBLE
            catCabelos -> vitrineCabelos.visibility = View.VISIBLE
            catPerfumes -> vitrinePerfumes.visibility = View.VISIBLE
        }
    }
}