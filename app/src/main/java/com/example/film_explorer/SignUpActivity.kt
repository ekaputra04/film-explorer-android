package com.example.film_explorer

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : Activity(), View.OnClickListener {
    private lateinit var edtUsername: EditText
    private lateinit var edtTelephone: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvSignIn: TextView
    private lateinit var database: Database
    private lateinit var db: SQLiteDatabase

    companion object {
        private const val TAG = "SignUpActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initComponents()

        btnSubmit.setOnClickListener(this)
        tvSignIn.setOnClickListener(this)
    }

    private fun initComponents() {
        edtUsername = findViewById(R.id.edt_sign_up_name)
        edtTelephone = findViewById(R.id.edt_sign_up_telephone)
        edtEmail = findViewById(R.id.edt_sign_up_email)
        edtPassword = findViewById(R.id.edt_sign_up_password)
        btnSubmit = findViewById(R.id.btn_sign_up)
        tvSignIn = findViewById(R.id.tv_sign_up_sign_in)
        database = Database(this)
        db = database.writableDatabase
        Log.d(TAG, "Database initialized.")
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_sign_up) {
            val username = edtUsername.text.trim().toString()
            val telephone = edtTelephone.text.trim().toString()
            val email = edtEmail.text.trim().toString()
            val password = edtPassword.text.trim().toString()

            if (username.isEmpty() || telephone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                if (username.isEmpty()) edtUsername.error = "Username tidak boleh kosong!"
                if (telephone.isEmpty()) edtTelephone.error = "Telephone tidak boleh kosong!"
                if (email.isEmpty()) edtEmail.error = "Email tidak boleh kosong!"
                if (password.isEmpty()) edtPassword.error = "Password tidak boleh kosong!"
            } else {
                if (isEmailRegistered(email)) {
                    Toast.makeText(this, "Email sudah terdaftar!", Toast.LENGTH_SHORT).show()
                } else {
                    if (registerUser(username, telephone, email, password)) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        if (v?.id == R.id.tv_sign_up_sign_in) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun isEmailRegistered(email: String): Boolean {
        var cursor: Cursor? = null
        return try {
            cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", arrayOf(email))
            cursor.count > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error checking email registration", e)
            false
        } finally {
            cursor?.close()
        }
    }

    private fun registerUser(
        username: String,
        telephone: String,
        email: String,
        password: String
    ): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentTime = dateFormat.format(Date())

        val values = ContentValues().apply {
            put("username", username)
            put("telephone", telephone)
            put("email", email)
            put("password", password)
            put("is_admin", 0) // SQLite uses 0/1 for boolean values
            put("CREATED_AT", currentTime)
            put("UPDATED_AT", currentTime)
        }
        return try {
            db.insertOrThrow("users", null, values)
            Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error registering user", e)
            Toast.makeText(this, "Gagal melakukan registrasi!", Toast.LENGTH_SHORT).show()
            false
        }
    }

//    override fun onDestroy() {
//        db.close()
//        database.close()
//        super.onDestroy()
//    }
}
