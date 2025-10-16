package com.practicum.playlistmaker.search.domain

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun load() : List<Track> {
        return repository.load()
    }

    override fun save(tracks: List<Track>) {
        repository.save(tracks)
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