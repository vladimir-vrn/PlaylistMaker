package com.practicum.playlistmaker.mediaLibrary.ui

sealed interface PlaylistsState {
    class Content : PlaylistsState
}