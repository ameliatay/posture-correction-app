package com.example.posturecorrectionapp.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.adapters.SearchAdapter
import com.example.posturecorrectionapp.data.Datasource
import com.example.posturecorrectionapp.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exercisesDataset = Datasource().loadExercises()
        val exercisesRecyclerView = findViewById<RecyclerView>(R.id.exercises_recycler_view)
        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        exercisesRecyclerView?.adapter = SearchAdapter(this, exercisesDataset)
    }

    fun backButtonClicked(view: View) {
        finish()
    }
}