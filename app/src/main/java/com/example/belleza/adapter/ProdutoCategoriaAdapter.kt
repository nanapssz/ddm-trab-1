package com.example.belleza.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belleza.databinding.ItemProdutoCategoriaBinding
import com.example.belleza.model.Produto

class ProdutoCategoriaAdapter(
    private val onProdutoClick: (Produto) -> Unit,
    private val onFavoritoClick: (Produto) -> Unit,
    private val onAdicionarCarrinhoClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoCategoriaAdapter.ProdutoViewHolder>() {

    private val produtos = mutableListOf<Produto>()

    inner class ProdutoViewHolder(
        private val binding: ItemProdutoCategoriaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(produto: Produto) {
            binding.txtNomeProduto.text = produto.titulo
            binding.txtPrecoProduto.text = formatarPreco(produto.preco)

            // Caso vocês ainda não tenham campo de nota no banco
            binding.txtNotaProduto.text = "★ 4,5"

            Glide.with(binding.imgProduto.context)
                .load(produto.urlImagem)
                .centerCrop()
                .into(binding.imgProduto)

            binding.cardProdutoCategoria.setOnClickListener {
                onProdutoClick(produto)
            }

            binding.btnFavoritoProduto.setOnClickListener {
                onFavoritoClick(produto)
            }

            binding.root.setOnLongClickListener {
                onAdicionarCarrinhoClick(produto)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val binding = ItemProdutoCategoriaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ProdutoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        holder.bind(produtos[position])
    }

    override fun getItemCount(): Int {
        return produtos.size
    }

    fun atualizarLista(novaLista: List<Produto>) {
        produtos.clear()
        produtos.addAll(novaLista)
        notifyDataSetChanged()
    }

    private fun formatarPreco(valor: Double): String {
        return "R$ %.2f".format(valor).replace(".", ",")
    }
}