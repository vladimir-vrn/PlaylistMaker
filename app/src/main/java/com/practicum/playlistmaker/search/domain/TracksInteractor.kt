package com.practicum.playlistmaker.search.domain

interface TracksInteractor {

    fun search(expression: String, consumer: TracksConsumer)

    fun interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}