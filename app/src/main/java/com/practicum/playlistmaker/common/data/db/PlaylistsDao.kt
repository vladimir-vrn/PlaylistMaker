package com.practicum.playlistmaker.common.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlaylistsDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlayList(playlistId: Long)

    @Query("SELECT playlists.id AS id, name, description, pathCoverFile, " +
            "IIF(playListData.numTracks IS NULL, 0, playListData.numTracks) AS numTracks,  " +
            "IIF(playListData.totalTime IS NULL, 0, playListData.totalTime) AS totalTime  " +
            "FROM playlists " +
            "LEFT JOIN (" +
                "SELECT playListId , COUNT(*) as numTracks, " +
                "SUM(IIF(tracks.timeMillis IS NULL, 0, tracks.timeMillis)) AS totalTime " +
                "FROM playlistContent " +
                "LEFT JOIN tracks ON playlistContent.trackId = tracks.trackId " +
                "GROUP BY playListId " +
            ") AS playListData ON playlists.id = playListData.playListId " +
            "ORDER BY name")
    suspend fun getPlaylists(): List<PlaylistWithTotals>

    @Insert(entity = PlaylistContentEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistContent(playlistContentEntity: PlaylistContentEntity)

    @Query("DELETE FROM playlistContent WHERE playListId = :playlistId AND (:trackId = 0 OR trackId = :trackId)")
    suspend fun deletePlayListContent(playlistId: Long, trackId: Long = 0)

    @Query("SELECT tracks.trackId, tracks.name, tracks.artistName, tracks.collectionName, " +
            "tracks.releaseDate, tracks.primaryGenreName, tracks.country, tracks.time, " +
            "tracks.timeMillis, tracks.previewUrl, tracks.artworkUrl100 " +
            "FROM playlistContent " +
            "LEFT JOIN tracks ON playlistContent.trackId = tracks.trackId " +
            "WHERE playlistContent.playListId = :playlistId " +
            "ORDER BY tracks.name")
    suspend fun getPlaylistTracks(playlistId: Long): List<TrackEntity>

    @Query("SELECT tracks.trackId FROM tracks " +
            "LEFT JOIN playlistContent ON tracks.trackId = playlistContent.trackId " +
            "WHERE tracks.trackId IN (:trackIds) AND playlistContent.playListId IS NULL")
    suspend fun getTracksWithoutPlaylists(trackIds: List<Long>): List<Long>
}