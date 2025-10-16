package com.practicum.playlistmaker.settings.domain

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(data: ThemeSettings)
    fun switchTheme(darkTheme: Boolean)
}