package com.example.belleza.model

data class ProdutoNoCarrinho(
    val produto: Produto = Produto(),
    val quantidade: Int = 1
)