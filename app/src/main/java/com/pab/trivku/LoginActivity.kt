package com.pab.trivku

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.pab.trivku.data.AuthRepository
import com.pab.trivku.data.local.AppDatabase
import com.pab.trivku.data.pref.SessionManager
import com.pab.trivku.viewmodel.AuthViewModel
import com.pab.trivku.viewmodel.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Inisialisasi Database dan Repository
        val dao = AppDatabase.getDatabase(applicationContext).userDao()
        val repository = AuthRepository(dao)
        val sessionManager = SessionManager(this)

        val factory = AuthViewModelFactory(repository, sessionManager)
        val authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        authViewModel.loginStatus.observe(this) { status ->
            when (status) {
                "Berhasil" -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                else -> {
                    // error message
                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            val pass = inputPassword.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Panggil fungsi login
                authViewModel.login(email, pass)
            } else {
                Toast.makeText(this, "Isi semua kolom!", Toast.LENGTH_SHORT).show()
            }
        }

        // visiblePassword
        val showPassword = findViewById<ImageView>(R.id.btnShowPassword)
        showPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                inputPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPassword.setImageResource(R.drawable.ic_eye)
            } else {
                inputPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPassword.setImageResource(R.drawable.ic_eye_off)
            }

            inputPassword.setSelection(inputPassword.text.length)
        }

        // ====== BUTTON BACK ======
        val btnBack = findViewById<TextView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val tvRegist = findViewById<TextView>(R.id.tvRegist)
        tvRegist.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val tvForget = findViewById<TextView>(R.id.tvForgotPassword)
        tvForget.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }
    }
}
