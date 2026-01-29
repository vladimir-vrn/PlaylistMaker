package com.practicum.playlistmaker.common.data.db

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practicum.playlistmaker.R

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val pathCoverFile: String,
) {
    companion object {
        const val FAVORITES_PLAY_LIST_ID = 1

        fun getSqlQueryInitFavorites(context: Context) =
            "INSERT INTO playlists (id, name, description, pathCoverFile) VALUES " +
                    "(${FAVORITES_PLAY_LIST_ID}, " +
                    "'${context.getString(R.string.media_library_tab_favorite_tracks)}', " +
                    "'', '');"
    }
}