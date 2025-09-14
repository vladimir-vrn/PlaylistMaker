package com.practicum.playlistmaker

import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {

    private var inputEditTextValue = INPUT_EDIT_TEXT_DEF
    private val adapter = TracksAdapter(this@SearchActivity)
    private val adapterSearchHistory = TracksAdapter(this@SearchActivity)
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesSearch = retrofit.create<iTunesSearchApi>()
    private lateinit var inputEditText: EditText
    private lateinit var msgNothingWasFound: LinearLayout
    private lateinit var msgCommunicationProblems: LinearLayout
    private lateinit var vgSearchHistory: LinearLayout
    private lateinit var progressBar: CircularProgressIndicator
    private val searchRunnable = Runnable { findTracks() }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        adapter.searchHistory = SearchHistory((applicationContext as App).getPlayListPrefs())

        msgNothingWasFound = findViewById(R.id.msgNothingWasFound)
        msgCommunicationProblems = findViewById(R.id.msgCommunicationProblems)
        vgSearchHistory = findViewById(R.id.vgSearchHistory)
        progressBar = findViewById(R.id.progressBar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        val recyclerViewSearchHistory = findViewById<RecyclerView>(R.id.recyclerViewSearchHistory)
        recyclerViewSearchHistory.adapter = adapterSearchHistory

        val tbSearch = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.tb_search)
        tbSearch.setNavigationOnClickListener {
            finish()
        }

        val imgClear = findViewById<ImageView>(R.id.clearIcon)
        imgClear.setOnClickListener {
            val inputEditText = findViewById<EditText>(R.id.inputEditText)
            inputEditText.setText(INPUT_EDIT_TEXT_DEF)
            clearTracks()
        }

        inputEditText = findViewById(R.id.inputEditText)
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    inputEditTextValue = INPUT_EDIT_TEXT_DEF
                    imgClear.visibility = View.GONE
                    val imm = this@SearchActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                    clearTracks()
                } else {
                    inputEditTextValue = s.toString()
                    imgClear.visibility = View.VISIBLE
                    searchDebounce()
                }
                updateSearchHistory(inputEditText.hasFocus() && s.isNullOrEmpty())
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
            updateSearchHistory(hasFocus && inputEditText.text.isEmpty())
        }

        val btnUpdateSearch = findViewById<Button>(R.id.btnUpdateSearch)
        btnUpdateSearch.setOnClickListener(View.OnClickListener { findTracks() })

        val btnClearHistory = findViewById<Button>(R.id.btnClearHistory)
        btnClearHistory.setOnClickListener(View.OnClickListener {
            adapter.searchHistory?.clearTracks()
            updateSearchHistory(false)
        })
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

    private fun findTracks() {

        if (inputEditText.text.isNotEmpty()) {

            clearTracks()
            progressBar.visibility = View.VISIBLE
            iTunesSearch.search(inputEditText.text.toString()).enqueue(object : Callback<TracksResponse> {

                override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {

                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val resultsResponse = response.body()?.results
                        if (resultsResponse == null) {
                            msgCommunicationProblems.visibility = View.VISIBLE
                        } else if (resultsResponse.isNotEmpty()) {
                            adapter.tracks.addAll(resultsResponse)
                            adapter.notifyDataSetChanged()
                        } else {
                            msgNothingWasFound.visibility = View.VISIBLE
                        }
                    } else {
                        msgCommunicationProblems.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {

                    progressBar.visibility = View.GONE
                    msgCommunicationProblems.visibility = View.VISIBLE
                    t.printStackTrace()
                }
            })
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onStop() {
        super.onStop()
        adapter.searchHistory?.onSaveTracks()
    }

    private fun clearTracks() {

        msgNothingWasFound.visibility = View.GONE
        msgCommunicationProblems.visibility = View.GONE
        if (adapter.tracks.isNotEmpty()) {
            adapter.tracks.clear()
            adapter.notifyDataSetChanged()
        }
    }

    private fun updateSearchHistory(showHistory: Boolean) {

        adapterSearchHistory.tracks.clear()
        val tracksSearchHistory = adapter.searchHistory?.getTracks()
        if (!showHistory || tracksSearchHistory.isNullOrEmpty()) {
            vgSearchHistory.visibility = View.GONE
        } else {
            adapterSearchHistory.tracks.addAll(tracksSearchHistory)
            adapterSearchHistory.notifyDataSetChanged()
            vgSearchHistory.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val INPUT_EDIT_TEXT_VALUE = "INPUT_EDIT_TEXT_VALUE"
        private const val INPUT_EDIT_TEXT_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
