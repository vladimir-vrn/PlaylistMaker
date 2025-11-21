package com.practicum.playlistmaker.settings.ui

sealed interface SettingsState {
    data class Content(
        val nightMode: Boolean
    ) : SettingsState

    data class Error(
        val message: String
    ) : SettingsState
}