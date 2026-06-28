package com.example.belleza.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.belleza.R
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.databinding.ActivityMinhaContaBinding
import com.example.belleza.model.Usuario
import com.example.belleza.repository.LojaRepository
import com.example.belleza.viewmodel.LojaViewModel
import com.example.belleza.viewmodel.LojaViewModelFactory

class MinhaContaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMinhaContaBinding
    private lateinit var viewModel: LojaViewModel

    private val galeriaLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imgPerfilConta.setImageURI(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                binding.imgPerfilConta.setImageBitmap(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMinhaContaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iniciarViewModel()
        configurarCliques()
        observarDados()

        viewModel.carregarPerfil()
    }

    private fun iniciarViewModel() {
        val banco = BancoDeDadosApp.obterBancoDeDados(this)
        val repositorio = LojaRepository(banco.favoritoDao())
        val factory = LojaViewModelFactory(repositorio)
        viewModel = ViewModelProvider(this, factory)[LojaViewModel::class.java]
    }

    private fun observarDados() {
        viewModel.perfilUsuario.observe(this) { usuario ->
            usuario?.let { preencherTela(it) }
        }
    }

    private fun configurarCliques() {
        binding.btnVoltarConta.setOnClickListener { finish() }

        binding.btnTrocarFoto.setOnClickListener {
            mostrarDialogoFoto()
        }

        binding.btnSalvarPerfil.setOnClickListener {
            Toast.makeText(this, "Alterações salvas!", Toast.LENGTH_SHORT).show()
        }

        binding.menuHomeConta.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun mostrarDialogoFoto() {
        val opcoes = arrayOf("Tirar Foto", "Escolher da Galeria")
        AlertDialog.Builder(this)
            .setTitle("Alterar foto de perfil")
            .setItems(opcoes) { _, which ->
                when (which) {
                    0 -> cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                    1 -> galeriaLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun preencherTela(usuario: Usuario) {
        binding.txtNomeUsuario.text = usuario.nome
        binding.txtEmailUsuario.text = usuario.email

        binding.edtNomeCompleto.setText(usuario.nome)
        binding.edtCpf.setText(usuario.cpf)
        binding.edtRg.setText(usuario.rg)
        binding.edtSexo.setText(usuario.sexo)
        binding.edtDataNascimento.setText(usuario.dtnascimento)
        binding.edtTelefoneCelular.setText(usuario.telefone_cel)
        binding.edtTelefoneSecundario.setText(usuario.telefone_sec)
        binding.edtCep.setText(usuario.cep)
        binding.edtEndereco.setText(usuario.endereco)

        if (!usuario.fotoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(usuario.fotoUrl)
                .placeholder(R.drawable.ic_user)
                .into(binding.imgPerfilConta)
        }
    }
}