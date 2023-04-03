package com.example.posturecorrectionapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.adapters.ExercisesAdapter
import com.example.posturecorrectionapp.data.Datasource
import com.example.posturecorrectionapp.databinding.ActivityExerciseListBinding
import com.example.posturecorrectionapp.models.Exercises

class ExerciseListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExerciseListBinding
    private lateinit var exercisesDataset: List<Exercises>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resourceId = intent.getStringExtra("category")?.toInt()
        val title = resourceId?.let { this.resources.getString(it) }
        findViewById<TextView>(R.id.exerciseListTitle).text = title

        if (title == "Abs & Core") {
            exercisesDataset = Datasource().loadAbExercises()
        } else if (title == "Arms") {
            exercisesDataset = Datasource().loadArmExercises()
        } else {
            exercisesDataset = Datasource().loadExercises()
        }

        val exercisesAdapter = ExercisesAdapter(this, exercisesDataset)
        exercisesAdapter.onItemClick = { exercises ->
            val it = Intent(this, ExercisePageActivity::class.java)
            it.putExtra("exercise", exercises.exerciseStringId.toString())
            it.putExtra("difficulty", exercises.difficulty.toString())
            it.putExtra("category", exercises.categoryStringId.toString())
            startActivity(it)
        }
        val exercisesRecyclerView = findViewById<RecyclerView>(R.id.exercises_recycler_view)
        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        exercisesRecyclerView?.adapter = exercisesAdapter
    }

    fun backButtonClicked(view: View) {
        finish()
    }
}