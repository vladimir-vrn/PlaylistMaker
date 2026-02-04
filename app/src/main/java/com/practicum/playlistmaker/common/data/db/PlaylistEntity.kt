package com.practicum.playlistmaker.common.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String,
    val pathCoverFile: String,
)