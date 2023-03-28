package com.example.posturecorrectionapp.screens

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.adapters.ProgramsAdapter
import com.example.posturecorrectionapp.data.Datasource
import com.example.posturecorrectionapp.models.ProgramCard

class ProgramsFragment : Fragment(R.layout.fragment_programs) {
    private lateinit var hiitAdapter: ProgramsAdapter
    private lateinit var barreAdapter: ProgramsAdapter
    private lateinit var crossfitAdapter: ProgramsAdapter
    private lateinit var pilatesAdapter: ProgramsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // search bar
        val searchView = view.findViewById<SearchView>(R.id.search_view)

        // hiit
        val hiitDataset = Datasource().loadHiitProgram()
        val hiitAdapter = ProgramsAdapter(this, hiitDataset)
        val hiit_rv = getView()?.findViewById<RecyclerView>(R.id.hiit_rv)
        hiit_rv?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        hiit_rv?.adapter = hiitAdapter

        // barre
        val barreDataset = Datasource().loadBarreProgram()
        val barreAdapter = ProgramsAdapter(this, barreDataset)
        val barre_rv = getView()?.findViewById<RecyclerView>(R.id.barre_rv)
        barre_rv?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        barre_rv?.adapter = barreAdapter

        // crossfit
        val crossfitDataset = Datasource().loadCrossfitProgram()
        val crossfitAdapter = ProgramsAdapter(this, crossfitDataset)
        val crossfit_rv = getView()?.findViewById<RecyclerView>(R.id.crossfit_rv)
        crossfit_rv?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        crossfit_rv?.adapter = crossfitAdapter

        // pilates
        val pilatesDataset = Datasource().loadPilatesProgram()
        val pilatesAdapter = ProgramsAdapter(this, pilatesDataset)
        val pilates_rv = getView()?.findViewById<RecyclerView>(R.id.pilates_rv)
        pilates_rv?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        pilates_rv?.adapter = pilatesAdapter

        // Add search listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Filter your adapter here based on the search query
                // Filter hiit dataset
                val filteredHiitDataset = filterDataset(hiitDataset, newText)
                hiitAdapter.filterDataset(filteredHiitDataset)

                // Filter barre dataset
                val filteredBarreDataset = filterDataset(barreDataset, newText)
                barreAdapter.filterDataset(filteredBarreDataset)

                // Filter crossfit dataset
                val filteredCrossfitDataset = filterDataset(crossfitDataset, newText)
                crossfitAdapter.filterDataset(filteredCrossfitDataset)

                // Filter pilates dataset
                val filteredPilatesDataset = filterDataset(pilatesDataset, newText)
                pilatesAdapter.filterDataset(filteredPilatesDataset)
                return false
            }
        })
    }

    private fun filterDataset(dataset: List<ProgramCard>, query: String): List<ProgramCard> {
        return dataset.filter {
            it.programStringId.toString().contains(query, true) || it.categoryStringId.toString().contains(query, true)
        }
    }

}