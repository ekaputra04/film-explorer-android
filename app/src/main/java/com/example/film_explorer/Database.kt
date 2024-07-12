package com.example.film_explorer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Database(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "films.db"
        private const val DATABASE_VERSION = 3
        private const val TAG = "DatabaseError"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "Creating database...")

        val sqlCreateFilmTable = """
            CREATE TABLE IF NOT EXISTS films (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                year TEXT NOT NULL,
                rating TEXT NOT NULL,
                duration TEXT NOT NULL,
                release_date TEXT NOT NULL,
                language TEXT NOT NULL,
                genre TEXT NOT NULL,
                director TEXT NOT NULL,
                writer TEXT NOT NULL,
                actor TEXT NOT NULL,
                plot TEXT NOT NULL,
                poster TEXT NOT NULL,
                view_count INTEGER NOT NULL DEFAULT 0,
                CREATED_AT TEXT NOT NULL,
                UPDATED_AT TEXT NOT NULL
            );
        """.trimIndent()

        val sqlCreateUserTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                telephone TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,
                is_admin BOOLEAN NOT NULL DEFAULT FALSE,
                CREATED_AT TEXT NOT NULL,
                UPDATED_AT TEXT NOT NULL
            );
        """.trimIndent()

        try {
            Log.d(TAG, "Creating database...")
            db?.execSQL(sqlCreateFilmTable)
            Log.d(TAG, "Creating films table...")
            db?.execSQL(sqlCreateUserTable)
            Log.d(TAG, "Creating users table...")


            Log.d(TAG, "Tables created successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating tables", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "Upgrading database from version $oldVersion to $newVersion...")
        try {
            db?.execSQL("DROP TABLE IF EXISTS films")
            db?.execSQL("DROP TABLE IF EXISTS users")
            onCreate(db)
        } catch (e: Exception) {
            Log.e(TAG, "Error upgrading database", e)
        }
    }

    fun updateViewCount(movieId: Int) {
        try {
            val contentValues = ContentValues().apply {
                put("view_count", getUpdatedViewCount(movieId))
            }
            val whereClause = "id = ?"
            val whereArgs = arrayOf(movieId.toString())
            writableDatabase.update("films", contentValues, whereClause, whereArgs)
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error updating view count for movie ID: $movieId", e)
        }
    }

    private fun getUpdatedViewCount(movieId: Int): Int {
        return try {
            val currentViewCount = getCurrentViewCount(movieId)
            currentViewCount + 1
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error getting current view count for movie ID: $movieId", e)
            0 // return default value or handle error as needed
        }
    }

    @SuppressLint("Range")
    private fun getCurrentViewCount(movieId: Int): Int {
        try {
            val query = "SELECT view_count FROM films WHERE id = ?"
            val whereArgs = arrayOf(movieId.toString())
            val cursor = readableDatabase.rawQuery(query, whereArgs)
            var viewCount = 0
            cursor.use {
                if (it.moveToFirst()) {
                    viewCount = it.getInt(it.getColumnIndex("view_count"))
                }
            }
            return viewCount
        } catch (e: SQLiteException) {
            Log.e(TAG, "Error getting current view count for movie ID: $movieId", e)
            return 0 // return default value or handle error as needed
        }
    }
}
