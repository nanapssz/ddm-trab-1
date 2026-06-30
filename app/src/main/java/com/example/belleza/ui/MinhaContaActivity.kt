package com.example.belleza.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.belleza.R
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.databinding.ActivityMinhaContaBinding
import com.example.belleza.model.Usuario
import com.example.belleza.repository.LojaRepository
import com.example.belleza.viewmodel.LojaViewModel
import com.example.belleza.viewmodel.LojaViewModelFactory
import java.io.File
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.graphics.BitmapFactory
import android.util.Base64

class MinhaContaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMinhaContaBinding
    private lateinit var viewModel: LojaViewModel

    private var fotoUriCamera: Uri? = null
    private var fotoUrlAtual: String = ""

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { sucesso ->
        if (sucesso) {
            fotoUriCamera?.let { uri ->
                mostrarFotoNaTela(uri)
                viewModel.atualizarFotoPerfil(uri)
            }
        } else {
            Toast.makeText(this, "Foto cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private val permissaoCameraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissaoConcedida ->
        if (permissaoConcedida) {
            abrirCameraComPermissao()
        } else {
            Toast.makeText(
                this,
                "Permissão da câmera negada",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val galeriaLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            mostrarFotoNaTela(uri)
            viewModel.atualizarFotoPerfil(uri)
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
        val repositorio = LojaRepository(banco.favoritoDao(), applicationContext)
        val factory = LojaViewModelFactory(repositorio)

        viewModel = ViewModelProvider(this, factory)[LojaViewModel::class.java]
    }

    private fun configurarCliques() {
        binding.btnVoltarConta.setOnClickListener {
            finish()
        }

        binding.btnTrocarFoto.setOnClickListener {
            mostrarDialogoFoto()
        }

        binding.btnSalvarPerfil.setOnClickListener {
            salvarPerfil()
        }

        binding.menuHomeConta.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.menuCarrinhoConta.setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
        }

        binding.menuUsuarioConta.setOnClickListener {
            binding.scrollMinhaConta.smoothScrollTo(0, 0)
        }

        binding.menuFavoritosConta.setOnClickListener {
            Toast.makeText(this, "Favoritos em desenvolvimento", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observarDados() {
        viewModel.perfilUsuario.observe(this) { usuario ->
            usuario?.let {
                preencherTela(it)
            }
        }

        viewModel.mensagemOperacao.observe(this) { mensagem ->
            Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarDialogoFoto() {
        val opcoes = arrayOf("Tirar foto", "Escolher da galeria")

        AlertDialog.Builder(this)
            .setTitle("Alterar foto de perfil")
            .setItems(opcoes) { _, opcao ->
                when (opcao) {
                    0 -> abrirCamera()
                    1 -> abrirGaleria()
                }
            }
            .show()
    }

    private fun abrirCamera() {
        val permissao = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )

        if (permissao == PackageManager.PERMISSION_GRANTED) {
            abrirCameraComPermissao()
        } else {
            permissaoCameraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCameraComPermissao() {
        try {
            val arquivoFoto = File.createTempFile(
                "foto_perfil_${System.currentTimeMillis()}",
                ".jpg",
                cacheDir
            )

            val uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                arquivoFoto
            )

            fotoUriCamera = uri
            cameraLauncher.launch(uri)

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Erro ao abrir a câmera: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun abrirGaleria() {
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()

        galeriaLauncher.launch(request)
    }

    private fun mostrarFotoNaTela(uri: Uri) {
        binding.imgPerfilConta.imageTintList = null

        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .circleCrop()
            .into(binding.imgPerfilConta)
    }

    private fun preencherTela(usuario: Usuario) {
        fotoUrlAtual = usuario.fotoUrl

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

        binding.imgPerfilConta.imageTintList = null

        if (usuario.fotoBase64.isNotEmpty()) {
            val bytes = Base64.decode(usuario.fotoBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            Glide.with(this)
                .load(bitmap)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .circleCrop()
                .into(binding.imgPerfilConta)
        } else if (usuario.fotoUrl.isNotEmpty()) {
            Glide.with(this)
                .load(usuario.fotoUrl)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .circleCrop()
                .into(binding.imgPerfilConta)
        } else {
            binding.imgPerfilConta.setImageResource(R.drawable.ic_user)
        }
    }

    private fun salvarPerfil() {
        val usuarioAtual = viewModel.perfilUsuario.value

        val usuario = Usuario(
            nome = binding.edtNomeCompleto.text.toString(),
            cpf = binding.edtCpf.text.toString(),
            rg = binding.edtRg.text.toString(),
            sexo = binding.edtSexo.text.toString(),
            dtnascimento = binding.edtDataNascimento.text.toString(),
            telefone_cel = binding.edtTelefoneCelular.text.toString(),
            telefone_sec = binding.edtTelefoneSecundario.text.toString(),
            cep = binding.edtCep.text.toString(),
            endereco = binding.edtEndereco.text.toString(),
            email = binding.txtEmailUsuario.text.toString(),
            fotoUrl = usuarioAtual?.fotoUrl ?: "",
            fotoBase64 = usuarioAtual?.fotoBase64 ?: ""
        )

        viewModel.salvarPerfil(usuario)
    }
}