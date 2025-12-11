package com.pab.trivku

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.pab.trivku.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // HOME sebagai tampilan pertama
        loadFragment(HomeFragment())
        binding.bottomNavigationView.selectedItemId = R.id.home

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> { loadFragment(HomeFragment()); true }
                R.id.best -> { loadFragment(BestFragment()); true }
                R.id.search -> { loadFragment(SearchFragment()); true }
                R.id.save -> { loadFragment(SaveFragment()); true }
                R.id.profile -> { loadFragment(ProfileFragment()); true }
                else -> false
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, true)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}