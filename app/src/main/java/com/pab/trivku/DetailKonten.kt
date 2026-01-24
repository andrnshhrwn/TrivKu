package com.pab.trivku

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.pab.trivku.data.models.Destination
import java.text.NumberFormat
import java.util.Locale

class DetailKonten : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_detail_konten)

        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("EXTRA_DESTINATION", Destination::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Destination>("EXTRA_DESTINATION")
        }

        if (data != null) {
            findViewById<TextView>(R.id.tvTitleDestination).text = data.name
            findViewById<TextView>(R.id.tvLocationDestination).text = data.location
            findViewById<ImageView>(R.id.ivDestination).setImageResource(data.imageResource)
            findViewById<TextView>(R.id.tvDetailsDesc).text = data.describe
            findViewById<TextView>(R.id.price).text = formatRupiah.format(data.price).replace(",00", "")
            val btnToMaps = findViewById<Button>(R.id.btn_to_maps)

            // ====== INTENT KE GOOGLE MAPS ======
            btnToMaps.setOnClickListener {
                val query = Uri.encode("${data.name}, ${data.location}, Kuningan")
                val mapUrl = "https://www.google.com/maps/search/?api=1&query=$query"

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
                intent.setPackage("com.google.android.apps.maps")

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
                    startActivity(webIntent)
                }
            }
        }

        // ====== BUTTON BACK ======
        val btnBack = findViewById<TextView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }
}