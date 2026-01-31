package com.practicum.playlistmaker.mediaLibrary.data

import com.practicum.playlistmaker.common.data.db.PlaylistContentEntity
import com.practicum.playlistmaker.common.data.db.PlaylistEntity
import com.practicum.playlistmaker.common.data.db.PlaylistsDao
import com.practicum.playlistmaker.mediaLibrary.domain.PlayList
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.mediaLibrary.domain.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao
) : PlaylistsRepository {

    override suspend fun insertPlayList(playList: PlayList) {
        playlistsDao.insertPlaylist(
            PlaylistEntity(
                name = playList.name,
                description = playList.description,
                pathCoverFile = playList.pathCoverFile,
            )
        )
    }

    override fun getPlaylists(): Flow<List<PlayList>>  = flow {

        val playlistsWithTracks = playlistsDao.getPlaylists()

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

    override suspend fun insertTrack(track: Track, playListId: Int) {
        playlistsDao.insertPlaylistContent(
            PlaylistContentEntity(
                trackId = track.trackId,
                playListId = playListId,
            )
        )
    }
}