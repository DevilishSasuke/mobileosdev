package com.example.gallery

import android.content.ContentValues
import android.content.Context

import com.example.gallery.models.PhotoData

class PhotoDb(context: Context) {
    private val dbHelper = PhotoDbHelper(context)

    fun addPhotoData(filepath: String, title: String = "",
                     description: String = "", tags: String = ""): PhotoData {
        val db = dbHelper.writableDatabase

        val toInsert = ContentValues().apply() {
            put(PhotoDbHelper.filepath, filepath)
            put(PhotoDbHelper.title, title)
            put(PhotoDbHelper.description, description)
            put(PhotoDbHelper.tags, tags)
        }

        val photoId = db.insert(PhotoDbHelper.table_name, null, toInsert)

        return PhotoData(photoId.toInt(), filepath, title, description, tags)
    }

    fun getAllPhotoData(): MutableList<PhotoData> {
        val db = dbHelper.readableDatabase
        val allPhotoData = mutableListOf<PhotoData>()
        val cursor = db.query(PhotoDbHelper.table_name,
            null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
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
}