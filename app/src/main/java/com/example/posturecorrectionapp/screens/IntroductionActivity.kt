package com.example.posturecorrectionapp.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.adapters.IntroductionAdapter
import com.example.posturecorrectionapp.data.Datasource


class IntroductionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)

        val carouselDataset = Datasource().loadCarouselItems()
        val introductionRecyclerView = findViewById<RecyclerView>(R.id.intro_recycler_view)
        introductionRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        LinearSnapHelper().attachToRecyclerView(introductionRecyclerView)
        introductionRecyclerView?.adapter = IntroductionAdapter(this, carouselDataset)
    }

    fun goToNextPosition(view: View) {
        val introductionRecyclerView = findViewById<RecyclerView>(R.id.intro_recycler_view)
        introductionRecyclerView.layoutManager?.getPosition(view)
    }
}