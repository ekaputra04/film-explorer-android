package com.example.film_explorer

import android.app.Activity
import android.app.AlertDialog
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
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
    private lateinit var imgFavorite: ImageView
    private lateinit var imgOption: ImageView
    private lateinit var database: Database
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initComponents()
        updateInfoPoster()
        checkFavoriteStatus()

        imgBack.setOnClickListener(this)
        imgFavorite.setOnClickListener(this)
        imgOption.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_detail_back) {
            finish()
        }

        if (v?.id == R.id.img_detail_favorite) {
            if (isFavorite) {
                database.removeFavorite(UserObject.id, MovieObject.id)
                isFavorite = false
                Toast.makeText(this, "Film dihapus dari favorit", Toast.LENGTH_SHORT).show()
            } else {
                database.addFavorite(UserObject.id, MovieObject.id)
                isFavorite = true
                Toast.makeText(this, "Film ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
            }
            updateFavoriteIcon()
        }

        if (v?.id == R.id.img_detail_option) {
            showDeleteConfirmationDialog()
        }
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
        imgFavorite = findViewById(R.id.img_detail_favorite)
        imgOption = findViewById(R.id.img_detail_option)
        database = Database(this)
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
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menampilkan data!", Toast.LENGTH_SHORT).show()
            Log.e("JSONException", e.toString())
            finish()
        }
    }

    private fun checkFavoriteStatus() {
        isFavorite = database.isFavorite(UserObject.id, MovieObject.id)
        updateFavoriteIcon()
    }

    private fun updateFavoriteIcon() {
        val favoriteIcon = if (isFavorite) R.drawable.favorite_red else R.drawable.favorite_white
        imgFavorite.setImageResource(favoriteIcon)
    }

    private fun showDeleteConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.btn_delete).setOnClickListener {
            database.deleteFilm(MovieObject.id)
            alertDialog.dismiss()
            Toast.makeText(this, "Film berhasil dihapus", Toast.LENGTH_SHORT).show()
            finish() // Kembali ke halaman sebelumnya setelah menghapus film
        }

        alertDialog.show()
    }
}
