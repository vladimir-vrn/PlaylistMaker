package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.Track

sealed interface SearchActivityState {

    object Loading: SearchActivityState

    data class Content(
        val tracks: List<Track>,
        val isSearchHistory: Boolean,
    ) : SearchActivityState

    data class Error(
        val errorMessage: String
    ) : SearchActivityState

    data class Empty(
        val message: String
    ) : SearchActivityState
}