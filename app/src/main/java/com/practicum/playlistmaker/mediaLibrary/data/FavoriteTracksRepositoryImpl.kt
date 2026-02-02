package com.practicum.playlistmaker.mediaLibrary.data

import com.practicum.playlistmaker.common.data.db.FavoriteTrackEntity
import com.practicum.playlistmaker.common.data.db.PlaylistsDao
import com.practicum.playlistmaker.common.data.db.TrackEntity
import com.practicum.playlistmaker.common.data.db.TracksDao
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.mediaLibrary.domain.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.Long

class FavoriteTracksRepositoryImpl(
    private val tracksDao: TracksDao,
    private val playlistsDao: PlaylistsDao,
) : FavoriteTracksRepository {

    override fun getTracks(): Flow<List<Track>> = flow {
        emit(
            tracksDao.getFavoriteTracks().map {
                Track(
                    it.trackId,
                    it.name,
                    it.artistName,
                    it.time,
                    it.timeMillis,
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
        emit(tracksDao.findFavoriteTracks(listOf(trackId)).isNotEmpty())
    }

    override suspend fun insertTrack(track: Track) {
        tracksDao.insertFavoriteTrack(
            FavoriteTrackEntity(
                track.trackId,
                System.currentTimeMillis(),
            )
        )
        tracksDao.insertTrack(
            TrackEntity(
                track.trackId,
                track.trackName,
                track.artistName,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.trackTime,
                track.trackTimeMillis,
                track.previewUrl,
                track.artworkUrl100
            )
        )
    }

    override suspend fun deleteTrack(trackId: Long) {
        tracksDao.deleteFavoriteTrack(trackId)
        if (playlistsDao.getTracksWithoutPlaylists(listOf(trackId)).contains(trackId))
            tracksDao.deleteTracks(listOf(trackId))
    }
}