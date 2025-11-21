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
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.utils.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                requireArguments()
                    .getParcelable(PlayerFragment.ARGS_TRACK, Track::class.java)
            else requireArguments()
                .getParcelable<Track>(PlayerFragment.ARGS_TRACK)
        )
    }
    private lateinit var binding: FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            binding.trackPlay.setImageResource(
                if (it == PlayerViewModel.STATE_PLAYING) R.drawable.track_pause
                else R.drawable.track_play
            )
        }

        viewModel.observeProgressTime().observe(viewLifecycleOwner) {
            binding.trackPlayTime.text = it
        }

        binding.tbPlayer.setNavigationOnClickListener { findNavController().navigateUp() }
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

    private fun showError(errorMsg: String) {
        binding.apply {
            scvMain.visibility = View.GONE
            txtPlaceholder.text = errorMsg
            txtPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Content -> showContent(state.track)
            is PlayerState.Error -> showError(state.message)
            is PlayerState.Empty -> showError(state.message)
        }
    }

    companion object {
        const val ARGS_TRACK = "track"
        fun createArgs(track: Track): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(ARGS_TRACK, track)
            return bundle
        }
    }
}