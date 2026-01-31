package com.practicum.playlistmaker.common.data.db

data class PlaylistWithTracks(
    val id: Int = 0,
    val name: String,
    val description: String,
    val pathCoverFile: String,
    val trackId: Long,
)
