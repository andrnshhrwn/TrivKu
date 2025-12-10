package com.pab.trivku

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge

class LoginActivity : AppCompatActivity() {

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // visiblePassword
        val inputPassword = findViewById<EditText>(R.id.etPassword)
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
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val tvRegist = findViewById<TextView>(R.id.tvRegist)
        tvRegist.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val login = findViewById<Button>(R.id.btnLogin)
        login.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }
    }
}
