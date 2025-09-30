package com.practicum.playlistmaker.domain

interface SearchHistoryInteractor {

    fun load() : List<Track>

    fun save(tracks: List<Track>)

    fun update(track: Track, tracks: MutableList<Track>)
}