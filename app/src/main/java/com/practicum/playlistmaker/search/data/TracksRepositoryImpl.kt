package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.common.data.NetworkClient
import com.practicum.playlistmaker.common.data.TracksSearchRequest
import com.practicum.playlistmaker.common.data.TracksSearchResponse
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.common.data.timeFormatMmSs
import com.practicum.playlistmaker.mediaLibrary.domain.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val favoriteTracksRepository: FavoriteTracksRepository
) : TracksRepository {

    override fun search(expression: String): Flow<List<Track>?> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            val favoriteTrackIds = favoriteTracksRepository.getTrackIds()
            emit((response as TracksSearchResponse).results.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    timeFormatMmSs(it.trackTimeMillis),
                    it.artworkUrl100,
                    it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl,
                    favoriteTrackIds.contains(it.trackId)
                )
            })
        } else {
            emit(null)
        }
    }
}