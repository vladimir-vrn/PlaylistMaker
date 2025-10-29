package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("track")
        )
    }
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observePlayerState().observe(this) {
            binding.trackPlay.setImageResource(
                if (it == PlayerViewModel.STATE_PLAYING) R.drawable.track_pause
                else R.drawable.track_play
            )
        }

        viewModel.observeProgressTime().observe(this) {
            binding.trackPlayTime.text = it
        }

        binding.tbPlayer.setNavigationOnClickListener { finish() }
        binding.trackPlay.setOnClickListener { viewModel.onTrackPlayClicked() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun showContent(track: Track) {

        Glide.with(binding.trackImage)
            .load(track.coverArtwork)
            .placeholder(R.drawable.placeholder_512)
            .transform(
                RoundedCorners(
                    dpToPx(8, this)
                )
            )
            .into(binding.trackImage)

        binding.apply {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.trackTime
            if (track.collectionName.isEmpty()) {
                collectionNameTitle.visibility = View.GONE
                collectionName.visibility = View.GONE
            } else {
                collectionName.text = track.collectionName
            }
            if (track.releaseDate.isEmpty()) {
                releaseDateTitle.visibility = View.GONE
                releaseDate.visibility = View.GONE
            } else {
                releaseDate.text = track.releaseDate.take(4)
            }
            primaryGenreName.text = track.primaryGenreName
            country.text = track.country
        }
    }

    private fun showError(errorMsg: String) {
        binding.apply {
            scvMain.visibility = View.GONE
            txtPlaceholder.text = errorMsg
            txtPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun render(state: PlayerActivityState) {
        when (state) {
            is PlayerActivityState.Content -> showContent(state.track)
            is PlayerActivityState.Error -> showError(state.message)
            is PlayerActivityState.Empty -> showError(state.message)
        }
    }
}