package com.example.posturecorrectionapp.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.Workout
import com.example.posturecorrectionapp.adapters.ProgramsListAdapter
import com.example.posturecorrectionapp.data.Datasource
import com.example.posturecorrectionapp.databinding.ActivityProgramsListBinding

class ProgramsListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProgramsListBinding
    private lateinit var workoutRoutine: ArrayList<Map<String, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgramsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val programName = intent.getStringExtra("program")
        findViewById<TextView>(R.id.tvJumpBackIn).text = programName!!

        val exercisesDataset = Datasource().loadExercisesDemo()
        workoutRoutine = ArrayList()
        var count = 0
        for (exercise in exercisesDataset) {
            val name = getStringFromId(exercise.exerciseStringId)
            count ++
            //Replace space with empty string, remove trailing s, and lowercase
            val key = name.replace(" ", "").removeSuffix("s").toLowerCase()
            // Create a map of the exercise name and duration
            val exerciseMap = mapOf("name" to key, "duration" to "15")
            workoutRoutine.add(exerciseMap)
            if (count < exercisesDataset.size) {
                workoutRoutine.add(mapOf("name" to "break", "duration" to "10"))
            }
        }
        val exercisesRecyclerView = findViewById<RecyclerView>(R.id.exercises_recycler_view)
        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        exercisesRecyclerView?.adapter = ProgramsListAdapter(this, exercisesDataset)

    }

    fun startWorkoutButtonClicked(view: View) {

        var it = Intent(this, Workout::class.java)
        it.putExtra("workoutRoutine", workoutRoutine)
        startActivity(it)
        finish()
    }

    fun getStringFromId(id: Int): String {
        return resources.getString(id)
    }

    fun backButtonClicked(view: View) {
        finish()
    }
}
