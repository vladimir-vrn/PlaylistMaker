package com.practicum.playlistmaker.common.data.db

data class PlaylistWithTotals(
    val id: Long,
    val name: String,
    val description: String,
    val pathCoverFile: String,
    val numTracks: Int,
    val totalTime: Long,
)
