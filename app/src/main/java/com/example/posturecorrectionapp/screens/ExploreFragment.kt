package com.example.posturecorrectionapp.screens

import androidx.fragment.app.Fragment
import com.example.posturecorrectionapp.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ExploreFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the string array from the resources
        val stringArray = resources.getStringArray(R.array.hiit_names)
        val barreArray = resources.getStringArray(R.array.barre_names)
        Log.d("item1", stringArray[0])
        Log.d("size", stringArray.size.toString())

        var count = 0
        val maxLength = 24

        for (element in stringArray) {
            count += 1

            // access the title of each workout textview by id
            val hiitName = view.findViewById<TextView>(
                resources.getIdentifier(
                    "hiit$count",
                    "id",
                    requireContext().packageName
                )
            )

            // assign string resource to title
            hiitName.text = element

            // truncate and add the ... if the string is too long
            hiitName.text = if (element.length > maxLength) {
                element.substring(0, maxLength) + "..."
            } else {
                element
            }
        }

        // barre
//        val barreName = view.findViewById<TextView>(R.id.barre1)
//        barreName.text = barreArray[0]

        /*
        for (element in barreArray) {
            count += 1
            val barreName = view.findViewById<TextView>(
                resources.getIdentifier(
                    "barre$count",
                    "id",
                    requireContext().packageName))

//            Log.d("element", element)
//            Log.d("barre array", barreArray[0])

            barreName.text = element
            barreName.text = if (element.length > maxLength) {
                element.substring(0, maxLength) + "..."
            } else {
                element
            }
        }
         */

    }
}
