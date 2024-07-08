package com.example.film_explorer

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class OnBoardingActivity : Activity(), View.OnClickListener {
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        initComponents()
        btnNext.setOnClickListener(this)
    }

    private fun initComponents() {
        btnNext = findViewById(R.id.btn_onboarding)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_onboarding) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}