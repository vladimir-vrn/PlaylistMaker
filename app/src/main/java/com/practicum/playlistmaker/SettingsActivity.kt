package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val imgLeftArrow = findViewById<ImageView>(R.id.img_left_arrow)
        imgLeftArrow.setOnClickListener {
            finish()
        }
    }
}