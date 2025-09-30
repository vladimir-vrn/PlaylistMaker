package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.RetrofitNetworkClient
import com.practicum.playlistmaker.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.TracksRepositoryImpl
import com.practicum.playlistmaker.domain.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.SearchHistoryRepository
import com.practicum.playlistmaker.domain.TracksInteractor
import com.practicum.playlistmaker.domain.TracksInteractorImpl
import com.practicum.playlistmaker.domain.TracksRepository

object Creator {

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }
}