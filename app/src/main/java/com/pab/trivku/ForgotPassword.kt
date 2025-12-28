package com.pab.trivku

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pab.trivku.data.AuthRepository
import com.pab.trivku.data.local.AppDatabase
import com.pab.trivku.data.pref.SessionManager
import com.pab.trivku.viewmodel.AuthViewModel
import com.pab.trivku.viewmodel.AuthViewModelFactory

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        // Inisialisasi Database dan Repository
        val dao = AppDatabase.getDatabase(applicationContext).userDao()
        val repository = AuthRepository(dao)
        val sessionManager = SessionManager(this)

        val factory = AuthViewModelFactory(repository, sessionManager)
        val authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        // inisialisasi variable
        val btnBack = findViewById<TextView>(R.id.btnBack)
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        // ===== Kembali ke login =====
        btnBack.setOnClickListener { finish() }

        // pindah activity sambil membawa data userId
        authViewModel.navigationEvent.observe(this) { userId ->
            userId?.let {
                val intent = Intent(this, ResetPassword::class.java)
                intent.putExtra("EXTRA_USER_ID", it)
                startActivity(intent)

                authViewModel.onNavigated()
            }
        }

        // jika email tidak ditemukan
        authViewModel.checkEmailStatus.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()

                authViewModel.checkEmailStatus.value = null
            }
        }

        // ===== Kirim Link Reset =====
        btnSend.setOnClickListener {
            val email = inputEmail.text.toString().trim()

            if (email.isEmpty()) {
                inputEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }

            if (!email.contains("@")) {
                inputEmail.error = "Email tidak valid"
                return@setOnClickListener
            }

            authViewModel.forget(email)
        }

        // ===== Text: kembali ke login =====
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
