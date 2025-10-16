package com.practicum.playlistmaker.search.domain

interface SearchHistoryRepository {

    fun load() : List<Track>
    fun save(tracks: List<Track>)

}