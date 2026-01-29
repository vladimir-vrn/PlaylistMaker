package com.practicum.playlistmaker.mediaLibrary.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayList(
    val id: Int,
    val name: String,
    val description: String,
    val pathCoverFile: String,
    val trackIDs: MutableList<Long>,
) : Parcelable