package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val repository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override fun findTrack(trackId: Long): Flow<List<Long>> {
        return repository.findTrack(trackId)
    }

    override suspend fun insertTrack(track: Track) {
        repository.insertTrack(track)
    }

    override suspend fun deleteTrack(trackId: Long) {
        repository.deleteTrack(trackId)
    }
}