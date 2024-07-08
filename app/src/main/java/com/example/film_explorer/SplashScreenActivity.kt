package com.example.film_explorer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val thread = Thread {
            try {
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        thread.start()
    }
}