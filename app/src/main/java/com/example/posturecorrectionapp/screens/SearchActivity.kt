package com.example.posturecorrectionapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.adapters.SearchAdapter
import com.example.posturecorrectionapp.data.Datasource
import com.example.posturecorrectionapp.databinding.ActivitySearchBinding
import com.example.posturecorrectionapp.models.Exercises

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var exercisesDataset: List<Exercises>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchInput = intent.getStringExtra("search").toString()
        findViewById<TextView>(R.id.search_result).text = "Search results for $searchInput"

        if (searchInput == "push up") {
            exercisesDataset = Datasource().loadArmExercises()
        } else if (searchInput == "sit up") {
            exercisesDataset = Datasource().loadAbExercises()
        } else {
            exercisesDataset = Datasource().loadExercises()
        }

        val exercisesRecyclerView = findViewById<RecyclerView>(R.id.exercises_recycler_view)
        exercisesRecyclerView.layoutManager = LinearLayoutManager(this)
        exercisesRecyclerView?.adapter = SearchAdapter(this, exercisesDataset)

        var searchBar = findViewById<EditText>(R.id.search_button)
        searchBar?.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
            val input = searchBar.text.toString()
            if (input != "") {
                val it = Intent(this, SearchActivity::class.java)
                it.putExtra("search", input)
                finish()
                startActivity(it)
                searchBar.setText("")
            }
            return@OnKeyListener true
        }
            false
        })
    }

    fun backButtonClicked(view: View) {
        finish()
    }
}