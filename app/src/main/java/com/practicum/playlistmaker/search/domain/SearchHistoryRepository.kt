package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {

    fun load() : Flow<List<Track>>
    fun save(tracks: List<Track>)

}