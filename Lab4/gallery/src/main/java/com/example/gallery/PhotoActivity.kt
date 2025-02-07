package com.example.gallery

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.util.Date
import java.util.Locale

class PhotoActivity : AppCompatActivity() {

    private val photoDb by lazy { PhotoDb(this) }
    private lateinit var imageUri: Uri
    private lateinit var takenImage: ImageView

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            success ->
        if (success) {
            takenImage.setImageURI(imageUri)
            Log.d("CameraDebug", "Written file name: $imageUri.lastPathSegment!!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_photo)

        takenImage = findViewById<ImageView>(R.id.takenPhoto)
        val titleText = findViewById<EditText>(R.id.titleEditText)
        val descriptionText = findViewById<EditText>(R.id.descEditText)
        val tagsText = findViewById<EditText>(R.id.tagsEditText)
        val btnSavePhoto = findViewById<Button>(R.id.btnSavePhoto)
        val btnBackToGallery = findViewById<Button>(R.id.btnBackToGallery)

        btnSavePhoto.setOnClickListener {
            val title = titleText.text.toString()
            val desc = descriptionText.text.toString()
            val tags = tagsText.text.toString()

            val imageObj = photoDb.addPhotoData(imageUri.lastPathSegment!!, title, desc, tags)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnBackToGallery.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val filename = getImageFilename()
        imageUri = createImageUri(filename)

        cameraActivity.launch(imageUri)
    }

    private fun getImageFilename(): String {
        /*
            getting unique filename by using timestamp to avoid
            file collision and overwrite
         */
        val sdf = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault())
        val curDate = sdf.format(Date())

        return "IMG_$curDate.jpg"
    }

    private fun createImageUri(filename: String): Uri {
        /*
            return new created identifier of unique named file

            creating it in default picture folder of app
         */
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)

        return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
    }
}

