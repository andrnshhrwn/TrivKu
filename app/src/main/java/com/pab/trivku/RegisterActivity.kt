package com.pab.trivku

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.pab.trivku.data.AuthRepository
import com.pab.trivku.data.local.AppDatabase
import com.pab.trivku.data.pref.SessionManager
import com.pab.trivku.viewmodel.AuthViewModel
import com.pab.trivku.viewmodel.AuthViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)   // pastikan nama file XML adalah register.xml

        // Inisialisasi Database dan Repository
        val dao = AppDatabase.getDatabase(applicationContext).userDao()
        val repository = AuthRepository(dao)
        val sessionManager = SessionManager(this)

        val factory = AuthViewModelFactory(repository, sessionManager)
        val authViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        authViewModel.registStatus.observe(this) { status ->
            when (status) {
                "Registrasi Berhasil" -> {
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

        // ====== FIND VIEW ======
        val inputUsername = findViewById<EditText>(R.id.inputUsername)
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)


        // ====== BUTTON BACK ======
        val btnBack = findViewById<TextView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // ====== SHOW/HIDE PASSWORD ======
        val btnShowPassword = findViewById<ImageView>(R.id.btnShowPassword)
        btnShowPassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                inputPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnShowPassword.setImageResource(R.drawable.ic_eye)
            } else {
                inputPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnShowPassword.setImageResource(R.drawable.ic_eye_off)
            }

            inputPassword.setSelection(inputPassword.text.length)
        }

        // ====== BUTTON REGISTER ======
        btnRegister.setOnClickListener {

            val username = inputUsername.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            // Validasi
            if (username.isEmpty()) {
                inputUsername.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                inputEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }

            if (!email.contains("@")) {
                inputEmail.error = "Email tidak valid"
                return@setOnClickListener
            }

            if (password.length < 8) {
                inputPassword.error = "Password minimal 8 karakter"
                return@setOnClickListener
            }

            authViewModel.register(username, email, password)
        }


        // ====== SUDAH PUNYA AKUN (PINDAH LOGIN) ======
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
