package com.example.belleza.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.belleza.data.repository.AuthRepository

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _loginState = MutableLiveData<Result<Boolean>>()
    val loginState: LiveData<Result<Boolean>> = _loginState

    fun realizarLogin(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _loginState.value = Result.failure(Exception("Preencha todos os campos"))
            return
        }

        repository.login(email, senha) { sucesso, erro ->
            if (sucesso) {
                _loginState.value = Result.success(true)
            } else {
                _loginState.value = Result.failure(Exception(erro ?: "E-mail ou senha incorretos. Tente novamente"))
            }
        }
    }
}
