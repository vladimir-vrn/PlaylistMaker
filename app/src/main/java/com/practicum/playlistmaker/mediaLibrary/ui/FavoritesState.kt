package com.practicum.playlistmaker.mediaLibrary.ui

sealed interface FavoritesState {
    class Content: FavoritesState
}