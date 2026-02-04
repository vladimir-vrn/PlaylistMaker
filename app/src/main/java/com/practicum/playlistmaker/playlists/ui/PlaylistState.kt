package com.practicum.playlistmaker.playlists.ui

import com.practicum.playlistmaker.playlists.domain.PlayList

sealed interface PlaylistState {

    data class Content(
        val playList: PlayList,
    ) : PlaylistState

    data class Error(
        val message: String
    ) : PlaylistState
}