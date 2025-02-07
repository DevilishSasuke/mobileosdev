package com.example.gallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gallery.models.PhotoData

class EditActivity : AppCompatActivity() {

    private val photoDb by lazy { PhotoDb(this) }
    private lateinit var imageUri: Uri
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_photo)

        val photoToEdit = intent.getSerializableExtra("photoToEdit") as? PhotoData
            ?: throw IllegalArgumentException("No photo data provided")


        imageView = findViewById(R.id.takenPhoto)
        val titleText = findViewById<EditText>(R.id.titleEditText)
        val descriptionText = findViewById<EditText>(R.id.descEditText)
        val tagsText = findViewById<EditText>(R.id.tagsEditText)
        val btnSavePhoto = findViewById<Button>(R.id.btnSavePhoto)
        val btnBackToGallery = findViewById<Button>(R.id.btnBackToGallery)

        imageView.setImageURI(MainActivity.createImageUri(this, photoToEdit.filename))
        titleText.setText(photoToEdit.title)
        descriptionText.setText(photoToEdit.description)
        tagsText.setText(photoToEdit.tags)

        btnSavePhoto.setOnClickListener {
            val title = titleText.text.toString()
            val desc = descriptionText.text.toString()
            val tags = tagsText.text.toString()

            val editedPhoto = photoDb.updatePhotoData(photoToEdit.id, photoToEdit.filename, title, desc, tags)

            val intent = Intent()
            intent.putExtra("editedPhoto", editedPhoto)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        btnBackToGallery.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

    }
}