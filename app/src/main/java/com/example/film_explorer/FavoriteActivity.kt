package com.example.film_explorer

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteActivity : Activity(), View.OnClickListener {
    private lateinit var imgBack: ImageView
    private lateinit var database: Database
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var movies: List<Movie>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        initComponents()

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = favoriteAdapter

        movies = emptyList()
        movies = database.getFavoriteFilms(UserObject.id)
        favoriteAdapter.setMovies(movies)

        imgBack.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        movies = emptyList()
        movies = database.getFavoriteFilms(UserObject.id)
        favoriteAdapter.setMovies(movies)
    }

    private fun initComponents() {
        imgBack = findViewById(R.id.img_favorite_back)
        recyclerView = findViewById(R.id.rv_favorite)
        database = Database(this)
        favoriteAdapter = FavoriteAdapter(this, listOf())
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_favorite_back) {
            finish()
        }
    }
}