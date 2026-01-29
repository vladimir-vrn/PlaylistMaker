package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    fun getPlaylists(): Flow<List<PlayList>>

    suspend fun insertPlayList(playList: PlayList)

    suspend fun deletePlayList(playListId: Int)

    fun findTrack(trackId: Long, playListId: Int): Flow<Boolean>

    suspend fun insertTrack(track: Track, playListId: Int)
}