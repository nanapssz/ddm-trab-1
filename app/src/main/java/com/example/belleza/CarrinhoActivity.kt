package com.example.belleza

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.belleza.database.BancoDeDadosApp
import com.example.belleza.model.CarrinhoItem
import com.example.belleza.repository.LojaRepository
import com.example.belleza.viewmodel.LojaViewModel
import com.example.belleza.viewmodel.LojaViewModelFactory

class CarrinhoActivity : AppCompatActivity() {

    private lateinit var viewModel: LojaViewModel

    private lateinit var txtSubtotalProdutos: TextView
    private lateinit var txtEntrega: TextView
    private lateinit var txtTotal: TextView

    private val valorEntrega = 15.59

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrinho)

        iniciarViewModel()
        iniciarComponentes()
        observarDados()

        viewModel.carregarCarrinho()
    }

    private fun iniciarViewModel() {
        val banco = BancoDeDadosApp.obterBancoDeDados(this)
        val repositorio = LojaRepository(banco.favoritoDao())
        val factory = LojaViewModelFactory(repositorio)

        viewModel = ViewModelProvider(this, factory)[LojaViewModel::class.java]
    }

    private fun iniciarComponentes() {
        txtSubtotalProdutos = findViewById(R.id.txtSubtotalProdutos)
        txtEntrega = findViewById(R.id.txtEntrega)
        txtTotal = findViewById(R.id.txtTotal)
    }

    private fun observarDados() {
        viewModel.carrinho.observe(this) { itens ->
            atualizarResumo(itens)

            // Depois entra:
            // adapter.atualizarLista(itens)
        }
    }

    private fun atualizarResumo(itens: List<CarrinhoItem>) {
        val subtotal = itens.sumOf { item ->
            item.preco * item.quantidade
        }

        val total = if (itens.isEmpty()) {
            0.0
        } else {
            subtotal + valorEntrega
        }

        txtSubtotalProdutos.text = formatarPreco(subtotal)
        txtEntrega.text = if (itens.isEmpty()) "R$ 0,00" else formatarPreco(valorEntrega)
        txtTotal.text = formatarPreco(total)
    }

    private fun formatarPreco(valor: Double): String {
        return "R$ %.2f".format(valor).replace(".", ",")
    }
}