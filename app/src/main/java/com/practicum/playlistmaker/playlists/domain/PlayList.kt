package com.practicum.playlistmaker.playlists.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayList(
    val id: Long,
    val name: String,
    val description: String,
    val pathCoverFile: String,
    val numTracks: Int,
    val totalTime: Long,
) : Parcelable