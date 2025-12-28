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

class ResetPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)

        val inputPassword = findViewById<EditText>(R.id.inputResetPassword)
        val inputConfirm = findViewById<EditText>(R.id.inputConfirmPassword)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Inisialisasi Database dan Repository
        val dao = AppDatabase.getDatabase(applicationContext).userDao()
        val repository = AuthRepository(dao)
        val sessionManager = SessionManager(this)

        val factory = AuthViewModelFactory(repository, sessionManager)
        val authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        authViewModel.resetStatus.observe(this) { status ->
            when (status) {
                "Password berhasil diganti" -> {
                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                else -> {
                    // error message
                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val userId = intent.getIntExtra("EXTRA_USER_ID", -1)

        btnSave.setOnClickListener {

            val password = inputPassword.text.toString()
            val confirm = inputConfirm.text.toString()

            if (password.length < 8) {
                inputPassword.error = "Password minimal 8 karakter"
                return@setOnClickListener
            }

            if (password != confirm) {
                inputConfirm.error = "Password tidak sama"
                return@setOnClickListener
            }

            if (userId != -1) {
                authViewModel.reset(password, userId)
            } else {
                Toast.makeText(this, "Error: User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
