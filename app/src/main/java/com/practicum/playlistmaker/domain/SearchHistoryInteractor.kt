package com.practicum.playlistmaker.domain

import android.content.Context

interface SearchHistoryInteractor {

    fun load(context: Context) : List<Track>

    fun save(tracks: List<Track>, context: Context)

    fun update(track: Track, tracks: MutableList<Track>)
}