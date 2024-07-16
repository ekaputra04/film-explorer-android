package com.example.film_explorer

import android.app.Activity
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

class SignInActivity : Activity(), View.OnClickListener {
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvSignUp: TextView
    private lateinit var database: Database
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initComponents()

        btnSubmit.setOnClickListener(this)
        tvSignUp.setOnClickListener(this)
    }

    private fun initComponents() {
        edtEmail = findViewById(R.id.edt_sign_in_email)
        edtPassword = findViewById(R.id.edt_sign_in_password)
        btnSubmit = findViewById(R.id.btn_sign_in)
        tvSignUp = findViewById(R.id.tv_sign_in_sign_up)
        database = Database(this)
        db = database.writableDatabase
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_sign_in) {
            val email = edtEmail.text.trim().toString()
            val password = edtPassword.text.trim().toString()
            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) edtEmail.error = "Email tidak boleh kosong!"
                if (password.isEmpty()) edtPassword.error = "Password tidak boleh kosong!"
            } else {
                if (checkUserCredentials(email, password)) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Email atau password salah!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (v?.id == R.id.tv_sign_in_sign_up) {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkUserCredentials(email: String, password: String): Boolean {
        var cursor: Cursor? = null
        return try {
            cursor = db.rawQuery(
                "SELECT * FROM users WHERE email = ? AND password = ?",
                arrayOf(email, password)
            )
            if (cursor.moveToFirst()) {
                UserObject.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                UserObject.username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                UserObject.email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                UserObject.password = cursor.getString(cursor.getColumnIndexOrThrow("password"))
                UserObject.isAdmin =
                    cursor.getString(cursor.getColumnIndexOrThrow("is_admin")).toBoolean()
            }
            cursor.count > 0
        } catch (e: Exception) {
            Log.e("SignInActivity", "Error checking user credentials", e)
            false
        } finally {
            cursor?.close()
        }
    }

//    override fun onDestroy() {
//        db.close()
//        database.close()
//        super.onDestroy()
//    }
}
