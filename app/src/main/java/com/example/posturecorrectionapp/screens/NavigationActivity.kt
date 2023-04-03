package com.example.posturecorrectionapp.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
    private var profileFragment = ProfileFragment()
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreference =  getSharedPreferences("userPreferences", MODE_PRIVATE)
        val darkMode = sharedPreference.getBoolean("dark mode", false)
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // To force see introduction
        // If only want to see once, clear sharedPreferences once and rerun
        /*var editor = sharedPreference.edit()
        editor.remove("name")
        editor.commit()*/

        val name = sharedPreference.getString("name", null)
        if (name == null) goToIntro()



        setUpUi()
    }

    private fun goToIntro() {
        var it = Intent(this, IntroductionActivity::class.java)
        startActivity(it)
        finish()
    }

    private fun setUpUi() {
        if (fragmentManager.fragments.size < 4) {
            Log.i("testing test", "HERE")
            fragmentManager.beginTransaction().apply {
                add(R.id.container, homeFragment, "home")
                add(R.id.container, programsFragment, "programs").hide(programsFragment)
                add(R.id.container, practiceFragment, "practice").hide(practiceFragment)
                add(R.id.container, profileFragment, "profile").hide(profileFragment)
            }.commit()
        } else {
            Log.i("testing test", "HERE2")
            fragmentManager.beginTransaction().apply {
                /*remove(profileFragment)*/
                replace(R.id.container, profileFragment, "profile")
            }.commit()
            fragmentManager.beginTransaction().apply {
                add(R.id.container, homeFragment, "home").hide(homeFragment)
                add(R.id.container, programsFragment, "programs").hide(programsFragment)
                add(R.id.container, practiceFragment, "practice").hide(practiceFragment)
            }.commit()
            fragmentManager.beginTransaction().hide(activeFragment).commit()
            fragmentManager.beginTransaction().show(profileFragment).commit()
            activeFragment = profileFragment
        }

        binding.bottomNavView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    fragmentManager.beginTransaction().hide(activeFragment).commit()
                    fragmentManager.beginTransaction().show(homeFragment).commit()
                    activeFragment = homeFragment
                    true
                }
                R.id.programs -> {
                    fragmentManager.beginTransaction().hide(activeFragment).commit()
                    fragmentManager.beginTransaction().show(programsFragment).commit()
                    activeFragment = programsFragment
                    true
                }
                R.id.practice -> {
                    fragmentManager.beginTransaction().hide(activeFragment).commit()
                    fragmentManager.beginTransaction().show(practiceFragment).commit()
                    activeFragment = practiceFragment
                    true
                }
                R.id.profile -> {
                    fragmentManager.beginTransaction().hide(activeFragment).commit()
                    fragmentManager.beginTransaction().show(profileFragment).commit()
                    activeFragment = profileFragment
                    true
                }
                else -> false
            }
        }
    }

     fun goToWorkout() {
         var workoutRoutine = ArrayList<Map<String,String>>()
         workoutRoutine.add(mapOf("name" to "treepose", "duration" to "5"))
         workoutRoutine.add(mapOf("name" to "break", "duration" to "5"))
         workoutRoutine.add(mapOf("name" to "pushup", "duration" to "5"))

         var it = Intent(this, Workout::class.java)
         it.putExtra("workoutRoutine", workoutRoutine)
         startActivity(it)
     }
}