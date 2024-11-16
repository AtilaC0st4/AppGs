package com.example.appgs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DicaAdapter(
    private val dicas: MutableList<String>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<DicaAdapter.DicaViewHolder>() {

    inner class DicaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDica: TextView = itemView.findViewById(R.id.textViewDicaSalva)
        val iconDelete: ImageView = itemView.findViewById(R.id.iconDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DicaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dica, parent, false)
        return DicaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DicaViewHolder, position: Int) {
        val dica = dicas[position]
        holder.textViewDica.text = dica

        // Ação de excluir a dica
        holder.iconDelete.setOnClickListener {
            onDeleteClick(dica)
        }
    }

    override fun getItemCount(): Int = dicas.size
}
