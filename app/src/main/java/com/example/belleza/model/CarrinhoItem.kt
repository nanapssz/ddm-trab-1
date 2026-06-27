package com.example.belleza.model

data class CarrinhoItem(
    val idProduto: String = "",
    val titulo: String = "",
    val preco: Double = 0.0,
    val urlImagem: String = "",
    val categoria: String = "",
    val quantidade: Int = 1
)
