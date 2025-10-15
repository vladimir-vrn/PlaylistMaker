package com.practicum.playlistmaker.search.domain

interface TracksRepository {

    fun search(expression: String): List<Track>?
}