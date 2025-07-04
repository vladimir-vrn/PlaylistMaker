package com.practicum.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_library)

        val tbMediaLibrary = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.tb_media_library)
        tbMediaLibrary.setNavigationOnClickListener {
            finish()
        }
    }
}