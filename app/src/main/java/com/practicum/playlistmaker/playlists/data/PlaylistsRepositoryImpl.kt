package com.practicum.playlistmaker.playlists.data

import com.practicum.playlistmaker.common.data.db.PlaylistContentEntity
import com.practicum.playlistmaker.common.data.db.PlaylistEntity
import com.practicum.playlistmaker.common.data.db.PlaylistsDao
import com.practicum.playlistmaker.common.data.db.TrackEntity
import com.practicum.playlistmaker.common.data.db.TracksDao
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.playlists.domain.PlayList
import com.practicum.playlistmaker.playlists.domain.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val tracksDao: TracksDao,
) : PlaylistsRepository {

    override suspend fun insertPlayList(playList: PlayList) {
        playlistsDao.insertPlaylist(
            PlaylistEntity(
                playList.id,
                playList.name,
                playList.description,
                playList.pathCoverFile
            )
        )
    }

    override suspend fun deletePlayList(playListId: Long) {
        playlistsDao.deletePlayList(playListId)
        val tracks = playlistsDao.getPlaylistTracks(playListId)
        playlistsDao.deletePlayListContent(playListId)
        val tracksWithoutPlaylists = playlistsDao.getTracksWithoutPlaylists(
            tracks.map { it.trackId }
        )
        val favoriteTracks = tracksDao.findFavoriteTracks(tracksWithoutPlaylists)
        tracksDao.deleteTracks(
            tracksWithoutPlaylists.filter { !favoriteTracks.contains(it) }
        )
    }

    override fun getPlaylists(): Flow<List<PlayList>> = flow {
        emit(playlistsDao.getPlaylists().map {
            PlayList(
                it.id,
                it.name,
                it.description,
                it.pathCoverFile,
                it.numTracks,
                it.totalTime
            )
        })
    }

    override fun getPlaylistTracks(playListId: Long): Flow<List<Track>> = flow {
        emit(
            playlistsDao.getPlaylistTracks(playListId).map {
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

    override suspend fun insertTrack(track: Track, playListId: Long) {
        playlistsDao.insertPlaylistContent(
            PlaylistContentEntity(
                trackId = track.trackId,
                playListId = playListId,
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

    override suspend fun deleteTrack(trackId: Long, playListId: Long) {
        playlistsDao.deletePlayListContent(playListId, trackId)
        if (playlistsDao.getTracksWithoutPlaylists(listOf(trackId)).isNotEmpty())
            if (tracksDao.findFavoriteTracks(listOf(trackId)).isEmpty())
                tracksDao.deleteTracks(listOf(trackId))
    }
}