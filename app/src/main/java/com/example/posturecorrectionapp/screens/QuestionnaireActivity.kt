package com.example.posturecorrectionapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.posturecorrectionapp.R

class QuestionnaireActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questionnaire)
    }

    fun submitClicked(view: View) {
        var name = findViewById<EditText>(R.id.name).text.toString()
        var age = findViewById<EditText>(R.id.age).text.toString()
        var weight = findViewById<EditText>(R.id.weight).text.toString()
        var height = findViewById<EditText>(R.id.height).text.toString()

        if (name == "" || age == "" || weight == "" || height == "") {
            Toast.makeText(
                this,
                "Please fill in all fields!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val sharedPreference =  getSharedPreferences("userPreferences", MODE_PRIVATE)
            var editor = sharedPreference.edit()
            editor.putString("name", name)
            editor.putString("age", age)
            editor.putString("weight", weight)
            editor.putString("height", height)
            editor.commit()
            startActivity(Intent(this, NavigationActivity::class.java))
        }
    }
}