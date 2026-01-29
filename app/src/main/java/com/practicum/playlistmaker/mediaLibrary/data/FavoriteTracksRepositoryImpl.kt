package com.practicum.playlistmaker.mediaLibrary.data

import com.practicum.playlistmaker.common.data.db.PlaylistEntity
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.mediaLibrary.domain.FavoriteTracksRepository
import com.practicum.playlistmaker.mediaLibrary.domain.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlin.Long

class FavoriteTracksRepositoryImpl(
    private val playlistsRepository: PlaylistsRepository
) : FavoriteTracksRepository {

    override fun getTracks(): Flow<List<Track>> {
        return playlistsRepository
            .getPlaylistTracks(PlaylistEntity.FAVORITES_PLAY_LIST_ID)
    }

    override fun findTrack(trackId: Long): Flow<Boolean> {
        return playlistsRepository.findTrack(trackId, PlaylistEntity.FAVORITES_PLAY_LIST_ID)
    }

    override suspend fun insertTrack(track: Track) {
        playlistsRepository.insertTrack(
            track,
            PlaylistEntity.FAVORITES_PLAY_LIST_ID
        )
    }

    override suspend fun deleteTrack(trackId: Long) {
        playlistsRepository.deleteTrack(
            trackId,
            PlaylistEntity.FAVORITES_PLAY_LIST_ID
        )
    }


}