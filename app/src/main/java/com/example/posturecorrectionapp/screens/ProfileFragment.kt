package com.example.posturecorrectionapp.screens

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.example.posturecorrectionapp.R
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class ProfileFragment : Fragment(R.layout.fragment_profile) {
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    loadUserData()

    view.findViewById<ImageView>(R.id.settings).setOnClickListener {
        startActivity(Intent(activity, SettingsActivity::class.java))
    }

    view.findViewById<Button>(R.id.changeProfile).setOnClickListener {
        startActivityForResult(Intent(activity, EditProfileActivity::class.java), 1)
    }

    loadStatistics()

    val lineGraphView = view.findViewById<GraphView>(R.id.idGraphView)

    // on below line we are adding data to our graph view.
    val series: LineGraphSeries<DataPoint> = LineGraphSeries(
        arrayOf(
            DataPoint(0.0, 1.0),
            DataPoint(1.0, 3.0),
            DataPoint(2.0, 4.0),
            DataPoint(3.0, 9.0),
            DataPoint(4.0, 6.0),
            DataPoint(5.0, 3.0),
            DataPoint(6.0, 6.0),
            DataPoint(7.0, 1.0),
            DataPoint(8.0, 2.0)
        )
    )

    // on below line adding animation
    lineGraphView.animate()

    // on below line we are setting scrollable
    // for point graph view
    lineGraphView.viewport.isScrollable = true

    // on below line we are setting scalable.
    lineGraphView.viewport.isScalable = true

    // on below line we are setting scalable y
    lineGraphView.viewport.setScalableY(true)

    // on below line we are setting scrollable y
    lineGraphView.viewport.setScrollableY(true)

    // on below line we are setting color for series.
    series.color = R.color.purple

    // on below line we are adding
    // data series to our graph view.
    lineGraphView.addSeries(series)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                loadUserData()
            }
        }
    }

    private fun loadUserData() {
        val sharedPreference =  activity?.getSharedPreferences("userPreferences",
            AppCompatActivity.MODE_PRIVATE
        )
        view?.findViewById<TextView>(R.id.userName)?.text = sharedPreference?.getString("name", "")
        view?.findViewById<TextView>(R.id.userAge)?.text = sharedPreference?.getString("age", "")
        view?.findViewById<TextView>(R.id.userWeight)?.text = sharedPreference?.getString("weight", "")
        view?.findViewById<TextView>(R.id.userHeight)?.text = sharedPreference?.getString("height", "")

        val image = sharedPreference?.getString("image", "")
        if (image != "") {
            val b: ByteArray = Base64.decode(image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
            view?.findViewById<ImageView>(R.id.profile_image)?.setImageBitmap(bitmap)
        }
    }

    fun loadStatistics() {
        val sharedPreference = activity?.getSharedPreferences("userStatistics", AppCompatActivity.MODE_PRIVATE)
        view?.findViewById<TextView>(R.id.totalExercises)?.text = "Total Exercises Completed: ${sharedPreference?.getInt("totalExercises", 0).toString()}"
        view?.findViewById<TextView>(R.id.totalDuration)?.text = "Total Duration Spent: ${sharedPreference?.getInt("totalDuration", 0).toString()}"
    }
}


