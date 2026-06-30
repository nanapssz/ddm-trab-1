package com.example.belleza.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.belleza.R
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.databinding.FragmentMinhaContaBinding
import com.example.belleza.model.Usuario
import com.example.belleza.repository.LojaRepository
import com.example.belleza.viewmodel.LojaViewModel
import com.example.belleza.viewmodel.LojaViewModelFactory
import java.io.File

class MinhaContaFragment : Fragment() {

    private var _binding: FragmentMinhaContaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LojaViewModel

    private var fotoUriCamera: Uri? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { sucesso ->
        if (sucesso) {
            fotoUriCamera?.let { uri ->
                mostrarFotoNaTela(uri)
                viewModel.atualizarFotoPerfil(uri)
            }
        } else {
            Toast.makeText(requireContext(), "Foto cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private val permissaoCameraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissaoConcedida ->
        if (permissaoConcedida) {
            abrirCameraComPermissao()
        } else {
            Toast.makeText(requireContext(), "Permissão da câmera negada", Toast.LENGTH_SHORT).show()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMinhaContaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        iniciarViewModel()
        configurarCliques()
        observarDados()

        viewModel.carregarPerfil()
    }

    private fun iniciarViewModel() {
        val banco = BancoDeDadosApp.obterBancoDeDados(requireContext())
        val repositorio = LojaRepository(banco.favoritoDao(), requireContext().applicationContext)
        val factory = LojaViewModelFactory(repositorio)

        viewModel = ViewModelProvider(this, factory)[LojaViewModel::class.java]
    }

    private fun configurarCliques() {
        binding.btnVoltarConta.visibility = View.GONE

        binding.btnTrocarFoto.setOnClickListener {
            mostrarDialogoFoto()
        }

        binding.btnSalvarPerfil.setOnClickListener {
            salvarPerfil()
        }

        binding.btnConfiguracoesConta.setOnClickListener {
            Toast.makeText(requireContext(), "Configurações em desenvolvimento", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observarDados() {
        viewModel.perfilUsuario.observe(viewLifecycleOwner) { usuario ->
            usuario?.let {
                preencherTela(it)
            }
        }

        viewModel.mensagemOperacao.observe(viewLifecycleOwner) { mensagem ->
            Toast.makeText(requireContext(), mensagem, Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarDialogoFoto() {
        val opcoes = arrayOf("Tirar foto", "Escolher da galeria")

        AlertDialog.Builder(requireContext())
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
            requireContext(),
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
                requireContext().cacheDir
            )

            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                arquivoFoto
            )

            fotoUriCamera = uri
            cameraLauncher.launch(uri)

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}