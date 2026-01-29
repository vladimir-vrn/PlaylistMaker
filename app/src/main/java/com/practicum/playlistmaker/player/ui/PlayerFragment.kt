package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.common.data.dpToPx
import com.practicum.playlistmaker.mediaLibrary.domain.PlayList
import com.practicum.playlistmaker.mediaLibrary.ui.PlayListsAdapter
import com.practicum.playlistmaker.mediaLibrary.ui.PlaylistFragment
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
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var callbackOnBackPressed: OnBackPressedCallback
    private lateinit var adapter: PlayListsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener()
    }

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

        bottomSheetBehavior = BottomSheetBehavior.from(binding.includedBottomSheet.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                updateOverlayVisible(newState != BottomSheetBehavior.STATE_HIDDEN)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.tbPlayer.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.trackPlay.setOnClickListener { viewModel.onTrackPlayClicked() }
        binding.trackAddFavourites.setOnClickListener { viewModel.onTrackAddFavourites() }
        binding.trackAddPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        callbackOnBackPressed = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
                    findNavController().navigateUp()
                else bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            callbackOnBackPressed
        )

        adapter = PlayListsAdapter(
            PlayListsAdapter.LAYOUT_OPTION_LINEAR,
            PlayListsAdapter.determineDeclensionTracks(requireContext())
        ) { position ->
            choosingPlaylist(adapter.playLists[position])
        }
        binding.includedBottomSheet.recyclerViewBottomSheet.adapter = adapter
        binding.includedBottomSheet.btnNewPlaylistBottomSheet.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_playlistFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun updateOverlayVisible(overlayIsVisible: Boolean) {

        binding.overlay.isVisible = overlayIsVisible

        binding.trackAddPlaylist.isEnabled = !overlayIsVisible
        binding.trackPlay.isEnabled = !overlayIsVisible
        binding.trackAddFavourites.isEnabled = !overlayIsVisible

        binding.tbPlayer.navigationIcon =
            if (overlayIsVisible) null
            else ContextCompat.getDrawable(requireContext(), R.drawable.ic_left_arrow)
    }

    private fun choosingPlaylist(playList: PlayList) {

        val track = viewModel.getCurTrack()
        val isFoundTrack = playList.trackIDs.contains(track.trackId)
        val textMsg = getString(
            if (isFoundTrack) R.string.player_track_was_found_in_playlist
            else R.string.player_track_added_to_playlist
        ).format(playList.name)

        Toast.makeText(
            requireContext(),
            textMsg,
            Toast.LENGTH_SHORT
        ).show()

        if (!isFoundTrack) {
            viewModel.insertTrackToPlaylist(track, playList.id)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

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

    private fun showPlayLists(playLists: List<PlayList>) {

        updateOverlayVisible( bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN)

        adapter.playLists.clear()
        adapter.playLists.addAll(playLists)
        adapter.notifyDataSetChanged()

    }

    private fun setFragmentResultListener() {

        parentFragmentManager.setFragmentResultListener(
            PlaylistFragment.ADD_NEW_PLAYLIST_KEY,
            this
        ) { requestKey, bundle ->
            val newPlayList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                bundle.getParcelable(PlaylistFragment.ADD_NEW_PLAYLIST_DATA_KEY, PlayList::class.java)
            else bundle.getParcelable(PlaylistFragment.ADD_NEW_PLAYLIST_DATA_KEY)

            if (newPlayList != null) viewModel.updatePlayLists()
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
                if (state.updatePlayLists) {
                    showPlayLists(state.playLists)
                }
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