package com.practicum.playlistmaker.mediaLibrary.ui

sealed interface MediaLibraryState {

    data class Content(
        val currentItem: Int
    ): MediaLibraryState

    data class Error(
        val message: String
    ): MediaLibraryState
}