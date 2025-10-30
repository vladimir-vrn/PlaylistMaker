package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.observeState().observe(this) {
            render(it)
        }

        binding.btnSearch.setOnClickListener {
            val intentSearch = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intentSearch)
        }

        binding.btnMediaLibrary.setOnClickListener {
            val intentMediaLibrary = Intent(this@MainActivity, MediaLibraryActivity::class.java)
            startActivity(intentMediaLibrary)
        }

        binding.btnSettings.setOnClickListener {
            val intentSettings = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intentSettings)
        }
    }

    private fun render(state: MainActivityState) {

    }
}