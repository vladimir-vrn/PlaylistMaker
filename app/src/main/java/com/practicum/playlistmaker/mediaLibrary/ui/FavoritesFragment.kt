package com.practicum.playlistmaker.mediaLibrary.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.common.ui.TracksAdapter
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel by viewModel<FavoritesViewModel>()
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TracksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        adapter = TracksAdapter { position ->
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_playerFragment,
                PlayerFragment.createArgs(adapter.tracks[position])
            )
        }
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateFavoriteTracks()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showFavoriteTracks(tracks: List<Track>) {

        binding.apply {
            imgFavoritesMissing.isVisible = false
            txtFavoritesMissing.isVisible = false
        }

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showEmpty() {

        if (adapter.tracks.isNotEmpty()) {
            adapter.tracks.clear()
            adapter.notifyDataSetChanged()
        }

        binding.apply {
            imgFavoritesMissing.isVisible = true
            txtFavoritesMissing.isVisible = true
        }
    }

    private fun showError() {

    }
    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Content -> {
                if (state.tracks.isEmpty()) showEmpty()
                else showFavoriteTracks(state.tracks)
            }
            is FavoritesState.Error -> showError()
        }
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}