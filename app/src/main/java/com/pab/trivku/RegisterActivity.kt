package com.pab.trivku

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)   // pastikan nama file XML adalah register.xml

        // ====== FIND VIEW ======
        val inputNama = findViewById<EditText>(R.id.inputNama)
        val inputEmail = findViewById<EditText>(R.id.inputEmail)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)


        // ====== BUTTON BACK ======
        val btnBack = findViewById<ImageView>(R.id.btnBack)
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

            val nama = inputNama.text.toString().trim()
            val email = inputEmail.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            // Validasi
            if (nama.isEmpty()) {
                inputNama.error = "Nama tidak boleh kosong"
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

            Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()

            // Setelah sukses, pindah ke Login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }


        // ====== SUDAH PUNYA AKUN (PINDAH LOGIN) ======
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
