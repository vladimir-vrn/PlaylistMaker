package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.common.data.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                requireArguments()
                    .getParcelable(ARGS_TRACK, Track::class.java)
            else requireArguments()
                .getParcelable(ARGS_TRACK)
        )
    }
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.tbPlayer.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.trackPlay.setOnClickListener { viewModel.onTrackPlayClicked() }
        binding.trackAddFavourites.setOnClickListener { viewModel.onTrackAddFavourites() }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showTrackData(track: Track) {

        Glide.with(binding.trackImage)
            .load(track.coverArtwork)
            .placeholder(R.drawable.placeholder_512)
            .transform(
                RoundedCorners(
                    dpToPx(8, requireContext())
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

    private fun showEmpty(message: String) {
        binding.apply {
            scvMain.visibility = View.GONE
            txtPlaceholder.text = message
            txtPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun showError(message: String) {

    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Content -> {
                if (state.updateTrack)
                    showTrackData(state.track)
                if (state.updateIsFavourite)
                    binding.trackAddFavourites.setImageResource(
                        if (state.isFavourite) R.drawable.track_is_favourite
                        else R.drawable.track_add_favourites
                    )
                if (state.updateMediaPlayerState)
                    binding.trackPlay.setImageResource(
                        if (state.mediaPlayerState == PlayerViewModel.STATE_PLAYING)
                            R.drawable.track_pause
                        else R.drawable.track_play
                    )
                if (state.updateProgressTime)
                    binding.trackPlayTime.text = state.progressTime
            }
            is PlayerState.Error -> showError(state.message)
            is PlayerState.Empty -> showEmpty(state.message)
        }
    }

    companion object {
        const val ARGS_TRACK = "track"
        fun createArgs(track: Track) = Bundle().apply { putParcelable(ARGS_TRACK, track) }
    }
}