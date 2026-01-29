package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override fun getPlaylists(): Flow<List<PlayList>> {
        return repository.getPlaylists()
    }

    override suspend fun insertPlayList(playList: PlayList) {
        repository.insertPlayList(playList)
    }

    override suspend fun deletePlayList(playListId: Int) {
        repository.deletePlayList(playListId)
    }

    override fun findTrack(trackId: Long, playListId: Int): Flow<Boolean> {
        return repository.findTrack(trackId, playListId)
    }

    override suspend fun insertTrack(track: Track, playListId: Int) {
        repository.insertTrack(track, playListId)
    }
}