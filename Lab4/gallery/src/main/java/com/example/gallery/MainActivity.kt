package com.example.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.util.Date
import java.util.Locale
import com.example.gallery.models.PhotoData
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private val photoDb by lazy { PhotoDb(this) }
    private var photosData: MutableList<PhotoData> = mutableListOf()
    private var fullData: MutableList<PhotoData> = mutableListOf()
    private var lastEditedPos: Int = -1

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PhotoAdapter

    private val photoActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val takenPhoto = result.data?.getSerializableExtra("takenPhoto")
                takenPhoto?.let { photosData.add(0, takenPhoto as PhotoData) }
                fullDataUpdate()
                adapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
        }
    }

    private val editPhotoActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val editedPhoto = result.data?.getSerializableExtra("editedPhoto")
                editedPhoto?.let { photosData[lastEditedPos] = editedPhoto as PhotoData }
                fullDataUpdate()
                adapter.notifyItemChanged(lastEditedPos)
            }
            else {
                lastEditedPos = -1
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNewPhoto = findViewById<Button>(R.id.btnNewPhoto)
        val searchText = findViewById<EditText>(R.id.searchText)
        val btnSearchBy = findViewById<Button>(R.id.btnSearch)
        requestCameraPermission()
        fullDataUpdate()
        photosData.addAll(fullData)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        adapter = PhotoAdapter(this, photosData)
            { photoData -> editPhotoData(photoData, this)}
        recyclerView.adapter = adapter

        btnSearchBy.setOnClickListener {
            val filterText = searchText.text.toString().trim()

            if (filterText.isBlank() || filterText.isEmpty()) {
                Log.d("CameraDebug", "filterText is empty")
                if (photosData.size != fullData.size) {
                    photosData.clear()
                    photosData.addAll(fullData)
                    adapter.notifyDataSetChanged()
                    Log.d("CameraDebug", photosData.toString())
                }
            }
            else {
                Log.d("CameraDebug", "filterText is $filterText")
                val filteredData = filterData(filterText)
                photosData.clear()
                photosData.addAll(filteredData)
                adapter.notifyDataSetChanged()
                Log.d("CameraDebug", photosData.toString())

            }
        }

        btnNewPhoto.setOnClickListener {
            val intent = Intent(this, PhotoActivity::class.java)
            photoActivity.launch(intent)
        }

    }

    private fun editPhotoData(photoData: PhotoData, context: Context) {
        lastEditedPos = photosData.indexOf(photoData)

        val intent = Intent(context, EditActivity::class.java)
        intent.putExtra("photoToEdit", photoData)
        editPhotoActivity.launch(intent)
    }

    private fun filterData(filterText: String): MutableList<PhotoData> {
        val filteredData: MutableList<PhotoData> = mutableListOf()
        for (photoData in fullData)
            if (filterText in photoData.title ||
                filterText in photoData.description ||
                filterText in photoData.tags)
                filteredData.add(photoData)

        return filteredData
    }

    private fun fullDataUpdate() {
        val dbData = photoDb.getAllPhotoData()
        dbData.reverse()

        fullData.clear()
        fullData.addAll(dbData)
    }

    companion object {
        public fun getImageFilename(): String {
            /*
                getting unique filename by using timestamp to avoid
                file collision and overwrite
             */
            val sdf = SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault())
            val curDate = sdf.format(Date())

            return "IMG_$curDate.jpg"
        }

        public fun createImageUri(context: Context, filename: String): Uri {
            /*
                return new created identifier of unique named file

                creating it in default picture folder of app
             */
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)

            return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }

        public fun addHashes(tags: String): String {
            val splited_tags = tags.split(" ")
            var result = ""

            for (tag in splited_tags)
                if (tag.isNotBlank() and tag.isNotEmpty())
                    result += "#$tag "

            return result.trim()
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
                100
            )
        }
    }
}