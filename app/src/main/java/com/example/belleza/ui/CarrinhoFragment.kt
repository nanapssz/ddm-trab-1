package com.example.belleza.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.belleza.adapter.CarrinhoAdapter
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.databinding.FragmentCarrinhoBinding
import com.example.belleza.model.CarrinhoItem
import com.example.belleza.repository.LojaRepository
import com.example.belleza.viewmodel.LojaViewModel
import com.example.belleza.viewmodel.LojaViewModelFactory
import java.util.Locale

class CarrinhoFragment : Fragment() {

    private var _binding: FragmentCarrinhoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LojaViewModel
    private lateinit var adapter: CarrinhoAdapter

    private val valorEntrega = 15.59

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarrinhoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        iniciarViewModel()
        configurarRecyclerView()
        configurarCliques()
        observarDados()

        viewModel.carregarCarrinho()
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarCarrinho()
    }

    private fun iniciarViewModel() {
        val banco = BancoDeDadosApp.obterBancoDeDados(requireContext())
        val repositorio = LojaRepository(banco.favoritoDao(), requireContext().applicationContext)
        val factory = LojaViewModelFactory(repositorio)

        viewModel = ViewModelProvider(this, factory)[LojaViewModel::class.java]
    }

    private fun configurarRecyclerView() {
        adapter = CarrinhoAdapter(
            onMaisClick = { item ->
                viewModel.alterarQuantidadeCarrinho(
                    idProduto = item.idProduto,
                    novaQuantidade = item.quantidade + 1
                )
            },
            onMenosClick = { item ->
                viewModel.alterarQuantidadeCarrinho(
                    idProduto = item.idProduto,
                    novaQuantidade = item.quantidade - 1
                )
            }
        )

        binding.recyclerCarrinho.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCarrinho.adapter = adapter
    }

    private fun configurarCliques() {
        binding.btnVoltarCarrinho.visibility = View.GONE

        binding.txtEscolherMais.setOnClickListener {
            startActivity(Intent(requireContext(), CategoriaActivity::class.java))
        }

        binding.btnFinalizarCompra.setOnClickListener {
            Toast.makeText(requireContext(), "Finalização em desenvolvimento", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observarDados() {
        viewModel.carrinho.observe(viewLifecycleOwner) { itens ->
            adapter.atualizarLista(itens)
            atualizarResumo(itens)
            atualizarEstadoVazio(itens)
        }

        viewModel.statusOperacao.observe(viewLifecycleOwner) { sucesso ->
            if (!sucesso) {
                Toast.makeText(requireContext(), "Não foi possível atualizar o carrinho", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarEstadoVazio(itens: List<CarrinhoItem>) {
        if (itens.isEmpty()) {
            binding.recyclerCarrinho.visibility = View.GONE
            binding.txtCarrinhoVazio.visibility = View.VISIBLE
        } else {
            binding.recyclerCarrinho.visibility = View.VISIBLE
            binding.txtCarrinhoVazio.visibility = View.GONE
        }
    }

    private fun atualizarResumo(itens: List<CarrinhoItem>) {
        val subtotal = itens.sumOf { item ->
            item.preco * item.quantidade
        }

        val entrega = if (itens.isEmpty()) 0.0 else valorEntrega
        val total = subtotal + entrega

        binding.txtSubtotalProdutos.text = formatarPreco(subtotal)
        binding.txtEntrega.text = formatarPreco(entrega)
        binding.txtTotal.text = formatarPreco(total)
    }

    private fun formatarPreco(valor: Double): String {
        return String.format(Locale("pt", "BR"), "R$ %.2f", valor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}