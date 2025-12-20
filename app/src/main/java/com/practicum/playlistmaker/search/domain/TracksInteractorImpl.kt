package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun search(expression: String): Flow<List<Track>?> {
        return repository.search(expression)
    }
}