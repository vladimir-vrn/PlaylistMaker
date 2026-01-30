package com.practicum.playlistmaker.mediaLibrary.data

import com.practicum.playlistmaker.common.data.db.TrackEntity
import com.practicum.playlistmaker.common.data.db.TracksDao
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.mediaLibrary.domain.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Long

class FavoriteTracksRepositoryImpl(
    private val tracksDao: TracksDao
) : FavoriteTracksRepository {

    override fun getTracks(): Flow<List<Track>> = flow {
        emit(
            tracksDao.getTracks().map {
                Track(
                    it.trackId,
                    it.name,
                    it.artistName,
                    it.time,
                    it.artworkUrl100,
                    it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl,
                )
            }
        )
    }

    override fun findTrack(trackId: Long): Flow<Boolean> = flow {
        emit(tracksDao.findTrack(trackId).isNotEmpty())
    }

    override suspend fun insertTrack(track: Track) {
        tracksDao.insertTrack(
            TrackEntity(
                track.trackId,
                System.currentTimeMillis(),
                track.trackName,
                track.artistName,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.trackTime,
                track.previewUrl,
                track.artworkUrl100
            )
        )
    }

    override suspend fun deleteTrack(trackId: Long) {
        tracksDao.deleteTrack(trackId)
    }
}