package com.example.belleza.repository

import com.example.belleza.database.FavoritoDao
import com.example.belleza.model.CarrinhoItem
import com.example.belleza.model.FavoritoEntity
import com.example.belleza.model.Produto
import com.example.belleza.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LojaRepository(private val favoritoDao: FavoritoDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun obterTodosProdutos(): List<Produto> {
        return try {
            val snapshot = firestore
                .collection("produtos")
                .get()
                .await()

            snapshot.toObjects(Produto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obterProdutosPorCategoria(categoria: String): List<Produto> {
        return try {
            val snapshot = firestore
                .collection("produtos")
                .whereEqualTo("categoria", categoria)
                .get()
                .await()

            snapshot.toObjects(Produto::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obterProdutoPorId(idProduto: String): Produto? {
        return try {
            val snapshot = firestore
                .collection("produtos")
                .document(idProduto)
                .get()
                .await()

            snapshot.toObject(Produto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun adicionarProdutoAoCarrinho(produto: Produto): Boolean {
        val uid = auth.currentUser?.uid ?: return false

        return try {
            val referenciaCarrinho = firestore
                .collection("usuarios")
                .document(uid)
                .collection("meu_carrinho")
                .document(produto.id)

            val itemAtual = referenciaCarrinho
                .get()
                .await()
                .toObject(CarrinhoItem::class.java)

            val novaQuantidade = (itemAtual?.quantidade ?: 0) + 1

            val itemCarrinho = CarrinhoItem(
                idProduto = produto.id,
                titulo = produto.titulo,
                preco = produto.preco,
                urlImagem = produto.urlImagem,
                categoria = produto.categoria,
                quantidade = novaQuantidade
            )

            referenciaCarrinho.set(itemCarrinho).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obterMeuCarrinho(): List<CarrinhoItem> {
        val uid = auth.currentUser?.uid ?: return emptyList()

        return try {
            val snapshot = firestore
                .collection("usuarios")
                .document(uid)
                .collection("meu_carrinho")
                .get()
                .await()

            snapshot.toObjects(CarrinhoItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun alterarQuantidadeCarrinho(idProduto: String, novaQuantidade: Int): Boolean {
        val uid = auth.currentUser?.uid ?: return false

        return try {
            val referenciaItem = firestore
                .collection("usuarios")
                .document(uid)
                .collection("meu_carrinho")
                .document(idProduto)

            if (novaQuantidade <= 0) {
                referenciaItem.delete().await()
            } else {
                referenciaItem
                    .update("quantidade", novaQuantidade)
                    .await()
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removerProdutoDoCarrinho(idProduto: String): Boolean {
        val uid = auth.currentUser?.uid ?: return false

        return try {
            firestore
                .collection("usuarios")
                .document(uid)
                .collection("meu_carrinho")
                .document(idProduto)
                .delete()
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun salvarFavoritoLocalmente(produto: Produto) {
        val uid = auth.currentUser?.uid ?: "usuario_deslogado"

        val favorito = FavoritoEntity(
            idProduto = produto.id,
            titulo = produto.titulo,
            preco = produto.preco,
            urlImagem = produto.urlImagem,
            idUsuario = uid
        )

        favoritoDao.inserirFavorito(favorito)
    }

    suspend fun obterFavoritosLocais(): List<FavoritoEntity> {
        val uid = auth.currentUser?.uid ?: "usuario_deslogado"
        return favoritoDao.obterFavoritosDoUsuario(uid)
    }

    suspend fun salvarPerfilUsuario(usuario: Usuario): Boolean {
        val uid = auth.currentUser?.uid ?: return false

        return try {
            val usuarioComId = usuario.copy(id = uid)

            firestore
                .collection("usuarios")
                .document(uid)
                .set(usuarioComId)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obterMeuPerfil(): Usuario? {
        val uid = auth.currentUser?.uid ?: return null

        return try {
            val snapshot = firestore
                .collection("usuarios")
                .document(uid)
                .get()
                .await()

            snapshot.toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }
}