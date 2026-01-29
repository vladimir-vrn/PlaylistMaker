package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    fun getPlaylists(): Flow<List<PlayList>>

    suspend fun insertPlayList(playList: PlayList)

    suspend fun deletePlayList(playListId: Int)

    fun getPlaylistTracks(playListId: Int): Flow<List<Track>>

    fun getTrackPlaylists(trackId: Long): Flow<List<PlayList>>

    suspend fun insertTrack(track: Track, playListId: Int)

    suspend fun deleteTrack(trackId: Long, playListId: Int)

    fun findTrack(trackId: Long, playListId: Int): Flow<Boolean>
}