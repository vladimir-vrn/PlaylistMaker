package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override fun getTracks(): Flow<List<Track>> {
        return repository.getTracks()
    }

    override fun findTrack(trackId: Long): Flow<Boolean> {
        return repository.findTrack(trackId)
    }

    override suspend fun insertTrack(track: Track) {
        repository.insertTrack(track)
    }

    override suspend fun deleteTrack(trackId: Long) {
        repository.deleteTrack(trackId)
    }
}