package com.example.belleza.repository

import com.example.belleza.database.FavoritoDao
import com.example.belleza.model.CarrinhoItem
import com.example.belleza.model.FavoritoEntity
import com.example.belleza.model.Produto
import com.example.belleza.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.firestore.SetOptions
import java.io.ByteArrayOutputStream

class LojaRepository(
    private val favoritoDao: FavoritoDao,
    private val appContext: android.content.Context
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    suspend fun obterTodosProdutos(): List<Produto> {
        return try {
            val snapshot = firestore.collection("produtos").get().await()

            snapshot.documents.mapNotNull { doc ->
                val produto = doc.toObject(Produto::class.java)
                // IMPORTANTE: O id do objeto produto deve receber o id do documento do Firebase
                produto?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun obterProdutosPorCategoria(categoria: String): List<Produto> {
        return try {
            val categoriaNormalizada = normalizarCategoria(categoria)

            val snapshot = firestore
                .collection("produtos")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Produto::class.java)?.copy(id = doc.id)
            }.filter { produto ->
                normalizarCategoria(produto.categoria) == categoriaNormalizada
            }
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

    suspend fun adicionarProdutoAoCarrinho(
        produto: Produto,
        quantidadeAdicionada: Int = 1
    ): Boolean {
        val uid = auth.currentUser?.uid ?: return false

        if (produto.id.isBlank()) {
            return false
        }

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

            val novaQuantidade = (itemAtual?.quantidade ?: 0) + quantidadeAdicionada

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
        val favorito = FavoritoEntity(produto.id, produto.titulo, produto.preco, produto.urlImagem, uid)
        favoritoDao.inserirFavorito(favorito)
    }
    suspend fun verificarSeEhFavorito(idProduto: String): Boolean {
        val uid = auth.currentUser?.uid ?: "usuario_deslogado"
        val favoritos = favoritoDao.obterFavoritosDoUsuario(uid)
        return favoritos.any { it.idProduto == idProduto }
    }

    suspend fun removerFavoritoLocalmente(idProduto: String) {
        val uid = auth.currentUser?.uid ?: "usuario_deslogado"
        favoritoDao.deletarFavorito(idProduto, uid)
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
                .set(usuarioComId, SetOptions.merge())
                .await()

            true
        } catch (e: Exception) {
            android.util.Log.e("LojaRepository", "Erro ao salvar perfil", e)
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
    private fun normalizarCategoria(categoria: String): String {
        return when (categoria.lowercase().trim()) {
            "make", "maquiagem", "maquiagens" -> "maquiagem"
            "perfume", "perfumes" -> "perfume"
            "cabelo", "cabelos" -> "cabelos"
            "skincare" -> "skincare"
            else -> categoria.lowercase().trim()
        }
    }

    private fun redimensionarBitmap(
        bitmap: Bitmap,
        larguraMaxima: Int,
        alturaMaxima: Int
    ): Bitmap {
        val larguraOriginal = bitmap.width
        val alturaOriginal = bitmap.height

        val proporcao = minOf(
            larguraMaxima.toFloat() / larguraOriginal,
            alturaMaxima.toFloat() / alturaOriginal
        )

        val novaLargura = (larguraOriginal * proporcao).toInt()
        val novaAltura = (alturaOriginal * proporcao).toInt()

        return Bitmap.createScaledBitmap(bitmap, novaLargura, novaAltura, true)
    }

    suspend fun atualizarFotoPerfil(uriFoto: Uri): String {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            return "Usuário não está logado no FirebaseAuth"
        }

        return try {
            val inputStream = appContext.contentResolver.openInputStream(uriFoto)
                ?: return "Não foi possível ler a imagem"

            val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
                ?: return "Não foi possível converter a imagem"

            val bitmapReduzido = redimensionarBitmap(bitmapOriginal, 300, 300)

            val saida = ByteArrayOutputStream()
            bitmapReduzido.compress(Bitmap.CompressFormat.JPEG, 65, saida)

            val bytesImagem = saida.toByteArray()

            if (bytesImagem.size > 700_000) {
                return "A imagem ficou muito grande. Tente outra foto."
            }

            val fotoBase64 = Base64.encodeToString(bytesImagem, Base64.DEFAULT)

            firestore
                .collection("usuarios")
                .document(uid)
                .set(
                    mapOf(
                        "fotoBase64" to fotoBase64,
                        "fotoUrl" to ""
                    ),
                    SetOptions.merge()
                )
                .await()

            "OK"
        } catch (e: Exception) {
            "Erro ao salvar foto no Firestore: ${e.message}"
        }
    }
}