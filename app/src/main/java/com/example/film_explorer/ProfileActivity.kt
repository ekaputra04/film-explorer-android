package com.example.film_explorer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

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
            showEditTextDialog()
        }

        if (v?.id == R.id.img_profile_detail_favorite_next) {

        }

        if (v?.id == R.id.img_profile_detail_logout_next) {
            val intent = Intent(this, SplashScreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showEditTextDialog() {
        // Inflate layout yang sudah dibuat
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input, null)
        val edtUsername = dialogView.findViewById<EditText>(R.id.edt_dialog_input_username)
        val edtEmail = dialogView.findViewById<EditText>(R.id.edt_dialog_input_email)
        val edtPassword = dialogView.findViewById<EditText>(R.id.edt_dialog_input_password)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_dialog_input_cancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_dialog_input_simpan)

        // Buat AlertDialog
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        edtUsername.setText(UserObject.username)
        edtEmail.setText(UserObject.email)
        edtPassword.setText(UserObject.password)

        // Set OnClickListener untuk tombol Cancel
        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        // Set OnClickListener untuk tombol Save
        btnSave.setOnClickListener {
            // Ambil teks dari EditText
            val username = edtUsername.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            // Lakukan sesuatu dengan data input
            Toast.makeText(
                this,
                "Username: $username\nEmail: $email\nPassword: $password",
                Toast.LENGTH_SHORT
            ).show()

            // Tutup dialog
            alertDialog.dismiss()
        }

        // Tampilkan dialog
        alertDialog.show()
    }
}