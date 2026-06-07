package com.example.belleza.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.belleza.database.FavoritoDao
import com.example.belleza.model.FavoritoEntity
import com.example.belleza.model.Produto
import com.example.belleza.model.Usuario
import kotlinx.coroutines.tasks.await

class LojaRepository(private val favoritoDao: FavoritoDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun obterTodosProdutos(): List<Produto> {
        return try {
            val snapshot = firestore.collection("produtos").get().await()
            snapshot.toObjects(Produto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obterMeuCarrinho(): List<Produto> {
        val usuarioAtual = auth.currentUser?.uid ?: return emptyList()

        return try {
            val snapshot = firestore.collection("usuarios")
                .document(usuarioAtual)
                .collection("meu_carrinho")
                .get()
                .await()
            snapshot.toObjects(Produto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun salvarFavoritoLocalmente(produto: Produto) {
        val usuarioAtual = auth.currentUser?.uid ?: "usuario_deslogado"

        val favorito = FavoritoEntity(
            idProduto = produto.id,
            titulo = produto.titulo,
            preco = produto.preco,
            urlImagem = produto.urlImagem,
            idUsuario = usuarioAtual
        )
        favoritoDao.inserirFavorito(favorito)
    }


    suspend fun obterFavoritosLocais(): List<FavoritoEntity> {
        val usuarioAtual = auth.currentUser?.uid ?: "usuario_deslogado"
        return favoritoDao.obterFavoritosDoUsuario(usuarioAtual)
    }


    suspend fun salvarPerfilUsuario(usuario: Usuario): Boolean {
        val uidAtual = auth.currentUser?.uid ?: return false

        return try {
            val usuarioComId = usuario.copy(id = uidAtual)
            firestore.collection("usuarios")
                .document(uidAtual)
                .set(usuarioComId)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obterMeuPerfil(): Usuario? {
        val uidAtual = auth.currentUser?.uid ?: return null

        return try {
            val snapshot = firestore.collection("usuarios")
                .document(uidAtual)
                .get()
                .await()
            snapshot.toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }
}