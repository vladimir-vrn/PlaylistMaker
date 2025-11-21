package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<SettingsState>(
        SettingsState.Content(
            settingsInteractor.getThemeSettings().nightMode
        )
    )
    fun observeState(): LiveData<SettingsState> = stateLiveData

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun switchTheme(nightMode: Boolean) {
        stateLiveData.postValue(
            SettingsState.Content(nightMode)
        )
        settingsInteractor.switchTheme(nightMode)
    }

}