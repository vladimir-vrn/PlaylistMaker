package com.practicum.playlistmaker.mediaLibrary.data

import android.util.Log
import com.practicum.playlistmaker.common.data.db.AppDatabase
import com.practicum.playlistmaker.common.data.db.PlaylistContentEntity
import com.practicum.playlistmaker.common.data.db.PlaylistEntity
import com.practicum.playlistmaker.common.data.db.TrackEntity
import com.practicum.playlistmaker.mediaLibrary.domain.PlayList
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.mediaLibrary.domain.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase
) : PlaylistsRepository {

    override suspend fun insertPlayList(playList: PlayList) {
        Log.d("PL", "insertPlayList, description=${playList.description}")
        appDatabase.playlistsDao().insertPlaylist(
            PlaylistEntity(
                name = playList.name,
                description = playList.description,
                pathCoverFile = playList.pathCoverFile,
            )
        )
    }

    override suspend fun deletePlayList(playListId: Int) {
        if (playListId != PlaylistEntity.FAVORITES_PLAY_LIST_ID) {
            val trackIds = appDatabase.playlistsDao()
                .getPlaylistTracks(playListId).map { it.trackId }
            appDatabase.playlistsDao().deletePlaylist(playListId)
            appDatabase.playlistsDao().deleteTracksWithoutPlayList(trackIds)
        }
    }

    override fun getPlaylists(): Flow<List<PlayList>>  = flow {

        val playlistsWithTracks = appDatabase.playlistsDao()
            .getPlaylists(PlaylistEntity.FAVORITES_PLAY_LIST_ID)

        val playLists = mutableListOf<PlayList>()
        var previousPlayListId = 0
        playlistsWithTracks.forEach { item ->
            if (previousPlayListId != item.id) {
                playLists.add(
                    PlayList(
                        item.id,
                        item.name,
                        item.description,
                        item.pathCoverFile,
                        mutableListOf()
                    )
                )
                previousPlayListId = item.id
            }
            if (item.trackId > 0) playLists[playLists.size - 1].trackIDs.add(item.trackId)
        }
        emit(playLists.toList())
    }

    override fun getPlaylistTracks(playListId: Int): Flow<List<Track>> = flow {
        val tracksEntity = appDatabase.playlistsDao().getPlaylistTracks(playListId)
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

    override fun getTrackPlaylists(trackId: Long): Flow<List<PlayList>> = flow {
        val playlistsWithTracks = appDatabase.playlistsDao().getTrackPlaylists(trackId)
        emit(
            playlistsWithTracks.map {
                PlayList(
                    it.id,
                    it.name,
                    it.description,
                    it.pathCoverFile,
                    mutableListOf(),
                )
            }
        )
    }

    override suspend fun insertTrack(track: Track, playListId: Int) {
        appDatabase.playlistsDao().insertTrack(
            TrackEntity(
                track.trackId,
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
        appDatabase.playlistsDao().insertPlaylistContent(
            PlaylistContentEntity(
                trackId = track.trackId,
                playListId = playListId,
            )
        )
    }

    override suspend fun deleteTrack(trackId: Long, playListId: Int) {
        appDatabase.playlistsDao().deletePlaylistContent(trackId, playListId)
        if (appDatabase.playlistsDao().getTrackPlaylists(trackId).isEmpty())
            appDatabase.playlistsDao().deleteTrack(trackId)
    }

    override fun findTrack(trackId: Long, playListId: Int): Flow<Boolean> = flow {
        emit(appDatabase.playlistsDao().findTrack(trackId, playListId).isNotEmpty())
    }
}