package com.example.posturecorrectionapp.adapters

import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.models.CarouselItem
import com.example.posturecorrectionapp.screens.IntroductionActivity
import com.example.posturecorrectionapp.screens.QuestionnaireActivity

class IntroductionAdapter(private val context: IntroductionActivity,
                          private val dataset: List<CarouselItem>): RecyclerView.Adapter<IntroductionAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.item_image)
        val titleView: TextView = view.findViewById(R.id.item_title)
        val subtitleView: TextView = view.findViewById(R.id.item_subtitle)
        val numberView: LinearLayout = view.findViewById(R.id.position_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.component_carousel_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.titleView.text = context.resources.getString(item.titleStringId)
        holder.subtitleView.text = context.resources.getString(item.subtitleStringId)
        holder.imageView.setImageResource(item.imageResourceId)

        val number = item.position
        holder.numberView.removeAllViews()

        for (i in 1..itemCount) {
            var newView = ImageView(context);
            if (i == number) {
                newView.layoutParams = LinearLayout.LayoutParams(25, 25)
                newView.marginLeft
                newView.setImageResource(R.drawable.selected_circle)
            } else if (number == itemCount) {
                val button = AppCompatButton(ContextThemeWrapper(context, R.style.PinkButton));
                button.text = "Get started"
                button.setOnClickListener {
                    context.startActivity(Intent(context, QuestionnaireActivity::class.java))
                }
                holder.numberView.addView(button)
                break
            }
            else {
                newView.layoutParams = LinearLayout.LayoutParams(20, 20)
                newView.setImageResource(R.drawable.light_circle)
            }
            holder.numberView.addView(newView)
        }
    }

    override fun getItemCount() = dataset.size
}