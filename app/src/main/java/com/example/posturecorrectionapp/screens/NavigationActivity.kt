package com.example.posturecorrectionapp.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.databinding.ActivityNavigationBinding

class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding

    private val fragmentManager = supportFragmentManager
    private val exploreFragment = ExploreFragment()
    private val savedFragment = SavedFragment()
    private var activeFragment: Fragment = exploreFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUi()
    }

    private fun setUpUi() {
        fragmentManager.beginTransaction().apply {
            add(R.id.container, exploreFragment, "explore")
            add(R.id.container, savedFragment, "saved").hide(savedFragment)
        }.commit()

        binding.bottomNavView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.explore -> {
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(exploreFragment).commit()
                    activeFragment = exploreFragment
                    true
                }
                R.id.saved -> {
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(savedFragment).commit()
                    activeFragment = savedFragment

                    true
                }
                else -> false
            }
        }
    }
}