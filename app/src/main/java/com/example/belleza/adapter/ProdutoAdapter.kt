package com.example.belleza

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belleza.model.Produto

class ProdutoAdapter(
    private val isGrid: Boolean = false,
    private val onClick: (Produto) -> Unit,
    private val onFavoritoClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    private val listaProdutos = mutableListOf<Produto>()

    private var idsFavoritos = setOf<String>()

    class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduto: ImageView = itemView.findViewById(R.id.imgProdutoItem)
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeProdutoItem)
        val txtPreco: TextView = itemView.findViewById(R.id.txtPrecoProdutoItem)
        val btnCoracao: ImageButton = itemView.findViewById(R.id.btnFavoritoItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val layout = if (isGrid) R.layout.item_produto_grid else R.layout.item_produto
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = listaProdutos[position]
        val contexto = holder.itemView.context

        holder.txtNome.text = produto.titulo
        holder.txtPreco.text = "R$ ${String.format("%.2f", produto.preco)}"

        Glide.with(contexto)
            .load(produto.urlImagem)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.imgProduto)

        val ehFavorito = idsFavoritos.contains(produto.id)

        if (ehFavorito) {
            holder.btnCoracao.setImageResource(R.drawable.ic_heart_filled)
            holder.btnCoracao.setColorFilter(ContextCompat.getColor(contexto, R.color.terracota_marrom))
        } else {
            holder.btnCoracao.setImageResource(R.drawable.ic_heart_border)
            holder.btnCoracao.setColorFilter(Color.GRAY)
        }

        // Cliques
        holder.itemView.setOnClickListener { onClick(produto) }
        holder.btnCoracao.setOnClickListener { onFavoritoClick(produto) }
    }

    override fun getItemCount() = listaProdutos.size

    fun atualizarLista(novaLista: List<Produto>) {
        listaProdutos.clear()
        listaProdutos.addAll(novaLista)
        notifyDataSetChanged()
    }

    fun atualizarFavoritos(novosIds: Set<String>) {
        this.idsFavoritos = novosIds
        notifyDataSetChanged()
    }
}