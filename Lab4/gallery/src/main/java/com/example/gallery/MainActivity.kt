package com.example.gallery

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract.Contacts.Photo
import android.provider.MediaStore
import android.util.Log
import android.util.Xml
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.Locale
import com.example.gallery.models.PhotoData
import org.xmlpull.v1.XmlPullParser

class MainActivity : AppCompatActivity() {

    private val photoXmlPath = "photo_data.xml"
    private var photosData: MutableList<PhotoData> = mutableListOf<PhotoData>()

    private lateinit var imageView: ImageView
    private lateinit var imageUri: Uri
    // creating camera activity to run camera from app
    private val cameraActivity = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        success ->
            if (success) {
                imageView.setImageURI(imageUri)
                Log.d("CameraDebug", imageUri.path!!)
                //saveImageToFile()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val buttonCapture = findViewById<Button>(R.id.btnNewPhoto)
        requestCameraPermission()
        photosData = getCurrentPhotoData()

        val idText = findViewById<TextView>(R.id.idText)
        val filepathText = findViewById<TextView>(R.id.filepathText)
        val titleText = findViewById<TextView>(R.id.titleText)
        val descriptionText = findViewById<TextView>(R.id.descriptionText)
        val tagsText = findViewById<TextView>(R.id.tagsText)

        buttonCapture.setOnClickListener {
            val photoData = photosData.first()

            filepathText.text = photoData.filename
            titleText.text = photoData.title
            descriptionText.text = photoData.description

            var str = ""
            for (tag in photoData.tags)
                str += "#$tag "
            tagsText.text = str

            imageUri = createImageUri(photoData.filename)
            imageView.setImageURI(imageUri)

            /*
            val filename = getImageFilename()
            imageUri = createImageUri(filename)

            cameraActivity.launch(imageUri)

             */

            /*
            val imgName = "IMG_06_02_2025_20_42_14.jpg"
            // IMG_06_02_2025_20_39_48.jpg
            imageUri = createImageUri(imgName)
            imageView.setImageURI(imageUri)

             */
        }

    }

    private fun getCurrentPhotoData(): MutableList<PhotoData> {
        val parser = resources.getXml(R.xml.photo_data)

        val resultData = mutableListOf<PhotoData>()
        var eventType = parser.eventType // event type of current string in xml
        var id = 0
        var filepath = ""
        var title = ""
        var description = ""
        var tags: MutableList<String> = mutableListOf<String>()

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType){
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "photo" -> id = parser.getAttributeValue(null, "id").toInt()
                        "filepath" -> filepath = parser.nextText()
                        "title" -> title = parser.nextText()
                        "description" -> description = parser.nextText()
                        "tag" -> tags.add(parser.nextText())
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "photo") {
                        val photoData = PhotoData(id, filepath, title, description, tags)
                        resultData.add(photoData)
                        id = 0
                        filepath = ""
                        title = ""
                        description = ""
                        tags = mutableListOf<String>()
                    }
                }
            }

            eventType = parser.next()
        }

        return resultData
    }

    private fun saveImageToFile() {
        val outputFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            imageUri.lastPathSegment!!)

        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
        val fos = FileOutputStream(outputFile.path)

        fos.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        }

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

    private fun requestCameraPermission() {
        /*
            getting camera permission every time app starts
         */

        // if permission is not granted
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // request it from user
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                100
            )
        }
    }

}