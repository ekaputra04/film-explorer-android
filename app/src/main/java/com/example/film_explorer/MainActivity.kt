package com.example.film_explorer

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class MainActivity : Activity(), View.OnClickListener {
    private lateinit var edtSearch: EditText
    private lateinit var imgSearch: ImageView
    private lateinit var loading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()

        edtSearch.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
    }

    private fun initComponents() {
        edtSearch = findViewById(R.id.edt_main_search)
        imgSearch = findViewById(R.id.img_main_search)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_main_search) {
            val textForSearch = edtSearch.text.trim()
            if (textForSearch.isEmpty()) {
                Toast.makeText(this, "Masukkan kata kunci!", Toast.LENGTH_SHORT).show()
            } else {

                fetchMovies(textForSearch.toString())
                val intent = Intent(this, DetailActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun fetchMovies(title: String) {
        loading = ProgressDialog.show(this, "Memuat Data", "Harap tunggu...")
        val omdbUrl = "${Env.omdbUrl}${title}"
        val queue: RequestQueue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, omdbUrl,
            { response ->
                try {
                    val jo = JSONObject(response)
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
                    loading.dismiss()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Gagal mengambil data!", Toast.LENGTH_SHORT).show()
                    Log.e("JSONException", e.toString())
                    loading.dismiss()
                } finally {
                    loading.dismiss()
                }
            },
            { error ->
                Toast.makeText(this, "Gagal mengambil Data!", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
                Log.e("VolleyError", error.toString())
            }
        )
        queue.add(stringRequest)
    }
}
