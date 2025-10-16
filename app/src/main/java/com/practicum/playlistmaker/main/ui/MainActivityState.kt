package com.practicum.playlistmaker.main.ui

sealed interface MainActivityState {

    class Content : MainActivityState

    data class Error(
        val errorMessage: String
    ) : MainActivityState
}