package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    private var darkTheme = false
    private lateinit var playListPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        playListPrefs = getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES, MODE_PRIVATE)
        val darkThemePrefs = playListPrefs.getBoolean(SWT_DARK_THEME_KEY, false)
        if (darkThemePrefs != ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)) {
            switchTheme(darkThemePrefs)
        }
        if (darkThemePrefs != darkTheme) darkTheme = darkThemePrefs
    }

    fun switchTheme(turnDarkTheme: Boolean) {
        darkTheme = turnDarkTheme
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else AppCompatDelegate.MODE_NIGHT_NO
        )
        playListPrefs.edit()
            .putBoolean(SWT_DARK_THEME_KEY, darkTheme)
            .apply()
    }

    fun getDarkTheme(): Boolean {
        return darkTheme
    }

    fun getPlayListPrefs(): SharedPreferences {
        return playListPrefs
    }

    companion object {
        private const val PLAY_LIST_MAKER_PREFERENCES = "play_list_maker_preferences"
        private const val SWT_DARK_THEME_KEY = "swt_dark_theme"
    }
}