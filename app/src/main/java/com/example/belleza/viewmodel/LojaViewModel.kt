package com.example.belleza.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.belleza.model.FavoritoEntity
import com.example.belleza.model.Produto
import com.example.belleza.model.Usuario
import com.example.belleza.repository.LojaRepository
import kotlinx.coroutines.launch


class LojaViewModel(private val repositorio: LojaRepository) : ViewModel() {

    private val _produtos = MutableLiveData<List<Produto>>()
    val produtos: LiveData<List<Produto>> = _produtos

    private val _carrinho = MutableLiveData<List<Produto>>()
    val carrinho: LiveData<List<Produto>> = _carrinho

    private val _favoritos = MutableLiveData<List<FavoritoEntity>>()
    val favoritos: LiveData<List<FavoritoEntity>> = _favoritos

    private val _perfilUsuario = MutableLiveData<Usuario?>()
    val perfilUsuario: LiveData<Usuario?> = _perfilUsuario

    private val _estaCarregando = MutableLiveData<Boolean>()
    val estaCarregando: LiveData<Boolean> = _estaCarregando

    private val _statusOperacao = MutableLiveData<Boolean>()
    val statusOperacao: LiveData<Boolean> = _statusOperacao


    fun carregarProdutos() {
        viewModelScope.launch {
            _estaCarregando.value = true
            val resultado = repositorio.obterTodosProdutos()
            _produtos.value = resultado
            _estaCarregando.value = false
        }
    }


    fun carregarCarrinho() {
        viewModelScope.launch {
            _estaCarregando.value = true
            val resultado = repositorio.obterMeuCarrinho()
            _carrinho.value = resultado
            _estaCarregando.value = false
        }
    }


    fun carregarFavoritos() {
        viewModelScope.launch {
            val resultado = repositorio.obterFavoritosLocais()
            _favoritos.value = resultado
        }
    }


    fun favoritarProduto(produto: Produto) {
        viewModelScope.launch {
            repositorio.salvarFavoritoLocalmente(produto)
            carregarFavoritos() // Atualiza a lista reativa instantaneamente
        }
    }


    fun carregarPerfil() {
        viewModelScope.launch {
            val perfil = repositorio.obterMeuPerfil()
            _perfilUsuario.value = perfil
        }
    }


    fun salvarPerfil(usuario: Usuario) {
        viewModelScope.launch {
            _estaCarregando.value = true
            val sucesso = repositorio.salvarPerfilUsuario(usuario)
            _statusOperacao.value = sucesso
            if (sucesso) {
                _perfilUsuario.value = usuario
            }
            _estaCarregando.value = false
        }
    }
}