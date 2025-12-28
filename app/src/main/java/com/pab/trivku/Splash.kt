package com.pab.trivku

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pab.trivku.data.pref.SessionManager

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Inisialisasi SessionManager
        val sessionManager = SessionManager(this)

        val motionLayout = findViewById<MotionLayout>(R.id.motionLayout)

        motionLayout.post {
            motionLayout.transitionToEnd()
        }

        motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                if (sessionManager.isLoggedIn()) {
                    val intent = Intent(this@Splash, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    startActivity(Intent(this@Splash, OnboardOne::class.java))
                }
                finish()
            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {}
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.motionLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}