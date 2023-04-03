package com.example.posturecorrectionapp.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.posturecorrectionapp.R
import com.github.dhaval2404.imagepicker.ImagePicker
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream


class EditProfileActivity : AppCompatActivity() {
    private lateinit var profileImage: CircleImageView

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.data
            profileImage.setImageURI(imageUri)
        }else if (result.resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(result.data), Toast.LENGTH_SHORT).show()
        } else {
            //Task Cancelled
            Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        profileImage = findViewById(R.id.profile_image)

        profileImage.setOnClickListener {
            ImagePicker.Companion.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        /*val sharedPreference = getSharedPreferences("userPreferences", MODE_PRIVATE)
        findViewById<EditText>(R.id.editName).setText(sharedPreference.getString("name", ""), TextView.BufferType.EDITABLE)
        findViewById<EditText>(R.id.editAge).setText(sharedPreference.getString("age", ""), TextView.BufferType.EDITABLE)
        findViewById<EditText>(R.id.editWeight).setText(sharedPreference.getString("weight", ""), TextView.BufferType.EDITABLE)
        findViewById<EditText>(R.id.editHeight).setText(sharedPreference.getString("height", ""), TextView.BufferType.EDITABLE)*/
    }

    fun updateClicked(view: View) {
        var name = findViewById<EditText>(R.id.editName).text.toString()
        var age = findViewById<EditText>(R.id.editAge).text.toString()
        var weight = findViewById<EditText>(R.id.editWeight).text.toString()
        var height = findViewById<EditText>(R.id.editHeight).text.toString()

        val sharedPreference = getSharedPreferences("userPreferences", MODE_PRIVATE)
        var editor = sharedPreference.edit()

        if (name != "") {
            editor.putString("name", name)
        }

        if (age != "") {
            editor.putString("age", age)
        }

        if (weight != "") {
            editor.putString("weight", weight)
        }

        if (height != "") {
            editor.putString("height", height)
        }

        val photo = profileImage.drawable.toBitmap()
        val baos = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)
        editor.putString("image", encodedImage)

        editor.commit()
        setResult(RESULT_OK);
        finish()
    }

    fun backButtonClicked(view: View) {
        finish()
    }
}