package com.example.gallery

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PhotoDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val queryCreation = """
            CREATE TABLE $table_name (
                $id INTEGER PRIMARY KEY AUTOINCREMENT,
                $filepath TEXT,
                $title TEXT,
                $description TEXT,
                $tags TEXT
            )
        """.trimIndent()
        db.execSQL(queryCreation)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $table_name")
        onCreate(db)
    }


    companion object {
        private const val DB_NAME = "photo_storage.db"

        const val table_name = "photos"
        const val id = "id"
        const val filepath = "filepath"
        const val title = "title"
        const val description = "description"
        const val tags = "tags"

    }
}