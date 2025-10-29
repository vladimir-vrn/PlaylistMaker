package com.practicum.playlistmaker.main.ui

sealed interface MediaLibraryActivityState {

    class Content : MediaLibraryActivityState

    data class Error(
        val message: String
    ) : MediaLibraryActivityState
}