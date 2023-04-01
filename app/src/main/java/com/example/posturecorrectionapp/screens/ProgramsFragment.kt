package com.example.posturecorrectionapp.screens

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private lateinit var hiitDataset: List<ProgramCard>
    private lateinit var barreDataset: List<ProgramCard>
    private lateinit var crossfitDataset: List<ProgramCard>
    private lateinit var pilatesDataset: List<ProgramCard>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // search bar
        val searchView = view.findViewById<SearchView>(R.id.search_view)

        // hiit
        hiitDataset = Datasource().loadHiitProgram()
        hiitAdapter = ProgramsAdapter(this, hiitDataset)
        hiitAdapter.onItemClick = { programCard -> viewProgramDetails(programCard.programStringId) }
        val hiit_rv = getView()?.findViewById<RecyclerView>(R.id.hiit_rv)
        hiit_rv?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        hiit_rv?.adapter = hiitAdapter

        // barre
        barreDataset = Datasource().loadBarreProgram()
        barreAdapter = ProgramsAdapter(this, barreDataset)
        barreAdapter.onItemClick = { programCard -> viewProgramDetails(programCard.programStringId) }
        val barre_rv = getView()?.findViewById<RecyclerView>(R.id.barre_rv)
        barre_rv?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        barre_rv?.adapter = barreAdapter

        // crossfit
        crossfitDataset = Datasource().loadCrossfitProgram()
        crossfitAdapter = ProgramsAdapter(this, crossfitDataset)
        crossfitAdapter.onItemClick = { programCard -> viewProgramDetails(programCard.programStringId) }
        val crossfit_rv = getView()?.findViewById<RecyclerView>(R.id.crossfit_rv)
        crossfit_rv?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        crossfit_rv?.adapter = crossfitAdapter

        // pilates
        pilatesDataset = Datasource().loadPilatesProgram()
        pilatesAdapter = ProgramsAdapter(this, pilatesDataset)
        pilatesAdapter.onItemClick = { programCard -> viewProgramDetails(programCard.programStringId) }
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
                Log.d("",newText)
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
        // Filter dataset
        return dataset.filter {
            checkIfContains(it, query)
        }
    }

    private fun checkIfContains(card: ProgramCard, query: String): Boolean {
        //Find string value of programStringId and categoryStringId from resource ID
        val programString = resources.getString(card.programStringId)
        val categoryString = resources.getString(card.categoryStringId)
        return programString.contains(query, true) || categoryString.contains(query, true)
    }

    private fun viewProgramDetails(int: Int) {
        val intent = Intent(activity, ProgramsListActivity::class.java)
        val string = resources.getString(int)
        intent.putExtra("program", string)
        startActivity(intent)
    }

}