package com.example.gallery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.gallery.models.PhotoData

class EditActivity : AppCompatActivity() {
    /*
        class for photo edit by clicking on image in gallery

        user can change title, description, tags in this activity
        and then save it
        it will rewrite db record with picked photo data
     */

    private val photoDb by lazy { PhotoDb(this) } // db connection
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_photo)
        // the same layout as on a photo creation

        // get transferred photo
        val photoToEdit = intent.getSerializableExtra("photoToEdit") as? PhotoData
            ?: throw IllegalArgumentException("No photo data provided")

        // get all ui elements
        imageView = findViewById(R.id.takenPhoto)
        val titleText = findViewById<EditText>(R.id.titleEditText)
        val descriptionText = findViewById<EditText>(R.id.descEditText)
        val tagsText = findViewById<EditText>(R.id.tagsEditText)
        val btnSavePhoto = findViewById<Button>(R.id.btnSavePhoto)
        val btnBackToGallery = findViewById<Button>(R.id.btnBackToGallery)

        // display ui elements
        imageView.setImageURI(MainActivity.createImageUri(this, photoToEdit.filename))
        titleText.setText(photoToEdit.title)
        descriptionText.setText(photoToEdit.description)
        tagsText.setText(photoToEdit.tags)

        btnSavePhoto.setOnClickListener {
            // save info only by clicking on button

            // get data that was changed by user
            val title = titleText.text.toString()
            val desc = descriptionText.text.toString()
            val tags = tagsText.text.toString()

            // update db record
            val editedPhoto = photoDb.updatePhotoData(photoToEdit.id, photoToEdit.filename, title, desc, tags)

            // optimized way of hand over control back to calling activity
            val intent = Intent()
            intent.putExtra("editedPhoto", editedPhoto)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        // return button if user wants so
        btnBackToGallery.setOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

    }
}