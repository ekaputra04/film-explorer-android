package com.example.film_explorer

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import com.bumptech.glide.Glide
import org.json.JSONObject

class SearchActivity : Activity() {
    private lateinit var tvTitle: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvGenre: TextView
    private lateinit var tvDirector: TextView
    private lateinit var tvWriter: TextView
    private lateinit var tvActors: TextView
    private lateinit var tvPlot: TextView
    private lateinit var btnKirim: Button
    private lateinit var btnReset: Button
    private lateinit var edtTitle: EditText
    private lateinit var imgPoster: ImageView
    private lateinit var layoutInfoPoster: LinearLayout
    private val urlOmdb = "https://www.omdbapi.com/?apikey=447ca6a9&t="
    private lateinit var loading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        layoutInfoPoster.visibility = View.GONE
    }

    private fun initComponents() {
        tvTitle = findViewById(R.id.tv_title)
        tvYear = findViewById(R.id.tv_year)
        tvGenre = findViewById(R.id.tv_genre)
        tvDirector = findViewById(R.id.tv_director)
        tvWriter = findViewById(R.id.tv_writer)
        tvActors = findViewById(R.id.tv_actors)
        tvPlot = findViewById(R.id.tv_plot)
        btnKirim = findViewById(R.id.btn_kirim)
        btnReset = findViewById(R.id.btn_reset)
        edtTitle = findViewById(R.id.edt_input)
        imgPoster = findViewById(R.id.img_poster)
        layoutInfoPoster = findViewById(R.id.layout_info_poster)

        btnKirim.setOnClickListener {
            if (edtTitle.text.isEmpty()) {
                Toast.makeText(this, "Masukkan Judul Film", Toast.LENGTH_SHORT).show()
                edtTitle.error = "Masukkan Judul Film"
            } else {
                val judulFilm = edtTitle.text.toString()
                tampilkanData(judulFilm)
            }
        }
        btnReset.setOnClickListener { resetData() }
    }

    private fun tampilkanData(judulFilm: String) {
        loading = ProgressDialog.show(this, "Memuat Data", "Harap tunggu...")
        val queue: RequestQueue = Volley.newRequestQueue(this)

        val url = "$urlOmdb" + judulFilm
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jo = JSONObject(response)
                    tvTitle.text = jo.getString("Title")
                    tvYear.text = jo.getString("Year")
                    tvGenre.text = jo.getString("Genre")
                    tvDirector.text = jo.getString("Director")
                    tvWriter.text = jo.getString("Writer")
                    tvActors.text = jo.getString("Actors")
                    tvPlot.text = jo.getString("Plot")
                    val posterUrl = jo.getString("Poster")
                    Glide.with(this).load(posterUrl).into(imgPoster)
                    layoutInfoPoster.visibility = LinearLayout.VISIBLE
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Gagal mengurai data!", Toast.LENGTH_SHORT).show()
                    Log.e("JSONException", e.toString())
                } finally {
                    loading.dismiss()
                }
            },
            { error ->
                Toast.makeText(this, "Gagal Mengambil Data!", Toast.LENGTH_SHORT).show()
                error.printStackTrace()
                Log.e("VolleyError", error.toString())
                loading.dismiss()
            }
        )

        queue.add(stringRequest)
        layoutInfoPoster.visibility = View.VISIBLE
    }

    private fun resetData() {
        tvTitle.text = ""
        tvYear.text = ""
        tvGenre.text = ""
        tvDirector.text = ""
        tvWriter.text = ""
        tvActors.text = ""
        edtTitle.text.clear()
        imgPoster.setImageDrawable(null)
        layoutInfoPoster.visibility = LinearLayout.GONE
    }
}
