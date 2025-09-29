package com.practicum.playlistmaker.domain

import android.content.Context

interface SearchHistoryRepository {

    fun load(context: Context) : List<Track>
    fun save(tracks: List<Track>, context: Context)

}