package com.practicum.playlistmaker.settings.ui

import android.app.Application
import android.content.res.Configuration
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.domainModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class App : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                domainModule,
                dataModule,
                viewModelModule,
            )
        }

        val settingsInteractor by inject<SettingsInteractor>()
        val themeSettings = settingsInteractor.getThemeSettings()
        if (themeSettings.nightMode != ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)) {
            settingsInteractor.switchTheme(themeSettings.nightMode)
        }
    }
}