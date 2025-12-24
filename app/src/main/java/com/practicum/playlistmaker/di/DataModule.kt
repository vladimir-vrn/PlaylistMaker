package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.common.data.NetworkClient
import com.practicum.playlistmaker.common.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.common.data.iTunesSearchApi
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.common.data.PrefsStorageClient
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.common.data.StorageClient
import com.practicum.playlistmaker.common.data.db.AppDatabase
import com.practicum.playlistmaker.mediaLibrary.data.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.mediaLibrary.domain.FavoriteTracksRepository
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

    factory<TracksRepository> {
        TracksRepositoryImpl(
            get()
        )
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

    factory<SearchHistoryRepository> {
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

    factory<SettingsRepository> {
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

    factory<ExternalNavigator> { params ->
        ExternalNavigatorImpl(params[0])
    }

    factory {
        MediaPlayer()
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database.db")
            .build()
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get())
    }
}