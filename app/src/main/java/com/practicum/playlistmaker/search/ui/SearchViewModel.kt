package com.practicum.playlistmaker.search.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractor
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.search.domain.TracksInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    context: Context
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>(SearchState.Content())
    fun observeState(): LiveData<SearchState> = stateLiveData

    private var lastSearchText = ""
    private var findTracksJob: Job? = null

    private val errorMsg = context.getString(R.string.communication_problems)
    private val emptyMsg = context.getString(R.string.nothing_was_found)

    fun search(searchText: String, searchDebounce: Boolean = true) {
        if (searchText.isNotEmpty()) {
            findTracksJob?.cancel()
            if (searchText.trim() != lastSearchText) {
                lastSearchText = searchText.trim()
                if (stateLiveData.value is SearchState.Content &&
                    (stateLiveData.value as SearchState.Content).isSearchHistory)
                    renderState(
                        SearchState.Content(
                            isSearchHistory = false,
                            isInputEditTextHasFocus = true,
                        )
                    )
                if (searchDebounce) findTracksJob =
                    viewModelScope.launch {
                        delay(SEARCH_DEBOUNCE_DELAY)
                        findTracks()
                    }
                else findTracks()
            }
        }
    }

    fun loadHistory() {
        lastSearchText = ""
        findTracksJob?.cancel()
        viewModelScope.launch {
            searchHistoryInteractor
                .load()
                .collect { foundTracks ->
                    renderState(
                        SearchState.Content(
                            foundTracks,
                            isSearchHistory = true,
                            isInputEditTextHasFocus = true,
                        )
                    )
                }
        }
    }

    fun updateHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor
                .load()
                .collect { foundTracks ->
                    val tracks = foundTracks.toMutableList()
                    searchHistoryInteractor.update(track, tracks)
                    searchHistoryInteractor.save(tracks)
                }
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.save(emptyList())
        renderState(
            SearchState.Content(
                isSearchHistory = true,
                isInputEditTextHasFocus = true,
            )
        )
    }

    private fun findTracks() {

        renderState(SearchState.Loading)

        viewModelScope.launch {
            tracksInteractor
                .search(lastSearchText)
                .collect { foundTracks ->
                    when {
                        foundTracks == null -> renderState(
                            SearchState.Error(errorMsg)
                        )
                        foundTracks.isEmpty() -> renderState(
                            SearchState.Empty(emptyMsg)
                        )
                        else -> renderState(
                            SearchState.Content(
                                foundTracks,
                                isSearchHistory = false,
                                isInputEditTextHasFocus = true,
                            )
                        )
                    }
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