package com.example.posturecorrectionapp.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.Workout
import com.example.posturecorrectionapp.databinding.ActivityNavigationBinding

class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavigationBinding

    private val fragmentManager = supportFragmentManager
    private val homeFragment = HomeFragment()
    private val programsFragment = ProgramsFragment()
    private val practiceFragment = PracticeFragment()
    private val profileFragment = ProfileFragment()
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUi()

        //To test out the the workout with camera, uncomment this line
        //goToWorkout()
    }

    private fun setUpUi() {
        fragmentManager.beginTransaction().apply {
            add(R.id.container, homeFragment, "home")
            add(R.id.container, programsFragment, "programs").hide(programsFragment)
            add(R.id.container, practiceFragment, "practice").hide(practiceFragment)
            add(R.id.container, profileFragment, "profile").hide(profileFragment)
        }.commit()

        binding.bottomNavView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(homeFragment).commit()
                    activeFragment = homeFragment
                    true
                }
                R.id.programs -> {
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(programsFragment).commit()
                    activeFragment = programsFragment
                    true
                }
                R.id.practice -> {
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(practiceFragment).commit()
                    activeFragment = practiceFragment
                    true
                }
                R.id.profile -> {
                    fragmentManager.beginTransaction().hide(activeFragment)
                        .show(profileFragment).commit()
                    activeFragment = profileFragment
                    true
                }
                else -> false
            }
        }
    }

     fun goToWorkout() {
         var it = Intent(this, Workout::class.java)
        startActivity(it)
     }
}