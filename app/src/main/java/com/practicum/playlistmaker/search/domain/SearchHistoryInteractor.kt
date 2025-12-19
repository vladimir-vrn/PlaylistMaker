package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.common.domain.Track

interface SearchHistoryInteractor {

    fun load() : List<Track>
    fun save(tracks: List<Track>)
    fun update(track: Track, tracks: MutableList<Track>)
}