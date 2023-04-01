package com.example.posturecorrectionapp.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.adapters.ProgramsListAdapter
import com.example.posturecorrectionapp.data.Datasource
import com.example.posturecorrectionapp.databinding.ActivityExerciseListBinding

class ProgramsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exercisesDataset = Datasource().loadExercises()
        val exercisesRecyclerView = findViewById<RecyclerView>(R.id.exercises_recycler_view)
        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        exercisesRecyclerView?.adapter = ProgramsListAdapter(this, exercisesDataset)
    }

    fun backButtonClicked(view: View) {
        finish()
    }
}
