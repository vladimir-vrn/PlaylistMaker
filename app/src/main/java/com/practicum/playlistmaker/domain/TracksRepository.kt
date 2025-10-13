package com.practicum.playlistmaker.domain

interface TracksRepository {

    fun search(expression: String): List<Track>?
}