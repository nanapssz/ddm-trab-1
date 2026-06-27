package com.example.belleza

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belleza.model.Produto

class ProdutoAdapter(
    private val isGrid: Boolean = false,
    private val onClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    private val listaProdutos = mutableListOf<Produto>()

    class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduto: ImageView = itemView.findViewById(R.id.imgProdutoItem)
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeProdutoItem)
        val txtPreco: TextView = itemView.findViewById(R.id.txtPrecoProdutoItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produto, parent, false)
        
        if (isGrid) {
            val params = view.layoutParams as RecyclerView.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.marginEnd = 0 // Remove margin end for better grid distribution
            // Adicionar uma margem pequena para espaçamento entre itens se necessário
            val margin = (4 * parent.context.resources.displayMetrics.density).toInt()
            params.setMargins(margin, margin, margin, margin)
            view.layoutParams = params
        }

        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = listaProdutos[position]

        holder.txtNome.text = produto.titulo
        holder.txtPreco.text = "R$ ${String.format("%.2f", produto.preco)}"

        Glide.with(holder.itemView.context)
            .load(produto.urlImagem)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.imgProduto)

        holder.itemView.setOnClickListener { onClick(produto) }
    }

    override fun getItemCount(): Int = listaProdutos.size

    fun atualizarLista(novaLista: List<Produto>) {
        listaProdutos.clear()
        listaProdutos.addAll(novaLista)
        notifyDataSetChanged()
    }
}