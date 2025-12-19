package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.common.domain.Track

interface SearchHistoryRepository {

    fun load() : List<Track>
    fun save(tracks: List<Track>)

}