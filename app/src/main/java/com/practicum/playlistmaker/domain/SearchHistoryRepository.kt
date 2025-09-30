package com.practicum.playlistmaker.domain

interface SearchHistoryRepository {

    fun load() : List<Track>
    fun save(tracks: List<Track>)

}