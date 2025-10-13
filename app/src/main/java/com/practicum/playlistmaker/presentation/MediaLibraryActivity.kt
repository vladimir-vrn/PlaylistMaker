package com.practicum.playlistmaker.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.R

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_library)

        val tbMediaLibrary = findViewById<MaterialToolbar>(R.id.tb_media_library)
        tbMediaLibrary.setNavigationOnClickListener {
            finish()
        }
    }
}