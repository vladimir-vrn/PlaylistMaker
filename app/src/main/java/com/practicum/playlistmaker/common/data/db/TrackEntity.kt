package com.practicum.playlistmaker.common.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey
    val trackId: Long,
    val id: Long,
    val name: String,
    val artistName: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val time: String,
    val previewUrl: String,
    val artworkUrl100: String,
)