package com.example.posturecorrectionapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.models.CategoryCard
import com.example.posturecorrectionapp.screens.PracticeFragment

class CategoryCardAdapter(private val context: PracticeFragment,
                         private val dataset: List<CategoryCard>): RecyclerView.Adapter<CategoryCardAdapter.ItemViewHolder>() {

    var onItemClick: ((CategoryCard) -> Unit)? = null

    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val categoryView: TextView = view.findViewById(R.id.item_category)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(dataset[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.component_category_card, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.categoryView.text = context.resources.getString(item.categoryStringId)
        holder.imageView.setImageResource(item.imageResourceId)
    }

    override fun getItemCount() = dataset.size
}