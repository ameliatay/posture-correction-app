package com.example.posturecorrectionapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.models.ListItem
import com.example.posturecorrectionapp.screens.ProfileFragment

class ProfileAdapter(private val context: ProfileFragment, private val listItems: List<ListItem>) : RecyclerView.Adapter<ProfileAdapter.MyViewHolder>(){

        class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val itemIcon: ImageView = itemView.findViewById(R.id.item_icon)
            val itemText: TextView = itemView.findViewById(R.id.item_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_profile_list, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val currentItem = listItems[position]
            holder.itemIcon.setImageResource(currentItem.icon)
            holder.itemText.text = currentItem.text
            holder.itemView.setOnClickListener {
                // Handle item click event here
                when (position) {
                    0 -> {
                        // Handle Profile click
                        // Start ProfileActivity
                    }
                    1 -> {
                        // Handle Notifications click
                        // Start NotificationsActivity
                    }
                    2 -> {
                        // Handle Security click
                        // Start SecurityActivity
                    }
                    3 -> {
                        // Handle Help click
                        // Start HelpActivity
                    }
                    4 -> {
                        // Handle Dark Theme click
                        // Start DarkThemeActivity
                    }
                    5 -> {
                        // Handle Logout click
                        // Start LoginActivity
                    }
                }
            }
        }

        override fun getItemCount() = listItems.size
    }
