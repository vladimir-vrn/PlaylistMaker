package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.mediaLibrary.ui.FavoritesViewModel
import com.practicum.playlistmaker.mediaLibrary.ui.MediaLibraryViewModel
import com.practicum.playlistmaker.mediaLibrary.ui.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(
            get(),
            get(),
            androidContext()
        )
    }

    viewModel { (context: Context) ->
        SettingsViewModel(
            get {
                parametersOf(context)
            },
            get()
        )
    }

    viewModel {
        MediaLibraryViewModel()
    }

    viewModel { (track: Track?) ->
        PlayerViewModel(
            track,
            androidContext(),
            get()
        )
    }

    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}