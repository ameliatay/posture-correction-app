package com.example.posturecorrectionapp.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatDelegate
import com.example.posturecorrectionapp.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val sharedPreference =  getSharedPreferences("userPreferences", MODE_PRIVATE)
        val notif = sharedPreference.getString("notification", "false")
        val darkMode = sharedPreference.getBoolean("dark mode", false)

        val notificationSwitch: Switch = findViewById(R.id.notificationSwitch)
        notificationSwitch.isChecked = notif != "false"
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                var editor = sharedPreference.edit()
                editor.putString("notification", "true")
                editor.commit()
            } else {
                var editor = sharedPreference.edit()
                editor.putString("notification", "false")
                editor.commit()
            }
            Toast.makeText(this, if(isChecked) "Notifications ON" else "Notifications OFF", Toast.LENGTH_SHORT).show()
        }

        val darkModeSwitch: Switch = findViewById(R.id.darkModeSwitch)
        darkModeSwitch.isChecked = darkMode != false
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            var editor = sharedPreference.edit()
            if(isChecked) {
                editor.putBoolean("dark mode", true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                editor.putBoolean("dark mode", false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            editor.commit()
            Toast.makeText(this, if(isChecked) "Dark Mode ON" else "Dark Mode OFF", Toast.LENGTH_SHORT).show()
            this.recreate()
        }
    }

    fun backButtonClicked(view: View) {
        finish()
    }
}