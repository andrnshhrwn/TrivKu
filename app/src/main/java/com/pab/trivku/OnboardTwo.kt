package com.pab.trivku

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

class OnboardTwo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboard_two)

        // Pindah Activity
        val btnNext = findViewById<Button>(R.id.ob2_next)

        btnNext.setOnClickListener {
            startActivity(Intent(this, OnboardThree::class.java))
        }
    }
}