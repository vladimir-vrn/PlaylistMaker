package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.common.domain.Track
import com.practicum.playlistmaker.common.data.StorageClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchHistoryRepositoryImpl(
    private val storageClient: StorageClient<List<Track>>
): SearchHistoryRepository {

    override fun load(): Flow<List<Track>> = flow {

        val response = storageClient.getData()
        if (response.resultCode == 200) {
            emit(response.data as List<Track>)
        } else emit(emptyList())
    }

    override fun save(tracks: List<Track>) {

        storageClient.storeData(tracks)

    }
}