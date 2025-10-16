package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.search.domain.Track

sealed interface PlayerActivityState {

    data class Content(
        val track: Track
    ) : PlayerActivityState

    data class Error(
        val errorMessage: String
    ) : PlayerActivityState

    data class Empty(
        val message: String
    ) : PlayerActivityState
}