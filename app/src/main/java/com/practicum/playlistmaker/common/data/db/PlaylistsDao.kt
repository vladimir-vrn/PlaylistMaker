package com.practicum.playlistmaker.common.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistsDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT playlists.id AS id, name, description, pathCoverFile, " +
            "IIF(playlistContent.trackId IS NULL, 0, playlistContent.trackId)  AS trackId " +
            "FROM playlists " +
            "LEFT JOIN playlistContent ON playlists.id = playlistContent.playListId " +
            "ORDER BY name")
    suspend fun getPlaylists(): List<PlaylistWithTracks>

    @Insert(entity = PlaylistContentEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistContent(playlistContentEntity: PlaylistContentEntity)
}