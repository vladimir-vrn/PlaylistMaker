package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.Track

sealed interface SearchState {

    object Loading: SearchState

    data class Content(
        val tracks: List<Track>,
        val isSearchHistory: Boolean,
    ) : SearchState

    data class Error(
        val message: String
    ) : SearchState

    data class Empty(
        val message: String
    ) : SearchState
}