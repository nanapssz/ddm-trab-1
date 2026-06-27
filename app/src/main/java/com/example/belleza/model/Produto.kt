package com.example.belleza.model

import java.io.Serializable

data class Produto(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val preco: Double = 0.0,
    val urlImagem: String = "",
    val categoria: String = ""
) : Serializable


