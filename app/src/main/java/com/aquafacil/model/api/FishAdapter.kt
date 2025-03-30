package com.aquafacil.model.api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aquafacil.R
import com.aquafacil.model.FishSpecies

class FishAdapter(private var fishList: List<FishSpecies>) :
    RecyclerView.Adapter<FishAdapter.FishViewHolder>() {

    class FishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textFishName)
        val scientificNameTextView: TextView = itemView.findViewById(R.id.textScientificName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fish, parent, false)
        return FishViewHolder(view)
    }

    override fun onBindViewHolder(holder: FishViewHolder, position: Int) {
        val fish = fishList[position]
        holder.nameTextView.text = fish.name
        holder.scientificNameTextView.text = fish.scientificName ?: "Nome científico desconhecido"
    }

    override fun getItemCount() = fishList.size

    fun updateData(newList: List<FishSpecies>) {
        fishList = newList
        notifyDataSetChanged()
    }
}
