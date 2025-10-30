package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.domain.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import java.util.concurrent.Executors

val domainModule = module {

    single<TracksInteractor> {
        TracksInteractorImpl(
            get(),
            Executors.newCachedThreadPool()
        )
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> { params ->
        SharingInteractorImpl(
            get {
                parametersOf(params[0])
            }
        )
    }

}