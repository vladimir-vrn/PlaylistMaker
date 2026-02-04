package com.practicum.playlistmaker.playlists.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override fun getPlaylists(): Flow<List<PlayList>> {
        return repository.getPlaylists()
    }

    override fun getPlaylistTracks(playListId: Long): Flow<List<Track>> {
        return repository.getPlaylistTracks(playListId)
    }

    override suspend fun insertPlayList(playList: PlayList) {
        repository.insertPlayList(playList)
    }

    override suspend fun deletePlayList(playListId: Long) {
        repository.deletePlayList(playListId)
    }

    override suspend fun deletePlayListContent(playListId: Long) {
        repository.deletePlayListContent(playListId)
    }

    override suspend fun insertTrack(track: Track, playListId: Long) {
        repository.insertTrack(track, playListId)
    }

    override suspend fun deleteTrack(trackId: Long, playListId: Long) {
        repository.deleteTrack(trackId, playListId)
    }

    override suspend fun deleteTracksWithoutPlaylists(playListId: Long) {
        repository.deleteTracksWithoutPlaylists(playListId)
    }
}