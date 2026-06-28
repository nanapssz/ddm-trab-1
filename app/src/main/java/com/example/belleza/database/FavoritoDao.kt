package com.example.belleza.database

import androidx.room.*
import com.example.belleza.model.FavoritoEntity

@Dao
interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirFavorito(favorito: FavoritoEntity)

    @Query("DELETE FROM produto_favorito WHERE idProduto = :idProduto AND idUsuario = :idUsuario")
    suspend fun deletarFavorito(idProduto: String, idUsuario: String)

    @Query("SELECT * FROM produto_favorito WHERE idUsuario = :idUsuario")
    suspend fun obterFavoritosDoUsuario(idUsuario: String): List<FavoritoEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM produto_favorito WHERE idProduto = :idProduto AND idUsuario = :idUsuario)")
    suspend fun verificarSeEhFavorito(idProduto: String, idUsuario: String): Boolean
}