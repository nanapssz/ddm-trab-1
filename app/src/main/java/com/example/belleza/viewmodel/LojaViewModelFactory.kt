package com.example.belleza.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.belleza.repository.LojaRepository


class LojaViewModelFactory(private val repositorio: LojaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LojaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LojaViewModel(repositorio) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}