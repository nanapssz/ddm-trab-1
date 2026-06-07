package com.example.belleza

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.database.FavoritoDao
import com.example.belleza.model.FavoritoEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class BancoDeDadosTest {

    private lateinit var daoFavoritos: FavoritoDao
    private lateinit var bancoDeDados: BancoDeDadosApp

    @Before
    fun criarBancoDeDadosEmMemoria() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()

        bancoDeDados = Room.inMemoryDatabaseBuilder(contexto, BancoDeDadosApp::class.java)
            .allowMainThreadQueries()
            .build()

        daoFavoritos = bancoDeDados.favoritoDao()
    }

    @After
    @Throws(IOException::class)
    fun fecharBancoDeDados() {
        bancoDeDados.close()
    }

    @Test
    @Throws(Exception::class)
    fun inserirProdutoFavoritoEValidarFiltroPorUsuario() = runBlocking {
        val favoritoUsuarioA = FavoritoEntity(
            idProduto = "prod_cosmetico_01",
            titulo = "Sérum Hidratante Vegano",
            preco = 89.90,
            urlImagem = "https://link_da_imagem.com/serum.png",
            idUsuario = "UID_CLIENTE_A"
        )

        val favoritoUsuarioB = FavoritoEntity(
            idProduto = "prod_perfume_02",
            titulo = "Colônia Ébano Express",
            preco = 240.00,
            urlImagem = "https://link_da_imagem.com/perfume.png",
            idUsuario = "UID_CLIENTE_B"
        )

        daoFavoritos.inserirFavorito(favoritoUsuarioA)
        daoFavoritos.inserirFavorito(favoritoUsuarioB)

        val listaDoUsuarioA = daoFavoritos.obterFavoritosDoUsuario("UID_CLIENTE_A")

        assertEquals(1, listaDoUsuarioA.size)
        assertEquals("Sérum Hidratante Vegano", listaDoUsuarioA[0].titulo)
    }
}