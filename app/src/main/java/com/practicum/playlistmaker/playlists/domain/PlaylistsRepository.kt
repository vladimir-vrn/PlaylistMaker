package com.practicum.playlistmaker.playlists.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    fun getPlaylists(): Flow<List<PlayList>>

    fun getPlaylistTracks(playListId: Long): Flow<List<Track>>

    suspend fun insertPlayList(playList: PlayList)

    suspend fun deletePlayList(playListId: Long)

    suspend fun deletePlayListContent(playListId: Long)

    suspend fun insertTrack(track: Track, playListId: Long)

    suspend fun deleteTrack(trackId: Long, playListId: Long)

    suspend fun deleteTracksWithoutPlaylists(playListId: Long)
}