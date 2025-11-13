package com.practicum.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaLibraryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryActivity : AppCompatActivity() {

    private val viewModel by viewModel<MediaLibraryViewModel>()
    private lateinit var binding: ActivityMediaLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = MediaLibraryViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.media_library_tab_favorite_tracks)
                1 -> tab.text = getString(R.string.media_library_tab_playlists)
            }
        }
        tabMediator.attach()

        viewModel.observeState().observe(this) {
            render(it)
        }

        binding.tbMediaLibrary.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }

    private fun render(state: MediaLibraryActivityState) {
        binding.tabLayout.getTabAt(
            when(state) {
                is MediaLibraryActivityState.FavoritesMissing -> 0
                is MediaLibraryActivityState.PlaylistsMissing -> 1
                else -> 0
            }
        )?.select()
    }
}