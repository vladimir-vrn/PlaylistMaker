package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    fun getPlaylists(): Flow<List<PlayList>>

    suspend fun insertPlayList(playList: PlayList)

    suspend fun insertTrack(track: Track, playListId: Int)
}