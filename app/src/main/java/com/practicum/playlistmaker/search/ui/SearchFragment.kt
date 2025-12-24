package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.common.ui.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TracksAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
        binding.btnUpdateSearch.setOnClickListener {
            viewModel.search(
                binding.inputEditText.text.toString(),
                false
            )
        }

        adapter = TracksAdapter { position ->
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(adapter.tracks[position])
            )
            viewModel.updateHistory(adapter.tracks[position])
        }
        binding.recyclerView.adapter = adapter

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText(INPUT_EDIT_TEXT_DEF)
            viewModel.loadHistory()
        }
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.clearIcon.visibility = View.GONE
                    val imm = requireContext().getSystemService(
                        INPUT_METHOD_SERVICE
                    ) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(
                        binding.inputEditText.windowToken, 0
                    )
                    if (binding.inputEditText.hasFocus()) viewModel.loadHistory()
                } else {
                    binding.clearIcon.visibility = View.VISIBLE
                    viewModel.search(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(
                    binding.inputEditText.text.toString(),
                    false
                )
            }
            false
        }
        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isEmpty()) viewModel.loadHistory()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showTracks(tracks: List<Track>) {

        binding.apply {
            recyclerView.visibility = View.VISIBLE
            msgNothingWasFound.visibility = View.GONE
            msgCommunicationProblems.visibility = View.GONE
            btnClearHistory.visibility = View.GONE
            txtHistory.visibility = View.GONE
            progressBar.visibility = View.GONE
        }

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSearchHistory(tracks: List<Track>) {

        binding.apply {
            recyclerView.visibility = View.VISIBLE
            msgNothingWasFound.visibility = View.GONE
            msgCommunicationProblems.visibility = View.GONE
            progressBar.visibility = View.GONE
            btnClearHistory.visibility =
                if (tracks.isNotEmpty()) View.VISIBLE else View.GONE
            txtHistory.visibility =
                if (tracks.isNotEmpty()) View.VISIBLE else View.GONE
        }

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showLoading() {

        if (adapter.tracks.isNotEmpty()) {
            adapter.tracks.clear()
            adapter.notifyDataSetChanged()
        }

        binding.apply {
            recyclerView.visibility = View.GONE
            msgNothingWasFound.visibility = View.GONE
            msgCommunicationProblems.visibility = View.GONE
            btnClearHistory.visibility = View.GONE
            txtHistory.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showError() {

        if (adapter.tracks.isNotEmpty()) {
            adapter.tracks.clear()
            adapter.notifyDataSetChanged()
        }

        binding.apply {
            recyclerView.visibility = View.GONE
            msgNothingWasFound.visibility = View.GONE
            msgCommunicationProblems.visibility = View.VISIBLE
            btnClearHistory.visibility = View.GONE
            txtHistory.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showEmpty() {

        if (adapter.tracks.isNotEmpty()) {
            adapter.tracks.clear()
            adapter.notifyDataSetChanged()
        }

        binding.apply {
            recyclerView.visibility = View.GONE
            msgNothingWasFound.visibility = View.VISIBLE
            msgCommunicationProblems.visibility = View.GONE
            btnClearHistory.visibility = View.GONE
            txtHistory.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> {
                if (state.isInputEditTextHasFocus && !binding.inputEditText.hasFocus())
                    binding.inputEditText.requestFocus()
                if (state.isSearchHistory) showSearchHistory(state.tracks)
                else showTracks(state.tracks)
            }
            is SearchState.Error -> showError()
            is SearchState.Empty -> showEmpty()
        }
    }

    companion object {
        private const val INPUT_EDIT_TEXT_DEF = ""
    }
}