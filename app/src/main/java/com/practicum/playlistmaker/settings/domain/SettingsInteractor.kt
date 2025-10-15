package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(data: ThemeSettings)

    fun switchTheme(darkTheme: Boolean)
}