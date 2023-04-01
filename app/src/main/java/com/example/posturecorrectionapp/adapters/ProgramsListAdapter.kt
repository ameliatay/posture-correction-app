package com.example.posturecorrectionapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.models.Exercises
import com.example.posturecorrectionapp.screens.ProgramsListActivity

class ProgramsListAdapter(private val context: ProgramsListActivity,
                          private val dataset: List<Exercises>): RecyclerView.Adapter<ProgramsListAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val categoryView: TextView = view.findViewById(R.id.item_category)
        val nameView: TextView = view.findViewById(R.id.item_name)
        val difficultyView: TextView = view.findViewById(R.id.item_difficulty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.component_exercise_card, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.categoryView.text = context.resources.getString(item.categoryStringId)
        holder.nameView.text = context.resources.getString(item.exerciseStringId)
        holder.difficultyView.text = context.resources.getString(item.difficulty)
        holder.imageView.setImageResource(item.imageResourceId)
    }

    override fun getItemCount() = dataset.size
}