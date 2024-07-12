package com.example.film_explorer

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.film_explorer.MovieObject
import org.json.JSONException

class DetailActivity : Activity(), View.OnClickListener {
    private lateinit var tvTitle: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvReleasedDate: TextView
    private lateinit var tvLanguage: TextView
    private lateinit var tvGenre: TextView
    private lateinit var tvDirector: TextView
    private lateinit var tvWriter: TextView
    private lateinit var tvActor: TextView
    private lateinit var tvPlot: TextView
    private lateinit var imgPoster: ImageView
    private lateinit var imgBack: ImageView

    private lateinit var database: Database
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initComponents()
        updateInfoPoster()
        imgBack.setOnClickListener(this)

        // Inisialisasi database
        database = Database(this)
        db = database.writableDatabase
    }

    private fun initComponents() {
        tvTitle = findViewById(R.id.tv_detail_title)
        tvYear = findViewById(R.id.tv_detail_year)
        tvRating = findViewById(R.id.tv_detail_rating)
        tvDuration = findViewById(R.id.tv_detail_duration)
        tvReleasedDate = findViewById(R.id.tv_detail_release_date)
        tvLanguage = findViewById(R.id.tv_detail_language)
        tvGenre = findViewById(R.id.tv_detail_genre)
        tvDirector = findViewById(R.id.tv_detail_director)
        tvWriter = findViewById(R.id.tv_detail_writer)
        tvActor = findViewById(R.id.tv_detail_actor)
        tvPlot = findViewById(R.id.tv_detail_plot)
        imgPoster = findViewById(R.id.img_detail_poster)
        imgBack = findViewById(R.id.img_detail_back)
    }

    private fun updateInfoPoster() {
        try {
            tvTitle.text = MovieObject.title
            tvYear.text = MovieObject.year
            tvRating.text = MovieObject.rating
            tvDuration.text = MovieObject.duration
            tvReleasedDate.text = MovieObject.release_date
            tvLanguage.text = MovieObject.language
            tvGenre.text = MovieObject.genre
            tvDirector.text = MovieObject.director
            tvWriter.text = MovieObject.writer
            tvActor.text = MovieObject.actor
            tvPlot.text = MovieObject.plot
            Glide.with(this).load(MovieObject.poster).into(imgPoster)

            // Memperbarui view_count ketika DetailActivity dimuat
            updateViewCount(MovieObject.id)

        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menampilkan data!", Toast.LENGTH_SHORT).show()
            Log.e("JSONException", e.toString())
            finish()
        }
    }

    private fun updateViewCount(movieId: Int) {
        database.updateViewCount(movieId)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_detail_back) {
            finish()
        }
    }

//    override fun onDestroy() {
//        db.close()
//        database.close()
//        super.onDestroy()
//    }
}
