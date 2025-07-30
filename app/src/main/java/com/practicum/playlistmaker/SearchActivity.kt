package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class SearchActivity : AppCompatActivity() {

    private var inputEditTextValue = INPUT_EDIT_TEXT_DEF
    private val adapter = TracksAdapter()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesSearch = retrofit.create<iTunesSearchApi>()
    private lateinit var msgNothingWasFound: LinearLayout
    private lateinit var msgCommunicationProblems: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        msgNothingWasFound = findViewById<LinearLayout>(R.id.msgNothingWasFound)
        msgCommunicationProblems = findViewById<LinearLayout>(R.id.msgCommunicationProblems)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter

        val tbSearch = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.tb_search)
        tbSearch.setNavigationOnClickListener {
            finish()
        }

        val imgClear = findViewById<ImageView>(R.id.clearIcon)
        imgClear.setOnClickListener {
            val inputEditText = findViewById<EditText>(R.id.inputEditText)
            inputEditText.setText(INPUT_EDIT_TEXT_DEF)
            clearTracks()
            msgNothingWasFound.visibility = View.GONE
            msgCommunicationProblems.visibility = View.GONE
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    clearTracks()
                    msgNothingWasFound.visibility = View.GONE
                    msgCommunicationProblems.visibility = View.GONE
                    inputEditTextValue = INPUT_EDIT_TEXT_DEF
                    imgClear.visibility = View.GONE
                    val imm = this@SearchActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
                } else {
                    inputEditTextValue = s.toString()
                    imgClear.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                findTracks(inputEditText.getText().toString())
                true
            }
            false
        }

        val btnUpdateSearch = findViewById<Button>(R.id.btnUpdateSearch)
        btnUpdateSearch.setOnClickListener(View.OnClickListener { findTracks(inputEditText.getText().toString()) })
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

    private fun findTracks(text: String) {

        if (text.isNotEmpty()) {
            iTunesSearch.search(text, "song").enqueue(object : Callback<TracksResponse> {

                override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {

                    msgNothingWasFound.visibility = View.GONE
                    msgCommunicationProblems.visibility = View.GONE
                    adapter.tracks.clear()

                    if (response.isSuccessful) {
                        val resultsResponse = response.body()?.results
                        if (resultsResponse == null) {
                            msgCommunicationProblems.visibility = View.VISIBLE
                        } else if (resultsResponse.isNotEmpty()) {
                            adapter.tracks.addAll(resultsResponse)
                        } else {
                            msgNothingWasFound.visibility = View.VISIBLE
                        }
                    } else {
                        msgCommunicationProblems.visibility = View.VISIBLE
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {

                    msgNothingWasFound.visibility = View.GONE
                    msgCommunicationProblems.visibility = View.VISIBLE
                    t.printStackTrace()
                }
            })
        }

    }

    private fun clearTracks() {
        if (adapter.tracks.isNotEmpty()) {
            adapter.tracks.clear()
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        private const val INPUT_EDIT_TEXT_VALUE = "INPUT_EDIT_TEXT_VALUE"
        private const val INPUT_EDIT_TEXT_DEF = ""
    }
}
