package com.practicum.playlistmaker

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

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl()
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }
}