package com.practicum.playlistmaker.settings.data

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.ThemeSettings

class SettingsRepositoryImpl(
    private val themeStorageClient: StorageClient<ThemeSettings>
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        val response = themeStorageClient.getData()
        return if (response.resultCode == 200) response.data as ThemeSettings
        else ThemeSettings(false)
    }

    override fun updateThemeSetting(data: ThemeSettings) {
        themeStorageClient.storeData(data)
    }

    override fun switchTheme(nightMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (nightMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else AppCompatDelegate.MODE_NIGHT_NO
        )
        updateThemeSetting(ThemeSettings(nightMode))
    }
}