package com.practicum.playlistmaker.player.ui

import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.playlists.domain.PlayList

sealed interface PlayerState {

    data class Content(
        val track: Track,
        val isFavourite: Boolean,
        val mediaPlayerState: Int,
        val progressTime: String,
        val updateTrack: Boolean,
        val updateIsFavourite: Boolean,
        val updateMediaPlayerState: Boolean,
        val updateProgressTime: Boolean,
        val updatePlayLists: Boolean,
        val playLists: List<PlayList>
    ) : PlayerState

    data class Error(
        val message: String
    ) : PlayerState

    data class Empty(
        val message: String
    ) : PlayerState
}