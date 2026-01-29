package com.practicum.playlistmaker.mediaLibrary.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediaLibrary.domain.PlayList
import com.practicum.playlistmaker.common.ui.RootActivity
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.text.format

class PlaylistsFragment : Fragment() {

    private val viewModel by viewModel<PlaylistsViewModel>()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlayListsAdapter
    private var newPlayList: PlayList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaLibraryFragment_to_playlistFragment)
        }

        adapter = PlayListsAdapter(
            PlayListsAdapter.LAYOUT_OPTION_GRID,
            PlayListsAdapter.determineDeclensionTracks(requireContext())
        ) { position -> }
        binding.recyclerViewPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerViewPlaylists.adapter = adapter

        viewModel.getPlayLists()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showPlayLists(playLists: List<PlayList>) {

        binding.recyclerViewPlaylists.isVisible = false
        binding.btnPlaylistCreated.isVisible = false
        binding.msgMissingPlaylists.isVisible = false

        val activity = getActivity()
        if (activity is RootActivity) activity.setVisibilityBottomNavigationView(true)

        if (playLists.isEmpty())
            binding.msgMissingPlaylists.isVisible = true
        else {
            if (newPlayList != null) {
                if (parentFragment is MediaLibraryFragment)
                        (parentFragment as MediaLibraryFragment).showArrow()
                if (activity is RootActivity) activity.setVisibilityBottomNavigationView(false)
                binding.btnPlaylistCreated.isVisible = true
                binding.btnPlaylistCreated.text =
                    getString(R.string.playlist_was_created_successfully)
                        .format(newPlayList?.name)
            }
            binding.recyclerViewPlaylists.isVisible = true

            adapter.playLists.clear()
            adapter.playLists.addAll(playLists)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setFragmentResultListener() {

        parentFragment?.parentFragmentManager?.setFragmentResultListener(
            PlaylistFragment.ADD_NEW_PLAYLIST_KEY,
            this
        ) { requestKey, bundle ->
            newPlayList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                bundle.getParcelable(PlaylistFragment.ADD_NEW_PLAYLIST_DATA_KEY, PlayList::class.java)
            else bundle.getParcelable(PlaylistFragment.ADD_NEW_PLAYLIST_DATA_KEY)
        }

        parentFragment?.parentFragmentManager?.setFragmentResultListener(
            GO_BACK_MEDIA_LIBRARY_KEY,
            this
        ) { requestKey, bundle ->
            if (bundle.getBoolean(GO_BACK_MEDIA_LIBRARY_KEY_DATA)) {
                val activity = getActivity()
                if (activity is RootActivity) activity.setVisibilityBottomNavigationView(true)
                binding.btnPlaylistCreated.isVisible = false
                newPlayList = null
            }
        }
    }

    private fun showError(message: String) {

    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> {
                showPlayLists(state.playLists)
            }
            is PlaylistsState.Error -> showError(state.message)
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()

        const val GO_BACK_MEDIA_LIBRARY_KEY = "GO_BACK_MEDIA_LIBRARY_KEY"
        const val GO_BACK_MEDIA_LIBRARY_KEY_DATA = "GO_BACK_MEDIA_LIBRARY_KEY_DATA"
    }
}