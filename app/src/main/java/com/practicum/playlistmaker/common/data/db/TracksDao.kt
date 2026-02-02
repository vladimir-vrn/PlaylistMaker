package com.practicum.playlistmaker.common.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TracksDao {

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Query("DELETE FROM tracks WHERE trackId IN (:trackIds)")
    suspend fun deleteTracks(trackIds: List<Long>)

    @Query("SELECT trackId FROM favoriteTracks WHERE trackId IN (:trackIds)")
    suspend fun findFavoriteTracks(trackIds: List<Long>): List<Long>

    @Insert(entity = FavoriteTrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(favoriteTrackEntity: FavoriteTrackEntity)

    @Query("DELETE FROM favoriteTracks WHERE trackId = :trackId")
    suspend fun deleteFavoriteTrack(trackId: Long)

    @Query("SELECT tracks.trackId, tracks.name, tracks.artistName, tracks.collectionName, " +
            "tracks.releaseDate, tracks.primaryGenreName, tracks.country, tracks.time, " +
            "tracks.timeMillis, tracks.previewUrl, tracks.artworkUrl100 " +
            "FROM favoriteTracks " +
            "LEFT JOIN tracks ON favoriteTracks.trackId = tracks.trackId " +
            "ORDER BY timeOfAddition DESC")
    suspend fun getFavoriteTracks(): List<TrackEntity>
}