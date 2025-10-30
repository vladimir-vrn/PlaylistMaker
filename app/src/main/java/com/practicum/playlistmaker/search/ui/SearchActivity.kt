package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TracksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.observeState().observe(this) {
            render(it)
        }

        binding.btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }
        binding.btnUpdateSearch.setOnClickListener {
            viewModel.search(binding.inputEditText.text.toString())
        }
        binding.tbSearch.setNavigationOnClickListener { finish() }

        adapter = TracksAdapter { position ->
            val intentPlayerActivity = Intent(
                this@SearchActivity, PlayerActivity::class.java
            )
            intentPlayerActivity.putExtra("track", adapter.tracks[position])
            startActivity(intentPlayerActivity)
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
                    val imm = this@SearchActivity.getSystemService(
                        INPUT_METHOD_SERVICE
                    ) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(
                        binding.inputEditText.windowToken, 0
                    )
                    viewModel.loadHistory()
                } else {
                    binding.clearIcon.visibility = View.VISIBLE
                    viewModel.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher)
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(binding.inputEditText.text.toString())
                true
            }
            false
        }
        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            viewModel.loadHistory()
        }
    }

    override fun onResume() {
        super.onResume()
        if (editTextInFocus()) {
            viewModel.loadHistory()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showTracks(tracks: List<Track>) {
        binding.apply {
            msgNothingWasFound.visibility = View.GONE
            msgCommunicationProblems.visibility = View.GONE
            btnClearHistory.visibility = View.GONE
            txtHistory.visibility = View.GONE
            progressBar.visibility = View.GONE

            adapter.tracks.clear()
            adapter.tracks.addAll(tracks)
            adapter.notifyDataSetChanged()
        }
    }

    private fun editTextInFocus(): Boolean = binding.inputEditText.text.isEmpty() &&
            binding.inputEditText.hasFocus()

    @SuppressLint("NotifyDataSetChanged")
    private fun showSearchHistory(tracks: List<Track>) {

        binding.apply {
            msgNothingWasFound.visibility = View.GONE
            msgCommunicationProblems.visibility = View.GONE
            btnClearHistory.visibility =
                if (editTextInFocus() && tracks.isNotEmpty()) View.VISIBLE else View.GONE
            txtHistory.visibility =
                if (editTextInFocus() && tracks.isNotEmpty()) View.VISIBLE else View.GONE
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
            msgNothingWasFound.visibility = View.VISIBLE
            msgCommunicationProblems.visibility = View.GONE
            btnClearHistory.visibility = View.GONE
            txtHistory.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun render(state: SearchActivityState) {
        when (state) {
            is SearchActivityState.Loading -> showLoading()
            is SearchActivityState.Content ->
                if (state.isSearchHistory) showSearchHistory(state.tracks)
                else showTracks(state.tracks)
            is SearchActivityState.Error -> showError()
            is SearchActivityState.Empty -> showEmpty()
        }
    }

    companion object {
        private const val INPUT_EDIT_TEXT_DEF = ""
    }
}