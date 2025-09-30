package com.practicum.playlistmaker.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.TracksInteractor

class SearchActivity : AppCompatActivity() {

    private val tracksInteractor = Creator.provideTracksInteractor()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this@SearchActivity)
    private val tracks = mutableListOf<Track>()
    private val tracksHistory = mutableListOf<Track>()
    private lateinit var adapter: TracksAdapter
    private lateinit var inputEditText: EditText
    private lateinit var msgNothingWasFound: LinearLayout
    private lateinit var msgCommunicationProblems: LinearLayout
    private lateinit var txtHistory: TextView
    private lateinit var btnClearHistory: Button
    private lateinit var progressBar: CircularProgressIndicator
    private val searchRunnable = Runnable { findTracks() }
    private val handler = Handler(Looper.getMainLooper())
    private val gson = Gson()
    private var inputEditTextValue = INPUT_EDIT_TEXT_DEF
    private var wasInputEditTextEmpty = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        tracksHistory.addAll(searchHistoryInteractor.load())

        msgNothingWasFound = findViewById(R.id.msgNothingWasFound)
        msgCommunicationProblems = findViewById(R.id.msgCommunicationProblems)
        progressBar = findViewById(R.id.progressBar)
        txtHistory = findViewById(R.id.txtHistory)
        btnClearHistory = findViewById(R.id.btnClearHistory)
        btnClearHistory.setOnClickListener(View.OnClickListener {
            tracksHistory.clear()
            showSearchHistory()
        })
        val btnUpdateSearch = findViewById<Button>(R.id.btnUpdateSearch)
        btnUpdateSearch.setOnClickListener(View.OnClickListener { findTracks() })
        val tbSearch = findViewById<MaterialToolbar>(R.id.tb_search)
        tbSearch.setNavigationOnClickListener {
            finish()
        }

        adapter = TracksAdapter(
            tracks,
            TracksAdapter.OnItemClickListener { position ->
                val intentPlayerActivity = Intent(this@SearchActivity, PlayerActivity::class.java)
                val trackJsonString = gson.toJson(tracks[position])
                intentPlayerActivity.putExtra("track", trackJsonString)
                startActivity(intentPlayerActivity)
                searchHistoryInteractor.update(tracks[position], tracksHistory)
            }
        )
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter

        inputEditText = findViewById(R.id.inputEditText)
        val imgClear = findViewById<ImageView>(R.id.clearIcon)
        imgClear.setOnClickListener {
            inputEditText.setText(INPUT_EDIT_TEXT_DEF)
            showSearchHistory()
        }
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                wasInputEditTextEmpty = s.isNullOrEmpty()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    inputEditTextValue = INPUT_EDIT_TEXT_DEF
                    imgClear.visibility = View.GONE
                    val imm = this@SearchActivity.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                    showSearchHistory()
                } else {
                    inputEditTextValue = s.toString()
                    imgClear.visibility = View.VISIBLE
                    searchDebounce()
                    if (wasInputEditTextEmpty) {
                        wasInputEditTextEmpty = false
                        showSearchHistory()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                findTracks()
                true
            }
            false
        }
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            showSearchHistory()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        inputEditText.setText(savedInstanceState.getString(INPUT_EDIT_TEXT_VALUE, INPUT_EDIT_TEXT_DEF))
        if (inputEditText.text.isNotEmpty()) {
            inputEditText.setSelection(inputEditText.text.length)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(INPUT_EDIT_TEXT_VALUE, inputEditTextValue)
    }

    override fun onStop() {
        super.onStop()
        searchHistoryInteractor.save(tracksHistory)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    private fun findTracks() {

        if (inputEditText.text.isNotEmpty()) {

            showSearchHistory()
            progressBar.visibility = View.VISIBLE

            tracksInteractor.search(
                inputEditText.text.toString(),
                TracksInteractor.TracksConsumer { foundTracks ->
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        if (foundTracks == null) {
                            msgCommunicationProblems.visibility = View.VISIBLE
                        } else if (foundTracks.isEmpty()) {
                            msgNothingWasFound.visibility = View.VISIBLE
                        } else {
                            tracks.addAll(foundTracks)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            )
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showSearchHistory() {

        msgNothingWasFound.visibility = View.GONE
        msgCommunicationProblems.visibility = View.GONE
        btnClearHistory.visibility = View.GONE
        txtHistory.visibility = View.GONE

        if (tracks.isNotEmpty()) {
            tracks.clear()
            adapter.notifyDataSetChanged()
        }
        if (inputEditText.text.isEmpty() && inputEditText.hasFocus() && tracksHistory.isNotEmpty()) {
            btnClearHistory.visibility = View.VISIBLE
            txtHistory.visibility = View.VISIBLE
            tracks.addAll(tracksHistory)
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        private const val INPUT_EDIT_TEXT_VALUE = "INPUT_EDIT_TEXT_VALUE"
        private const val INPUT_EDIT_TEXT_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}