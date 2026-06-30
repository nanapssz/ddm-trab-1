package com.example.belleza.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belleza.R
import com.example.belleza.databinding.ItemCarrinhoBinding
import com.example.belleza.model.CarrinhoItem
import java.util.Locale

class CarrinhoAdapter(
    private val onMaisClick: (CarrinhoItem) -> Unit,
    private val onMenosClick: (CarrinhoItem) -> Unit
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    private val itens = mutableListOf<CarrinhoItem>()

    inner class CarrinhoViewHolder(
        private val binding: ItemCarrinhoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CarrinhoItem) {
            binding.txtNomeProdutoCarrinho.text = item.titulo
            binding.txtPrecoProdutoCarrinho.text = formatarPreco(item.preco)
            binding.txtQuantidadeCarrinho.text = item.quantidade.toString()

            Glide.with(binding.imgProdutoCarrinho.context)
                .load(item.urlImagem)
                .placeholder(R.drawable.fundo_card)
                .error(R.drawable.fundo_card)
                .centerCrop()
                .into(binding.imgProdutoCarrinho)

            binding.btnMaisCarrinho.setOnClickListener {
                onMaisClick(item)
            }

            binding.btnMenosCarrinho.setOnClickListener {
                onMenosClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val binding = ItemCarrinhoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CarrinhoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        holder.bind(itens[position])
    }

    override fun getItemCount(): Int {
        return itens.size
    }

    fun atualizarLista(novaLista: List<CarrinhoItem>) {
        itens.clear()
        itens.addAll(novaLista)
        notifyDataSetChanged()
    }

    private fun formatarPreco(valor: Double): String {
        return String.format(Locale("pt", "BR"), "R$ %.2f", valor)
    }
}