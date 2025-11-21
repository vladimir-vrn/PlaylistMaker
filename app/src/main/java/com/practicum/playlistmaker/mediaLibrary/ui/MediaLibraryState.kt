package com.practicum.playlistmaker.mediaLibrary.ui

sealed interface MediaLibraryState {

    object FavoritesMissing: MediaLibraryState

    object PlaylistsMissing: MediaLibraryState

    data class Error(
        val message: String
    ) : MediaLibraryState
}