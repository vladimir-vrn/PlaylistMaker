package com.practicum.playlistmaker.mediaLibrary.data

import com.practicum.playlistmaker.common.data.db.AppDatabase
import com.practicum.playlistmaker.common.data.db.TrackEntity
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.mediaLibrary.domain.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Long

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavoriteTracksRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val tracksEntity = appDatabase.tracksDao().getTracks()
        emit(
            tracksEntity.map {
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

    override fun findTrack(trackId: Long): Flow<List<Long>> = flow {
        emit(appDatabase.tracksDao().findTrack(trackId))
    }

    override suspend fun insertTrack(track: Track) {
        appDatabase.tracksDao().insertTrack(
            TrackEntity(
                track.trackId,
                appDatabase.tracksDao().getMaxId()[0] + 1,
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
        appDatabase.tracksDao().deleteTrack(trackId)
    }


}