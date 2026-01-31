package com.practicum.playlistmaker.common.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TracksDao {

    @Query("SELECT trackId from tracks WHERE trackId = :trackId")
    suspend fun findTrack(trackId: Long): List<Long>

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Query("DELETE from tracks WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)

    @Query("SELECT * FROM tracks ORDER BY timeOfAddition DESC")
    suspend fun getTracks(): List<TrackEntity>
}