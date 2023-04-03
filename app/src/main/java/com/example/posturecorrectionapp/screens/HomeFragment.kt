package com.example.posturecorrectionapp.screens

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.Workout
import com.example.posturecorrectionapp.adapters.ProgramCardAdapter
import com.example.posturecorrectionapp.data.Datasource

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference =  activity?.getSharedPreferences("userPreferences",
            AppCompatActivity.MODE_PRIVATE
        )
        getView()?.findViewById<TextView>(R.id.tvUserGreeting)?.text = "Hello, ${sharedPreference?.getString("name", "welcome back")}!"

        val jumpBackInDataset = Datasource().loadJumpBackIn()
        val programCardAdapterA = ProgramCardAdapter(this, jumpBackInDataset)
        programCardAdapterA.onItemClick = { programCard -> goToWorkout() }
        val jumpBackInRecyclerView = getView()?.findViewById<RecyclerView>(R.id.jump_back_in_recycler_view)
        jumpBackInRecyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        jumpBackInRecyclerView?.adapter = programCardAdapterA

        val recommendedDataset = Datasource().loadRecommended()
        val programCardAdapterB = ProgramCardAdapter(this, recommendedDataset)
        programCardAdapterB.onItemClick = { programCard -> goToWorkout() }
        val recommendedRecyclerView = getView()?.findViewById<RecyclerView>(R.id.recommended_recycler_view)
        recommendedRecyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recommendedRecyclerView?.adapter = programCardAdapterB

        val recentlyCompletedDataset = Datasource().loadRecentlyCompleted()
        val programCardAdapterC = ProgramCardAdapter(this, recentlyCompletedDataset)
        programCardAdapterC.onItemClick = { programCard -> goToWorkout() }
        val recentlyCompletedRecyclerView = getView()?.findViewById<RecyclerView>(R.id.completed_recycler_view)
        recentlyCompletedRecyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recentlyCompletedRecyclerView?.adapter = programCardAdapterC
    }

    fun goToWorkout() {
        var workoutRoutine = ArrayList<Map<String,String>>()
        workoutRoutine.add(mapOf("name" to "squat", "duration" to "5"))
        workoutRoutine.add(mapOf("name" to "break", "duration" to "5"))
        workoutRoutine.add(mapOf("name" to "treepose", "duration" to "5"))
        workoutRoutine.add(mapOf("name" to "break", "duration" to "5"))
//        workoutRoutine.add(mapOf("name" to "pushup", "duration" to "25"))

        var it = Intent(activity, Workout::class.java)
        it.putExtra("workoutRoutine", workoutRoutine)
        startActivity(it)
    }
}