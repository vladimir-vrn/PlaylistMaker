package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.common.domain.Track

sealed interface SearchState {

    object Loading: SearchState

    data class Content(
        val tracks: List<Track> = listOf(),
        val isSearchHistory: Boolean = false,
        val isInputEditTextHasFocus: Boolean = false,
    ) : SearchState

    data class Error(
        val message: String
    ) : SearchState

    data class Empty(
        val message: String
    ) : SearchState
}