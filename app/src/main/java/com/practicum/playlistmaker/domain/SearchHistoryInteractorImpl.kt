package com.practicum.playlistmaker.domain

import android.content.Context

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun load(context: Context) : List<Track> {
        return repository.load(context)
    }

    override fun save(tracks: List<Track>, context: Context) {
        repository.save(tracks, context)
    }

    override fun update(track: Track, tracks: MutableList<Track>) {
        val trackPosition = tracks.indexOf(track)
        if (trackPosition == -1) {
            if (tracks.size == DEPTH_SEARCH_HISTORY) tracks.removeAt(DEPTH_SEARCH_HISTORY - 1)
        } else {
            tracks.removeAt(trackPosition)
        }
        tracks.add(0, track)
    }

    companion object {
        private const val DEPTH_SEARCH_HISTORY = 10
    }
}