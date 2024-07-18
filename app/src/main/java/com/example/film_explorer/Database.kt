package com.example.film_explorer

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class Database(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "films.db"
        private const val DATABASE_VERSION = 8
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

        val sqlCreateFavoriteTable = """
            CREATE TABLE IF NOT EXISTS favorites (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                film_id INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (film_id) REFERENCES films(id)
            );
        """.trimIndent()

        try {
            Log.d(TAG, "Creating database...")
            db?.execSQL(sqlCreateFilmTable)
            Log.d(TAG, "Creating films table...")
            db?.execSQL(sqlCreateUserTable)
            Log.d(TAG, "Creating users table...")
            db?.execSQL(sqlCreateFavoriteTable)
            Log.d(TAG, "Creating favorites table...")

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
            db?.execSQL("DROP TABLE IF EXISTS favorites")
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

    // Fungsi untuk menambah favorit
    fun addFavorite(userId: Int, filmId: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("film_id", filmId)
        }
        db.insert("favorites", null, values)
    }

    // Fungsi untuk menghapus favorit
    fun removeFavorite(userId: Int, filmId: Int) {
        val db = this.writableDatabase
        db.delete(
            "favorites",
            "user_id = ? AND film_id = ?",
            arrayOf(userId.toString(), filmId.toString())
        )
    }

    // Di dalam kelas Database
    fun isFavorite(userId: Int, filmId: Int): Boolean {
        val dbRead = this.readableDatabase
        val cursor = dbRead.rawQuery(
            "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND film_id = ?",
            arrayOf(userId.toString(), filmId.toString())
        )
        var exists = false
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                exists = cursor.getInt(0) > 0
            }
            cursor.close()
        }
        return exists
    }

    fun deleteFilm(filmId: Int) {
        val db = writableDatabase
        db.delete("films", "id = ?", arrayOf(filmId.toString()))
    }

    // Fungsi untuk mendapatkan film favorit pengguna
    fun getAllFilms(): List<Movie> {
        val dbRead: SQLiteDatabase = this.readableDatabase
        val cursor = dbRead.rawQuery("SELECT * FROM films ORDER BY UPDATED_AT DESC", null)
        val movies = mutableListOf<Movie>()
        try {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                    val year = cursor.getString(cursor.getColumnIndexOrThrow("year"))
                    val rating = cursor.getString(cursor.getColumnIndexOrThrow("rating"))
                    val duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"))
                    val release_date =
                        cursor.getString(cursor.getColumnIndexOrThrow("release_date"))
                    val language = cursor.getString(cursor.getColumnIndexOrThrow("language"))
                    val genre = cursor.getString(cursor.getColumnIndexOrThrow("genre"))
                    val director = cursor.getString(cursor.getColumnIndexOrThrow("director"))
                    val writer = cursor.getString(cursor.getColumnIndexOrThrow("writer"))
                    val actor = cursor.getString(cursor.getColumnIndexOrThrow("actor"))
                    val plot = cursor.getString(cursor.getColumnIndexOrThrow("plot"))
                    val poster = cursor.getString(cursor.getColumnIndexOrThrow("poster"))
                    movies.add(
                        Movie(
                            id,
                            title,
                            year,
                            rating,
                            duration,
                            release_date,
                            language,
                            genre,
                            director,
                            writer,
                            actor,
                            plot,
                            poster
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting favorite films from database", e)
        } finally {
            cursor.close()
            dbRead.close()
        }
        return movies
    }

    // Fungsi untuk mendapatkan film favorit pengguna
    fun getFavoriteFilms(userId: Int): List<Movie> {
        val dbRead: SQLiteDatabase = this.readableDatabase
        val cursor = dbRead.rawQuery(
            "SELECT films.* FROM films INNER JOIN favorites ON films.id = favorites.film_id WHERE favorites.user_id = ? ORDER BY films.UPDATED_AT DESC",
            arrayOf(userId.toString())
        )
        val movies = mutableListOf<Movie>()
        try {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                    val year = cursor.getString(cursor.getColumnIndexOrThrow("year"))
                    val rating = cursor.getString(cursor.getColumnIndexOrThrow("rating"))
                    val duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"))
                    val release_date =
                        cursor.getString(cursor.getColumnIndexOrThrow("release_date"))
                    val language = cursor.getString(cursor.getColumnIndexOrThrow("language"))
                    val genre = cursor.getString(cursor.getColumnIndexOrThrow("genre"))
                    val director = cursor.getString(cursor.getColumnIndexOrThrow("director"))
                    val writer = cursor.getString(cursor.getColumnIndexOrThrow("writer"))
                    val actor = cursor.getString(cursor.getColumnIndexOrThrow("actor"))
                    val plot = cursor.getString(cursor.getColumnIndexOrThrow("plot"))
                    val poster = cursor.getString(cursor.getColumnIndexOrThrow("poster"))
                    movies.add(
                        Movie(
                            id,
                            title,
                            year,
                            rating,
                            duration,
                            release_date,
                            language,
                            genre,
                            director,
                            writer,
                            actor,
                            plot,
                            poster
                        )
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting favorite films from database", e)
        } finally {
            cursor.close()
            dbRead.close()
        }
        return movies
    }
}
