package com.practicum.playlistmaker.domain

interface TracksInteractor {

    fun search(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}