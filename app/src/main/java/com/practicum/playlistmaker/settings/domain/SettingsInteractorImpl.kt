package com.practicum.playlistmaker.settings.domain

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSetting(data: ThemeSettings) {
        repository.updateThemeSetting(data)
    }

    override fun switchTheme(darkTheme: Boolean) {
        repository.switchTheme(darkTheme)
    }

}
