package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.common.data.StorageClient
import com.practicum.playlistmaker.mediaLibrary.domain.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchHistoryRepositoryImpl(
    private val storageClient: StorageClient<List<Track>>,
    private val favoriteTracksRepository: FavoriteTracksRepository
): SearchHistoryRepository {

    override fun load(): Flow<List<Track>> = flow {

        val response = storageClient.getData()
        if (response.resultCode == 200) {
            val favoriteTrackIds = favoriteTracksRepository.getTrackIds()
            emit((response.data as List<Track>).apply {
                forEach {
                    it.isFavorite = favoriteTrackIds.contains(it.trackId)
                }
            })
        } else emit(emptyList())
    }

    override fun save(tracks: List<Track>) {

        storageClient.storeData(tracks)

    }
}