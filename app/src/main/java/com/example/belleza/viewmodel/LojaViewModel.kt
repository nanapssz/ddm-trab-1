package com.example.belleza.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.belleza.model.CarrinhoItem
import com.example.belleza.model.FavoritoEntity
import com.example.belleza.model.Produto
import com.example.belleza.model.Usuario
import com.example.belleza.repository.LojaRepository
import kotlinx.coroutines.launch

class LojaViewModel(private val repositorio: LojaRepository) : ViewModel() {

    private val _produtos = MutableLiveData<List<Produto>>()
    val produtos: LiveData<List<Produto>> = _produtos

    private val _carrinho = MutableLiveData<List<CarrinhoItem>>()
    val carrinho: LiveData<List<CarrinhoItem>> = _carrinho

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
            _produtos.value = repositorio.obterTodosProdutos()
            _estaCarregando.value = false
        }
    }

    fun carregarProdutosPorCategoria(categoria: String) {
        viewModelScope.launch {
            _estaCarregando.value = true
            _produtos.value = repositorio.obterProdutosPorCategoria(categoria)
            _estaCarregando.value = false
        }
    }

    fun carregarCarrinho() {
        viewModelScope.launch {
            _estaCarregando.value = true
            _carrinho.value = repositorio.obterMeuCarrinho()
            _estaCarregando.value = false
        }
    }

    fun adicionarAoCarrinho(produto: Produto) {
        viewModelScope.launch {
            _estaCarregando.value = true
            val sucesso = repositorio.adicionarProdutoAoCarrinho(produto)
            _statusOperacao.value = sucesso

            if (sucesso) {
                carregarCarrinho()
            }

            _estaCarregando.value = false
        }
    }

    fun alterarQuantidadeCarrinho(idProduto: String, novaQuantidade: Int) {
        viewModelScope.launch {
            val sucesso = repositorio.alterarQuantidadeCarrinho(idProduto, novaQuantidade)
            _statusOperacao.value = sucesso

            if (sucesso) {
                carregarCarrinho()
            }
        }
    }

    fun removerDoCarrinho(idProduto: String) {
        viewModelScope.launch {
            val sucesso = repositorio.removerProdutoDoCarrinho(idProduto)
            _statusOperacao.value = sucesso

            if (sucesso) {
                carregarCarrinho()
            }
        }
    }


    fun carregarFavoritos() {
        viewModelScope.launch { _favoritos.value = repositorio.obterFavoritosLocais() }
    }

    fun alternarFavorito(produto: Produto) {
        viewModelScope.launch {
            val jaEh = repositorio.verificarSeEhFavorito(produto.id)
            if (jaEh) {
                repositorio.removerFavoritoLocalmente(produto.id)
            } else {
                repositorio.salvarFavoritoLocalmente(produto)
            }
            carregarFavoritos()
        }
    }


    fun favoritarProduto(produto: Produto) {
        viewModelScope.launch {
            val jaEhFavorito = repositorio.verificarSeEhFavorito(produto.id)

            if (jaEhFavorito) {
                repositorio.removerFavoritoLocalmente(produto.id)
            } else {
                repositorio.salvarFavoritoLocalmente(produto)
            }
            carregarFavoritos()
        }
    }

    fun carregarPerfil() {
        viewModelScope.launch {
            _perfilUsuario.value = repositorio.obterMeuPerfil()
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