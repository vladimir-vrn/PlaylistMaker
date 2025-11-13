package com.practicum.playlistmaker.mediaLibrary.ui

sealed interface MediaLibraryActivityState {

    object FavoritesMissing: MediaLibraryActivityState

    object PlaylistsMissing: MediaLibraryActivityState

    data class Error(
        val message: String
    ) : MediaLibraryActivityState
}