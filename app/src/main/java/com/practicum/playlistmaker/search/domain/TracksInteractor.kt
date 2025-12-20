package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun search(expression: String): Flow<List<Track>?>
}