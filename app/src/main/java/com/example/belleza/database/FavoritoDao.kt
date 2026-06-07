package com.example.belleza.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.belleza.model.FavoritoEntity

@Dao
interface FavoritoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirFavorito(favorito: FavoritoEntity)

    @Query("DELETE FROM produto_favorito WHERE idProduto = :idProduto AND idUsuario = :idUsuario")
    suspend fun deletarFavorito(idProduto: String, idUsuario: String): Int

    @Query("SELECT * FROM produto_favorito WHERE idUsuario = :idUsuario")
    suspend fun obterFavoritosDoUsuario(idUsuario: String): List<FavoritoEntity>
}