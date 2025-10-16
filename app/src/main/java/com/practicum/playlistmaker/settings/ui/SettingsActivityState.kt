package com.practicum.playlistmaker.settings.ui

sealed interface SettingsActivityState {
    data class Content(
        val nightMode: Boolean
    ) : SettingsActivityState

    data class Error(
        val errorMessage: String
    ) : SettingsActivityState
}