package com.practicum.playlistmaker.playlists.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.common.ui.TracksAdapter
import com.practicum.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.playlists.domain.PlayList
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue
import kotlin.math.round

class PlaylistDetailsFragment : Fragment() {

    private val viewModel by viewModel<PlaylistDetailsViewModel> {
        parametersOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                requireArguments()
                    .getParcelable(ARGS_PLAYLIST_DETAILS, PlayList::class.java)
            else requireArguments()
                .getParcelable(ARGS_PLAYLIST_DETAILS),
            requireContext()
        )
    }
    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetBehaviorMore: BottomSheetBehavior<View>
    private lateinit var adapter: TracksAdapter
    private lateinit var adapterOnlyOnePlaylist: PlayListsAdapter
    private lateinit var playList: PlayList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.includedBottomSheetPlayList.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehaviorMore = BottomSheetBehavior.from(binding.includedBottomSheetPlayListMore.root)
        bottomSheetBehaviorMore.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehaviorMore.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                binding.overlayPlayListDetails.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.tbPlayListDetails.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.imgMorePlayListDetails.setOnClickListener {
            bottomSheetBehaviorMore.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.includedBottomSheetPlayListMore.txtEditBottomSheetPlayListMore.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_playlistFragment,
                PlaylistFragment.createArgs(playList)
            )
        }
        binding.includedBottomSheetPlayListMore.txtDeleteBottomSheetPlayListMore.setOnClickListener {
            deletePlayList()
        }
        binding.imgSharePlayListDetails.setOnClickListener {
            viewModel.sharePlayListData(requireContext())
        }
        binding.includedBottomSheetPlayListMore.txtShareBottomSheetPlayListMore.setOnClickListener {
            viewModel.sharePlayListData(requireContext())
        }

        adapterOnlyOnePlaylist = PlayListsAdapter(
            PlayListsAdapter.LAYOUT_OPTION_LINEAR,
            PlayListsAdapter.determineDeclensionTracks(requireContext())
        ) { position -> }
        binding.includedBottomSheetPlayListMore.rcvBottomSheetPlayListMore.adapter = adapterOnlyOnePlaylist

        adapter = TracksAdapter { position, longPress ->
            if (longPress) deleteTrackFromPlayList(adapter.tracks[position])
            else findNavController().navigate(
                R.id.action_playlistDetailsFragment_to_playerFragment,
                PlayerFragment.createArgs(adapter.tracks[position])
            )
        }
        binding.includedBottomSheetPlayList.rcvBottomSheetPlayList.adapter = adapter

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setFragmentResultListener() {

        parentFragmentManager.setFragmentResultListener(
            PlaylistFragment.PLAYLIST_KEY,
            this
        ) { requestKey, bundle ->
            val newPlayList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                bundle.getParcelable(PlaylistFragment.UPDATE_PLAYLIST_DATA_KEY, PlayList::class.java)
            else bundle.getParcelable(PlaylistFragment.UPDATE_PLAYLIST_DATA_KEY)
            if (newPlayList != null) viewModel.updatePlayList(newPlayList)
        }
    }

    private fun deletePlayList() {

        binding.overlayPlayListDetails.isVisible = true

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.playlist_details_playlist_delete)
                .format(playList.name))
            .setMessage("\n\n")
            .setNegativeButton(R.string.playlist_details_dialog_negative_btn) { dialog, which ->
                binding.overlayPlayListDetails.isVisible = false
            }
            .setPositiveButton(R.string.playlist_details_dialog_positive_btn) { dialog, which ->
                viewModel.deletePlayList()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun deleteTrackFromPlayList(track: Track) {

        binding.overlayPlayListDetails.isVisible = true

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.playlist_details_track_delete)
            .setMessage(R.string.playlist_details_dialog_indent)
            .setNegativeButton(R.string.playlist_details_dialog_negative_btn) { dialog, which ->
                binding.overlayPlayListDetails.isVisible = false
            }
            .setPositiveButton(R.string.playlist_details_dialog_positive_btn) { dialog, which ->
                viewModel.deleteTrackFromPlayList(track)
                binding.overlayPlayListDetails.isVisible = false
            }
            .show()
    }

    private fun showPlayList() {

        Glide.with(binding.imgPlayListDetailsCover)
            .load(playList.pathCoverFile)
            .placeholder(R.drawable.placeholder_512)
            .into(binding.imgPlayListDetailsCover)


        binding.txtNamePlayListDetails.text = playList.name
        binding.txtDescriptionPlayListDetails.text = playList.description
        binding.txtTimePlayListDetails.text = getString(R.string.playlist_details_totals)
            .format(
                PlayListsAdapter.numberEntities(
                    round(playList.totalTime.toFloat() / 60000).toInt(),
                    PlayListsAdapter.determineDeclensionMinutes(requireContext())
                ),
                PlayListsAdapter.numberEntities(
                    playList.numTracks,
                    PlayListsAdapter.determineDeclensionTracks(requireContext())
                )
            )

        adapterOnlyOnePlaylist.playLists.clear()
        adapterOnlyOnePlaylist.playLists.add(playList)
        adapterOnlyOnePlaylist.notifyDataSetChanged()

    }

    private fun showTracks(tracks: List<Track>) {

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()

    }

    private fun showError(message: String) {

    }

    private fun render(state: PlaylistDetailsState) {
        when (state) {
            is PlaylistDetailsState.Content -> {
                playList = state.playList
                showPlayList()
                showTracks(state.tracks)
            }
            is PlaylistDetailsState.Error -> showError(state.message)
        }
    }

    companion object {
        const val ARGS_PLAYLIST_DETAILS = "ARGS_PLAYLIST_DETAILS"
        fun createArgs(playList: PlayList) = Bundle().apply { putParcelable(ARGS_PLAYLIST_DETAILS, playList) }
    }
}