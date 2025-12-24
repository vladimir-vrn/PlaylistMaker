package com.practicum.playlistmaker.mediaLibrary.ui

import com.practicum.playlistmaker.common.domain.Track

sealed interface FavoritesState {

    data class Content(
        val tracks: List<Track> = listOf()
    ): FavoritesState

    data class Error(
        val message: String
    ) : FavoritesState
}