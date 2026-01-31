package com.practicum.playlistmaker.mediaLibrary.ui

sealed interface PlaylistState {

    data class Content(
        val name: String = "",
        val description: String = "",
        val coverUri: String = "",
    ) : PlaylistState

    data class Error(
        val message: String
    ) : PlaylistState
}