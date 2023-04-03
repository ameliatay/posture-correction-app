package com.example.posturecorrectionapp.screens

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.adapters.CategoryCardAdapter
import com.example.posturecorrectionapp.data.Datasource


class PracticeFragment : Fragment(R.layout.fragment_practice) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryDataset = Datasource().loadPracticeCategories()
        val categoryCardAdapter = CategoryCardAdapter(this, categoryDataset)
        categoryCardAdapter.onItemClick = { categoryCard ->
            val it = Intent(activity, ExerciseListActivity::class.java)
            it.putExtra("category", categoryCard.categoryStringId.toString())
            startActivity(it)
        }
        val practiceRecyclerView = getView()?.findViewById<RecyclerView>(R.id.practice_recycler_view)
        practiceRecyclerView?.layoutManager = LinearLayoutManager(activity)
        practiceRecyclerView?.adapter = categoryCardAdapter

        var searchBar = getView()?.findViewById<EditText>(R.id.search_button)
        searchBar?.setOnKeyListener(View.OnKeyListener { v, keyCode, event -> if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
            val input = searchBar.text.toString()
            if (input != "") {
                val it = Intent(activity, SearchActivity::class.java)
                it.putExtra("search", input)
                startActivity(it)
                searchBar.setText("")
            }
            return@OnKeyListener true
        }
            false
        })
    }

}