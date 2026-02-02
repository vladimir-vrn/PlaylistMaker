package com.practicum.playlistmaker.playlists.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import com.practicum.playlistmaker.playlists.domain.PlayList
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {

    private val viewModel by viewModel<PlaylistViewModel> {
        parametersOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                requireArguments()
                    .getParcelable(ARGS_PLAYLIST, PlayList::class.java)
            else requireArguments()
                .getParcelable(ARGS_PLAYLIST)
        )
    }
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var callbackOnBackPressed: OnBackPressedCallback
    private lateinit var playList: PlayList
    private var previousPathCoverFile = ""

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
                    playList.copy(
                        name = binding.tieName.text.toString(),
                        description = binding.tieDescription.text.toString(),
                    )
                )
            }
        }

        binding.tieName.addTextChangedListener(textWatcher)
        binding.tieDescription.addTextChangedListener(textWatcher)

        pickMedia = registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null)
                viewModel.updateData(playList.copy(pathCoverFile = uri.toString()))
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
            savePlayList()
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

    private fun savePlayList() {

        val newCoverUri =
            if (playList.pathCoverFile != viewModel.initialPlayList.pathCoverFile)
                viewModel.copyCoverFile(
                    playList.pathCoverFile.toUri(),
                    getString(R.string.dir_playlist_covers),
                    requireContext()
                )
            else playList.pathCoverFile

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

        val newPlayList = playList.copy(pathCoverFile = newCoverUri)
        viewModel.savePlayList(newPlayList)
        if (viewModel.isNewPlayList) msg.show()
        setFragmentResult(
            PLAYLIST_KEY,
            bundleOf((if (viewModel.isNewPlayList) ADD_NEW_PLAYLIST_DATA_KEY
                    else UPDATE_PLAYLIST_DATA_KEY) to playList)
        )
        findNavController().navigateUp()
    }

    private fun checkPossibilityClosing() {
        if (playList != viewModel.initialPlayList && viewModel.isNewPlayList) {

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

    private fun showPlayList() {

        binding.btnCreatePlayList.isEnabled = playList.name.isNotEmpty()
        if (playList.name != binding.tieName.text.toString()) {
            binding.tieName.setText(playList.name)
            binding.btnCreatePlayList.isEnabled = !binding.tieName.text.isNullOrEmpty()
        }
        if (playList.description != binding.tieDescription.text.toString())
            binding.tieDescription.setText(playList.description)
        if (playList.pathCoverFile.isEmpty()) {
            binding.imgPlayListCover.setBackgroundResource(
                R.drawable.shape_playlist_cover_placeholder
            )
        } else if (playList.pathCoverFile != previousPathCoverFile) {
            Glide.with(binding.imgPlayListCover)
                .load(playList.pathCoverFile)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        dpToPx(8, requireContext())
                    )
                )
                .into(binding.imgPlayListCover)
            binding.imgPlayListCover.background = null
            previousPathCoverFile = playList.pathCoverFile
        }

    }

    private fun showError(message: String) {

    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> {
                playList = state.playList
                showPlayList()
                binding.tbPlayList.title = getString(
                    if (viewModel.isNewPlayList) R.string.playlists_new
                    else R.string.playlists_edit
                )
                binding.btnCreatePlayList.text = getString(
                    if (viewModel.isNewPlayList) R.string.playlist_create_btn
                    else R.string.playlist_create_btn_edit
                )
            }
            is PlaylistState.Error -> showError(state.message)
        }
    }

    companion object {

        const val PLAYLIST_KEY = "PLAYLIST_KEY"
        const val ADD_NEW_PLAYLIST_DATA_KEY = "ADD_NEW_PLAYLIST_DATA_KEY"
        const val UPDATE_PLAYLIST_DATA_KEY = "UPDATE_PLAYLIST_DATA_KEY"
        const val ARGS_PLAYLIST = "ARGS_PLAYLIST"
        fun createArgs(playList: PlayList?) = Bundle().apply { putParcelable(ARGS_PLAYLIST, playList) }
    }
}