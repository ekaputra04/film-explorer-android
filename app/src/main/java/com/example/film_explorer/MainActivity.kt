package com.example.film_explorer

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : Activity(), View.OnClickListener {
    private lateinit var tvUsername: TextView
    private lateinit var edtSearch: EditText
    private lateinit var imgSearch: ImageView
    private lateinit var imgProfile: ImageView
    private lateinit var imgFilter: ImageView
    private lateinit var loading: ProgressDialog
    private lateinit var database: Database
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var movies: List<Movie>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        updateUIUser()

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = movieAdapter

        movies = emptyList()
        movies = database.getAllFilms()
        movieAdapter.setMovies(movies)

        edtSearch.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgProfile.setOnClickListener(this)
        imgFilter.setOnClickListener(this)
    }

    private fun updateUIUser() {
        tvUsername.text = "Hey, ${UserObject.username}"
    }

    override fun onResume() {
        super.onResume()
        movies = emptyList()
        movies = database.getAllFilms()
        movieAdapter.setMovies(movies)
        updateUIUser()
    }

    private fun initComponents() {
        tvUsername = findViewById(R.id.tv_main_hey_name)
        edtSearch = findViewById(R.id.edt_main_search)
        imgSearch = findViewById(R.id.img_main_search)
        imgProfile = findViewById(R.id.img_main_profile)
        imgFilter = findViewById(R.id.img_main_filter)
        recyclerView = findViewById(R.id.rv_main)
        database = Database(this)
        movieAdapter = MovieAdapter(this, listOf())
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_main_search) {
            val textForSearch = edtSearch.text.trim()
            if (textForSearch.isEmpty()) {
                Toast.makeText(this, "Masukkan kata kunci!", Toast.LENGTH_SHORT).show()
            } else {
                if (checkDatabaseForMovie(textForSearch.toString())) {
                    // Data found in the database
                    val intent = Intent(this, DetailActivity::class.java)
                    startActivity(intent)
                } else {
                    // Data not found, fetch from API
                    fetchMovies(textForSearch.toString())
                }
            }
        }

        if (v?.id == R.id.img_main_profile) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.img_main_filter) {
            showFilterDialog()
        }
    }

    private fun checkDatabaseForMovie(title: String): Boolean {
        val dbRead: SQLiteDatabase = database.readableDatabase
        var cursor: Cursor? = null
        return try {
            val formattedTitle = "%${title}%"
            cursor =
                dbRead.rawQuery("SELECT * FROM films WHERE title LIKE ?", arrayOf(formattedTitle))
            if (cursor.moveToFirst()) {
                // Data found in database, populate MovieObject
                MovieObject.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                MovieObject.title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                MovieObject.year = cursor.getString(cursor.getColumnIndexOrThrow("year"))
                MovieObject.rating = cursor.getString(cursor.getColumnIndexOrThrow("rating"))
                MovieObject.duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"))
                MovieObject.release_date =
                    cursor.getString(cursor.getColumnIndexOrThrow("release_date"))
                MovieObject.language = cursor.getString(cursor.getColumnIndexOrThrow("language"))
                MovieObject.genre = cursor.getString(cursor.getColumnIndexOrThrow("genre"))
                MovieObject.director = cursor.getString(cursor.getColumnIndexOrThrow("director"))
                MovieObject.writer = cursor.getString(cursor.getColumnIndexOrThrow("writer"))
                MovieObject.actor = cursor.getString(cursor.getColumnIndexOrThrow("actor"))
                MovieObject.plot = cursor.getString(cursor.getColumnIndexOrThrow("plot"))
                MovieObject.poster = cursor.getString(cursor.getColumnIndexOrThrow("poster"))
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error checking database for movie", e)
            false
        } finally {
            cursor?.close()
            dbRead.close()
        }
    }

    private fun fetchMovies(title: String) {
        loading = ProgressDialog.show(this, "Memuat Data", "Harap tunggu...")
        val omdbUrl = "${Env.omdbUrl}${title}"
        val queue: RequestQueue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, omdbUrl, { response ->
            try {
                val jo = JSONObject(response)
                val dbWrite: SQLiteDatabase = database.writableDatabase

                MovieObject.title = jo.getString("Title")
                MovieObject.year = jo.getString("Year")
                MovieObject.rating = jo.getString("imdbRating")
                MovieObject.duration = jo.getString("Runtime")
                MovieObject.release_date = jo.getString("Released")
                MovieObject.language = jo.getString("Language")
                MovieObject.genre = jo.getString("Genre")
                MovieObject.director = jo.getString("Director")
                MovieObject.writer = jo.getString("Writer")
                MovieObject.actor = jo.getString("Actors")
                MovieObject.plot = jo.getString("Plot")
                MovieObject.poster = jo.getString("Poster")

                // Insert data into the database
                val values = ContentValues().apply {
                    put("title", MovieObject.title)
                    put("year", MovieObject.year)
                    put("rating", MovieObject.rating)
                    put("duration", MovieObject.duration)
                    put("release_date", MovieObject.release_date)
                    put("language", MovieObject.language)
                    put("genre", MovieObject.genre)
                    put("director", MovieObject.director)
                    put("writer", MovieObject.writer)
                    put("actor", MovieObject.actor)
                    put("plot", MovieObject.plot)
                    put("poster", MovieObject.poster)
                    put("view_count", 0) // Initial view count
                    put("CREATED_AT", System.currentTimeMillis().toString())
                    put("UPDATED_AT", System.currentTimeMillis().toString())
                }
                dbWrite.insert("films", null, values)

                loading.dismiss()
                val intent = Intent(this, DetailActivity::class.java)
                startActivity(intent)
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(this, "Gagal mengambil data!", Toast.LENGTH_SHORT).show()
                Log.e("JSONException", e.toString())
                loading.dismiss()
            } finally {
                loading.dismiss()
            }
        }, { error ->
            Toast.makeText(this, "Gagal mengambil Data!", Toast.LENGTH_SHORT).show()
            error.printStackTrace()
            Log.e("VolleyError", error.toString())
            loading.dismiss()
        })
        queue.add(stringRequest)
    }

    private fun showFilterDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_filter, null)

        builder.setView(dialogLayout)
        val dialog = builder.create()

        dialogLayout.findViewById<TextView>(R.id.tv_sort_abjad).setOnClickListener {
            dialog.dismiss()
            sortMovies("abjad")
        }
        dialogLayout.findViewById<TextView>(R.id.tv_sort_terbaru).setOnClickListener {
            dialog.dismiss()
            sortMovies("terbaru")
        }
        dialogLayout.findViewById<TextView>(R.id.tv_sort_terlama).setOnClickListener {
            dialog.dismiss()
            sortMovies("terlama")
        }

        dialog.show()
    }

    private fun sortMovies(criteria: String) {
        val dbRead: SQLiteDatabase = database.readableDatabase
        var cursor: Cursor? = null
        val movies = mutableListOf<Movie>()
        val orderBy = when (criteria) {
            "abjad" -> "title ASC"
            "terbaru" -> "release_date DESC"
            "terlama" -> "release_date ASC"
            else -> "UPDATED_AT DESC"
        }

        try {
            cursor = dbRead.rawQuery("SELECT * FROM films ORDER BY $orderBy", null)
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
            Log.e("MainActivity", "Error loading movies from database", e)
        } finally {
            cursor?.close()
            dbRead.close()
        }
        movieAdapter.setMovies(movies)
    }
}
