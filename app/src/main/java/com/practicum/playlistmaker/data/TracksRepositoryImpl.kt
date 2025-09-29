package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.TracksRepository
import com.practicum.playlistmaker.timeFormatMmSs

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun search(expression: String): List<Track>? {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    timeFormatMmSs(it.trackTimeMillis),
                    it.artworkUrl100,
                    it.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"),
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else {
            return null
        }
    }
}