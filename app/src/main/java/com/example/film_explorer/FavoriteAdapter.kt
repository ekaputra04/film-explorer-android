package com.example.film_explorer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoriteAdapter(
    private val context: Context,
    private var movies: List<Movie>
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movies, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val movie = movies[position]

        holder.title.text = movie.title
        Glide.with(context)
            .load(movie.poster)
            .into(holder.poster)

        holder.itemView.setOnClickListener {
            // Copy movie data to MovieObject
            MovieObject.id = movie.id
            MovieObject.title = movie.title
            MovieObject.year = movie.year
            MovieObject.rating = movie.rating
            MovieObject.duration = movie.duration
            MovieObject.release_date = movie.release_date
            MovieObject.language = movie.language
            MovieObject.genre = movie.genre
            MovieObject.director = movie.director
            MovieObject.writer = movie.writer
            MovieObject.actor = movie.actor
            MovieObject.plot = movie.plot
            MovieObject.poster = movie.poster

            val intent = Intent(context, DetailActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun setMovies(movies: List<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.img_item_movie)
        val title: TextView = view.findViewById(R.id.tv_item_movie_title)
    }
}
