package com.example.gallery

import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val cameraRequestCode = 100

    private lateinit var imageView: ImageView
    private lateinit var imageUri: Uri

    // creating camera activity to run camera from app
    private val cameraActivity = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        success ->
            if (success)
                imageView.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        val buttonCapture = findViewById<Button>(R.id.btnNewPhoto)
        requestCameraPermission()

        buttonCapture.setOnClickListener {
            imageUri = createImageUri()

            cameraActivity.launch(imageUri)
        }

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
                cameraRequestCode
            )
        }
    }

    private fun createImageUri(): Uri {
        /*
            return new created identifier of unique named file

            creating it in default picture folder of app
         */
        val fileName = getImageFilename()
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

        val realPath = file.absolutePath
        Log.d("CameraDebug", "Real Path: $realPath")


        return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
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

}