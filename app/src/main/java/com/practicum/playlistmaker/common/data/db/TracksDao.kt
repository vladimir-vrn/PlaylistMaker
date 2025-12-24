package com.practicum.playlistmaker.common.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TracksDao {

    @Query("SELECT IIF(MAX(id) IS NULL, 0, MAX(id)) as maxId FROM tracks")
    suspend fun getMaxId(): List<Long>

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Query("DELETE from tracks WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)

    @Query("SELECT * FROM tracks ORDER BY id DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM tracks")
    suspend fun getTrackIds(): List<Long>
}