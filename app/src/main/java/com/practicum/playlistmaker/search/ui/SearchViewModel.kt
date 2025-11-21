package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksInteractor

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    context: Context
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>(
        SearchState.Content(
            emptyList(),
            true
        )
    )
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var lastSearchText = ""
    private val searchRunnable = Runnable { findTracks() }
    private val handler = Handler(Looper.getMainLooper())

    private val errorMsg = context.getString(R.string.communication_problems)
    private val emptyMsg = context.getString(R.string.nothing_was_found)


    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

    fun search(searchText: String) {
        if (searchText.isNotEmpty()) {
            handler.removeCallbacks(searchRunnable)
            lastSearchText = searchText.trim()
            findTracks()
        }
    }
    fun searchDebounce(searchText: String) {
        if (searchText.isNotEmpty()) {
            handler.removeCallbacks(searchRunnable)
            lastSearchText = searchText.trim()
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    fun loadHistory() {
        handler.removeCallbacks(searchRunnable)
        renderState(
            SearchState.Content(
                searchHistoryInteractor.load(),
                true
            )
        )
    }

    fun updateHistory(track: Track) {
        val tracks = searchHistoryInteractor.load().toMutableList()
        searchHistoryInteractor.update(track, tracks)
        searchHistoryInteractor.save(tracks)
    }

    fun clearHistory() {
        searchHistoryInteractor.save(emptyList())
        renderState(
            SearchState.Content(
                emptyList(),
                true
            )
        )
    }

    private fun findTracks() {

        renderState(SearchState.Loading)

        tracksInteractor.search(
            lastSearchText
        ) { foundTracks ->
            when {
                foundTracks == null -> renderState(
                    SearchState.Error(errorMsg)
                )
                foundTracks.isEmpty() -> renderState(
                    SearchState.Empty(emptyMsg)
                )
                else -> renderState(
                    SearchState.Content(
                        foundTracks, false
                    )
                )
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    companion object {

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}