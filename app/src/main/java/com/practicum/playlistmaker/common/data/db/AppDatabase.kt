package com.practicum.playlistmaker.common.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 1,
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistContentEntity::class,
    ]
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun tracksDao(): TracksDao
    abstract fun playlistsDao(): PlaylistsDao

}