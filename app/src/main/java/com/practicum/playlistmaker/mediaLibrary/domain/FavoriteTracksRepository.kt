package com.practicum.playlistmaker.mediaLibrary.domain

import com.practicum.playlistmaker.common.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    fun getTracks(): Flow<List<Track>>

    fun findTrack(trackId: Long): Flow<Boolean>

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(trackId: Long)
}