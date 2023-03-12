package com.example.posturecorrectionapp.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.adapters.ProgramCardAdapter
import com.example.posturecorrectionapp.data.Datasource

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jumpBackInDataset = Datasource().loadJumpBackIn()
        val jumpBackInRecyclerView = getView()?.findViewById<RecyclerView>(R.id.jump_back_in_recycler_view)
        jumpBackInRecyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        jumpBackInRecyclerView?.adapter = ProgramCardAdapter(this, jumpBackInDataset)

        val recommendedDataset = Datasource().loadRecommended()
        val recommendedRecyclerView = getView()?.findViewById<RecyclerView>(R.id.recommended_recycler_view)
        recommendedRecyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recommendedRecyclerView?.adapter = ProgramCardAdapter(this, recommendedDataset)

        val recentlyCompletedDataset = Datasource().loadRecentlyCompleted()
        val recentlyCompletedRecyclerView = getView()?.findViewById<RecyclerView>(R.id.completed_recycler_view)
        recentlyCompletedRecyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recentlyCompletedRecyclerView?.adapter = ProgramCardAdapter(this, recentlyCompletedDataset)
    }
}