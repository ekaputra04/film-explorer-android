package com.example.film_explorer

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Database(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "movies.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Buat tabel dan aturan-aturan lainnya saat database pertama kali dibuat
        // Misalnya:
        // db?.execSQL("CREATE TABLE IF NOT EXISTS nama_tabel (...);")

        val sql = "CREATE TABLE IF NOT EXISTS movies (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "year TEXT NOT NULL," +
                "genre TEXT NOT NULL," +
                "director TEXT NOT NULL," +
                "writer TEXT NOT NULL," +
                "actors TEXT NOT NULL," +
                "plot TEXT NOT NULL," +
                "poster TEXT NOT NULL);"
        Log.d("data", "OnCreate: $sql")
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Di sini Anda dapat menulis kode untuk mengupgrade struktur database saat versi database berubah
        // Misalnya:
        // db?.execSQL("DROP TABLE IF EXISTS nama_tabel;")
        // onCreate(db)
    }
}
