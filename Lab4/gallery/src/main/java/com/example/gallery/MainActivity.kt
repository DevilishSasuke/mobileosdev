package com.example.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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

class MainActivity : AppCompatActivity() {
    /*
        activity with gallery display


     */

    private val photoDb by lazy { PhotoDb(this) } // db connection
    private var photosData: MutableList<PhotoData> = mutableListOf() // current displayed photos
    private var fullData: MutableList<PhotoData> = mutableListOf() // the whole photos
    private var lastEditedPos: Int = -1 // id to optimize photo editing

    private lateinit var recyclerView: RecyclerView // object to create scrollable list of photos
    private lateinit var adapter: PhotoAdapter // adapter to display all picked photos in recycler

    /*
        activity launcher that opens phone camera and wait any user action
        such as camera close or photo confirmation
     */
    private val photoActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
            // ok means that photo was successfully received
            if (result.resultCode == Activity.RESULT_OK) {
                // get receiver photo
                val takenPhoto = result.data?.getSerializableExtra("takenPhoto")
                takenPhoto?.let { photosData.add(0, takenPhoto as PhotoData) }
                // update all db records
                fullDataUpdate()
                // notify that 1 element was added at index 0
                adapter.notifyItemInserted(0)
                // scroll to this element
                // otherwise it will be shown from row 2 of recycler
                recyclerView.scrollToPosition(0)
        }
    }

    /*
        activity launcher that allows user to edit any photo he has in gallery
     */
    private val editPhotoActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
            // OK on saving changed information
            if (result.resultCode == Activity.RESULT_OK) {
                val editedPhoto = result.data?.getSerializableExtra("editedPhoto")
                // change previous object to edited
                editedPhoto?.let { photosData[lastEditedPos] = editedPhoto as PhotoData }
                fullDataUpdate()
                // notify that changed object on position
                adapter.notifyItemChanged(lastEditedPos)
            }
            // any other - was aborted, backed to menu, etc.
            else {
                lastEditedPos = -1
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get ui elements
        val btnNewPhoto = findViewById<Button>(R.id.btnNewPhoto)
        val searchText = findViewById<EditText>(R.id.searchText)
        val btnSearchBy = findViewById<Button>(R.id.btnSearch)
        // get permission to use camera
        requestCameraPermission()
        // get all data from db
        fullDataUpdate()
        // add data to displayable list
        photosData.addAll(fullData)

        recyclerView = findViewById(R.id.recyclerView)
        // creating grid with 2 columns in row
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // create photo display
        // pass the function to bind photo listener to edit activity
        adapter = PhotoAdapter(this, photosData)
            { photoData -> editPhotoData(photoData, this)}
        // bind adapter
        recyclerView.adapter = adapter

        btnSearchBy.setOnClickListener {
            // get entered filters
            val filterText = searchText.text.toString().trim()

            // empty means show all
            if (filterText.isBlank() || filterText.isEmpty()) {
                // if was filtered
                if (photosData.size != fullData.size) {
                    // display all photos
                    photosData.clear()
                    photosData.addAll(fullData)
                    // update all data in recycler
                    adapter.notifyDataSetChanged()
                }
            }
            else {
                // get data that contains entered text in title, desc or tagsd
                val filteredData = filterData(filterText)
                photosData.clear()
                photosData.addAll(filteredData)
                adapter.notifyDataSetChanged()

            }
        }

        // launch activity to get new photo
        btnNewPhoto.setOnClickListener {
            val intent = Intent(this, PhotoActivity::class.java)
            photoActivity.launch(intent)
        }

    }

    // starting edit activity on photo click with bound photo data
    private fun editPhotoData(photoData: PhotoData, context: Context) {
        // remember index if editing photo
        lastEditedPos = photosData.indexOf(photoData)

        // launch activity
        val intent = Intent(context, EditActivity::class.java)
        intent.putExtra("photoToEdit", photoData)
        editPhotoActivity.launch(intent)
    }

    // return filtered data that contains filterText in any of title, description or tags values
    private fun filterData(filterText: String): MutableList<PhotoData> {
        val filteredData: MutableList<PhotoData> = mutableListOf()
        for (photoData in fullData)
            if (filterText in photoData.title ||
                filterText in photoData.description ||
                filterText in photoData.tags)
                filteredData.add(photoData)

        return filteredData
    }

    // get all data from db
    private fun fullDataUpdate() {
        val dbData = photoDb.getAllPhotoData()
        dbData.reverse()

        fullData.clear()
        fullData.addAll(dbData)
    }

    // static functions
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
            val splitedTags = tags.split(" ")
            var result = ""

            // for any non empty tag adding hash in the beginning
            for (tag in splitedTags)
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