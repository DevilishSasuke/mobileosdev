package com.example.gallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PhotoActivity : AppCompatActivity() {

    private val photoDb by lazy { PhotoDb(this) }
    private lateinit var imageUri: Uri
    private lateinit var takenImage: ImageView

    // stats phone camera app and wait for result
    private val cameraActivity = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        success ->
            if (success) {
                // display taken photo and then don't finish activity to get title, desc, tags
                takenImage.setImageURI(imageUri)
                Log.d("CameraDebug", "Written file name: ${imageUri.lastPathSegment!!}")
            }
            else {
                val intent = Intent()
                setResult(Activity.RESULT_CANCELED, intent)
                finish()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_photo)

        // ui elements
        takenImage = findViewById<ImageView>(R.id.takenPhoto)
        val titleText = findViewById<EditText>(R.id.titleEditText)
        val descriptionText = findViewById<EditText>(R.id.descEditText)
        val tagsText = findViewById<EditText>(R.id.tagsEditText)
        val btnSavePhoto = findViewById<Button>(R.id.btnSavePhoto)
        val btnBackToGallery = findViewById<Button>(R.id.btnBackToGallery)

        btnSavePhoto.setOnClickListener {
            // get entered values for taken photo
            val title = titleText.text.toString()
            val desc = descriptionText.text.toString()
            val tags = tagsText.text.toString()

            // add new record to db
            val takenPhoto = photoDb.addPhotoData(imageUri.lastPathSegment!!, title, desc, tags)

            // send photo object and finish activity with OK
            val intent = Intent()
            intent.putExtra("takenPhoto", takenPhoto)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        btnBackToGallery.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

        // create new unique name
        val filename = MainActivity.getImageFilename()
        imageUri = MainActivity.createImageUri(this, filename)

        // start activity with created identifier
        cameraActivity.launch(imageUri)
    }
}

