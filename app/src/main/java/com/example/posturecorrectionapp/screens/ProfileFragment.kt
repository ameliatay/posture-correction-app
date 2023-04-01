package com.example.posturecorrectionapp.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.posturecorrectionapp.R
import com.example.posturecorrectionapp.adapters.ProfileAdapter
import com.example.posturecorrectionapp.data.Datasource
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView
//import com.google.android.gms.cast.framework.media.ImagePicker

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var imageView: ImageView
    private lateinit var profileImage: CircleImageView
    private lateinit var changeProfile: FloatingActionButton

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data
            profileImage.setImageURI(imageUri)
        }else if (result.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
        } else {
            //Task Cancelled
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadProfile = Datasource().loadProfile()
        val profileAdapter = ProfileAdapter(this, loadProfile)
        val loadProfileView = getView()?.findViewById<RecyclerView>(R.id.profileRecyclerView)
        loadProfileView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        loadProfileView?.adapter = profileAdapter


//        imageView = view.findViewById(R.id.imageView)
        profileImage = view.findViewById(R.id.profile_image)
        changeProfile = view.findViewById(R.id.changeProfile)

        changeProfile.setOnClickListener {
            ImagePicker.Companion.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
    }


}


