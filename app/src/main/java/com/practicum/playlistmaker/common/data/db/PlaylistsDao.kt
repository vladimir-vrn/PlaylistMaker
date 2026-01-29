package com.practicum.playlistmaker.common.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistsDao {

    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :playListId")
    suspend fun deletePlaylist(playListId: Int)

    @Query("SELECT playlists.id AS id, name, description, pathCoverFile, " +
            "IIF(playlistContent.trackId IS NULL, 0, playlistContent.trackId)  AS trackId " +
            "FROM playlists " +
            "LEFT JOIN playlistContent ON playlists.id = playlistContent.playListId " +
            "WHERE playlists.id != :favoritesPlayListId " +
            "ORDER BY name")
    suspend fun getPlaylists(favoritesPlayListId: Int): List<PlaylistWithTracks>

    @Insert(entity = PlaylistContentEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylistContent(playlistContentEntity: PlaylistContentEntity)

    @Query("DELETE FROM playlistContent WHERE trackId = :trackId AND playListId = :playListId")
    suspend fun deletePlaylistContent(trackId: Long, playListId: Int)

    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Query("DELETE FROM tracks WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)

    @Query("DELETE FROM tracks WHERE trackId IN (" +
            "SELECT tracks.trackId " +
            "FROM tracks " +
            "LEFT JOIN playlistContent ON tracks.trackId = playlistContent.trackId " +
            "WHERE tracks.trackId IN (:tracks) AND playlistContent.trackId IS NULL" +
            ") ")
    suspend fun deleteTracksWithoutPlayList(tracks: List<Long>)

    @Query("SELECT trackId FROM playlistContent " +
            "WHERE playlistContent.trackId = :trackId AND playlistContent.playListId = :playListId")
    suspend fun findTrack(trackId: Long, playListId: Int): List<Long>

    @Query("SELECT " +
            "tracks.trackId AS trackId, tracks.name AS name, tracks.artistName, " +
            "tracks.collectionName, tracks.releaseDate, tracks.primaryGenreName, " +
            "tracks.country , tracks.time, tracks.previewUrl, tracks.artworkUrl100, " +
            "iif(:sortByOrderOfAddition, playlistContent.id, 0) AS orderByOfAddition " +
            "FROM playlistContent " +
            "LEFT JOIN tracks ON playlistContent.trackId = tracks.trackId " +
            "WHERE playlistContent.playListId = :playListId " +
            "ORDER BY orderByOfAddition DESC, name ")
    suspend fun getPlaylistTracks(
        playListId: Int,
        sortByOrderOfAddition: Boolean = false): List<TrackEntity>

    @Query("SELECT playlists.id, playlists.name, playlists.description, " +
        "playlists.pathCoverFile, 0 AS trackId " +
        "FROM playlistContent " +
        "LEFT JOIN playlists ON playlistContent.playListId = playlists.id " +
        "WHERE playlistContent.trackId = :trackId " +
        "ORDER BY name ")
    suspend fun getTrackPlaylists(trackId: Long): List<PlaylistWithTracks>
}