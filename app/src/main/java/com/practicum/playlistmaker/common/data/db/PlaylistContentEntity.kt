package com.practicum.playlistmaker.common.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "playlistContent",
    indices = [
        Index(value = ["trackId"]),
        Index(value = ["playListId"]),
        Index(value = ["trackId", "playListId"], unique = true),
    ],
)
data class PlaylistContentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackId: Long,
    val playListId: Long,
)