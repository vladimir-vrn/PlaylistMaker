package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.search.domain.Track

sealed interface PlayerState {

    data class Content(
        val track: Track
    ) : PlayerState

    data class Error(
        val message: String
    ) : PlayerState

    data class Empty(
        val message: String
    ) : PlayerState
}