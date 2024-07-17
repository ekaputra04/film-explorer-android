package com.example.film_explorer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "films.db"
        private const val DATABASE_VERSION = 7
        private const val TAG = "Database"
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

    // Fungsi untuk mengupdate data pengguna
    fun updateUser(id: Int, username: String, email: String, password: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("password", password)
            put("updated_at", System.currentTimeMillis().toString())
        }
        return db.update("users", values, "id = ?", arrayOf(id.toString()))
    }
}
