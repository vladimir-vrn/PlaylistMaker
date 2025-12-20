package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

    fun search(expression: String): Flow<List<Track>?>
}