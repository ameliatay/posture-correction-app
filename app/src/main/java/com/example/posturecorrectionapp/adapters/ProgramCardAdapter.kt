package com.example.posturecorrectionapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.models.CategoryCard
import com.example.posturecorrectionapp.models.ProgramCard
import com.example.posturecorrectionapp.screens.HomeFragment

class ProgramCardAdapter(private val context: HomeFragment,
                         private val dataset: List<ProgramCard>): RecyclerView.Adapter<ProgramCardAdapter.ItemViewHolder>() {

    var onItemClick: ((ProgramCard) -> Unit)? = null

    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val categoryView: TextView = view.findViewById(R.id.item_category)
        val programView: TextView = view.findViewById(R.id.item_title)
        val ratingView: TextView = view.findViewById(R.id.item_rating)
        val durationView: TextView = view.findViewById(R.id.item_duration)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(dataset[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.component_program_card, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.categoryView.text = context.resources.getString(item.categoryStringId)
        holder.programView.text = context.resources.getString(item.programStringId)
        holder.ratingView.text = context.resources.getString(item.rating)
        holder.durationView.text = context.resources.getString(item.duration)
        holder.imageView.setImageResource(item.imageResourceId)
    }

    override fun getItemCount() = dataset.size
}