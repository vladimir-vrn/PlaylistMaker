package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {

    fun load() : Flow<List<Track>>
    fun save(tracks: List<Track>)
    fun update(track: Track, tracks: MutableList<Track>)
}