package com.practicum.playlistmaker.mediaLibrary.ui

import android.Manifest
import android.content.pm.PackageManager
import com.practicum.playlistmaker.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.practicum.playlistmaker.common.data.dpToPx
import com.practicum.playlistmaker.mediaLibrary.domain.PlayList
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private val viewModel by viewModel<PlaylistViewModel>()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var callbackOnBackPressed: OnBackPressedCallback
    private var previousCoverUri = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        val textWatcher = object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                viewModel.updateData(
                    PlaylistState.Content(
                        binding.tieName.text.toString(),
                        binding.tieDescription.text.toString(),
                        previousCoverUri
                    )
                )
            }
        }

        binding.tieName.addTextChangedListener(textWatcher)
        binding.tieDescription.addTextChangedListener(textWatcher)

        pickMedia = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) viewModel.displayImage(uri.toString())
        }
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) selectCover()
            else
                Snackbar.make(
                    binding.root,
                    R.string.playlist_read_external_storage_permission_is_required,
                    Snackbar.LENGTH_LONG)
                    .setTextMaxLines(3)
                    .show()
        }
        binding.imgPlayListCover.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) ==
                PackageManager.PERMISSION_GRANTED) selectCover()
            else requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }

        binding.tbPlayList.setNavigationOnClickListener {
            checkPossibilityClosing()
        }
        binding.btnCreatePlayList.setOnClickListener {
            createPlayList()
        }

        callbackOnBackPressed = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                checkPossibilityClosing()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            callbackOnBackPressed
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun selectCover() {
        pickMedia.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    private fun createPlayList() {

        val newCoverUri =
            if (previousCoverUri.isEmpty()) ""
            else viewModel.copyCoverFile(
                    previousCoverUri.toUri(),
                    getString(R.string.dir_playlist_covers),
                    requireContext()
            )

        val msg = Snackbar.make(
            binding.root,
            getString(
                if (newCoverUri == null)
                    R.string.playlist_could_not_place_cover_in_vault
                else R.string.playlist_was_created_successfully
            ).format(binding.tieName.text),
            Snackbar.LENGTH_LONG)
                .setTextMaxLines(3)
        if (newCoverUri == null) {
            msg.show()
            return
        }

        val newPlaylist = PlayList(
            0,
            binding.tieName.text.toString(),
            binding.tieDescription.text.toString(),
            newCoverUri,
            mutableListOf(),
        )
        viewModel.createPlayList(newPlaylist)
        msg.show()
        setFragmentResult(
            ADD_NEW_PLAYLIST_KEY,
            bundleOf(ADD_NEW_PLAYLIST_DATA_KEY to newPlaylist)
        )
        findNavController().navigateUp()
    }

    private fun checkPossibilityClosing() {
        if (previousCoverUri.isNotEmpty() ||
            !binding.tieName.text.isNullOrEmpty() ||
            !binding.tieDescription.text.isNullOrEmpty()) {

            MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.PlayListDialogBtn)
                .setTitle(R.string.playlist_dialog_title)
                .setMessage(R.string.playlist_dialog_message)
                .setNeutralButton(R.string.playlist_dialog_neutral_btn) { dialog, which -> }
                .setPositiveButton(R.string.playlist_dialog_positive_btn) { dialog, which ->
                    findNavController().navigateUp()
                }
                .show()
        }
        else findNavController().navigateUp()
    }

    private fun showError(message: String) {

    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> {
                binding.btnCreatePlayList.isEnabled = state.name.isNotEmpty()
                if (state.name != binding.tieName.text.toString()) {
                    binding.tieName.setText(state.name)
                    binding.btnCreatePlayList.isEnabled = !binding.tieName.text.isNullOrEmpty()
                }
                if (state.description != binding.tieDescription.text.toString())
                    binding.tieDescription.setText(state.description)
                if (state.coverUri.isEmpty()) {
                    binding.imgPlayListCover.setBackgroundResource(
                        R.drawable.shape_playlist_cover_placeholder
                    )
                } else if (state.coverUri != previousCoverUri) {
                    Glide.with(binding.imgPlayListCover)
                        .load(state.coverUri)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(
                                dpToPx(8, requireContext())
                            )
                        )
                        .into(binding.imgPlayListCover)
                    binding.imgPlayListCover.background = null
                    previousCoverUri = state.coverUri
                }
            }
            is PlaylistState.Error -> showError(state.message)
        }
    }

    companion object {

        const val ADD_NEW_PLAYLIST_KEY = "ADD_NEW_PLAYLIST_KEY"
        const val ADD_NEW_PLAYLIST_DATA_KEY = "ADD_NEW_PLAYLIST_DATA_KEY"
    }
}