package com.practicum.playlistmaker.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intentSearch = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intentSearch)
            }
        }
        btnSearch.setOnClickListener(btnSearchClickListener)

        val btnMediaLibrary = findViewById<Button>(R.id.btn_media_library)
        btnMediaLibrary.setOnClickListener {
            val intentMediaLibrary = Intent(this@MainActivity, MediaLibraryActivity::class.java)
            startActivity(intentMediaLibrary)
        }

        val btnSettings = findViewById<Button>(R.id.btn_settings)
        btnSettings.setOnClickListener {
            val intentSettings = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intentSettings)
        }
    }
}