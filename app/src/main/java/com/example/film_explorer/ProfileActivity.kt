package com.example.film_explorer

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ProfileActivity : Activity(), View.OnClickListener {
    private lateinit var imgBack: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var imgProfileDetail: ImageView
    private lateinit var imgFavoriteDetail: ImageView
    private lateinit var imgLogoutDetail: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initComponents()

        updateUIProfile()

        imgBack.setOnClickListener(this)
        imgProfileDetail.setOnClickListener(this)
        imgFavoriteDetail.setOnClickListener(this)
        imgLogoutDetail.setOnClickListener(this)
    }

    private fun updateUIProfile() {
        tvUsername.text = UserObject.username
        tvEmail.text = UserObject.email
    }

    private fun initComponents() {
        imgBack = findViewById(R.id.img_profile_back)
        tvUsername = findViewById(R.id.tv_profile_username)
        tvEmail = findViewById(R.id.tv_profile_email)
        imgProfileDetail = findViewById(R.id.img_profile_detail_profile_next)
        imgFavoriteDetail = findViewById(R.id.img_profile_detail_favorite_next)
        imgLogoutDetail = findViewById(R.id.img_profile_detail_logout_next)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_profile_back) {
            finish()
        }

        if (v?.id == R.id.img_profile_detail_profile_next) {

        }

        if (v?.id == R.id.img_profile_detail_favorite_next) {

        }

        if (v?.id == R.id.img_profile_detail_logout_next) {
            val intent = Intent(this, SplashScreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}