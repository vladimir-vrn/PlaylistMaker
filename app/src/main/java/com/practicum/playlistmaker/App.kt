package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App() : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        playListPrefs = getSharedPreferences(PLAY_LIST_MAKER_PREFERENCES, MODE_PRIVATE)
        darkTheme = playListPrefs.getBoolean(SWT_DARK_THEME_KEY, false)
        if (darkTheme != ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)) {
            switchTheme()
        }
    }

    fun switchTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        playListPrefs.edit()
            .putBoolean(SWT_DARK_THEME_KEY, darkTheme)
            .apply()
    }

    fun getPlayListPrefs(): SharedPreferences {
        return playListPrefs
    }

    companion object {
        private const val PLAY_LIST_MAKER_PREFERENCES = "play_list_maker_preferences"
        private const val SWT_DARK_THEME_KEY = "swt_dark_theme"
        private lateinit var playListPrefs: SharedPreferences
    }
}