package com.example.belleza.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produto_favorito")
data class FavoritoEntity(
    @PrimaryKey val idProduto: String,
    val titulo: String,
    val preco: Double,
    val urlImagem: String,
    val idUsuario: String
)