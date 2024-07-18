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
    private lateinit var database: Database

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

    override fun onResume() {
        super.onResume()
        updateUIProfile()
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
        database = Database(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_profile_back) {
            finish()
        }

        if (v?.id == R.id.img_profile_detail_profile_next) {
            showEditTextDialog()
        }

        if (v?.id == R.id.img_profile_detail_favorite_next) {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.img_profile_detail_logout_next) {
            val intent = Intent(this, SplashScreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showEditTextDialog() {
        // Inflate layout yang sudah dibuat
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
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

            // Update data di database
            val result = database.updateUser(UserObject.id, username, email, password)

            if (result > 0) {
                // Update berhasil
                Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                // Perbarui data di UserObject
                UserObject.username = username
                UserObject.email = email
                UserObject.password = password
            } else {
                // Update gagal
                Toast.makeText(this, "Data gagal diupdate", Toast.LENGTH_SHORT).show()
            }

            // Tutup dialog
            alertDialog.dismiss()
        }

        // Tampilkan dialog
        alertDialog.show()
    }
}