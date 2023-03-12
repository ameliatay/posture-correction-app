package com.example.posturecorrectionapp.screens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.adapters.ExercisesAdapter
import com.example.posturecorrectionapp.adapters.ProgramCardAdapter
import com.example.posturecorrectionapp.data.Datasource
import com.example.posturecorrectionapp.databinding.ActivityExerciseListBinding
import com.example.posturecorrectionapp.databinding.ActivityNavigationBinding

class ExerciseListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exercisesDataset = Datasource().loadExercises()
        val exercisesRecyclerView = findViewById<RecyclerView>(R.id.exercises_recycler_view)
        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        exercisesRecyclerView?.adapter = ExercisesAdapter(this, exercisesDataset)
    }

    fun backButtonClicked(view: View) {
        finish()
    }
}