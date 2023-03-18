package com.example.posturecorrectionapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.posturecorrectionapp.R

class QuestionnaireActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)
    }

    fun goToHome(view: View) {
        val sharedPreference =  getSharedPreferences("userPreferences", MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("username","testing")
        editor.commit()
        startActivity(Intent(this, NavigationActivity::class.java))
    }
}