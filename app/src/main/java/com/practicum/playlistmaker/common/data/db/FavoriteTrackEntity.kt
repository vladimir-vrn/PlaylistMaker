package com.practicum.playlistmaker.common.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoriteTracks")
class FavoriteTrackEntity(
    @PrimaryKey
    val trackId: Long,
    val timeOfAddition: Long,
)