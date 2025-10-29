package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.iTunesSearchApi
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.PrefsStorageClient
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.data.StorageClient
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.ThemeSettings
import com.practicum.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import kotlin.collections.List

val dataModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            androidContext(),
            get()
        )
    }

    single<iTunesSearchApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<iTunesSearchApi>()
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            get<StorageClient<List<Track>>>(
                named("searchHistoryStorage")
            )
        )
    }

    single<StorageClient<List<Track>>>(
        named("searchHistoryStorage")
    ) {
        PrefsStorageClient(
            "search_history_tracks",
            object : TypeToken<List<Track>>() {}.type,
            get(),
            get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            get<StorageClient<ThemeSettings>>(
                named("themeSettingsStorage")
            )
        )
    }

    single<StorageClient<ThemeSettings>>(
        named("themeSettingsStorage")
    ) {
        PrefsStorageClient(
            "theme_settings",
            object : TypeToken<ThemeSettings>() {}.type,
            get(),
            get()
        )
    }

    single {
        androidContext().getSharedPreferences(
            "play_list_maker_preferences",
            Context.MODE_PRIVATE
        )
    }

    factory {
        Gson()
    }

    single<ExternalNavigator> { params ->
        ExternalNavigatorImpl(params[0])
    }

    factory {
        MediaPlayer()
    }
}