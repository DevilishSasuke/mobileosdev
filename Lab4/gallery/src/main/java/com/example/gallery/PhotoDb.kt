package com.example.gallery

import android.content.ContentValues
import android.content.Context

import com.example.gallery.models.PhotoData

class PhotoDb(context: Context) {
    /*
        allows app to communicate with database (sqlite)
     */
    private val dbHelper = PhotoDbHelper(context) // get access to db

    fun addPhotoData(filepath: String, title: String = "",
                     description: String = "", tags: String = ""): PhotoData {
        // need to write new record
        val db = dbHelper.writableDatabase

        // inserting data
        val toInsert = ContentValues().apply() {
            put(PhotoDbHelper.filepath, filepath)
            put(PhotoDbHelper.title, title)
            put(PhotoDbHelper.description, description)
            put(PhotoDbHelper.tags, tags)
        }

        // insert returns id of created object
        val photoId = db.insert(PhotoDbHelper.table_name, null, toInsert)

        // return structured object
        return PhotoData(photoId.toInt(), filepath, title, description, tags)
    }

    fun getAllPhotoData(): MutableList<PhotoData> {
        // no need to write anything
        val db = dbHelper.readableDatabase
        val allPhotoData = mutableListOf<PhotoData>()
        // create query
        // have no any conditions
        val cursor = db.query(PhotoDbHelper.table_name,
            null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                // immediately create object
                allPhotoData.add(
                    PhotoData(
                        getInt(getColumnIndexOrThrow(PhotoDbHelper.id)),
                        getString(getColumnIndexOrThrow(PhotoDbHelper.filepath)),
                        getString(getColumnIndexOrThrow(PhotoDbHelper.title)),
                        getString(getColumnIndexOrThrow(PhotoDbHelper.description)),
                        getString(getColumnIndexOrThrow(PhotoDbHelper.tags))
                    )
                )
            }
        }
        cursor.close()

        return allPhotoData
    }

    /*
        update existing photo in db

        returns edited PhotoData object
     */
    fun updatePhotoData(photoId: Int, filename: String, title: String = "",
                        description: String = "", tags: String = ""): PhotoData {
        // need to rewrite record
        val db = dbHelper.writableDatabase

        // new values
        val values = ContentValues().apply {
            put(PhotoDbHelper.title, title)
            put(PhotoDbHelper.description, description)
            put(PhotoDbHelper.tags, tags)
        }

        // update only on this photo id (unique, primary key)
        db.update(
            PhotoDbHelper.table_name,
            values,
            "${PhotoDbHelper.id} = ?",
            arrayOf(photoId.toString())
        )

        // returns edited object
        return PhotoData(photoId.toInt(), filename, title, description, tags)
    }

}