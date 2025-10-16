package com.practicum.playlistmaker.settings.ui

import android.app.Application
import android.content.res.Configuration
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val settingsInteractor = Creator.providerSettingsInteractor(this)
        val themeSettings = settingsInteractor.getThemeSettings()
        if (themeSettings.nightMode != ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)) {
            settingsInteractor.switchTheme(themeSettings.nightMode)
        }
    }
}