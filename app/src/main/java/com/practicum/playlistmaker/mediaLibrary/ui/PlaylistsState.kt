package com.practicum.playlistmaker.mediaLibrary.ui

import com.practicum.playlistmaker.mediaLibrary.domain.PlayList

sealed interface PlaylistsState {

    data class Content(
        val playLists: List<PlayList> = listOf(),
        val clickedAddPlaylist: Boolean = false,
    ) : PlaylistsState

    data class Error(
        val message: String
    ) : PlaylistsState
}