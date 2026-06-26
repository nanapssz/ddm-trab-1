package com.example.belleza

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.model.Usuario
import com.example.belleza.repository.LojaRepository
import com.example.belleza.viewmodel.LojaViewModel
import com.example.belleza.viewmodel.LojaViewModelFactory

class MinhaContaActivity : AppCompatActivity() {

    private lateinit var viewModel: LojaViewModel

    private lateinit var txtNomeUsuario: TextView
    private lateinit var txtEmailUsuario: TextView

    private lateinit var edtNomeCompleto: EditText
    private lateinit var edtCpf: EditText
    private lateinit var edtRg: EditText
    private lateinit var edtSexo: EditText
    private lateinit var edtDataNascimento: EditText
    private lateinit var edtTelefoneCelular: EditText
    private lateinit var edtTelefoneSecundario: EditText
    private lateinit var edtCep: EditText
    private lateinit var edtEndereco: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minha_conta)

        iniciarViewModel()
        iniciarComponentes()
        observarDados()

        viewModel.carregarPerfil()
    }

    private fun iniciarViewModel() {
        val banco = BancoDeDadosApp.obterBancoDeDados(this)
        val repositorio = LojaRepository(banco.favoritoDao())
        val factory = LojaViewModelFactory(repositorio)

        viewModel = ViewModelProvider(this, factory)[LojaViewModel::class.java]
    }

    private fun iniciarComponentes() {
        txtNomeUsuario = findViewById(R.id.txtNomeUsuario)
        txtEmailUsuario = findViewById(R.id.txtEmailUsuario)

        edtNomeCompleto = findViewById(R.id.edtNomeCompleto)
        edtCpf = findViewById(R.id.edtCpf)
        edtRg = findViewById(R.id.edtRg)
        edtSexo = findViewById(R.id.edtSexo)
        edtDataNascimento = findViewById(R.id.edtDataNascimento)
        edtTelefoneCelular = findViewById(R.id.edtTelefoneCelular)
        edtTelefoneSecundario = findViewById(R.id.edtTelefoneSecundario)
        edtCep = findViewById(R.id.edtCep)
        edtEndereco = findViewById(R.id.edtEndereco)
    }

    private fun observarDados() {
        viewModel.perfilUsuario.observe(this) { usuario ->
            if (usuario != null) {
                preencherTela(usuario)
            }
        }
    }

    private fun preencherTela(usuario: Usuario) {
        txtNomeUsuario.text = usuario.nome
        txtEmailUsuario.text = usuario.email

        edtNomeCompleto.setText(usuario.nome)
        edtCpf.setText(usuario.cpf)
        edtRg.setText(usuario.rg)
        edtSexo.setText(usuario.sexo)
        edtDataNascimento.setText(usuario.dtnascimento)
        edtTelefoneCelular.setText(usuario.telefone_cel)
        edtTelefoneSecundario.setText(usuario.telefone_sec)
        edtCep.setText(usuario.cep)
        edtEndereco.setText(usuario.endereco)
    }
}