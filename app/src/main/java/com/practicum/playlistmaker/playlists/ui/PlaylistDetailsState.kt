package com.practicum.playlistmaker.playlists.ui

import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.playlists.domain.PlayList

sealed interface PlaylistDetailsState {

    data class Content(
        val playList: PlayList,
        val tracks: List<Track>,
    ) : PlaylistDetailsState

    data class Error(
        val message: String
    ) : PlaylistDetailsState
}